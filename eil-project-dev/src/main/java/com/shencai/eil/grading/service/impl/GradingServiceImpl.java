package com.shencai.eil.grading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.power.common.util.ObjectUtil;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.grading.entity.*;
import com.shencai.eil.grading.mapper.*;
import com.shencai.eil.grading.model.*;
import com.shencai.eil.grading.service.IEntRiskAssessResultService;
import com.shencai.eil.grading.service.IEntRiskParamValueService;
import com.shencai.eil.grading.service.IGradingService;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.policy.model.EnterpriseQueryParam;
import com.shencai.eil.policy.model.EnterpriseVO;
import com.shencai.eil.risk.entity.RiskControlPollutionValue;
import com.shencai.eil.risk.mapper.RiskControlPollutionValueMapper;
import com.shencai.eil.risk.service.IRiskControlPollutionValueService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * Created by zhoujx on 2018/9/19.
 */
@Service
public class GradingServiceImpl implements IGradingService {

    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    @Autowired
    private EntMappingTargetTypeMapper entMappingTargetTypeMapper;
    @Autowired
    private RiskMaterialMapper riskMaterialMapper;
    @Autowired
    private EnvStatisticsMapper envStatisticsMapper;
    @Autowired
    private EnvStatisticsParamValueMapper envStatisticsParamValueMapper;
    @Autowired
    private IEntRiskParamValueService entRiskParamValueService;
    @Autowired
    private EntRiskParamValueMapper entRiskParamValueMapper;
    @Autowired
    private EntMappingEnvStatisticsMapper entMappingEnvStatisticsMapper;
    @Autowired
    private EntRiskAssessResultMapper entRiskAssessResultMapper;
    @Autowired
    private IEntRiskAssessResultService entRiskAssessResultService;
    @Autowired
    private ComputeConstantMapper computeConstantMapper;
    @Autowired
    private TargetWeightMapper targetWeightMapper;
    @Autowired
    private IRiskControlPollutionValueService riskControlPollutionValueService;
    @Autowired
    private CategoryEnterpriseDefaultMapper categoryEnterpriseDefaultMapper;
    @Autowired
    private EntViolationHistoryMapper entViolationHistoryMapper;
    @Autowired
    private RiskControlPollutionValueMapper riskControlPollutionValueMapper;
    @Autowired
    private PollutionEmissionsIntensityMapper pollutionEmissionsIntensityMapper;

    @Override
    public void execute(GradingParam param) {
        EnterpriseVO enterprise = getEnterpriseInfo(param.getEnterpriseId());
        disposeEnterpriseRiskProfileData(enterprise);
        List<String> riskIndicatorSystemType = determineEnterpriseRiskIndicatorSystem(enterprise);
        //calculate risk factor R1
        calculateRiskFactor(enterprise, riskIndicatorSystemType);
        //calculate compliance capability
        calculateComplianceCapability(enterprise, riskIndicatorSystemType);
        //calculate the secondary control mechanism R3
        calculateSecondaryControlMechanism(enterprise, riskIndicatorSystemType);
    }

    @Override
    public GradingVO getGradingResult(GradingQueryParam queryParam) {
        EnterpriseVO enterpriseInfo = getEnterpriseInfo(queryParam.getEnterpriseId());

        disposeEnterpriseRiskProfileData(enterpriseInfo);
        List<String> riskIndicatorSystemType = determineEnterpriseRiskIndicatorSystem(enterpriseInfo);
        //calculate risk factor R1
        calculateRiskFactor(enterpriseInfo, riskIndicatorSystemType);
        //calculate compliance capability
        calculateComplianceCapability(enterpriseInfo, riskIndicatorSystemType);
        //calculate the secondary control mechanism R3
        calculateSecondaryControlMechanism(enterpriseInfo, riskIndicatorSystemType);

        GradingVO returnData = getReturnData(queryParam, enterpriseInfo, riskIndicatorSystemType);
        return returnData;
    }

    private GradingVO getReturnData(GradingQueryParam queryParam, EnterpriseVO enterpriseInfo,
                                    List<String> riskIndicatorSystemType) {
        List<TargetResult> suddenTargetResultList = listRootTargetResult(queryParam);
        List<String> rootTargetIds = listRootTargetId(suddenTargetResultList);
        GradingVO returnData = new GradingVO();
        returnData.setEnterpriseName(enterpriseInfo.getName());
        returnData.setEnterpriseStatus(enterpriseInfo.getStatus());
        List<EntRiskAssessResultVO> allSuddenChildTargetResultList =
                listAllChildTargetResult(queryParam, rootTargetIds, TargetWeightType.SUDDEN_RISK.getCode());
        setChildTargetResults(suddenTargetResultList, allSuddenChildTargetResultList);
        returnData.setSuddenTargetResultList(suddenTargetResultList);
        if (riskIndicatorSystemType.contains(TargetWeightType.PROGRESSIVE_RISK.getCode())) {
            List<TargetResult> progressiveTargetResultList = listRootTargetResult(queryParam);
            List<EntRiskAssessResultVO> allProgressiveChildTargetResultList =
                    listAllChildTargetResult(queryParam, rootTargetIds, TargetWeightType.PROGRESSIVE_RISK.getCode());
            setChildTargetResults(progressiveTargetResultList, allProgressiveChildTargetResultList);
            returnData.setProgressiveTargetResultList(progressiveTargetResultList);
        }
        return returnData;
    }

    private void setChildTargetResults(List<TargetResult> targetResultList,
                                       List<EntRiskAssessResultVO> allChildTargetResultList) {
        for (TargetResult targetResult : targetResultList) {
            String targetId = targetResult.getTargetId();
            List<EntRiskAssessResultVO> childTargetResultList = new ArrayList<>();
            for (EntRiskAssessResultVO entRiskAssessResult : allChildTargetResultList) {
                if (entRiskAssessResult.getParentId().equals(targetId)) {
                    childTargetResultList.add(entRiskAssessResult);
                }
            }
            targetResult.setEntRiskAssessResultList(childTargetResultList);
        }
    }

    private List<EntRiskAssessResultVO> listAllChildTargetResult(GradingQueryParam queryParam,
                                                                 List<String> rootTargetIds, String targetType) {
        EntRiskAssessResultQueryParam param = new EntRiskAssessResultQueryParam();
        param.setParentIdList(rootTargetIds);
        param.setEnterpriseId(queryParam.getEnterpriseId());
        param.setTargetType(targetType);
        List<EntRiskAssessResultVO> childTargetList = targetWeightMapper.listChildTarget(param);
        List<EntRiskAssessResultVO> riskAssessResultVOS = entRiskAssessResultMapper.listEntRiskAssessResult(param);
        for (EntRiskAssessResultVO childTarget : childTargetList) {
            for (EntRiskAssessResultVO riskAssessResult : riskAssessResultVOS) {
                if (riskAssessResult.getTargetCode().equals(childTarget.getTargetCode())) {
                    childTarget.setTargetResult(riskAssessResult.getTargetResult());
                }
            }
        }
        return childTargetList;
    }

    private List<String> listRootTargetId(List<TargetResult> targetResultList) {
        List<String> rootTargetIdList = new ArrayList<>();
        for (TargetResult targetResult : targetResultList) {
            rootTargetIdList.add(targetResult.getTargetId());
        }
        return rootTargetIdList;
    }

    private List<TargetResult> listRootTargetResult(GradingQueryParam queryParam) {
        List<TargetResult> rootTargetResults = targetWeightMapper.listRootTarget();
        EntRiskAssessResultQueryParam param = new EntRiskAssessResultQueryParam();
        param.setEnterpriseId(queryParam.getEnterpriseId());
        List<TargetResult> rootTargetResultValues = entRiskAssessResultMapper.listRootTargetResult(param);
        for (TargetResult targetResult : rootTargetResults) {
            for (TargetResult result : rootTargetResultValues) {
                if (result.getTargetId().equals(targetResult.getTargetId())) {
                    targetResult.setTargetResult(result.getTargetResult());
                }
            }
        }
        return rootTargetResults;
    }

    private EnterpriseVO getEnterpriseInfo(String enterpriseId) {
        EnterpriseQueryParam queryParam = new EnterpriseQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        EnterpriseVO enterprise = enterpriseInfoMapper.getEnterprise(queryParam);
        if (ObjectUtils.isEmpty(enterprise)) {
            throw new BusinessException("The enterprise has been removed!");
        }
        return enterprise;
    }

    private void disposeEnterpriseRiskProfileData(EnterpriseVO enterprise) {
        EnvStatistics envStatistics = getEnvironmentStatistics(enterprise);
        insertEnterpriseMappingEnvironmentStatistics(enterprise, envStatistics);
        List<EnvStatisticsParamValueVO> envStatisticsParamValueList = listEnvStatisticsParamValue(envStatistics);
        insertEnterpriseRiskParamValues(enterprise, envStatisticsParamValueList);
    }

    private List<String> determineEnterpriseRiskIndicatorSystem(EnterpriseVO enterprise) {
        List<String> riskType = new ArrayList<>();
        List<EntMappingTargetType> targetTypeList =
                entMappingTargetTypeMapper.selectList(new QueryWrapper<EntMappingTargetType>()
                        .eq("ent_id", enterprise.getId())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        for (EntMappingTargetType entMappingTargetType : targetTypeList) {
            if (riskType.indexOf(entMappingTargetType.getTargetWeightType()) < 0) {
                riskType.add(entMappingTargetType.getTargetWeightType());
            }
        }
        if (CollectionUtils.isEmpty(riskType)) {
            EntRiskParamValueQueryParam queryParam = new EntRiskParamValueQueryParam();
            queryParam.setEnterpriseId(enterprise.getId());
            int count = entRiskParamValueMapper.countGradualRiskParams(queryParam);
            if (count > 0) {
                riskType.add(TargetWeightType.PROGRESSIVE_RISK.getCode());
                saveEntRiskType(enterprise, TargetWeightType.PROGRESSIVE_RISK);
            }
            riskType.add(TargetWeightType.SUDDEN_RISK.getCode());
            saveEntRiskType(enterprise, TargetWeightType.SUDDEN_RISK);
        }
        return riskType;
    }

    /**
     * calculate risk factor R1
     */
    private void calculateRiskFactor(EnterpriseVO enterprise, List<String> riskType) {
        //calculate R1.1
        firstStepOfRiskFactorCalculation(enterprise, riskType);
        //calculate R1.2
        secondStepOfRiskFactorCalculation(enterprise, riskType);
    }

    /**
     * calculate R1.1
     */
    private void firstStepOfRiskFactorCalculation(EnterpriseVO enterprise, List<String> riskType) {
        //obtain the R1.1 index weight of the enterprise
        List<TargetWeight> targetWeightList =
                obtainTargetWeight(riskType, TargetEnum.SECONDARY_INDICATORS_OF_RISK_FACTORS.getCode());
        List<String> targetWeightIds = listTargetWeightId(targetWeightList);
        //obtain the existing R1.1 calculation results of the enterprise
        List<EntRiskAssessResult> riskAssessResultList = listEntRiskAssessResults(enterprise, targetWeightIds);
        //if results is not found, calculate R1.1
        if (CollectionUtils.isEmpty(riskAssessResultList)) {
            List<EntRiskParamValueVO> entRiskParamValueList = listEnterpriseRiskParamValue(enterprise
                    , TemplateCategory.RAW_MATERIAL.getCode());
            List<RiskMaterialVO> riskMaterialList = listRiskMaterial(enterprise, entRiskParamValueList);
            setRiskMaterialValueToEntRiskParamValueList(entRiskParamValueList, riskMaterialList);
            List<EntRiskAssessResult> entRiskAssessResults = new ArrayList<>();
            if (riskType.contains(TargetWeightType.PROGRESSIVE_RISK.getCode())) {
                //calculate progressive risk R1.1
                double progressiveSecondaryIndicatorsOfRiskFactors =
                        calculateProgressiveSecondaryIndicatorsOfRiskFactors(enterprise, entRiskParamValueList,
                                riskMaterialList);
                String progressiveTargetWeightId = getTargetWeightId(targetWeightList,
                        TargetWeightType.PROGRESSIVE_RISK.getCode());
                createEntRiskAssessResult(enterprise, entRiskAssessResults,
                        progressiveSecondaryIndicatorsOfRiskFactors, progressiveTargetWeightId);
            }
            //calculate sudden risk R1.1
            double suddenSecondaryIndicatorsOfRiskFactors =
                    calculateSuddenSecondaryIndicatorsOfRiskFactors(enterprise, entRiskParamValueList, riskMaterialList);
            String suddenTargetWeightId = getTargetWeightId(targetWeightList, TargetWeightType.SUDDEN_RISK.getCode());
            createEntRiskAssessResult(enterprise, entRiskAssessResults,
                    suddenSecondaryIndicatorsOfRiskFactors, suddenTargetWeightId);
            saveRiskAssessResults(entRiskAssessResults);
        }
    }

    /**
     * calculate R1.2
     */
    private void secondStepOfRiskFactorCalculation(EnterpriseVO enterprise, List<String> riskType) {
        //obtain the R1.2 index weight of the enterprise
        List<TargetWeight> targetWeightList =
                obtainTargetWeight(riskType, TargetEnum.RISK_FACTORS_SECONDARY_STEP.getCode());
        List<String> targetWeightIds = listTargetWeightId(targetWeightList);
        //obtain the existing R1.2 calculation results of the enterprise
        List<EntRiskAssessResult> riskAssessResultList = listEntRiskAssessResults(enterprise, targetWeightIds);
        if (CollectionUtils.isEmpty(riskAssessResultList)) {
            //calculate R1.2
            Double result = calculateOnePointTwoSecondaryIndicatorsOfRiskFactors(enterprise);
            saveEntRiskAssessResult(enterprise, targetWeightIds, result);
        }
    }

    /**
     * R2.1
     * calculate compliance capability
     */
    private void calculateComplianceCapability(EnterpriseVO enterprise, List<String> riskType) {
        //obtain the R2.1 index weight of the enterprise
        List<TargetWeight> targetWeightList =
                obtainTargetWeight(riskType, TargetEnum.ENTERPRISE_COMPLIANCE.getCode());
        List<String> targetWeightIds = listTargetWeightId(targetWeightList);
        //obtain the existing R2.1 calculation results of the enterprise
        List<EntRiskAssessResult> riskAssessResultList = listEntRiskAssessResults(enterprise, targetWeightIds);
        //if results is not found, calculate R2.1
        if (CollectionUtils.isEmpty(riskAssessResultList)) {
            Double result = calculateCompliance(enterprise);
            saveEntRiskAssessResult(enterprise, targetWeightIds, result);
        }
    }

    /**
     * calculate the secondary control mechanism R3
     */
    private void calculateSecondaryControlMechanism(EnterpriseVO enterprise, List<String> riskIndicatorSystemType) {
        //calculate R3.1
        secondaryControlMechanismOfOne(enterprise, riskIndicatorSystemType);
        //calculate R3.2
        secondaryControlMechanismOfTwo(enterprise, riskIndicatorSystemType);
    }


    /**
     * Secondary control mechanism R3.1
     */
    private void secondaryControlMechanismOfOne(EnterpriseVO enterpriseVO, List<String> riskType) {
        //obtain the R3.1 index weight of the enterprise
        List<TargetWeight> targetWeightList =
                obtainTargetWeight(riskType, TargetEnum.R_THREE_ONE.getCode());
        List<String> targetWeightIds = listTargetWeightId(targetWeightList);
        //obtain the existing R3.1 calculation results of the enterprise
        List<EntRiskAssessResult> entRiskAssessResultList = listEntRiskAssessResults(enterpriseVO, targetWeightIds);
        //if results is not found, calculate R3.1
        if (CollectionUtils.isEmpty(entRiskAssessResultList)) {
            RiskControlPollutionValue riskControlPollutionValue = riskControlPollutionValueService.getOne(
                    new QueryWrapper<RiskControlPollutionValue>()
                            .eq("area_code", enterpriseVO.getProvinceCode())
                            .eq("valid", BaseEnum.VALID_YES.getCode()));
            double result = ObjectUtils.isEmpty(riskControlPollutionValue) ? 0.0 : riskControlPollutionValue.getRiskControl();
            saveEntRiskAssessResult(enterpriseVO, targetWeightIds, result);
        }
    }

    /**
     * Secondary control mechanism R3.2 (progressive)
     */
    private void secondaryControlMechanismOfTwo(EnterpriseVO enterpriseVO, List<String> typeList) {
        if (typeList.contains(TargetWeightType.PROGRESSIVE_RISK.getCode())) {
            //obtain the R3.2 index weight of the enterprise
            List<TargetWeight> targetWeightList =
                    obtainTargetWeight(typeList, TargetEnum.R_THREE_TWO.getCode());
            List<String> targetWeightIds = listTargetWeightId(targetWeightList);
            //obtain the existing R3.2 calculation results of the enterprise
            List<EntRiskAssessResult> entRiskAssessResultList = listEntRiskAssessResults(enterpriseVO, targetWeightIds);
            //if results is not found, calculate R3.2
            if (CollectionUtils.isEmpty(entRiskAssessResultList)) {
                RiskControlPollutionValue riskControlPollutionValue = riskControlPollutionValueService.getOne(
                        new QueryWrapper<RiskControlPollutionValue>()
                                .eq("area_code", enterpriseVO.getProvinceCode())
                                .eq("valid", BaseEnum.VALID_YES.getCode()));
                double result = ObjectUtils.isEmpty(riskControlPollutionValue) ? 0.0 : riskControlPollutionValue.getLandPollutionIndex();
                saveEntRiskAssessResult(enterpriseVO, targetWeightIds, result);
            }
        }
    }

    private List<EntRiskParamValueVO> listEnterpriseRiskParamValue(EnterpriseVO enterprise, String templateCategoryCode) {
        EntRiskParamValueQueryParam queryParam = new EntRiskParamValueQueryParam();
        queryParam.setEnterpriseId(enterprise.getId());
        queryParam.setTemplateType(templateCategoryCode);
        return entRiskParamValueMapper.listEntRiskParamValue(queryParam);
    }

    private List<RiskMaterialVO> listRiskMaterial(EnterpriseVO enterprise,
                                                  List<EntRiskParamValueVO> envRiskParamValueList) {
        List<String> riskMaterialNameList = new ArrayList<>();
        riskMaterialNameList.add(enterprise.getMainProductName());
        for (EntRiskParamValueVO envRiskParamValue : envRiskParamValueList) {
            riskMaterialNameList.add(envRiskParamValue.getName());
        }
        RiskMaterialQueryParam queryParam = new RiskMaterialQueryParam();
        queryParam.setRiskMaterialNameList(riskMaterialNameList);
        queryParam.setTemplateType(TemplateCategory.FAST_GRADING.getCode());
        return riskMaterialMapper.listRiskMaterial(queryParam);
    }

    private void setRiskMaterialValueToEntRiskParamValueList(List<EntRiskParamValueVO> entRiskParamValueList,
                                                             List<RiskMaterialVO> riskMaterialList) {
        for (EntRiskParamValueVO riskParamValueVO : entRiskParamValueList) {
            for (RiskMaterialVO riskMaterialVO : riskMaterialList) {
                if (riskParamValueVO.getName().equals(riskMaterialVO.getName())) {
                    String templateParamName = riskMaterialVO.getTemplateParamName();
                    if (TemplateEnum.CRITICAL_QUANTITY.getName().equals(templateParamName)) {
                        riskParamValueVO.setCriticalQuantity(riskMaterialVO.getValue());
                    }
                    if (TemplateEnum.BIOAVAILABILITY.getName().equals(templateParamName)) {
                        riskParamValueVO.setBioavailability(riskMaterialVO.getValue());
                    }
                    if (TemplateEnum.BIOLOGICAL_ENRICHMENT.getName().equals(templateParamName)) {
                        riskParamValueVO.setBiologicalEnrichment(riskMaterialVO.getValue());
                    }
                    if (TemplateEnum.CARCINOGENICITY.getName().equals(templateParamName)) {
                        riskParamValueVO.setCarcinogenicity(riskMaterialVO.getValue());
                    }
                }
            }
        }
    }

    /**
     * obtain index weight
     */
    private List<TargetWeight> obtainTargetWeight(List<String> riskType, String targetCode) {
        if (CollectionUtils.isEmpty(riskType)) {
            throw new BusinessException("The enterprise risk indicator system type cannot be empty!");
        }
        List<TargetWeight> result = new ArrayList<>();
        if (riskType.contains(TargetWeightType.PROGRESSIVE_RISK.getCode())) {
            TargetWeight targetWeight = targetWeightMapper.selectOne(new QueryWrapper<TargetWeight>()
                    .eq("type", TargetWeightType.PROGRESSIVE_RISK.getCode())
                    .eq("code", targetCode)
                    .eq("valid", BaseEnum.VALID_YES.getCode()));
            result.add(targetWeight);
        }
        if (!TargetEnum.R_THREE_TWO.getCode().equals(targetCode)
                && riskType.contains(TargetWeightType.SUDDEN_RISK.getCode())) {
            TargetWeight targetWeight = targetWeightMapper.selectOne(new QueryWrapper<TargetWeight>()
                    .eq("type", TargetWeightType.SUDDEN_RISK.getCode())
                    .eq("code", targetCode)
                    .eq("valid", BaseEnum.VALID_YES.getCode()));
            result.add(targetWeight);
        }
        return result;
    }

    private List<String> listTargetWeightId(List<TargetWeight> targetWeightList) {
        List<String> targetWeightIds = new ArrayList<>();
        for (TargetWeight targetWeight : targetWeightList) {
            targetWeightIds.add(targetWeight.getId());
        }
        return targetWeightIds;
    }

    private String getTargetWeightId(List<TargetWeight> targetWeightList, String targetWeightType) {
        String targetWeightId = null;
        for (TargetWeight targetWeight : targetWeightList) {
            if (targetWeightType.equals(targetWeight.getType())) {
                targetWeightId = targetWeight.getId();
            }
        }
        return targetWeightId;
    }

    /**
     * obtain the existing calculation results
     */
    private List<EntRiskAssessResult> listEntRiskAssessResults(EnterpriseVO enterprise, List<String> targetIds) {
        List<EntRiskAssessResult> entRiskAssessResults = entRiskAssessResultMapper
                .selectList(new QueryWrapper<EntRiskAssessResult>()
                        .eq("ent_id", enterprise.getId())
                        .in("target_weight_id", targetIds)
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        return entRiskAssessResults;
    }

    private void createEntRiskAssessResult(EnterpriseVO enterprise, List<EntRiskAssessResult> entRiskAssessResults,
                                           double result, String targetWeightId) {
        EntRiskAssessResult riskAssessResult = new EntRiskAssessResult();
        riskAssessResult.setId(StringUtil.getUUID());
        riskAssessResult.setEntId(enterprise.getId());
        riskAssessResult.setTargetWeightId(targetWeightId);
        riskAssessResult.setAssessValue(result);
        riskAssessResult.setCreateTime(DateUtil.getNowTimestamp());
        riskAssessResult.setUpdateTime(DateUtil.getNowTimestamp());
        riskAssessResult.setValid((Integer) BaseEnum.VALID_YES.getCode());
        entRiskAssessResults.add(riskAssessResult);
    }

    /**
     * save the index calculation results
     */
    private void saveRiskAssessResults(List<EntRiskAssessResult> entRiskAssessResults) {
        if (!CollectionUtils.isEmpty(entRiskAssessResults)) {
            entRiskAssessResultService.saveBatch(entRiskAssessResults);
        }
    }

    /**
     * calculate progressive risk R1.1
     */
    private double calculateProgressiveSecondaryIndicatorsOfRiskFactors(EnterpriseVO enterprise,
                                                                        List<EntRiskParamValueVO> envRiskParamValueList,
                                                                        List<RiskMaterialVO> riskMaterialList) {
        String mainProductName = enterprise.getMainProductName();
        Double mainProductQty = enterprise.getMainProductQty();
        double mainProductBioavailability = 0;
        double mainProductBiologicalEnrichment = 0;
        double mainProductCarcinogenicity = 0;
        for (RiskMaterialVO riskMaterialVO : riskMaterialList) {
            if (mainProductName.equals(riskMaterialVO.getName())) {
                String templateParamName = riskMaterialVO.getTemplateParamName();
                if (TemplateEnum.BIOAVAILABILITY.getName().equals(templateParamName)) {
                    mainProductBioavailability = riskMaterialVO.getValue();
                }
                if (TemplateEnum.BIOLOGICAL_ENRICHMENT.getName().equals(templateParamName)) {
                    mainProductBiologicalEnrichment = riskMaterialVO.getValue();
                }
                if (TemplateEnum.CARCINOGENICITY.getName().equals(templateParamName)) {
                    mainProductCarcinogenicity = riskMaterialVO.getValue();
                }
            }
        }
        double k1 = getK1();
        Double yield = enterprise.getYield();
        double progressiveSecondaryIndicatorsOfRiskFactors = mainProductQty / k1 * mainProductBioavailability
                * mainProductBiologicalEnrichment / mainProductCarcinogenicity;
        for (EntRiskParamValueVO riskParamValue : envRiskParamValueList) {
            if (riskParamValue.getBioavailability() != null && riskParamValue.getBioavailability() != 0
                    && riskParamValue.getCarcinogenicity() != null && riskParamValue.getCarcinogenicity() != 0) {
                progressiveSecondaryIndicatorsOfRiskFactors +=
                        (riskParamValue.getValue() * yield) / k1 * riskParamValue.getBioavailability()
                                * riskParamValue.getBiologicalEnrichment() / riskParamValue.getCarcinogenicity();
            }
        }
        return progressiveSecondaryIndicatorsOfRiskFactors;
    }

    /**
     * calculate sudden risk R1.1
     */
    private double calculateSuddenSecondaryIndicatorsOfRiskFactors(EnterpriseVO enterprise,
                                                                   List<EntRiskParamValueVO> envRiskParamValueList,
                                                                   List<RiskMaterialVO> riskMaterialList) {
        String mainProductName = enterprise.getMainProductName();
        Double mainProductQty = enterprise.getMainProductQty();
        double mainProductCriticalQuantity = 0;
        for (RiskMaterialVO riskMaterialVO : riskMaterialList) {
            if (mainProductName.equals(riskMaterialVO.getName())) {
                if (TemplateEnum.CRITICAL_QUANTITY.getName().equals(riskMaterialVO.getTemplateParamName())) {
                    mainProductCriticalQuantity = riskMaterialVO.getValue();
                    break;
                }
            }
        }
        double k1 = getK1();
        Double yield = enterprise.getYield();
        double suddenSecondaryIndicatorsOfRiskFactors = mainProductQty / k1 / mainProductCriticalQuantity;
        for (EntRiskParamValueVO riskParamValue : envRiskParamValueList) {
            if (riskParamValue.getCriticalQuantity() != null && riskParamValue.getCriticalQuantity() != 0) {
                suddenSecondaryIndicatorsOfRiskFactors +=
                        (riskParamValue.getValue() * yield) / k1 / riskParamValue.getCriticalQuantity();
            }
        }
        return suddenSecondaryIndicatorsOfRiskFactors;
    }

    /**
     * calculate progressive/sudden R1.2
     */
    private Double calculateOnePointTwoSecondaryIndicatorsOfRiskFactors(EnterpriseVO enterprise) {
        List<EntRiskParamValueVO> envRiskParamValueList = listEnterpriseRiskParamValue(enterprise
                , TemplateCategory.EMISSIONS_INTENSITY.getCode());
        List<String> pollutionCategoryNameList = new ArrayList<>();
        for (EntRiskParamValueVO envRiskParamValueVO : envRiskParamValueList) {
            if (!pollutionCategoryNameList.contains(envRiskParamValueVO.getRemark())) {
                pollutionCategoryNameList.add(envRiskParamValueVO.getRemark());
            }
        }
        HashMap<String, Double> emissionMap = new HashMap<>();
        List<PollutionEmissionsIntensity> emissionsIntensityList = pollutionEmissionsIntensityMapper
                .selectList(new QueryWrapper<PollutionEmissionsIntensity>()
                        .in("pollution_category", pollutionCategoryNameList)
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        for (PollutionEmissionsIntensity pollutionEmissionsIntensity : emissionsIntensityList) {
            if (EmissionModeType.DIRECT.getCode().equalsIgnoreCase(enterprise.getEmissionModeTypeCode())) {
                emissionMap.put(pollutionEmissionsIntensity.getPollutionCategory()
                        , pollutionEmissionsIntensity.getDirectValue());
            } else {
                emissionMap.put(pollutionEmissionsIntensity.getPollutionCategory()
                        , pollutionEmissionsIntensity.getIndirectValue());
            }
        }
        Double r = 0.0;
        for (EntRiskParamValueVO envRiskParamValueVO : envRiskParamValueList) {
            Double value = emissionMap.get(envRiskParamValueVO.getRemark());
            if (value != null && value.doubleValue() != 0) {
                r += envRiskParamValueVO.getValue() * enterprise.getYield() / value;
            }
        }
        return r;
    }

    /**
     * calculate progressive/sudden R2.1
     */
    private double calculateCompliance(EnterpriseVO enterprise) {
        //Obtain enterprise compliance defaults
        CategoryEnterpriseDefault categoryEnterpriseDefault = getCategoryEnterpriseDefault(enterprise);
        //Obtain enterprise history compliance records
        List<EntViolationHistory> historyList = listEntViolationHistories(enterprise);
        //Obtain the maximum enterprise compliance
        double maxViolationQty = getMaxViolationQty(enterprise, categoryEnterpriseDefault, historyList);
        double result = acquireEnterpriseComplianceCapability(enterprise, maxViolationQty);
        return result;
    }

    private void insertEnterpriseMappingEnvironmentStatistics(EnterpriseVO enterprise, EnvStatistics envStatistics) {
        EntMappingEnvStatistics entMappingEnvStatistics = new EntMappingEnvStatistics();
        entMappingEnvStatistics.setId(StringUtil.getUUID());
        entMappingEnvStatistics.setEntId(enterprise.getId());
        entMappingEnvStatistics.setEnvStatisticsId(envStatistics.getId());
        entMappingEnvStatistics.setCreateTime(DateUtil.getNowTimestamp());
        entMappingEnvStatistics.setUpdateTime(DateUtil.getNowTimestamp());
        entMappingEnvStatistics.setValid((Integer) BaseEnum.VALID_YES.getCode());
        entMappingEnvStatisticsMapper.insert(entMappingEnvStatistics);
    }

    private void insertEnterpriseRiskParamValues(EnterpriseVO enterprise,
                                                 List<EnvStatisticsParamValueVO> envStatisticsParamValueList) {
        List<EntRiskParamValue> envRiskParamValueList = new ArrayList<>();
        for (EnvStatisticsParamValueVO envStatisticsParamValue : envStatisticsParamValueList) {
            EntRiskParamValue envRiskParamValue = new EntRiskParamValue();
            envRiskParamValue.setId(StringUtil.getUUID());
            envRiskParamValue.setParamId(envStatisticsParamValue.getParamId());
            envRiskParamValue.setEnterpriseId(enterprise.getId());
            envRiskParamValue.setValue(envStatisticsParamValue.getValue());
            envRiskParamValue.setCreateTime(DateUtil.getNowTimestamp());
            envRiskParamValue.setUpdateTime(DateUtil.getNowTimestamp());
            envRiskParamValue.setValid((Integer) BaseEnum.VALID_YES.getCode());
            envRiskParamValueList.add(envRiskParamValue);
        }
        if (!CollectionUtils.isEmpty(envRiskParamValueList)) {
            entRiskParamValueService.saveBatch(envRiskParamValueList);
        }
    }

    private List<EnvStatisticsParamValueVO> listEnvStatisticsParamValue(EnvStatistics envStatistics) {
        EnvStatisticsParamValueQueryParam queryParam = new EnvStatisticsParamValueQueryParam();
        queryParam.setEnvStatisticsId(envStatistics.getId());
        queryParam.setTemplateType(TemplateCategory.ENV_BASE_TEMPLE.getCode());
        return envStatisticsParamValueMapper.listEnvStatisticsParamValue(queryParam);
    }

    private EnvStatistics getEnvironmentStatistics(EnterpriseVO enterprise) {
        EnvStatistics envStatistics = envStatisticsMapper.selectOne(
                new QueryWrapper<EnvStatistics>()
                        .eq("province_canton_code", enterprise.getProvinceCode())
                        .eq("ent_scale", enterprise.getScale())
                        .eq("product_id", enterprise.getMainProductId())
                        .eq("technique_code", enterprise.getTechniqueCode())
                        .eq("emission_mode_type_code", enterprise.getEmissionModeTypeCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (ObjectUtils.isEmpty(envStatistics)) {
            throw new BusinessException("The enterprise has no matching environmental statistics!");
        }
        //TODO
        enterprise.setMainProductQty(envStatistics.getMainProductQty());
        return envStatistics;
    }

    private void saveEntRiskType(EnterpriseVO enterprise, TargetWeightType suddenRisk) {
        insertEntMappingTargetType(enterprise, suddenRisk);
    }

    private void insertEntMappingTargetType(EnterpriseVO enterprise, TargetWeightType gradualRisk) {
        EntMappingTargetType entMappingTargetType = new EntMappingTargetType();
        entMappingTargetType.setId(StringUtil.getUUID());
        entMappingTargetType.setEntId(enterprise.getId());
        entMappingTargetType.setTargetWeightType(gradualRisk.getCode());
        entMappingTargetType.setCreateTime(DateUtil.getNowTimestamp());
//                    entMappingTargetType.setCreateUserId();
        entMappingTargetType.setUpdateTime(DateUtil.getNowTimestamp());
//                    entMappingTargetType.setUpdateUserId();
        entMappingTargetType.setValid((Integer) BaseEnum.VALID_YES.getCode());
        entMappingTargetTypeMapper.insert(entMappingTargetType);
    }

    /**
     * Obtain enterprise history compliance records
     */
    private List<EntViolationHistory> listEntViolationHistories(EnterpriseVO enterprise) {
        return entViolationHistoryMapper.selectList(new QueryWrapper<EntViolationHistory>()
                .eq("social_credit_code", enterprise.getSocialCreditCode())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    /**
     * Obtain enterprise compliance defaults
     */
    private CategoryEnterpriseDefault getCategoryEnterpriseDefault(EnterpriseVO enterprise) {
        List<CategoryEnterpriseDefault> defaultList = categoryEnterpriseDefaultMapper
                .selectList(new QueryWrapper<CategoryEnterpriseDefault>()
                        .eq("industry_id", enterprise.getIndustryId())
                        .eq("canton_code", enterprise.getProvinceCode())
                        .eq("enterprise_scale", enterprise.getScale())
                        .eq("product_type", enterprise.getMainProductType())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (CollectionUtils.isEmpty(defaultList) || defaultList.size() != 1) {
            throw new BusinessException("Error searching for matching enterprise compliance defaults");
        }
        return defaultList.get(0);
    }

    /**
     * Obtain the maximum enterprise compliance
     */
    private double getMaxViolationQty(EnterpriseVO enterprise, CategoryEnterpriseDefault categoryEnterpriseDefault,
                                      List<EntViolationHistory> historyList) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(enterprise.getStartTime());
        int startYear = cal.get(Calendar.YEAR);
        int beginYear = (int) BaseEnum.RECORD_BEGIN_YEAR.getCode();
        cal.setTime(new Date());
        int thisYear = cal.get(Calendar.YEAR);
        boolean needBeCorrected = false;
        if (startYear > beginYear) {
            needBeCorrected = true;
        }
        double correctionFactor = (thisYear - beginYear + 1) / (thisYear - startYear + 1);
        double maxWaterViolationQty = categoryEnterpriseDefault.getWaterRelatedDefault();
        double maxGasViolationQty = categoryEnterpriseDefault.getGasRelatedDefault();
        double maxSoilViolationQty = categoryEnterpriseDefault.getSoilRelatedDefault();
        if (CollectionUtils.isNotEmpty(historyList)) {
            for (EntViolationHistory entViolationHistory : historyList) {
                Double waterRelatedViolationQty = entViolationHistory.getWaterRelatedViolationQty();
                if (ObjectUtil.isNotEmpty(waterRelatedViolationQty) && waterRelatedViolationQty > 0) {
                    if (needBeCorrected) {
                        waterRelatedViolationQty = waterRelatedViolationQty * correctionFactor;
                    }
                    if (waterRelatedViolationQty > maxWaterViolationQty) {
                        maxWaterViolationQty = waterRelatedViolationQty;
                    }
                }
                Double gasRelatedViolationQty = entViolationHistory.getGasRelatedViolationQty();
                if (ObjectUtil.isNotEmpty(gasRelatedViolationQty) && gasRelatedViolationQty > 0) {
                    if (needBeCorrected) {
                        gasRelatedViolationQty = gasRelatedViolationQty * correctionFactor;
                    }
                    if (gasRelatedViolationQty > maxGasViolationQty) {
                        maxGasViolationQty = gasRelatedViolationQty;
                    }
                }
                Double soilRelatedViolationQty = entViolationHistory.getSoilRelatedViolationQty();
                if (ObjectUtil.isNotEmpty(soilRelatedViolationQty) && soilRelatedViolationQty > 0) {
                    if (needBeCorrected) {
                        soilRelatedViolationQty = soilRelatedViolationQty * correctionFactor;
                    }
                    if (soilRelatedViolationQty > maxSoilViolationQty) {
                        maxSoilViolationQty = soilRelatedViolationQty;
                    }
                }
            }
        }
        return maxWaterViolationQty + maxGasViolationQty + maxSoilViolationQty;
    }

    private double acquireEnterpriseComplianceCapability(EnterpriseVO enterprise, double maxViolationQty) {
        RiskControlPollutionValue riskControlPollutionValue = riskControlPollutionValueMapper
                .selectOne(new QueryWrapper<RiskControlPollutionValue>()
                        .eq("area_code", enterprise.getCantonCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (!ObjectUtils.isEmpty(riskControlPollutionValue)
                && riskControlPollutionValue.getEnforcementIntensity() != null
                && riskControlPollutionValue.getEnforcementIntensity().doubleValue() != 0) {

            double result = maxViolationQty / riskControlPollutionValue.getEnforcementIntensity();
            return result;
        }
        return maxViolationQty;
    }

    private void saveEntRiskAssessResult(EnterpriseVO enterprise, List<String> targetWeightIds, Double result) {
        List<EntRiskAssessResult> entRiskAssessResults = new ArrayList<>();
        for (String targetWeightId : targetWeightIds) {
            createEntRiskAssessResult(enterprise, entRiskAssessResults, result, targetWeightId);
        }
        saveRiskAssessResults(entRiskAssessResults);
    }

    /**
     * obtain computing coefficient for R1.1
     */
    private double getK1() {
        ComputeConstant computeConstant = computeConstantMapper.selectOne(new QueryWrapper<ComputeConstant>()
                .eq("code", BaseEnum.CONSTANT_K1.getCode())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        return Double.parseDouble(computeConstant.getValue());
    }

}
