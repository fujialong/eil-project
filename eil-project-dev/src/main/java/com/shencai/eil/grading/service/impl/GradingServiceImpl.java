package com.shencai.eil.grading.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.power.common.util.ObjectUtil;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.CommonsUtil;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.gis.mapper.GisValueMapper;
import com.shencai.eil.gis.model.GisValueQueryParam;
import com.shencai.eil.gis.model.GisValueVO;
import com.shencai.eil.grading.entity.*;
import com.shencai.eil.grading.mapper.*;
import com.shencai.eil.grading.model.*;
import com.shencai.eil.grading.service.*;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.policy.model.EnterpriseQueryParam;
import com.shencai.eil.policy.model.EnterpriseVO;
import com.shencai.eil.risk.entity.RiskControlPollutionValue;
import com.shencai.eil.risk.mapper.RiskControlPollutionValueMapper;
import com.shencai.eil.risk.service.IRiskControlPollutionValueService;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhoujx
 * @date 2018/9/19
 */
@Service
@Slf4j
public class GradingServiceImpl implements IGradingService {

    public static final double DOUBLE_DEFAULT_VALUE = 0.0;
    private final static int THREAD_POOL_SIZE = 6;
    private static final int MAX_THREAD_POOL_SIZE = 10;
    public static final int SLEEP_TIME = 1000;
    public static final int HIGH_SCORE = 100;
    public static final int LOW_SCORE = 0;
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
    @Autowired
    private IComputeConstantService computeConstantService;
    @Autowired
    private GisValueMapper gisValueMapper;
    @Autowired
    private ITargetWeightGradeLineService targetWeightGradeLineService;
    @Autowired
    private IEntSurveyPlanService entSurveyPlanService;

    List<GradingCalculateParamHeadVO> gradingCalculateParamHeadVOList = new ArrayList<>();

    public GradingVO getGradingResult(GradingQueryParam queryParam) {
        EnterpriseVO enterpriseInfo = getEnterpriseInfo(queryParam.getEnterpriseId());
        List<String> riskTypeList;
        if (StatusEnum.VERIFIED.getCode().equals(enterpriseInfo.getStatus())) {
            disposeEnterpriseRiskProfileData(enterpriseInfo);
            riskTypeList = determineEnterpriseRiskIndicatorSystem(enterpriseInfo);
        } else {
            riskTypeList = listEntRiskType(queryParam);
        }
        List<TargetResultVO> targetResultList = listAllTargetResult(queryParam, riskTypeList);
        List<TargetResultVO> targetTree = generateTargetTree(targetResultList);
        List<TargetResultVO> targetResultVOList = new ArrayList<>();
        getAllTargetResultListFromTree(targetResultVOList, targetTree);
        GradingVO returnData = getReturnData(enterpriseInfo, targetTree);
        if (StatusEnum.VERIFIED.getCode().equals(enterpriseInfo.getStatus())) {
            updateEnterpriseStatus(enterpriseInfo, StatusEnum.FASTING.getCode());
            ThreadUtil.execute(() -> {
                try {
                    calculateTarget(enterpriseInfo, riskTypeList, targetResultVOList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return returnData;
    }

    private void getAllTargetResultListFromTree(List<TargetResultVO> resultList, List<TargetResultVO> list) {
        for (TargetResultVO target : list) {
            resultList.add(target);
            if (CollectionUtils.isNotEmpty(target.getChildTargetResultList())) {
                getAllTargetResultListFromTree(resultList, target.getChildTargetResultList());
            }
        }
    }

    private void updateEnterpriseStatus(EnterpriseVO enterpriseInfo, String status) {
        EnterpriseInfo enterprise = new EnterpriseInfo();
        enterprise.setStatus(status);
        enterprise.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseInfoMapper.update(enterprise, new QueryWrapper<EnterpriseInfo>()
                .eq("id", enterpriseInfo.getId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private List<String> listEntRiskType(GradingQueryParam queryParam) {
        List<EntMappingTargetType> entMappingTargetTypes = entMappingTargetTypeMapper.selectList(
                new QueryWrapper<EntMappingTargetType>()
                        .eq("ent_id", queryParam.getEnterpriseId())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        List<String> riskTypeList = new ArrayList<>();
        for (EntMappingTargetType entMappingTargetType : entMappingTargetTypes) {
            riskTypeList.add(entMappingTargetType.getTargetWeightType());
        }
        return riskTypeList;
    }

    private void calculateTarget(EnterpriseVO enterpriseInfo, List<String> riskTypeList, List<TargetResultVO> targetList) {
        //use code+type as key,weight as value
        Map<String, TargetResultVO> targetMap = castTargetListToMap(enterpriseInfo.getId(), targetList);
        List<CodeAndValueUseDouble> containsCodeValues = computeConstantService.listCodeValue(BaseConstants.FAST_GRADING);
        Map<String, Double> computeMap = CommonsUtil.castListToMap(containsCodeValues);
        BlockingQueue<Runnable> workQuene = new LinkedBlockingQueue<>();
        ThreadPoolExecutor executor1 = new ThreadPoolExecutor(THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE,
                0, TimeUnit.MICROSECONDS, workQuene);
        for (TargetResultVO target : targetList) {
            executor1.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (target.getParentId() == null) {
                                    try {
                                        calculateSingleTarget(target, enterpriseInfo, riskTypeList, targetMap, computeMap);
                                    } catch (InterruptedException e) {
                                        log.error("the thread was interrupted", e);
                                    } catch (BusinessException e2) {
                                        log.error("error in calculateTarget", e2);
                                    }
                                }
                            } catch (BusinessException e2) {
                                log.error("error in calculateTarget", e2);
                            }
                        }
                    }
            );
        }
        for (String targetType : TargetCodeConstants.LEAF_TARGET_CODE_ARRAY) {
            executor1.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                calculateLeafTarget(targetType, enterpriseInfo, riskTypeList, targetMap, computeMap);
                            } catch (InterruptedException e) {
                                log.error("the thread was interrupted", e);
                            } catch (BusinessException e2) {
                                log.error("error in calculateTarget", e2);
                            }
                        }
                    }
            );
        }
        executor1.shutdown();
        try {
            if (!executor1.awaitTermination(10 * 60, TimeUnit.SECONDS)) {
                // pool didn't terminate after the first try
                executor1.shutdownNow();
            }

            if (!executor1.awaitTermination(10 * 60, TimeUnit.SECONDS)) {
                // pool didn't terminate after the second try
            }
        } catch (InterruptedException ex) {
            executor1.shutdownNow();
            Thread.currentThread().interrupt();
        }

//        try {
//            executor1.awaitTermination(10*60,TimeUnit.SECONDS);
//            executor2.awaitTermination(10*60,TimeUnit.SECONDS);
//            executor1.shutdown();
//            executor2.shutdown();
//
//        } catch (InterruptedException e) {
//            log.error("the thread was interrupted",e);
//        }
        updateEnterpriseStatus(enterpriseInfo, StatusEnum.W_SURVEY.getCode());
        entSurveyPlanService.initSurveyPlan(enterpriseInfo.getId());
    }

    private void calculateSingleTarget(TargetResultVO target, EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        List<TargetResultVO> children = target.getChildTargetResultList();
        if (CollectionUtils.isNotEmpty(children)) {
            double value = 0.0;
            List<TargetResultVO> remainingTargetList = new ArrayList<>(children);
            while (remainingTargetList.size() > 0) {
                for (Iterator<TargetResultVO> it = remainingTargetList.iterator(); it.hasNext(); ) {
                    TargetResultVO child = it.next();
                    TargetResultVO childTarget = targetMap.get(getTargetMapKey(enterprise.getId(), child.getTargetCode(), child.getTargetType()));
                    if (childTarget.getTargetResult() != null) {
                        double weight = childTarget.getWeight() != null ? childTarget.getWeight() : 0;
                        value += childTarget.getTargetResult() * weight;
                        it.remove();
                    } else {
                        calculateSingleTarget(child, enterprise, riskTypeList, targetMap, computeMap);
                    }
                }
                if (remainingTargetList.size() > 0) {
                    Thread.sleep(SLEEP_TIME);
                }
            }
            saveParentTarget(value, target, enterprise, targetMap);
        }
    }

    private void calculateLeafTarget(String targetType, EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        switch (targetType) {
            case TargetCodeConstants.R_ONE_POINT_ONE:
                calculateROnePointOne(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_ONE_POINT_TWO:
                calculateROnePointTwo(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_TWO_POINT_ONE:
                calculateRTwoPointOne(enterprise, riskTypeList, targetMap);
                break;
            case TargetCodeConstants.R_TWO_POINT_TWO:
                calculateRTwoPointTwo(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_THREE_POINT_ONE:
                calculateRThreePointOne(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_THREE_POINT_TWO_POINT_ONE:
                calculateRThreePointTwoPointOne(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_THREE_POINT_TWO_POINT_TWO:
                calculateRThreePointTwoPointTwo(enterprise, riskTypeList, targetMap);
                break;
            case TargetCodeConstants.R_THREE_POINT_THREE_POINT_ONE:
                calculateRThreePointThreePointOneAndTwoAndRFourPointFive(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_THREE_POINT_THREE_POINT_TWO:
//                calculateRThreePointThreePointTwo(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_FOUR_POINT_ONE_POINT_ONE:
                calculateRFourPointOnePointOneAndTwo(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_FOUR_POINT_ONE_POINT_TWO:
//                calculateRFourPointOnePointTwo(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_FOUR_POINT_TWO_POINT_ONE:
                calculateRFourPointTwoPointOne(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_FOUR_POINT_TWO_POINT_TWO:
                calculateRFourPointTwoPointTwo(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_FOUR_POINT_THREE:
                calculateRFourPointThree(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_FOUR_POINT_FOUR:
                calculateRFourPointFour(enterprise, riskTypeList, targetMap, computeMap);
                break;
            case TargetCodeConstants.R_FOUR_POINT_FIVE:
//                calculateRFourPointFive(enterprise, riskTypeList, targetMap, computeMap);
                break;
            default:
                throw new BusinessException("error target code");
        }
    }

    /**
     * calculate R1.1
     */
    private void calculateROnePointOne(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) {
        List<EntRiskParamValueVO> entRiskParamValueList = listEnterpriseRiskParamValue(enterprise
                , TemplateCategory.RAW_MATERIAL.getCode());
        List<RiskMaterialVO> riskMaterialList = listRiskMaterial(enterprise, entRiskParamValueList);
        setRiskMaterialValueToEntRiskParamValueList(entRiskParamValueList, riskMaterialList);
        double[] valueArray = new double[riskTypeList.size()];
        int index = 0;
        for (String riskType : riskTypeList) {
            double result;
            if (TargetWeightType.PROGRESSIVE_RISK.getCode().equals(riskType)) {
                result = calculateROnePointOneForProgressiveRisk(enterprise, entRiskParamValueList,
                        riskMaterialList, computeMap);
                valueArray[index] = result;
            } else {
                result = calculateROnePointOneForSuddenRisk(enterprise, entRiskParamValueList, riskMaterialList, computeMap);
                valueArray[index] = result;
            }
            index++;
        }
        saveSpecialLeafTarget(valueArray, TargetCodeConstants.R_ONE_POINT_ONE, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate progressive risk R1.1
     */
    private double calculateROnePointOneForProgressiveRisk(EnterpriseVO enterprise,
                                                           List<EntRiskParamValueVO> entRiskParamValueList,
                                                           List<RiskMaterialVO> riskMaterialList, Map<String, Double> computeMap) {
        GradingCalculateParamHeadVO gradingCalculateParamHeadVO = new GradingCalculateParamHeadVO();
        Map<String, Double> childMap = new HashMap<>();

        double k1 = computeMap.get(ComputeConstantEnum.K_ONE.getCode());
        Double yield = enterprise.getYield();
        double progressiveSecondaryIndicatorsOfRiskFactors = 0;
        //If the raw materials are less than five, the main product should be added to the calculation
        if (entRiskParamValueList.size() < 5) {
            String mainProductName = enterprise.getMainProductName();
            Double mainProductQty = enterprise.getMainProductQty() * yield;
            double mainProductBioavailability = 0;
            double mainProductBiologicalEnrichment = 0;
            double mainProductCarcinogenicity = 0;
            double mainProductStability = 0;
            for (RiskMaterialVO riskMaterialVO : riskMaterialList) {
                if (mainProductName.equals(riskMaterialVO.getName())) {
                    String templateParamCode = riskMaterialVO.getTemplateParamCode();
                    if (TemplateEnum.STABILITY.getCode().equals(templateParamCode)) {
                        Double stability = Double.valueOf(riskMaterialVO.getValue());
                        if (stability == 0) {
                            break;
                        }
                        mainProductStability = Double.valueOf(riskMaterialVO.getValue());
                    }
                    if (TemplateEnum.BIOAVAILABILITY.getCode().equals(templateParamCode)) {
                        mainProductBioavailability = Double.valueOf(riskMaterialVO.getValue());
                    }
                    if (TemplateEnum.BIOLOGICAL_ENRICHMENT.getCode().equals(templateParamCode)) {
                        mainProductBiologicalEnrichment = Double.valueOf(riskMaterialVO.getValue());
                    }
                    if (TemplateEnum.CARCINOGENICITY.getCode().equals(templateParamCode)) {
                        mainProductCarcinogenicity = Double.valueOf(riskMaterialVO.getValue());
                    }
                }
            }
            if (mainProductStability == 1 && mainProductCarcinogenicity != 0) {
                progressiveSecondaryIndicatorsOfRiskFactors = mainProductQty / k1 * mainProductBioavailability
                        * mainProductBiologicalEnrichment / mainProductCarcinogenicity;
            }

           /* childMap.putIfAbsent("mainProductQty", mainProductQty);
            childMap.putIfAbsent("mainProductStability", mainProductStability);
            childMap.putIfAbsent("mainProductCarcinogenicity", mainProductCarcinogenicity);
            childMap.putIfAbsent("mainProductBiologicalEnrichment", mainProductBiologicalEnrichment);
            childMap.putIfAbsent("mainProductBioavailability", mainProductBioavailability);*/
        }
        for (EntRiskParamValueVO riskParamValue : entRiskParamValueList) {
            if (riskParamValue.getStability() == 1
                    && riskParamValue.getBioavailability() != null && riskParamValue.getBiologicalEnrichment() != null
                    && riskParamValue.getCarcinogenicity() != null && riskParamValue.getCarcinogenicity() != 0) {
                progressiveSecondaryIndicatorsOfRiskFactors +=
                        (riskParamValue.getValue() * yield) / k1 * riskParamValue.getBioavailability()
                                * riskParamValue.getBiologicalEnrichment() / riskParamValue.getCarcinogenicity();
            }
        }

        gradingCalculateParamHeadVO.setRiskType(TargetWeightType.PROGRESSIVE_RISK.getCode());
        gradingCalculateParamHeadVO.setTargetType(TargetCodeConstants.R_ONE_POINT_ONE);
        childMap.putIfAbsent(ComputeConstantEnum.K_ONE.getCode(), k1);
        childMap.putIfAbsent("yield", yield);
       // gradingCalculateParamHeadVO.setChildParams(childMap);
        gradingCalculateParamHeadVOList.add(gradingCalculateParamHeadVO);
        return progressiveSecondaryIndicatorsOfRiskFactors;
    }

    /**
     * calculate sudden risk R1.1
     */
    private double calculateROnePointOneForSuddenRisk(EnterpriseVO enterprise,
                                                      List<EntRiskParamValueVO> entRiskParamValueList,
                                                      List<RiskMaterialVO> riskMaterialList, Map<String, Double> computeMap) {

        GradingCalculateParamHeadVO gradingCalculateParamHeadVO = new GradingCalculateParamHeadVO();
        Map<String, Double> childMap = new HashMap<>();
        double k1 = computeMap.get(ComputeConstantEnum.K_ONE.getCode());
        Double yield = enterprise.getYield();
        double suddenSecondaryIndicatorsOfRiskFactors = 0;
        double mainProductCriticalQuantity = 0;
        Double mainProductQty = null;
        //If the raw materials are less than five, the main product is added to the calculation
        if (entRiskParamValueList.size() < 5) {
            String mainProductName = enterprise.getMainProductName();
            mainProductQty = enterprise.getMainProductQty() * yield;
            for (RiskMaterialVO riskMaterialVO : riskMaterialList) {
                if (mainProductName.equals(riskMaterialVO.getName())) {
                    if (TemplateEnum.CRITICAL_QUANTITY.getCode().equals(riskMaterialVO.getTemplateParamCode())) {
                        mainProductCriticalQuantity = Double.parseDouble(riskMaterialVO.getValue());
                        break;
                    }
                }
            }
            if (mainProductCriticalQuantity > 0) {
                suddenSecondaryIndicatorsOfRiskFactors = mainProductQty / k1 / mainProductCriticalQuantity;
            }
        }

        for (EntRiskParamValueVO riskParamValue : entRiskParamValueList) {
            if (riskParamValue.getCriticalQuantity() != null && riskParamValue.getCriticalQuantity() != 0) {
                suddenSecondaryIndicatorsOfRiskFactors +=
                        (riskParamValue.getValue() * yield) / k1 / riskParamValue.getCriticalQuantity();
                saveOnePointOneCalculateParamOfRiskParamValue(riskParamValue, enterprise.getId());
            }
        }
        saveOnePointOneSuddenParams(k1, mainProductCriticalQuantity, mainProductQty, enterprise.getId());
        return suddenSecondaryIndicatorsOfRiskFactors;
    }

    private void saveOnePointOneSuddenParams(double k1, double mainProductCriticalQuantity, Double mainProductQty, String entId) {
        GradingCalculateParamHeadVO gradingCalculateParamHeadVO = new GradingCalculateParamHeadVO();
        gradingCalculateParamHeadVO.setEntId(entId);
        gradingCalculateParamHeadVO.setTargetType(TargetWeightType.SUDDEN_RISK.getCode());
        gradingCalculateParamHeadVO.setTargetType(TargetCodeConstants.R_ONE_POINT_ONE);
        //TODO insert
    }

    /**
     * The variables needed to hold the value of the risk parameter
     *
     * @param riskParamValue
     */
    private void saveOnePointOneCalculateParamOfRiskParamValue(EntRiskParamValueVO riskParamValue, String entId) {
        CalculateParamOfRiskParamValue calculateParamOfRiskParamValue = new CalculateParamOfRiskParamValue();
        calculateParamOfRiskParamValue.setId(StringUtil.getUUID());
        calculateParamOfRiskParamValue.setRiskType(TargetWeightType.SUDDEN_RISK.getCode());
        calculateParamOfRiskParamValue.setValue(riskParamValue.getValue());
        calculateParamOfRiskParamValue.setEntId(entId);
        calculateParamOfRiskParamValue.setCriticalQuantity(riskParamValue.getCriticalQuantity());
        calculateParamOfRiskParamValue.setTargetType(TargetEnum.R_FOUR_ONE_ONE.getCode());
        //TODO insert
    }

    /**
     * calculate R1.2
     */
    private void calculateROnePointTwo(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) {
        double result = calculateROnePointTwoValue(enterprise);
        saveCommonLeafTarget(result, TargetCodeConstants.R_ONE_POINT_TWO, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate progressive/sudden R1.2
     */
    private double calculateROnePointTwoValue(EnterpriseVO enterprise) {
        List<EntRiskParamValueVO> entRiskParamValueList = listEnterpriseRiskParamValue(enterprise
                , TemplateCategory.EMISSIONS_INTENSITY.getCode());
        List<String> pollutionCategoryNameList = new ArrayList<>();
        for (EntRiskParamValueVO envRiskParamValueVO : entRiskParamValueList) {
            pollutionCategoryNameList.add(envRiskParamValueVO.getRemark());
        }
        Map<String, Double> emissionMap = new HashMap<>();
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
        double r = 0.0;
        for (EntRiskParamValueVO entRiskParamValueVO : entRiskParamValueList) {
            Double value = emissionMap.get(entRiskParamValueVO.getRemark());
            if (value != null && value.doubleValue() != 0) {
                r += entRiskParamValueVO.getValue() * enterprise.getYield() / value;
            }
        }
        return r;
    }

    /**
     * R2.1
     * calculate compliance capability
     */
    private void calculateRTwoPointOne(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap) {
        double result = calculateRTwoPointOneValue(enterprise);
        saveCommonLeafTarget(result, TargetCodeConstants.R_TWO_POINT_ONE, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate progressive/sudden R2.1
     */
    private double calculateRTwoPointOneValue(EnterpriseVO enterprise) {
        //Obtain enterprise compliance defaults
        CategoryEnterpriseDefault categoryEnterpriseDefault = getCategoryEnterpriseDefault(enterprise);
        //Obtain enterprise history compliance records
        List<EntViolationHistory> historyList = listEntViolationHistories(enterprise);
        //Obtain the maximum enterprise compliance
        double maxViolationQty = getMaxViolationQty(enterprise, categoryEnterpriseDefault, historyList);
        double result = acquireEnterpriseComplianceCapability(enterprise, maxViolationQty);
        return result;
    }

    /**
     * calculate R2.2
     */
    private void calculateRTwoPointTwo(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        //Query the required constants
        double sw1Value = computeMap.getOrDefault(ComputeConstantEnum.S_W_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double sw2Value = computeMap.getOrDefault(ComputeConstantEnum.S_W_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double ba2Value = computeMap.getOrDefault(ComputeConstantEnum.B_A_TWO.getCode(), 25 * Math.PI);
        if (computeMap.get(GisValueEnum.R_TWO_TWO_ZERO_ONE.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_ONE.getCode(), computeMap);
        }
        double r2201Value = computeMap.getOrDefault(GisValueEnum.R_TWO_TWO_ZERO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double r2202Value = computeMap.getOrDefault(GisValueEnum.R_TWO_TWO_ZERO_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double result = (r2201Value * sw1Value + r2202Value * sw2Value) / ba2Value;
        GradingCalculateParamHeadVO gradingCalculateParamHeadVO = new GradingCalculateParamHeadVO();
        Map<String, Double> childMap = new HashMap<>();
        gradingCalculateParamHeadVO.setTargetType(TargetWeightType.PROGRESSIVE_RISK.getCode());
        childMap.putIfAbsent("sw1Value", sw1Value);
        childMap.putIfAbsent("sw2Value", sw2Value);
        childMap.putIfAbsent("ba2Value", ba2Value);
        childMap.putIfAbsent("r2201Value", r2201Value);
        childMap.putIfAbsent("r2202Value", r2202Value);
       // gradingCalculateParamHeadVO.setChildParams(childMap);
        gradingCalculateParamHeadVO.setEntId(enterprise.getId());
        gradingCalculateParamHeadVOList.add(gradingCalculateParamHeadVO);
        saveCommonLeafTarget(result, TargetCodeConstants.R_TWO_POINT_TWO, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate R3.1
     */
    private void calculateRThreePointOne(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) {
        RiskControlPollutionValue riskControlPollutionValue = riskControlPollutionValueService.getOne(
                new QueryWrapper<RiskControlPollutionValue>()
                        .eq("area_code", enterprise.getProvinceCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (riskControlPollutionValue.getRiskControl() == null) {
            throw new BusinessException("the area risk control capacity is null");
        }
        double result = riskControlPollutionValue.getRiskControl();
        GradingCalculateParamHeadVO gradingCalculateParamHeadVO = new GradingCalculateParamHeadVO();
        Map<String, Double> childMap = new HashMap<>();

        gradingCalculateParamHeadVO.setTargetType(TargetWeightType.PROGRESSIVE_RISK.getCode());
        childMap.putIfAbsent("RiskControl", result);
        //gradingCalculateParamHeadVO.setChildParams(childMap);
        gradingCalculateParamHeadVO.setEntId(enterprise.getId());
        gradingCalculateParamHeadVOList.add(gradingCalculateParamHeadVO);

        saveCommonLeafTarget(result, TargetCodeConstants.R_THREE_POINT_ONE, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate R3.2.1
     */
    private void calculateRThreePointTwoPointOne(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (riskTypeList.size() == 2) {
            if (computeMap.get(GisValueEnum.R_THREE_TWO_ONE.getCode()) == null) {
                putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_TWO.getCode(), computeMap);
            }
            double value = computeMap.getOrDefault(GisValueEnum.R_THREE_TWO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
            double[] valueArray = new double[riskTypeList.size()];
            for (int i = 0; i < riskTypeList.size() - 1; i++) {
                valueArray[i] = value;
            }
            GradingCalculateParamHeadVO gradingCalculateParamHeadVO = new GradingCalculateParamHeadVO();
            Map<String, Double> childMap = new HashMap<>();

            gradingCalculateParamHeadVO.setTargetType(TargetWeightType.PROGRESSIVE_RISK.getCode());
            childMap.putIfAbsent(GisValueEnum.R_THREE_TWO_ONE.getCode(), value);
          //  gradingCalculateParamHeadVO.setChildParams(childMap);
            gradingCalculateParamHeadVO.setTargetType(TargetCodeConstants.R_THREE_POINT_TWO_POINT_ONE);
            gradingCalculateParamHeadVO.setEntId(enterprise.getId());
            gradingCalculateParamHeadVOList.add(gradingCalculateParamHeadVO);
            saveSpecialLeafTarget(valueArray, TargetCodeConstants.R_THREE_POINT_TWO_POINT_ONE, enterprise, riskTypeList, targetMap);
        }

    }

    /**
     * calculate R3.2.2
     */
    private void calculateRThreePointTwoPointTwo(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap) throws InterruptedException {
        if (riskTypeList.size() == 2) {
            RiskControlPollutionValue riskControlPollutionValue = riskControlPollutionValueService.getOne(
                    new QueryWrapper<RiskControlPollutionValue>()
                            .eq("area_code", enterprise.getProvinceCode())
                            .eq("valid", BaseEnum.VALID_YES.getCode()));
            if (riskControlPollutionValue.getLandPollutionIndex() == null) {
                throw new BusinessException("the area soil environmental background is null");
            }
            double value = riskControlPollutionValue.getLandPollutionIndex();
            double[] valueArray = new double[riskTypeList.size()];
            valueArray[0] = value;
            for (int i = 0; i < riskTypeList.size() - 1; i++) {
                valueArray[i] = value;
            }
            GradingCalculateParamHeadVO gradingCalculateParamHeadVO = new GradingCalculateParamHeadVO();
            Map<String, Double> childMap = new HashMap<>();

            gradingCalculateParamHeadVO.setTargetType(TargetWeightType.PROGRESSIVE_RISK.getCode());
            childMap.putIfAbsent("landPollutionIndex", value);
           // gradingCalculateParamHeadVO.setChildParams(childMap);
            gradingCalculateParamHeadVO.setTargetType(TargetCodeConstants.R_THREE_POINT_TWO_POINT_TWO);
            gradingCalculateParamHeadVO.setEntId(enterprise.getId());
            gradingCalculateParamHeadVOList.add(gradingCalculateParamHeadVO);
            saveSpecialLeafTarget(valueArray, TargetCodeConstants.R_THREE_POINT_TWO_POINT_TWO, enterprise, riskTypeList, targetMap);
        }
    }

    private void calculateRThreePointThreePointOneAndTwoAndRFourPointFive(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        calculateRThreePointThreePointOne(enterprise, riskTypeList, targetMap, computeMap);
        calculateRThreePointThreePointTwo(enterprise, riskTypeList, targetMap, computeMap);
        calculateRFourPointFive(enterprise, riskTypeList, targetMap, computeMap);
    }


    /**
     * calculate R3.3.1
     */
    private void calculateRThreePointThreePointOne(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (computeMap.get(GisValueEnum.DEM.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_THREE.getCode(), computeMap);
        }
        double dem = computeMap.get(GisValueEnum.DEM.getCode());
        double r33101 = computeMap.getOrDefault(GisValueEnum.R_THREE_THREE_ONE_ZERO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double value = dem * r33101;
        GradingCalculateParamHeadVO gradingCalculateParamHeadVO = new GradingCalculateParamHeadVO();
        Map<String, Double> childMap = new HashMap<>();
        for (String riskType : riskTypeList) {
            gradingCalculateParamHeadVO.setTargetType(riskType);
            childMap.putIfAbsent(GisValueEnum.R_THREE_THREE_ONE_ZERO_ONE.getCode(), r33101);
            childMap.putIfAbsent(GisValueEnum.DEM.getCode(), dem);
          //  gradingCalculateParamHeadVO.setChildParams(childMap);
            gradingCalculateParamHeadVO.setTargetType(TargetCodeConstants.R_THREE_POINT_THREE_POINT_ONE);
            gradingCalculateParamHeadVO.setEntId(enterprise.getId());
            gradingCalculateParamHeadVOList.add(gradingCalculateParamHeadVO);
        }

        saveCommonLeafTarget(value, TargetCodeConstants.R_THREE_POINT_THREE_POINT_ONE, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate R3.3.2
     */
    private void calculateRThreePointThreePointTwo(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (computeMap.get(GisValueEnum.DEM.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_THREE.getCode(), computeMap);
        }
        double dem = computeMap.get(GisValueEnum.DEM.getCode());
        double r33201 = computeMap.getOrDefault(GisValueEnum.R_THREE_THREE_TWO_ZERO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double r33202 = computeMap.getOrDefault(GisValueEnum.R_THREE_THREE_TWO_ZERO_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double r33203 = computeMap.getOrDefault(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode(), DOUBLE_DEFAULT_VALUE);
        double d1 = computeMap.getOrDefault(ComputeConstantEnum.D_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double d2 = computeMap.getOrDefault(ComputeConstantEnum.D_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double d3 = computeMap.getOrDefault(ComputeConstantEnum.D_THREE.getCode(), DOUBLE_DEFAULT_VALUE);
        double value = dem * (r33201 * d1 + r33202 * d2 + r33203 * d3);

        GradingCalculateParamHeadVO gradingCalculateParamHeadVO = new GradingCalculateParamHeadVO();
        Map<String, Double> childMap = new HashMap<>();
        for (String riskType : riskTypeList) {
            gradingCalculateParamHeadVO.setTargetType(riskType);
            childMap.putIfAbsent(GisValueEnum.R_THREE_THREE_TWO_ZERO_ONE.getCode(), r33201);
            childMap.putIfAbsent(GisValueEnum.R_THREE_THREE_TWO_ZERO_TWO.getCode(), r33202);
            childMap.putIfAbsent(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode(), r33203);
            childMap.putIfAbsent(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode(), r33203);
            childMap.putIfAbsent(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode(), r33203);
            childMap.putIfAbsent(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode(), r33203);
            childMap.putIfAbsent(GisValueEnum.DEM.getCode(), value);
          //  gradingCalculateParamHeadVO.setChildParams(childMap);
            gradingCalculateParamHeadVO.setTargetType(TargetCodeConstants.R_THREE_POINT_THREE_POINT_ONE);
            gradingCalculateParamHeadVO.setEntId(enterprise.getId());
            gradingCalculateParamHeadVOList.add(gradingCalculateParamHeadVO);
        }
        saveCommonLeafTarget(value, TargetCodeConstants.R_THREE_POINT_THREE_POINT_TWO, enterprise, riskTypeList, targetMap);
    }

    private void calculateRFourPointOnePointOneAndTwo(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        calculateRFourPointOnePointOne(enterprise, riskTypeList, targetMap, computeMap);
        calculateRFourPointOnePointTwo(enterprise, riskTypeList, targetMap, computeMap);
    }

    /**
     * calculate R4.1.1
     */
    private void calculateRFourPointOnePointOne(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (computeMap.get(GisValueEnum.POP.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_FOUR.getCode(), computeMap);
        }
        double pop = computeMap.get(GisValueEnum.POP.getCode());
        double r41101 = computeMap.getOrDefault(GisValueEnum.R_FOUR_ONE_ONE_ZERO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double r41102 = computeMap.getOrDefault(GisValueEnum.R_FOUR_ONE_ONE_ZERO_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double r41103 = computeMap.getOrDefault(GisValueEnum.R_FOUR_ONE_ONE_ZERO_THREE.getCode(), DOUBLE_DEFAULT_VALUE);
        double he = computeMap.getOrDefault(ComputeConstantEnum.R_HE.getCode(), 1.0);
        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double value = pop * (r41101 * sw1 + r41102 * sw2 + r41103 * sw3) / he;
        saveCommonLeafTarget(value, TargetCodeConstants.R_FOUR_POINT_ONE_POINT_ONE, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate R4.1.2
     */
    private void calculateRFourPointOnePointTwo(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (computeMap.get(GisValueEnum.POP.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_FOUR.getCode(), computeMap);
        }
        double pop = computeMap.get(GisValueEnum.POP.getCode());
        double r41201 = computeMap.getOrDefault(GisValueEnum.R_FOUR_ONE_TWO_ZERO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double r41202 = computeMap.getOrDefault(GisValueEnum.R_FOUR_ONE_TWO_ZERO_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double r41203 = computeMap.getOrDefault(GisValueEnum.R_FOUR_ONE_TWO_ZERO_THREE.getCode(), DOUBLE_DEFAULT_VALUE);
        double ws = computeMap.getOrDefault(ComputeConstantEnum.R_WS.getCode(), 1.0);
        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double value = pop * (r41201 * sw1 + r41202 * sw2 + r41203 * sw3) / ws;
        saveCommonLeafTarget(value, TargetCodeConstants.R_FOUR_POINT_ONE_POINT_TWO, enterprise, riskTypeList, targetMap);
    }


    /**
     * calculate R4.2.1
     */
    private void calculateRFourPointTwoPointOne(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (computeMap.get(GisValueEnum.GDP.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_FIVE.getCode(), computeMap);
        }
        double gdp = computeMap.get(GisValueEnum.GDP.getCode());
        double r42101 = computeMap.getOrDefault(GisValueEnum.R_FOUR_TWO_ONE_ZERO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double r42102 = computeMap.getOrDefault(GisValueEnum.R_FOUR_TWO_ONE_ZERO_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double r42103 = computeMap.getOrDefault(GisValueEnum.R_FOUR_TWO_ONE_ZERO_THREE.getCode(), DOUBLE_DEFAULT_VALUE);
        double ba3 = computeMap.getOrDefault(ComputeConstantEnum.B_A_THREE.getCode(), 1.0);
        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double value = gdp * (r42101 * sw1 + r42102 * sw2 + r42103 * sw3) / ba3;
        saveCommonLeafTarget(value, TargetCodeConstants.R_FOUR_POINT_TWO_POINT_ONE, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate R4.2.2
     */
    private void calculateRFourPointTwoPointTwo(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (computeMap.get(GisValueEnum.GDP.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_FIVE.getCode(), computeMap);
        }
        double gdp = computeMap.get(GisValueEnum.GDP.getCode());
        double r42201 = computeMap.getOrDefault(GisValueEnum.R_FOUR_TWO_TWO_ZERO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double r42202 = computeMap.getOrDefault(GisValueEnum.R_FOUR_TWO_TWO_ZERO_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double r42203 = computeMap.getOrDefault(GisValueEnum.R_FOUR_TWO_TWO_ZERO_THREE.getCode(), DOUBLE_DEFAULT_VALUE);
        double ws = computeMap.getOrDefault(ComputeConstantEnum.R_WS.getCode(), 1.0);
        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double value = gdp * (r42201 * sw1 + r42202 * sw2 + r42203 * sw3) / ws;
        saveCommonLeafTarget(value, TargetCodeConstants.R_FOUR_POINT_TWO_POINT_TWO, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate R4.3
     */
    private void calculateRFourPointThree(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (computeMap.get(GisValueEnum.R_FOUR_THREE_ZERO_ONE.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_SIX.getCode(), computeMap);
        }
        double r4301 = computeMap.getOrDefault(GisValueEnum.R_FOUR_THREE_ZERO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double r4302 = computeMap.getOrDefault(GisValueEnum.R_FOUR_THREE_ZERO_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double r4303 = computeMap.getOrDefault(GisValueEnum.R_FOUR_THREE_ZERO_THREE.getCode(), DOUBLE_DEFAULT_VALUE);
        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double value = r4301 * sw1 + r4302 * sw2 + r4303 * sw3;
        saveCommonLeafTarget(value, TargetCodeConstants.R_FOUR_POINT_THREE, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate R4.4
     */
    private void calculateRFourPointFour(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (computeMap.get(GisValueEnum.R_FOUR_FOUR.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_SIX.getCode(), computeMap);
        }
        double r44 = computeMap.getOrDefault(GisValueEnum.R_FOUR_FOUR.getCode(), DOUBLE_DEFAULT_VALUE);
        double value = r44 == 0 ? 0 : (1 / r44);
        saveCommonLeafTarget(value, TargetCodeConstants.R_FOUR_POINT_FOUR, enterprise, riskTypeList, targetMap);
    }

    /**
     * calculate R4.5
     */
    private void calculateRFourPointFive(EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap, Map<String, Double> computeMap) throws InterruptedException {
        if (computeMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_ONE.getCode()) == null) {
            putGisValueMapByEntAndClassToComputeMap(enterprise.getId(), GisValueEnum.CLASS_THREE.getCode(), computeMap);
        }

        double r33201 = computeMap.getOrDefault(GisValueEnum.R_THREE_THREE_TWO_ZERO_ONE.getCode(), DOUBLE_DEFAULT_VALUE);
        double r33202 = computeMap.getOrDefault(GisValueEnum.R_THREE_THREE_TWO_ZERO_TWO.getCode(), DOUBLE_DEFAULT_VALUE);
        double r33203 = computeMap.getOrDefault(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode(), DOUBLE_DEFAULT_VALUE);
        double p1 = computeMap.get(ComputeConstantEnum.P_ONE.getCode());
        double p2 = computeMap.get(ComputeConstantEnum.P_TWO.getCode());
        double p3 = computeMap.get(ComputeConstantEnum.P_THREE.getCode());
        double value = r33201 * p1 + r33202 * p2 + r33203 * p3;
        saveCommonLeafTarget(value, TargetCodeConstants.R_FOUR_POINT_FIVE, enterprise, riskTypeList, targetMap);
    }

    private void putGisValueMapByEntAndClassToComputeMap(String entId, String classCode, Map<String, Double> computeMap) throws InterruptedException {
        GisValueQueryParam param = new GisValueQueryParam();
        param.setEntId(entId);
        param.setClassCode(classCode);
        getGisValueUntilHaveValue(param, computeMap);
        if (GisValueEnum.CLASS_THREE.getCode().equals(classCode)) {
            calculateDem(computeMap);
        }
        if (GisValueEnum.CLASS_FOUR.getCode().equals(classCode)) {
            calculatePop(computeMap);
        }
        if (GisValueEnum.CLASS_FIVE.getCode().equals(classCode)) {
            calculateGdp(computeMap);
        }
    }

    private void calculateDem(Map<String, Double> computeMap) {
        Double r3301 = computeMap.get(GisValueEnum.R_THREE_THREE_ZERO_ONE.getCode());
        if (r3301 == null) {
            System.out.printf("&&&&&&");
        }
        double r3302 = computeMap.get(GisValueEnum.R_THREE_THREE_ZERO_TWO.getCode());
        double r3303 = computeMap.get(GisValueEnum.R_THREE_THREE_ZERO_THREE.getCode());
        double r3304 = computeMap.get(GisValueEnum.R_THREE_THREE_ZERO_FOUR.getCode());
        double r3305 = computeMap.get(GisValueEnum.R_THREE_THREE_ZERO_FIVE.getCode());
        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double dem = (r3301 - (r3302 * sw1 + r3303 * sw2 + r3304 * sw3)) * r3305;
        computeMap.put(GisValueEnum.DEM.getCode(), dem);
    }

    private void calculatePop(Map<String, Double> computeMap) {
        double r4101 = computeMap.get(GisValueEnum.R_FOUR_ONE_ZERO_ONE.getCode());
        ;
        double r4102 = computeMap.get(GisValueEnum.R_FOUR_ONE_ZERO_TWO.getCode());
        double r4103 = computeMap.get(GisValueEnum.R_FOUR_ONE_ZERO_THREE.getCode());
        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double pop = r4101 * sw1 + r4102 * sw2 + r4103 * sw3;
        computeMap.put(GisValueEnum.POP.getCode(), pop);
    }

    private void calculateGdp(Map<String, Double> computeMap) {
        double r4201 = computeMap.get(GisValueEnum.R_FOUR_TWO_ZERO_ONE.getCode());
        double r4202 = computeMap.get(GisValueEnum.R_FOUR_TWO_ZERO_TWO.getCode());
        double r4203 = computeMap.get(GisValueEnum.R_FOUR_TWO_ZERO_THREE.getCode());
        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double gdp = r4201 * sw1 + r4202 * sw2 + r4203 * sw3;
        computeMap.put(GisValueEnum.GDP.getCode(), gdp);
    }


    private void getGisValueUntilHaveValue(GisValueQueryParam queryParam, Map<String, Double> computeMap) throws InterruptedException {
        List<GisValueVO> gisValueList = gisValueMapper.getGisValueByEntIdAndClassCode(queryParam);
        while (CollectionUtils.isEmpty(gisValueList)) {
            Thread.sleep(SLEEP_TIME);
            gisValueList = gisValueMapper.getGisValueByEntIdAndClassCode(queryParam);
        }
        Map<String, Double> codeAndValueMap = CommonsUtil.castGisValueListToMap(gisValueList);
        computeMap.putAll(codeAndValueMap);
    }

    private void saveCommonLeafTarget(double targetValue, String targetCode, EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap) {
        List<TargetResultVO> targetList = new ArrayList<>();
        for (String riskType : riskTypeList) {
            TargetResultVO target = targetMap.get(getTargetMapKey(enterprise.getId(), targetCode, riskType));
            double normalizationResultResult = normalize(target, targetValue);
            target.setTargetResult(normalizationResultResult);
            targetList.add(target);
        }
        saveEntRiskAssessResultListAndPutIntoMap(enterprise, targetList, targetMap);
    }

    private void saveSpecialLeafTarget(double[] valueList, String targetCode, EnterpriseVO enterprise, List<String> riskTypeList, Map<String, TargetResultVO> targetMap) {
        List<TargetResultVO> targetList = new ArrayList<>();
        int index = 0;
        for (String riskType : riskTypeList) {
            TargetResultVO target = targetMap.get(getTargetMapKey(enterprise.getId(), targetCode, riskType));
            if (target != null) {
                double normalizationResultResult = normalize(target, valueList[index]);
                target.setTargetResult(normalizationResultResult);
                targetList.add(target);
                index++;
            }
        }
        saveEntRiskAssessResultListAndPutIntoMap(enterprise, targetList, targetMap);
    }

    private void saveParentTarget(double targetValue, TargetResultVO target, EnterpriseVO enterprise, Map<String, TargetResultVO> targetMap) {
        List<TargetResultVO> targetList = new ArrayList<>();
        target.setTargetResult(targetValue);
        targetList.add(target);
        saveEntRiskAssessResultListAndPutIntoMap(enterprise, targetList, targetMap);
    }

    private void saveEntRiskAssessResultListAndPutIntoMap(EnterpriseVO enterprise, List<TargetResultVO> targetList, Map<String, TargetResultVO> targetMap) {
        List<EntRiskAssessResult> insertList = new ArrayList<>();
        for (TargetResultVO target : targetList) {
            EntRiskAssessResult
                    entRiskAssessResult = buildEntRiskAssessResult(enterprise.getId(), target.getTargetResult(), target.getTargetId());
            insertList.add(entRiskAssessResult);
            targetMap.put(getTargetMapKey(enterprise.getId(), target.getTargetCode(), target.getTargetType()), target);
        }
        entRiskAssessResultService.saveBatch(insertList);
    }

    private void saveEntRiskAssessResultAndPutIntoMap(EnterpriseVO enterprise, TargetResultVO target, Map<String, TargetResultVO> targetMap) {
        EntRiskAssessResult
                entRiskAssessResult = buildEntRiskAssessResult(enterprise.getId(), target.getTargetResult(), target.getTargetId());
        targetMap.put(getTargetMapKey(enterprise.getId(), target.getTargetCode(), target.getTargetType()), target);
        entRiskAssessResultService.save(entRiskAssessResult);
    }

    private double normalize(TargetResultVO target, double value) {
        String normalizationCode = target.getNormalizationCode();
        double result;
        switch (normalizationCode) {
            case BaseConstants.NORMALIZATION_MAX:
                result = value * 100 / target.getMax();
                break;
            case BaseConstants.NORMALIZATION_RANGE:
                result = (value - target.getMin()) * 100 / (target.getMax() - target.getMin());
                break;
            default:
                throw new BusinessException("this target do not have normalization code");
        }
        return result;
    }

    private EntRiskAssessResult buildEntRiskAssessResult(String entId, double result, String targetWeightId) {
        String resultLevelId = getGradeLineLevel(result, targetWeightId);
        EntRiskAssessResult riskAssessResult = new EntRiskAssessResult();
        riskAssessResult.setId(StringUtil.getUUID());
        riskAssessResult.setEntId(entId);
        riskAssessResult.setTargetWeightId(targetWeightId);
        riskAssessResult.setGradeLineId(resultLevelId);
        riskAssessResult.setAssessValue(result);
        riskAssessResult.setCreateTime(DateUtil.getNowTimestamp());
        riskAssessResult.setUpdateTime(DateUtil.getNowTimestamp());
        riskAssessResult.setValid((Integer) BaseEnum.VALID_YES.getCode());
        return riskAssessResult;
    }

    private Map<String, TargetResultVO> castTargetListToMap(String entId, List<TargetResultVO> targetList) {
        Map<String, TargetResultVO> result = new Hashtable<>();
        if (CollectionUtils.isNotEmpty(targetList)) {
            for (TargetResultVO target : targetList) {
                result.put(getTargetMapKey(entId, target.getTargetCode(), target.getTargetType()), target);
            }
        }
        return result;
    }

    private String getTargetMapKey(String entId, String targetCode, String targetType) {
        return entId + "_" + targetCode + "_" + targetType;
    }

    private GradingVO getReturnData(EnterpriseVO enterpriseInfo, List<TargetResultVO> targetResultOfRu) {
        GradingVO returnData = new GradingVO();
        returnData.setEnterpriseName(enterpriseInfo.getName());
        returnData.setEnterpriseStatus(enterpriseInfo.getStatus());
        for (TargetResultVO ru : targetResultOfRu) {
            if (TargetWeightType.SUDDEN_RISK.getCode().equals(ru.getTargetType())) {
                returnData.setSuddenTargetResultOfRu(ru);
            } else {
                returnData.setProgressiveTargetResultOfRu(ru);
            }
        }
        return returnData;
    }

    private List<TargetResultVO> generateTargetTree(List<TargetResultVO> targetResultList) {
        List<TargetResultVO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(targetResultList)) {
            for (int i = targetResultList.size() - 1; i >= 0; i--) {
                TargetResultVO target = targetResultList.get(i);
                if (StringUtil.isBlank(target.getParentId())) {
                    List<TargetResultVO> children = getChildren(target, targetResultList);
                    if (CollectionUtils.isNotEmpty(children)) {
                        target.setChildTargetResultList(children);
                    }
                    result.add(target);
                }
            }
        }
        return result;
    }

    private List<TargetResultVO> getChildren(TargetResultVO parent, List<TargetResultVO> targetResultList) {
        List<TargetResultVO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(targetResultList)) {
            for (TargetResultVO target : targetResultList) {
                if (parent.getTargetId().equals(target.getParentId())) {
                    List<TargetResultVO> children = getChildren(target, targetResultList);
                    if (CollectionUtils.isNotEmpty(children)) {
                        target.setChildTargetResultList(children);
                    }
                    result.add(target);
                }
            }
        }
        return result;
    }

    private List<TargetResultVO> listAllTargetResult(GradingQueryParam queryParam, List<String> targetTypes) {
        EntRiskAssessResultQueryParam param = new EntRiskAssessResultQueryParam();
        param.setEnterpriseId(queryParam.getEnterpriseId());
        param.setTargetTypeList(targetTypes);
        List<TargetResultVO> targetResults = targetWeightMapper.listAllTarget(param);
        return targetResults;
    }

    private EnterpriseVO getEnterpriseInfo(String enterpriseId) {
        EnterpriseQueryParam queryParam = new EnterpriseQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        EnterpriseVO enterprise = enterpriseInfoMapper.getEnterprise(queryParam);
        if (ObjectUtil.isEmpty(enterprise)) {
            throw new BusinessException("The enterprise has been removed!");
        }
        return enterprise;
    }

    private void disposeEnterpriseRiskProfileData(EnterpriseVO enterprise) {
        EnvStatistics envStatistics = getEnvironmentStatistics(enterprise);
        setMainProductQty(enterprise, envStatistics);
        insertEnterpriseMappingEnvironmentStatistics(enterprise, envStatistics);
        List<EnvStatisticsParamValueVO> envStatisticsParamValueList =
                listEnvStatisticsParamValue(envStatistics, TemplateCategory.ENV_BASE_TEMPLE.getCode());
        insertEnterpriseRiskParamValues(enterprise, envStatisticsParamValueList);
    }

    private List<String> determineEnterpriseRiskIndicatorSystem(EnterpriseVO enterprise) {
        List<String> riskType = new ArrayList<>();
        List<EntMappingTargetType> targetTypeList =
                entMappingTargetTypeMapper.selectList(new QueryWrapper<EntMappingTargetType>()
                        .eq("ent_id", enterprise.getId())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (CollectionUtils.isNotEmpty(targetTypeList)) {
            for (EntMappingTargetType entMappingTargetType : targetTypeList) {
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


    private void setMainProductQty(EnterpriseVO enterprise, EnvStatistics envStatistics) {
        List<EnvStatisticsParamValueVO> envStatisticsParamValueOfMainProduct =
                listEnvStatisticsParamValue(envStatistics, TemplateCategory.PRODUCTION.getCode());
        String mainProductOutput = envStatisticsParamValueOfMainProduct.get(0).getValue();
        enterprise.setMainProductQty(Double.valueOf(mainProductOutput));
    }

    private List<EntRiskParamValueVO> listEnterpriseRiskParamValue(EnterpriseVO enterprise, String templateCategoryCode) {
        EntRiskParamValueQueryParam queryParam = new EntRiskParamValueQueryParam();
        queryParam.setEnterpriseId(enterprise.getId());
        queryParam.setTemplateType(templateCategoryCode);
        return entRiskParamValueMapper.listEntRiskParamValue(queryParam);
    }

    private List<RiskMaterialVO> listRiskMaterial(EnterpriseVO enterprise,
                                                  List<EntRiskParamValueVO> entRiskParamValueList) {
        List<String> riskMaterialNameList = new ArrayList<>();
        riskMaterialNameList.add(enterprise.getMainProductName());
        for (EntRiskParamValueVO entRiskParamValue : entRiskParamValueList) {
            riskMaterialNameList.add(entRiskParamValue.getName());
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
                    String templateParamCode = riskMaterialVO.getTemplateParamCode();
                    if (TemplateEnum.CRITICAL_QUANTITY.getCode().equals(templateParamCode)) {
                        riskParamValueVO.setCriticalQuantity(Double.parseDouble(riskMaterialVO.getValue()));
                    }
                    if (TemplateEnum.BIOAVAILABILITY.getCode().equals(templateParamCode)) {
                        riskParamValueVO.setBioavailability(Double.parseDouble(riskMaterialVO.getValue()));
                    }
                    if (TemplateEnum.BIOLOGICAL_ENRICHMENT.getCode().equals(templateParamCode)) {
                        riskParamValueVO.setBiologicalEnrichment(Double.parseDouble(riskMaterialVO.getValue()));
                    }
                    if (TemplateEnum.CARCINOGENICITY.getCode().equals(templateParamCode)) {
                        riskParamValueVO.setCarcinogenicity(Double.parseDouble(riskMaterialVO.getValue()));
                    }
                    if (TemplateEnum.STABILITY.getCode().equals(templateParamCode)) {
                        riskParamValueVO.setStability(Double.parseDouble(riskMaterialVO.getValue()));
                    }
                }
            }
        }
    }

    /**
     * get targetWeight by type code
     */
    private TargetWeight getTargetWeightByCodeAndType(String riskType, String targetCode) {
        TargetWeight targetWeight = targetWeightMapper.selectOne(new QueryWrapper<TargetWeight>()
                .eq("type", riskType)
                .eq("code", targetCode)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        return targetWeight;
    }

    /**
     * obtain the existing calculation results
     */
    private EntRiskAssessResult getEntRiskAssessResult(EnterpriseVO enterprise, String targetId) {
        EntRiskAssessResult entRiskAssessResult = entRiskAssessResultMapper
                .selectOne(new QueryWrapper<EntRiskAssessResult>()
                        .eq("ent_id", enterprise.getId())
                        .eq("target_weight_id", targetId)
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        return entRiskAssessResult;
    }

    /**
     * save the index calculation results
     */
    private void saveRiskAssessResults(List<EntRiskAssessResult> entRiskAssessResults) {
        if (!CollectionUtils.isEmpty(entRiskAssessResults)) {
            entRiskAssessResultService.saveBatch(entRiskAssessResults);
        }
    }


    private void insertEnterpriseMappingEnvironmentStatistics(EnterpriseVO enterprise, EnvStatistics envStatistics) {
        Integer count = entMappingEnvStatisticsMapper.selectCount(new QueryWrapper<EntMappingEnvStatistics>()
                .eq("ent_id", enterprise.getId())
                .eq("env_statistics_id", envStatistics.getId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (count == 0) {
            EntMappingEnvStatistics entMappingEnvStatistics = new EntMappingEnvStatistics();
            entMappingEnvStatistics.setId(StringUtil.getUUID());
            entMappingEnvStatistics.setEntId(enterprise.getId());
            entMappingEnvStatistics.setEnvStatisticsId(envStatistics.getId());
            entMappingEnvStatistics.setCreateTime(DateUtil.getNowTimestamp());
            entMappingEnvStatistics.setUpdateTime(DateUtil.getNowTimestamp());
            entMappingEnvStatistics.setValid((Integer) BaseEnum.VALID_YES.getCode());
            entMappingEnvStatisticsMapper.insert(entMappingEnvStatistics);
        }
    }

    private void insertEnterpriseRiskParamValues(EnterpriseVO enterprise,
                                                 List<EnvStatisticsParamValueVO> envStatisticsParamValueList) {
        Integer count = entRiskParamValueMapper.selectCount(new QueryWrapper<EntRiskParamValue>()
                .eq("enterprise_id", enterprise.getId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (count == 0) {
            List<EntRiskParamValue> envRiskParamValueList = new ArrayList<>();
            for (EnvStatisticsParamValueVO envStatisticsParamValue : envStatisticsParamValueList) {
                EntRiskParamValue entRiskParamValue = new EntRiskParamValue();
                entRiskParamValue.setId(StringUtil.getUUID());
                entRiskParamValue.setParamId(envStatisticsParamValue.getParamId());
                entRiskParamValue.setEnterpriseId(enterprise.getId());
                entRiskParamValue.setValue(envStatisticsParamValue.getValue());
                entRiskParamValue.setCreateTime(DateUtil.getNowTimestamp());
                entRiskParamValue.setUpdateTime(DateUtil.getNowTimestamp());
                entRiskParamValue.setValid((Integer) BaseEnum.VALID_YES.getCode());
                envRiskParamValueList.add(entRiskParamValue);
            }
            if (!CollectionUtils.isEmpty(envRiskParamValueList)) {
                entRiskParamValueService.saveBatch(envRiskParamValueList);
            }
        }
    }

    private List<EnvStatisticsParamValueVO> listEnvStatisticsParamValue(EnvStatistics envStatistics,
                                                                        String templateType) {
        EnvStatisticsParamValueQueryParam queryParam = new EnvStatisticsParamValueQueryParam();
        queryParam.setEnvStatisticsId(envStatistics.getId());
        queryParam.setTemplateType(templateType);
        List<EnvStatisticsParamValueVO> envStatisticsParamValueVOS =
                envStatisticsParamValueMapper.listEnvStatisticsParamValue(queryParam);
        if (CollectionUtils.isEmpty(envStatisticsParamValueVOS)) {
            throw new BusinessException("The matching environment statistics parameter value data was not queried!");
        }
        return envStatisticsParamValueVOS;
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
        if (ObjectUtil.isEmpty(envStatistics)) {
            throw new BusinessException("The enterprise has no matching environmental statistics!");
        }
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
        double correctionFactor = (thisYear - beginYear + 1) * 1.0 / (thisYear - startYear + 1);
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
                        .eq("area_code", enterprise.getProvinceCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (!ObjectUtil.isEmpty(riskControlPollutionValue)
                && riskControlPollutionValue.getRiskControl() != null
                && riskControlPollutionValue.getRiskControl().doubleValue() != 0) {
            double result = maxViolationQty * riskControlPollutionValue.getRiskControl();
            return result;
        }
        return maxViolationQty;
    }


    private String getGradeLineLevel(double score, String weightId) {
        TargetWeightGradeLine targetWeightGradeLine;
        if (score >= HIGH_SCORE) {
            targetWeightGradeLine = targetWeightGradeLineService.getOne(new QueryWrapper<TargetWeightGradeLine>()
                    .eq("target_weight_id", weightId)
                    .eq("result_code", GradeLineResultCode.HIGH.getCode())
                    .eq("valid", BaseEnum.VALID_YES.getCode()));
            if (ObjectUtil.isEmpty(targetWeightGradeLine)) {
                targetWeightGradeLine = targetWeightGradeLineService.getOne(new QueryWrapper<TargetWeightGradeLine>()
                        .eq("type", GradeLineResultCode.TYPE_DEFAULT.getCode())
                        .eq("result_code", GradeLineResultCode.HIGH.getCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
            }
            return targetWeightGradeLine.getId();
        }
        if (score <= LOW_SCORE) {
            targetWeightGradeLine = targetWeightGradeLineService.getOne(new QueryWrapper<TargetWeightGradeLine>()
                    .eq("target_weight_id", weightId)
                    .eq("result_code", GradeLineResultCode.LOW.getCode())
                    .eq("valid", BaseEnum.VALID_YES.getCode()));
            if (ObjectUtil.isEmpty(targetWeightGradeLine)) {
                targetWeightGradeLine = targetWeightGradeLineService.getOne(new QueryWrapper<TargetWeightGradeLine>()
                        .eq("type", GradeLineResultCode.TYPE_DEFAULT.getCode())
                        .eq("result_code", GradeLineResultCode.LOW.getCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
            }
            return targetWeightGradeLine.getId();
        }
        targetWeightGradeLine = targetWeightGradeLineService.getOne(new QueryWrapper<TargetWeightGradeLine>()
                .eq("target_weight_id", weightId)
                .lt("percent_start", score)
                .ge("percent_end", score)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (ObjectUtil.isEmpty(targetWeightGradeLine)) {
            targetWeightGradeLine = targetWeightGradeLineService.getOne(new QueryWrapper<TargetWeightGradeLine>()
                    .eq("type", GradeLineResultCode.TYPE_DEFAULT.getCode())
                    .lt("percent_start", score)
                    .ge("percent_end", score)
                    .eq("valid", BaseEnum.VALID_YES.getCode()));
        }
        return targetWeightGradeLine.getId();
    }
}
