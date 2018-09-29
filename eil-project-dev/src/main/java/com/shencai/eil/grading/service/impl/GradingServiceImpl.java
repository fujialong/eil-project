package com.shencai.eil.grading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.power.common.util.ObjectUtil;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.CommonsUtil;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.gis.service.IGisValueService;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author zhoujx
 * @date 2018/9/19
 */
@Service
@Slf4j
public class GradingServiceImpl implements IGradingService {

    private final static int THREAD_POOL_SIZE = 3;
    private static final int MAX_THREAD_POOL_SIZE = 5;

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
    @Autowired
    private IComputeConstantService computeConstantService;
    @Autowired
    private IGisValueService gisValueService;
    @Autowired
    private ITargetWeightService targetWeightService;

    @Override
    public void execute(GradingParam param) throws ExecutionException, InterruptedException {
        EnterpriseVO enterprise = getEnterpriseInfo(param.getEnterpriseId());
        disposeEnterpriseRiskProfileData(enterprise);
        List<String> riskIndicatorSystemType = determineEnterpriseRiskIndicatorSystem(enterprise);
        //calculate risk factor R1
/*        calculateRiskFactor(enterprise, riskIndicatorSystemType);
        //calculate compliance capability
        calculateComplianceCapability(enterprise, riskIndicatorSystemType);
        //calculate the secondary control mechanism R3
        calculateSecondaryControlMechanism(enterprise, riskIndicatorSystemType);*/
        calculateRu(enterprise, riskIndicatorSystemType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GradingVO getGradingResult(GradingQueryParam queryParam) throws ExecutionException, InterruptedException {
        EnterpriseVO enterpriseInfo = getEnterpriseInfo(queryParam.getEnterpriseId());

        disposeEnterpriseRiskProfileData(enterpriseInfo);
        List<String> riskIndicatorSystemType = determineEnterpriseRiskIndicatorSystem(enterpriseInfo);

        calculateRu(enterpriseInfo, riskIndicatorSystemType);
        List<TargetResultVO> targetResultList = listAllTargetResult(queryParam, riskIndicatorSystemType);
        //Deal with parent-child relationships
        List<TargetResultVO> targetResultOfRu = listTargetResultOfRu(targetResultList);
        GradingVO returnData = getReturnData(enterpriseInfo, targetResultOfRu);
        return returnData;
    }

    /**
     * Ru >> R1*R1_w+R2*R2_w+R3*R3_w+R4*R4_w
     */
    public void calculateRu(EnterpriseVO enterpriseInfo, List<String> riskIndicatorSystemType) throws ExecutionException, InterruptedException {
        List<TargetResultVO> targetList = targetWeightService.lisTargetWeightTypeAndWeight(null);
        //use code+type as key,weight as value
        Map<String, Double> targetWeightMap = CommonsUtil.castListToMapAppendKey(targetList);
        List<CodeAndValueUseDouble> containsCodeValues = computeConstantService.listCodeValue();
        Map<String, Double> computeMap = CommonsUtil.castListToMap(containsCodeValues);

        for (String calculateType : riskIndicatorSystemType) {
         /*   double r1w = targetWeightMap.get(TargetEnum.RISK_FACTOR.getCode() + calculateType);
            double r2w = targetWeightMap.get(TargetEnum.PRIMARY_CONTROL_MECHANISM.getCode() + calculateType);
            double r3w = targetWeightMap.get(TargetEnum.SECONDARY_CONTROL_MECHANISM.getCode() + calculateType);
            double r4w = targetWeightMap.get(TargetEnum.RECEPTOR_SENSITIVITY.getCode() + calculateType);

            double r1Result = calculateRiskFactor(enterpriseInfo, riskIndicatorSystemType, calculateType, computeMap);
            double r2Result = calculateRtwo(enterpriseInfo, riskIndicatorSystemType, computeMap);
            double r3Result = calculateSecondaryControlMechanism(enterpriseInfo, calculateType, targetWeightMap, computeMap);
            double r4Result = calculateRFour(enterpriseInfo, calculateType, targetWeightMap, computeMap);
            double ruResult = r1Result * r1w + r2Result * r2w + r3Result * r3w + r4Result * r4w;*/
            double ruResult= ThreadTask(targetWeightMap, riskIndicatorSystemType,
                     enterpriseInfo, calculateType, computeMap);
            TargetWeight targetWeight = getTargetWeightByCodeAndType(calculateType, TargetEnum.R_U.getCode());
            EntRiskAssessResult entRiskAssessResult = getEntRiskAssessResults(enterpriseInfo, targetWeight.getId());
            EnterpriseInfo enterprise = enterpriseInfoMapper.selectOne(new QueryWrapper<EnterpriseInfo>()
                    .eq("id", enterpriseInfo.getId()));

            if (ObjectUtils.isEmpty(entRiskAssessResult)) {
                entRiskAssessResult = buildEntRiskAssessResult(enterpriseInfo.getId(), ruResult, targetWeight.getId());
                entRiskAssessResultMapper.insert(entRiskAssessResult);
                enterprise.setStatus(StatusEnum.W_SURVEY.getCode());
            }
        }
    }

    /**
     * thread run task
     */
    public double ThreadTask(Map<String, Double> targetWeightMap, List<String> riskIndicatorSystemType,
                           EnterpriseVO enterpriseInfo, String calculateType, Map<String, Double> computeMap)  {
        BlockingQueue<Runnable> workQuene = new ArrayBlockingQueue<>(10);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE,
                0, TimeUnit.MICROSECONDS, workQuene);


        double r1w = targetWeightMap.get(TargetEnum.RISK_FACTOR.getCode() + calculateType);
        double r2w = targetWeightMap.get(TargetEnum.PRIMARY_CONTROL_MECHANISM.getCode() + calculateType);
        double r3w = targetWeightMap.get(TargetEnum.SECONDARY_CONTROL_MECHANISM.getCode() + calculateType);
        double r4w = targetWeightMap.get(TargetEnum.RECEPTOR_SENSITIVITY.getCode() + calculateType);

        CompletionService completionService = new ExecutorCompletionService(executor);
        List<String> list = Arrays.asList("R1", "R2", "R3", "R4");
        for (String type : list) {
            completionService.submit(new Callable() {
                double result = 0;
                @Override
                public Object call() throws Exception {
                    switch (type) {
                        case "R1":
                            result = r1w * calculateRiskFactor(enterpriseInfo, riskIndicatorSystemType, calculateType, computeMap);
                            break;
                        case "R2":
                            result = r2w * calculateRtwo(enterpriseInfo, riskIndicatorSystemType, computeMap);
                            break;
                        case "R3":
                            result = r3w * calculateSecondaryControlMechanism(enterpriseInfo, calculateType, targetWeightMap, computeMap);
                            break;
                        case "R4":
                            result = r4w * calculateRFour(enterpriseInfo, calculateType, targetWeightMap, computeMap);
                            break;
                        default:
                            break;
                    }
                    return result;
                }

            });
        }
        executor.shutdown();
        final long start = System.nanoTime();
        double computerResult = 0.0d;
        for (String type : list) {

            try {
                Future<Double> f = completionService.take();
                computerResult += f.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return computerResult;
    }



    /**
     * R2 result
     *
     * @param enterpriseInfo
     * @param riskIndicatorSystemType
     * @return
     */
    private double calculateRtwo(EnterpriseVO enterpriseInfo,
                                 List<String> riskIndicatorSystemType, Map<String, Double> computeMap) {
        double r21Value = calculateComplianceCapability(enterpriseInfo, riskIndicatorSystemType, computeMap);
        double r22value = calculateRtwoTwo(enterpriseInfo, riskIndicatorSystemType, computeMap);

        return r21Value + r22value;
    }

    @Override
    public void gisTest(EnterpriseVO enterpriseVO) {
        //  log.info("----------R2.2---------value:" + calculateRtowPontTwoAboutGis "");
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

    /**
     * Deal with parent-child relationships
     */
    private List<TargetResultVO> listTargetResultOfRu(List<TargetResultVO> targetResultList) {
        List<TargetResultVO> targetResultOfRu = new ArrayList<>();
        for (TargetResultVO targetResultVO : targetResultList) {
            if (ObjectUtils.isEmpty(targetResultVO.getParentId())) {
                String targetIdOfRu = targetResultVO.getTargetId();
                List<TargetResultVO> childTargetResultList = new ArrayList<>();
                for (TargetResultVO child : targetResultList) {
                    if (targetIdOfRu.equals(child.getParentId())) {
                        String childId = child.getTargetId();
                        List<TargetResultVO> grandchildTargetResultList = new ArrayList<>();
                        for (TargetResultVO grandchild : targetResultList) {
                            if (childId.equals(grandchild.getParentId())) {
                                grandchildTargetResultList.add(grandchild);
                            }
                        }
                        child.setChildTargetResultList(grandchildTargetResultList);
                        childTargetResultList.add(child);
                    }
                }
                targetResultVO.setChildTargetResultList(childTargetResultList);
                targetResultOfRu.add(targetResultVO);
            }
        }
        return targetResultOfRu;
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
        if (ObjectUtils.isEmpty(enterprise)) {
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
    private double calculateRiskFactor(EnterpriseVO enterprise, List<String> riskIndicatorSystemType,
                                       String riskType, Map<String, Double> computeMap) {
        //calculate R1.1
        double onePointOneResult = firstStepOfRiskFactorCalculation(enterprise, riskIndicatorSystemType, riskType, computeMap);
        //calculate R1.2
        double OnePointTwoResult = secondStepOfRiskFactorCalculation(enterprise, riskIndicatorSystemType, computeMap);
        return onePointOneResult + OnePointTwoResult;
    }

    /**
     * calculate R1.1
     */
    private double firstStepOfRiskFactorCalculation(EnterpriseVO enterprise, List<String> riskType,
                                                    String type, Map<String, Double> computeMap) {
        double suddenResult = 0.0;
        double progressiveResult = 0.0;
        double r11max1 = computeMap.get(ComputeConstantEnum.R_ONE_ONE_MAX_ONE.getCode());
        double r11max2 = computeMap.get(ComputeConstantEnum.R_ONE_ONE_MAX_TWO.getCode());

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
                progressiveResult =
                        calculateProgressiveSecondaryIndicatorsOfRiskFactors(enterprise, entRiskParamValueList,
                                riskMaterialList);
                String progressiveTargetWeightId = getTargetWeightId(targetWeightList,
                        TargetWeightType.PROGRESSIVE_RISK.getCode());
                createEntRiskAssessResult(enterprise, entRiskAssessResults,
                        progressiveResult * 100 / r11max1, progressiveTargetWeightId);
            }
            //calculate sudden risk R1.1
            suddenResult =
                    calculateSuddenSecondaryIndicatorsOfRiskFactors(enterprise, entRiskParamValueList, riskMaterialList);
            String suddenTargetWeightId = getTargetWeightId(targetWeightList, TargetWeightType.SUDDEN_RISK.getCode());
            createEntRiskAssessResult(enterprise, entRiskAssessResults,
                    suddenResult * 100 / r11max2, suddenTargetWeightId);
            saveRiskAssessResults(entRiskAssessResults);
        }
        if (TargetWeightType.PROGRESSIVE_RISK.getCode().equals(type)) {
            return progressiveResult;
        }
        if (TargetWeightType.SUDDEN_RISK.getCode().equals(type)) {
            return suddenResult;
        }
        return 0;
    }

    /**
     * calculate R1.2
     */
    private double secondStepOfRiskFactorCalculation(EnterpriseVO enterprise,
                                                     List<String> riskType, Map<String, Double> computeMap) {
        double r12max = computeMap.get(ComputeConstantEnum.R_ONE_TWO_MAX.getCode());
        double result = 0.0;
        //obtain the R1.2 index weight of the enterprise
        List<TargetWeight> targetWeightList =
                obtainTargetWeight(riskType, TargetEnum.RISK_FACTORS_SECONDARY_STEP.getCode());
        List<String> targetWeightIds = listTargetWeightId(targetWeightList);
        //obtain the existing R1.2 calculation results of the enterprise
        List<EntRiskAssessResult> riskAssessResultList = listEntRiskAssessResults(enterprise, targetWeightIds);
        if (CollectionUtils.isEmpty(riskAssessResultList)) {
            //calculate R1.2
            result = calculateOnePointTwoSecondaryIndicatorsOfRiskFactors(enterprise);
            saveEntRiskAssessResult(enterprise, targetWeightIds, result * 100 / r12max);
        }
        return result;
    }

    /**
     * R2.1
     * calculate compliance capability
     */
    private double calculateComplianceCapability(EnterpriseVO enterprise, List<String> riskType, Map<String, Double> computeMap) {
        double result = 0.0;
        double r21max = computeMap.get(ComputeConstantEnum.R_TWO_ONE_MAX.getCode());
        //obtain the R2.1 index weight of the enterprise
        List<TargetWeight> targetWeightList =
                obtainTargetWeight(riskType, TargetEnum.ENTERPRISE_COMPLIANCE.getCode());
        List<String> targetWeightIds = listTargetWeightId(targetWeightList);
        //obtain the existing R2.1 calculation results of the enterprise
        List<EntRiskAssessResult> riskAssessResultList = listEntRiskAssessResults(enterprise, targetWeightIds);
        //if results is not found, calculate R2.1
        if (CollectionUtils.isEmpty(riskAssessResultList)) {
            result = calculateCompliance(enterprise);
            saveEntRiskAssessResult(enterprise, targetWeightIds, result * 100 / r21max);
        }
        return result;
    }

    /**
     * R2 = R2.1 result + R.2 result
     * Calculate  cluster effect
     */
    private double calculateRtwoTwo(EnterpriseVO enterprise, List<String> riskType, Map<String, Double> computeMap) {
        double result = 0;
        double r22max = computeMap.get(ComputeConstantEnum.R_TWO_TWO_MAX.getCode());

        List<TargetWeight> targetWeightList =
                obtainTargetWeight(riskType, TargetEnum.R_TWO_POINT_TWO.getCode());
        List<String> targetWeightIds = listTargetWeightId(targetWeightList);
        //obtain the existing R2.2 calculation results of the enterprise
        List<EntRiskAssessResult> riskAssessResultList = listEntRiskAssessResults(enterprise, targetWeightIds);
        if (CollectionUtils.isEmpty(riskAssessResultList)) {
            result = calculateRtowPontTwoAboutGis(enterprise);
            saveEntRiskAssessResult(enterprise, targetWeightIds, result * 100 / r22max);
        }
        return result;
    }

    /**
     * Compute gis related R2.1-R2.2
     *
     * @param enterprise
     * @return Standardized values
     */
    private double calculateRtowPontTwoAboutGis(EnterpriseVO enterprise) {

        List<CodeAndValueUseDouble> containsCodeValues = computeConstantService.listCodeValue();
        Map<String, Double> computeMap = CommonsUtil.castListToMap(containsCodeValues);

        //Query the required constants
        double sw1Value = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2Value = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());

        double ba2Value = computeMap.get(ComputeConstantEnum.B_A_TWO.getCode());
        double r22max = computeMap.get(ComputeConstantEnum.R_TWO_TWO_MAX.getCode());

        //Query gis data for this enterprise
        double r2201Value = gisValueService.getValueByEntIdAndCode(enterprise.getId(), GisValueEnum.R_TWO_TWO_ZERO_ONE.getCode());
        double r2202Value = gisValueService.getValueByEntIdAndCode(enterprise.getId(), GisValueEnum.R_TWO_TWO_ZERO_TWO.getCode());

        double result = (r2201Value * sw1Value + r2202Value * sw2Value) / ba2Value;
        return result / r22max;
    }


    /**
     * R3.3 = R3.3.1 result + R.3.3.2 result
     * Calculate  cluster effect
     */
    private double calculateThreePointThree(EnterpriseVO enterprise, String riskType, Map<String, Double> targetWeightMap) {
        List<CodeAndValueUseDouble> containsCodeValues = computeConstantService.listCodeValue();
        Map<String, Double> computeMap = CommonsUtil.castListToMap(containsCodeValues);

        List<CodeAndValueUseDouble> gisCodeValueList = gisValueService.getCodeValueByEntId(enterprise.getId());
        Map<String, Double> codeAndValueMap = CommonsUtil.castListToMap(gisCodeValueList);

        double r3301Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ZERO_ONE.getCode());
        double r3302Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ZERO_TWO.getCode());
        double r3303Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ZERO_THREE.getCode());
        double r3305Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ZERO_FIVE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ZERO_FIVE.getCode());
        double r33101Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ONE_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_ONE_ZERO_ONE.getCode());
        double r33201Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_ONE.getCode());
        double r33202Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_TWO.getCode());
        double r33203Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode());

        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double r331min = computeMap.get(ComputeConstantEnum.R_THREE_THREE_ONE_MIN.getCode());
        double r331max = computeMap.get(ComputeConstantEnum.R_THREE_THREE_ONE_MAX.getCode());
        double r332min = computeMap.get(ComputeConstantEnum.R_THREE_THREE_TWO_MIN.getCode());
        double r332max = computeMap.get(ComputeConstantEnum.R_THREE_THREE_TWO_MAX.getCode());
        double d1 = computeMap.get(ComputeConstantEnum.D_ONE.getCode());
        double d2 = computeMap.get(ComputeConstantEnum.D_TWO.getCode());
        double d3 = computeMap.get(ComputeConstantEnum.D_THREE.getCode());

        double r331w = targetWeightMap.get(TargetEnum.R_THREE_THREE_ONE + riskType);
        double r332w = targetWeightMap.get(TargetEnum.R_THREE_THREE_TWO + riskType);

        //dem=(R3_3_01-(R3_3_01*sw1+R3_3_02*sw2+R3_3_03sw3))>0?1*R3_3_05:-1*R3_3_05
        double dem = (r3301Value - (r3301Value * sw1 + r3302Value * sw2 + r3303Value * sw3)) > 0 ? 1 * r3305Value : -1 * r3305Value;

        //R3.3.1 = dem*R3_3_1_01
        double r331Value = dem * r33101Value;
        //R3.3.1 standard value = (R3.3.1-R3_3_1_min)*100/(R3_3_1_max-R3_3_1_min)
        double r331StandValue = (r331Value - r331min) * 100 / (r331max - r331min);
        //R3.3.2 =dem*(R3_3_2_01*d1+R3_3_2_02*d2+R3_3_2_03*d3)
        double r332Value = dem * (r33201Value * d1 + r33202Value * d2 + r33203Value * d3);
        //R3.3.2 standard value = (R3.3.2-R3_3_2_min)*100/(R3_3_2_max-R3_3_2_min)
        double r332StndValue = (r332Value - r332min) * 100 / (r332max - r332min);

        //R3.3 =  R3.3.1*R3_3_1_w+R3.3.2*R3_3_2_w
        double r33Value = r331Value * r331w + r332Value * r332w;

        TargetWeight targetWeight = getTargetWeightByCodeAndType(riskType, TargetEnum.R_THREE_THREE.getCode());

        //obtain the existing R3.3 calculation results of the enterprise
        EntRiskAssessResult riskAssessResultList = getEntRiskAssessResults(enterprise, targetWeight.getId());
        if (ObjectUtils.isEmpty(riskAssessResultList)) {
            EntRiskAssessResult riskAssessResult = buildEntRiskAssessResult(enterprise.getId(), r33Value, targetWeight.getId());
            entRiskAssessResultMapper.insert(riskAssessResult);
        }
        return r33Value;
    }

    /**
     * R4 R4.1*R4_1_w+R4.2*R4_2_w+R4.3*R4_3_w+R4.4*R4_4_w+R4.5*R4_5_w
     */
    private double calculateRFour(EnterpriseVO enterprise, String riskType,
                                  Map<String, Double> targetWeightMap, Map<String, Double> computeMap) {

        double r41w = targetWeightMap.get(TargetEnum.R_FOUR_ONE.getCode() + riskType);
        double r42w = targetWeightMap.get(TargetEnum.R_FOUR_TWO.getCode() + riskType);
        double r43w = targetWeightMap.get(TargetEnum.R_FOUR_THREE.getCode() + riskType);
        double r44w = targetWeightMap.get(TargetEnum.R_FOUR_FOUR.getCode() + riskType);
        double r45w = targetWeightMap.get(TargetEnum.R_FOUR_FIVE.getCode() + riskType);


        double r41 = calculateRFourOne(enterprise, riskType, targetWeightMap);

        double r42 = calculateRFourTwo(enterprise, riskType, targetWeightMap);

        double r43 = calculateRFourThree(enterprise, riskType, computeMap);

        double r44 = calculateRFourFour(enterprise, riskType, targetWeightMap, computeMap);

        double r45 = calculateRfourFive(enterprise, riskType, computeMap);


        return r41 * r41w + r42 * r42w + r43 * r43w + r44 * r44w + r45 * r45w;
    }

    /**
     * R4.5 >>R3_3_2_01*p1+R3_3_2_02*p2+R3_3_2_03*p3
     *
     * @param enterprise
     * @return
     */
    private double calculateRfourFive(EnterpriseVO enterprise, String riskType, Map<String, Double> computeMap) {


        List<CodeAndValueUseDouble> gisCodeValueList = gisValueService.getCodeValueByEntId(enterprise.getId());
        Map<String, Double> codeAndValueMap = CommonsUtil.castListToMap(gisCodeValueList);

        double r33201Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_ONE.getCode());
        double r33202Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_TWO.getCode());
        double r33203Value = codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_THREE_TWO_ZERO_THREE.getCode());

        double p1 = computeMap.get(ComputeConstantEnum.P_ONE.getCode());
        double p2 = computeMap.get(ComputeConstantEnum.P_TWO.getCode());
        double p3 = computeMap.get(ComputeConstantEnum.P_THREE.getCode());
        double r45max = computeMap.get(ComputeConstantEnum.R_FOUR_FIVE_MAX.getCode());

        double r45Result = r33201Value * p1 + r33202Value * p2 + r33203Value * p3;

        TargetWeight targetWeight = getTargetWeightByCodeAndType(riskType, TargetEnum.R_FOUR_FIVE.getCode());

        //obtain the existing R4.3 calculation results of the enterprise
        EntRiskAssessResult riskAssessResultList = getEntRiskAssessResults(enterprise, targetWeight.getId());

        if (ObjectUtils.isEmpty(riskAssessResultList)) {
            EntRiskAssessResult entRiskAssessResult = buildEntRiskAssessResult(enterprise.getId(), r45Result * 100 / r45max, targetWeight.getId());
            entRiskAssessResultMapper.insert(entRiskAssessResult);
        }

        return r45Result;
    }

    /**
     * R4.3 >> R4_3_01*sw1+R4_3_02*sw2+R4_3_03*sw3
     *
     * @param enterprise
     * @param riskType
     * @return
     */
    private double calculateRFourThree(EnterpriseVO enterprise, String riskType, Map<String, Double> computeMap) {


        List<CodeAndValueUseDouble> gisCodeValueList = gisValueService.getCodeValueByEntId(enterprise.getId());
        Map<String, Double> codeAndValueMap = CommonsUtil.castListToMap(gisCodeValueList);

        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double r43max = computeMap.get(ComputeConstantEnum.R_FOUR_THREE_MAX.getCode());

        double r4301Value = codeAndValueMap.get(GisValueEnum.R_FOUR_THREE_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_THREE_ZERO_ONE.getCode());
        double r4302Value = codeAndValueMap.get(GisValueEnum.R_FOUR_THREE_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_THREE_ZERO_TWO.getCode());
        double r4303Value = codeAndValueMap.get(GisValueEnum.R_FOUR_THREE_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_THREE_ZERO_THREE.getCode());

        double r43Value = r4301Value * sw1 + r4302Value * sw2 + r4303Value * sw3;
        TargetWeight targetWeight = getTargetWeightByCodeAndType(riskType, TargetEnum.R_FOUR_THREE.getCode());

        //obtain the existing R4.3 calculation results of the enterprise
        EntRiskAssessResult riskAssessResultList = getEntRiskAssessResults(enterprise, targetWeight.getId());

        if (ObjectUtils.isEmpty(riskAssessResultList)) {
            EntRiskAssessResult entRiskAssessResult = buildEntRiskAssessResult(enterprise.getId(), r43Value * 100 / r43max, targetWeight.getId());
            entRiskAssessResultMapper.insert(entRiskAssessResult);
        }

        return r43Value;
    }

    /**
     * R4.4 >> R4.4
     *
     * @param enterprise
     * @param riskType
     * @param targetWeightMap
     * @return
     */
    private double calculateRFourFour(EnterpriseVO enterprise, String riskType,
                                      Map<String, Double> targetWeightMap, Map<String, Double> computeMap) {
        List<CodeAndValueUseDouble> gisCodeValueList = gisValueService.getCodeValueByEntId(enterprise.getId());
        Map<String, Double> codeAndValueMap = CommonsUtil.castListToMap(gisCodeValueList);

        double r44Value = codeAndValueMap.get(GisValueEnum.R_FOUR_FOUR.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_FOUR.getCode());
        double r44max = computeMap.get(ComputeConstantEnum.R_FOUR_FOUR_MAX.getCode());

        TargetWeight targetWeight = getTargetWeightByCodeAndType(riskType, TargetEnum.R_FOUR_FOUR.getCode());
        //obtain the existing R4.4 calculation results of the enterprise
        EntRiskAssessResult riskAssessResultList = getEntRiskAssessResults(enterprise, targetWeight.getId());

        if (ObjectUtils.isEmpty(riskAssessResultList)) {
            EntRiskAssessResult entRiskAssessResult = buildEntRiskAssessResult(enterprise.getId(), r44Value * 100 / r44max, targetWeight.getId());
            entRiskAssessResultMapper.insert(entRiskAssessResult);
        }
        return r44Value;
    }

    /**
     * R4.2 >> R4.2.1*R4_2_1_w+R4.2.2*R4_2_2_w
     *
     * @param enterprise
     * @param riskType
     * @param targetWeightMap
     * @return
     */
    private double calculateRFourTwo(EnterpriseVO enterprise, String riskType, Map<String, Double> targetWeightMap) {
        List<CodeAndValueUseDouble> containsCodeValues = computeConstantService.listCodeValue();
        Map<String, Double> computeMap = CommonsUtil.castListToMap(containsCodeValues);

        List<CodeAndValueUseDouble> gisCodeValueList = gisValueService.getCodeValueByEntId(enterprise.getId());
        Map<String, Double> codeAndValueMap = CommonsUtil.castListToMap(gisCodeValueList);

        double r4201Value = codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ZERO_ONE.getCode());
        double r4202Value = codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ZERO_TWO.getCode());
        double r4203Value = codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ZERO_THREE.getCode());

        double r42101Value = codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ONE_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ONE_ZERO_ONE.getCode());
        double r42102Value = codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ONE_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ONE_ZERO_TWO.getCode());
        double r42103Value = codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ONE_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_ONE_ZERO_THREE.getCode());

        double r42201Value = codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_TWO_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_TWO_ZERO_ONE.getCode());
        double r42202Value = codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_TWO_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_TWO_ZERO_TWO.getCode());
        double r42203Value = codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_TWO_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_TWO_TWO_ZERO_THREE.getCode());


        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double ws = computeMap.get(ComputeConstantEnum.R_WS.getCode());
        double ba3 = computeMap.get(ComputeConstantEnum.B_A_THREE.getCode());

        //gdp >> R4_2_01*sw1+R4_2_02*sw2+R4_2_03*sw3
        double gdp = r4201Value * sw1 + r4202Value * sw2 + r4203Value * sw3;

        //R4.2.1 >> gdp*(R4_2_1_01*sw1+R4_2_1_02*sw2+R4_2_1_03*sw3)/ba3
        double r421Value = gdp * (r42101Value * sw1 + r42102Value * sw2 + r42103Value * sw3) / ba3;
        //R4.2.2 = gdp*(R4_2_2_01*sw1+R4_2_2_02*sw2+R4_2_2_03*sw3)/ws
        double r422Value = gdp * (r42201Value * sw1 + r42202Value * sw2 + r42203Value * sw3) / ws;

        //R4.2 = R4.2.1*R4_2_1_w+R4.2.2*R4_2_2_w
        double r421 = r421Value * targetWeightMap.get(TargetEnum.R_FOUR_TWO_ONE.getCode() + riskType);
        double r422 = r422Value * targetWeightMap.get(TargetEnum.R_FOUR_TWO_TWO.getCode() + riskType);
        double r42Value = r421 + r422;

        TargetWeight targetWeight = getTargetWeightByCodeAndType(riskType, TargetEnum.R_FOUR_TWO.getCode());
        //obtain the existing R4.2 calculation results of the enterprise
        EntRiskAssessResult riskAssessResultList = getEntRiskAssessResults(enterprise, targetWeight.getId());

        if (ObjectUtils.isEmpty(riskAssessResultList)) {
            EntRiskAssessResult entRiskAssessResult = buildEntRiskAssessResult(enterprise.getId(), r42Value, targetWeight.getId());
            entRiskAssessResultMapper.insert(entRiskAssessResult);
        }

        return r42Value;
    }

    /**
     * R4.1
     *
     * @param enterprise
     * @param riskType
     * @param targetWeightMap
     * @return
     */
    private double calculateRFourOne(EnterpriseVO enterprise, String riskType, Map<String, Double> targetWeightMap) {


        List<CodeAndValueUseDouble> containsCodeValues = computeConstantService.listCodeValue();
        Map<String, Double> computeMap = CommonsUtil.castListToMap(containsCodeValues);

        List<CodeAndValueUseDouble> gisCodeValueList = gisValueService.getCodeValueByEntId(enterprise.getId());
        Map<String, Double> codeAndValueMap = CommonsUtil.castListToMap(gisCodeValueList);

        double r4101Value = codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ZERO_ONE.getCode());
        double r4102Value = codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ZERO_TWO.getCode());
        double r4103Value = codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ZERO_THREE.getCode());

        double r41101Value = codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ONE_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ONE_ZERO_ONE.getCode());
        double r41102Value = codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ONE_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ONE_ZERO_TWO.getCode());
        double r41103Value = codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ONE_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_ONE_ZERO_THREE.getCode());

        double r41201Value = codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_TWO_ZERO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_TWO_ZERO_ONE.getCode());
        double r41202Value = codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_TWO_ZERO_TWO.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_TWO_ZERO_TWO.getCode());
        double r41203Value = codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_TWO_ZERO_THREE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_FOUR_ONE_TWO_ZERO_THREE.getCode());

        double sw1 = computeMap.get(ComputeConstantEnum.S_W_ONE.getCode());
        double sw2 = computeMap.get(ComputeConstantEnum.S_W_TWO.getCode());
        double sw3 = computeMap.get(ComputeConstantEnum.S_W_THREE.getCode());
        double he = computeMap.get(ComputeConstantEnum.R_HE.getCode());
        double ws = computeMap.get(ComputeConstantEnum.R_WS.getCode());

        //pop=R4_1_01*sw1+R4_1_02*sw2+R4_1_03sw3
        double pop = r4101Value * sw1 + r4102Value * sw2 + r4103Value * sw3;

        //R4.1.1 =  pop*(R4_1_1_01*sw1+R4_1_1_02*sw2+R4_1_1_03*sw3)/he
        double r411Value = pop * (r41101Value * sw1 + r41102Value * sw2 + r41103Value * sw3) / he;
        //R4.1.2 = pop*(R4_1_2_01*sw1+R4_1_2_02*sw2+R4_1_2_03sw3)/ws
        double r412Value = pop * (r41201Value * sw1 + r41202Value * sw2 + r41203Value * sw3) / ws;

        //R4.1 = R4.1.1*R4_1_1_w+R4.1.2*R4_1_2_w
        double r411 = r411Value * targetWeightMap.get(TargetEnum.R_FOUR_ONE_ONE.getCode() + riskType);
        double r412 = r412Value * targetWeightMap.get(TargetEnum.R_FOUR_ONE_TWO.getCode() + riskType);
        double r41Value = r411 + r412;

        TargetWeight targetWeight = getTargetWeightByCodeAndType(riskType, TargetEnum.R_FOUR_ONE.getCode());
        EntRiskAssessResult riskAssessResultList = getEntRiskAssessResults(enterprise, targetWeight.getId());

        //obtain the existing R4.1 calculation results of the enterprise
        if (ObjectUtils.isEmpty(riskAssessResultList)) {
            EntRiskAssessResult entRiskAssessResult = buildEntRiskAssessResult(enterprise.getId(), r41Value, targetWeight.getId());
            entRiskAssessResultMapper.insert(entRiskAssessResult);
        }

        return r41Value;
    }


    /**
     * @param enterprise
     * @return
     */
    private Map<String, Double> getTargetAssessResultMap(EnterpriseVO enterprise) {
        List<TargetResultVO> targetResultVOS = targetWeightService.listCodeTypeAndAssessResult(enterprise.getId());
        Map<String, Double> map = new HashMap<>(targetResultVOS.size());
        for (TargetResultVO targetResultVO : targetResultVOS) {
            map.put(targetResultVO.getTargetCode() + targetResultVO.getTargetType(), targetResultVO.getTargetResult());
        }
        return map;
    }

    /**
     * Query weight value
     */
    public double queryWeightValue(String code, String type) {
        TargetWeight weight = targetWeightMapper.selectOne(new QueryWrapper<TargetWeight>()
                .eq("code", code).isNotNull("weight")
                .eq("type", type).eq("valid", BaseEnum.VALID_YES.getCode()));

        return ObjectUtils.isEmpty(weight) ? 0 : Double.parseDouble(weight.getWeight());
    }

    /**
     * Query enterprise risk result
     *
     * @param entId    enterprise id
     * @param targetId target weight id
     * @return
     */
    public EntRiskAssessResult getRiskAssessResultByEntIdAndTargetId(String entId, String targetId) {
        return entRiskAssessResultMapper.selectOne(new QueryWrapper<EntRiskAssessResult>()
                .eq("ent_id", entId)
                .eq("target_weight_id", targetId)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }


    /**
     * calculate the secondary control mechanism R3.1-R3.2-R3.3
     */
    private double calculateSecondaryControlMechanism(EnterpriseVO enterprise, String type,
                                                      Map<String, Double> targetWeightMap, Map<String, Double> computeMap) {

        double r31w = targetWeightMap.get(TargetEnum.R_THREE_ONE.getCode() + type);
        double r32w = targetWeightMap.get(TargetEnum.R_THREE_TWO.getCode() + type);
        double r33w = targetWeightMap.get(TargetEnum.R_THREE_THREE.getCode() + type);

        //calculate R3.1
        double threePointOneResult = secondaryControlMechanismOfOne(enterprise, type, computeMap);
        //calculate R3.2
        double threePointTwoResult = secondaryControlMechanismOfTwo(enterprise, targetWeightMap, type, computeMap);
        //calculate R3.3
        double threePointThreeResult = calculateThreePointThree(enterprise, type, targetWeightMap);
        //R3 = R3.1*R3_1_w+R3.2*R3_2_w+R3.3*R3_3_w
        double r3result = threePointOneResult * r31w + threePointTwoResult * r32w + r33w * threePointThreeResult;
        return r3result;
    }


    /**
     * Secondary control mechanism R3.1
     */
    private double secondaryControlMechanismOfOne(EnterpriseVO enterpriseVO, String riskType, Map<String, Double> computeMap) {
        double result = 0.0;
        double r31max = computeMap.get(ComputeConstantEnum.R_THREE_ONE_MAX.getCode());
        TargetWeight targetWeight = getTargetWeightByCodeAndType(riskType, TargetEnum.R_THREE_ONE.getCode());
        //obtain the existing R3.1 calculation results of the enterprise
        EntRiskAssessResult entRiskAssessResultList = getEntRiskAssessResults(enterpriseVO, targetWeight.getId());
        //if results is not found, calculate R3.1
        if (ObjectUtils.isEmpty(entRiskAssessResultList)) {
            RiskControlPollutionValue riskControlPollutionValue = riskControlPollutionValueService.getOne(
                    new QueryWrapper<RiskControlPollutionValue>()
                            .eq("area_code", enterpriseVO.getProvinceCode())
                            .eq("valid", BaseEnum.VALID_YES.getCode()));
            result = ObjectUtils.isEmpty(riskControlPollutionValue) ? 0.0 : riskControlPollutionValue.getRiskControl();
            saveEntRiskAssessResult(enterpriseVO, targetWeight.getId(), result * 100 / r31max);
        }
        return result;
    }

    /**
     * Secondary control mechanism R3.2 (progressive)
     */
    private double secondaryControlMechanismOfTwo(EnterpriseVO enterpriseVO, Map<String, Double> targetWeightMap,
                                                  String riskType, Map<String, Double> computeMap) {
        double result = 0.0;
        List<CodeAndValueUseDouble> gisCodeValueList = gisValueService.getCodeValueByEntId(enterpriseVO.getId());
        Map<String, Double> codeAndValueMap = CommonsUtil.castListToMap(gisCodeValueList);

        double r321w = targetWeightMap.get(TargetEnum.R_THREE_TWO_ONE.getCode() + riskType);
        double r322w = targetWeightMap.get(TargetEnum.R_THREE_TWO_TWO.getCode() + riskType);

        double r321Value = codeAndValueMap.get(GisValueEnum.R_THREE_TWO_ONE.getCode()) == null ? 0
                : codeAndValueMap.get(GisValueEnum.R_THREE_TWO_ONE.getCode());
        double r322Value =0;

        if (TargetWeightType.PROGRESSIVE_RISK.getCode().equals(riskType)) {
            //obtain the R3.2 index weight of the enterprise
            TargetWeight targetWeight = getTargetWeightByCodeAndType(riskType, TargetEnum.R_THREE_TWO.getCode());
            //obtain the existing R3.2 calculation results of the enterprise
            EntRiskAssessResult entRiskAssessResultList = getEntRiskAssessResults(enterpriseVO, targetWeight.getId());
            //if results is not found, calculate R3.2
            if (ObjectUtils.isEmpty(entRiskAssessResultList)) {
                RiskControlPollutionValue riskControlPollutionValue = riskControlPollutionValueService.getOne(
                        new QueryWrapper<RiskControlPollutionValue>()
                                .eq("area_code", enterpriseVO.getProvinceCode())
                                .eq("valid", BaseEnum.VALID_YES.getCode()));
                r322Value = ObjectUtils.isEmpty(riskControlPollutionValue) ? 0.0 : riskControlPollutionValue.getLandPollutionIndex();

                result = r321Value * r321w + r322Value * r322w;
                saveEntRiskAssessResult(enterpriseVO, targetWeight.getId(), result);
            }
        }
        return result;
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
                .eq("type", TargetWeightType.PROGRESSIVE_RISK.getCode())
                .eq("code", targetCode)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        return targetWeight;
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
    private EntRiskAssessResult getEntRiskAssessResults(EnterpriseVO enterprise, String targetIds) {
        EntRiskAssessResult entRiskAssessResults = entRiskAssessResultMapper
                .selectOne(new QueryWrapper<EntRiskAssessResult>()
                        .eq("ent_id", enterprise.getId())
                        .in("target_weight_id", targetIds)
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        return entRiskAssessResults;
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

    private EntRiskAssessResult buildEntRiskAssessResult(String entId, double result, String targetWeightId) {
        EntRiskAssessResult riskAssessResult = new EntRiskAssessResult();
        riskAssessResult.setId(StringUtil.getUUID());
        riskAssessResult.setEntId(entId);
        riskAssessResult.setTargetWeightId(targetWeightId);
        riskAssessResult.setAssessValue(result);
        riskAssessResult.setCreateTime(DateUtil.getNowTimestamp());
        riskAssessResult.setUpdateTime(DateUtil.getNowTimestamp());
        riskAssessResult.setValid((Integer) BaseEnum.VALID_YES.getCode());

        return riskAssessResult;
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
        double k1 = getK1();
        Double yield = enterprise.getYield();
        double progressiveSecondaryIndicatorsOfRiskFactors = 0;
        if (mainProductStability == 1 && mainProductCarcinogenicity != 0) {
            progressiveSecondaryIndicatorsOfRiskFactors = mainProductQty / k1 * mainProductBioavailability
                    * mainProductBiologicalEnrichment / mainProductCarcinogenicity;
        }
        for (EntRiskParamValueVO riskParamValue : envRiskParamValueList) {
            if (riskParamValue.getStability() == 1
                    && riskParamValue.getBioavailability() != null && riskParamValue.getBiologicalEnrichment() != null
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
                    mainProductCriticalQuantity = Double.parseDouble(riskMaterialVO.getValue());
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
        List<EntRiskParamValueVO> entRiskParamValueList = listEnterpriseRiskParamValue(enterprise
                , TemplateCategory.EMISSIONS_INTENSITY.getCode());
        List<String> pollutionCategoryNameList = new ArrayList<>();
        for (EntRiskParamValueVO envRiskParamValueVO : entRiskParamValueList) {
            if (!pollutionCategoryNameList.contains(envRiskParamValueVO.getRemark())) {
                pollutionCategoryNameList.add(envRiskParamValueVO.getRemark());
            }
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
        Double r = 0.0;
        for (EntRiskParamValueVO entRiskParamValueVO : entRiskParamValueList) {
            Double value = emissionMap.get(entRiskParamValueVO.getRemark());
            if (value != null && value.doubleValue() != 0) {
                r += entRiskParamValueVO.getValue() * enterprise.getYield() / value;
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
        if (ObjectUtils.isEmpty(envStatistics)) {
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
                        .eq("area_code", enterprise.getProvinceCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (!ObjectUtils.isEmpty(riskControlPollutionValue)
                && riskControlPollutionValue.getRiskControl() != null
                && riskControlPollutionValue.getRiskControl().doubleValue() != 0) {
            double result = maxViolationQty * riskControlPollutionValue.getRiskControl();
            return result;
        }
        return maxViolationQty;
    }

    private void saveEntRiskAssessResult(EnterpriseVO enterprise, String targetWeightId, Double result) {
        List<EntRiskAssessResult> entRiskAssessResults = new ArrayList<>();
        createEntRiskAssessResult(enterprise, entRiskAssessResults, result, targetWeightId);
        saveRiskAssessResults(entRiskAssessResults);
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
