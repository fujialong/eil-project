package com.shencai.eil.scenario.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.ScenarioCode;
import com.shencai.eil.common.constants.ScenarioEnum;
import com.shencai.eil.common.constants.ScenarioParamEnum;
import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.scenario.entity.*;
import com.shencai.eil.scenario.mapper.AccidentScenarioMapper;
import com.shencai.eil.scenario.mapper.AccidentScenarioResultMapper;
import com.shencai.eil.scenario.mapper.ScenarioSelectionInfoMapper;
import com.shencai.eil.scenario.mapper.TechMappingLeakageModelMapper;
import com.shencai.eil.scenario.model.AccidentScenarioParamVO;
import com.shencai.eil.scenario.model.AccidentScenarioResultParam;
import com.shencai.eil.scenario.service.IAccidentScenarioResultService;
import com.shencai.eil.scenario.service.ILeakageTimeService;
import com.shencai.eil.survey.entity.EntSurveyPlan;
import com.shencai.eil.survey.entity.EntSurveyResult;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import com.shencai.eil.survey.service.IEntSurveyResultService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fanhj
 * @since 2018-10-12
 */
@Service
public class AccidentScenarioResultServiceImpl extends ServiceImpl<AccidentScenarioResultMapper, AccidentScenarioResult> implements IAccidentScenarioResultService {

    @Autowired
    private TechMappingLeakageModelMapper techMappingLeakageModelMapper;
    @Autowired
    private ScenarioSelectionInfoMapper scenarioSelectionInfoMapper;
    @Autowired
    private IEntSurveyPlanService entSurveyPlanService;
    @Autowired
    private IEntSurveyResultService entSurveyResultService;
    @Autowired
    private ILeakageTimeService leakageTimeService;
    @Autowired
    private AccidentScenarioMapper accidentScenarioMapper;

    private static final double A = 7.85398E-05;
    private static final double T_MAX = 600;
    private static final List<String> SURVEY_ITEM_ID_WORKING_HOURS = Arrays.asList("S260", "S262");
    private static final String PART_TYPE_NAME_LOADING_ARM = "装卸臂";
    private static final String PART_TYPE_NAME_LOADING_HOSE = "装卸软管";
    private static final int LEAKAGE_MODEL_CODE_ONE = 1;
    private static final int LEAKAGE_MODEL_CODE_TWO = 2;
    private static final int LEAKAGE_MODEL_CODE_THREE = 3;
    private static final int LEAKAGE_MODEL_CODE_FOUR = 4;
    private static final int LEAKAGE_MODEL_CODE_FIVE = 5;

    @Override
    public double calculateScenarioResult(AccidentScenarioResultParam param) {
        String scenarioCode = getAccidentScenarioCode(param);
        switch (scenarioCode) {
            case ScenarioCode.S_ONE:
            case ScenarioCode.S_TWO:
            case ScenarioCode.S_FIVE:
                double result = 1;
                for (AccidentScenarioParamVO accidentScenarioParamVO : param.getScenarioParamList()) {
                    String code = accidentScenarioParamVO.getCode();
                    if (!scenarioCode.equals(ScenarioCode.LC_FIVE_ZERO)) {
                        double paramValue = Double.valueOf(accidentScenarioParamVO.getValue());
                        if (ScenarioCode.R_ONE.equals(code)) {
                            paramValue = paramValue / 100;
                        }
                        result *= paramValue;
                    }
                }
                return Double.valueOf(String.format("%.18f", result));
            default:
                return scenarioResultCalculation(param, scenarioCode);
        }
    }

    private String getAccidentScenarioCode(AccidentScenarioResultParam param) {
        AccidentScenario accidentScenario = accidentScenarioMapper.selectOne(new QueryWrapper<AccidentScenario>()
                .eq("id", param.getScenarioId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        return accidentScenario.getCode();
    }

    public double scenarioResultCalculation(AccidentScenarioResultParam param, String scenarioCode) {
        Map<String, Double> leakTimeMap = getLeakageTime();
        ScenarioSelectionInfo scenarioSelectionInfo = getScenarioSelectionInfo(param);
        Double onlineContent = scenarioSelectionInfo.getOnlineContent();
        String scenarioId = param.getScenarioId();
        Map<String, String> paramMap = getScenarioParamMap(param);
        String partType = paramMap.get(ScenarioParamEnum.PART_TYPE.getCode());
        List<TechMappingLeakageModel> techMappingLeakageModels = listTechMappingLeakageModel(scenarioId, partType);
        double scenarioResult = 0;
        if (ScenarioEnum.S_THREE.getCode().equals(scenarioCode)) {
            scenarioResult = getLeakage(leakTimeMap, onlineContent, scenarioId, paramMap, techMappingLeakageModels);
        } else if (ScenarioEnum.S_FOUR.getCode().equals(scenarioCode)) {
            scenarioResult = calculateLeakage(leakTimeMap, onlineContent, scenarioId, paramMap, techMappingLeakageModels);
        }
        if (PART_TYPE_NAME_LOADING_ARM.equals(partType) || PART_TYPE_NAME_LOADING_HOSE.equals(partType)) {
            String workingHoursPerYear = getSurveyResult(scenarioSelectionInfo.getEntId(), SURVEY_ITEM_ID_WORKING_HOURS);
            scenarioResult *= Double.valueOf(workingHoursPerYear);
        }
        return Double.valueOf(String.format("%.18f", scenarioResult));
    }

    private double calculateLeakage(Map<String, Double> leakTimeMap, Double onlineContent, String scenarioId,
                                    Map<String, String> paramMap, List<TechMappingLeakageModel> techMappingLeakageModels) {
        double leakage = 0.0;
        for (TechMappingLeakageModel techMappingLeakageModel : techMappingLeakageModels) {
            Double leakageFreq = techMappingLeakageModel.getLeakageFreq();
            Double leakCoefficient = Double.valueOf(paramMap.get(ScenarioParamEnum.LEAKAGE_COEFFICIENT_OF_LIQUID.getCode()));
            Double liquidDensity = Double.valueOf(paramMap.get(ScenarioParamEnum.LIQUID_DENSITY.getCode()));
            Double containerPressure = Double.valueOf(paramMap.get(ScenarioParamEnum.CONTAINER_PRESSURE.getCode()));
            Double envPressure = Double.valueOf(paramMap.get(ScenarioParamEnum.ENVIRONMENT_PRESSURE.getCode()));
            Double g = Double.valueOf(paramMap.get(ScenarioParamEnum.GRAVITATIONAL_ACCELERATION.getCode()));
            Double h = Double.valueOf(paramMap.get(ScenarioParamEnum.LIQUID_HEIGHT.getCode()));
            String pipeInternalDiameter = paramMap.get(ScenarioParamEnum.INTERNAL_DIAMETER_OF_PIPELINE.getCode());
            Double pipeDiameter = 0.0;
            if (!ObjectUtil.isEmpty(pipeInternalDiameter)) {
                pipeDiameter = Double.valueOf(pipeInternalDiameter);
            }
            String detectionSystemClassification = paramMap.get(ScenarioParamEnum.DETECTION_SYSTEM_CLASSIFICATION.getCode());
            String isolationSystemClassification = paramMap.get(ScenarioParamEnum.ISOLATION_SYSTEM_CLASSIFICATION.getCode());
            Double leakageTime = leakTimeMap.get(scenarioId + detectionSystemClassification + isolationSystemClassification);
            Integer leakageModelCode = techMappingLeakageModel.getLeakageModelCode();
            if (leakageModelCode == LEAKAGE_MODEL_CODE_ONE) {
                //QL1=Cdl*A*ρ*sqrt(2*(P-P0)/ρ+2*g*h)
                double leakageRate = leakCoefficient * A * liquidDensity
                        * Math.sqrt(2 * (containerPressure - envPressure) / liquidDensity + 2 * g * h);
                //泄漏量＝泄漏频率＊泄漏时间＊泄漏速率
                leakage += leakageFreq * leakageTime * leakageRate;
            } else if (leakageModelCode == LEAKAGE_MODEL_CODE_TWO) {
                //QL2=Q1*1000/Tmax
                double leakageRate = onlineContent * 1000 / T_MAX;
                //泄漏量＝泄漏频率＊泄漏时间＊泄漏速率
                leakage += leakageFreq * leakageTime * leakageRate;
            } else if (leakageModelCode == LEAKAGE_MODEL_CODE_THREE || leakageModelCode == LEAKAGE_MODEL_CODE_FIVE) {
                //泄漏量＝泄漏频率＊物质在线量 Q1*1000
                leakage += leakageFreq * onlineContent * 1000;
            } else if (leakageModelCode == LEAKAGE_MODEL_CODE_FOUR) {
                double cleftArea = Math.pow(pipeDiameter * 0.1 / 2, 2) * Math.PI / 1000000;
                //QL4=Cdl*A*ρ*sqrt(2*(P-P0)/ρ+2*g*h)
                double leakageRate = leakCoefficient * cleftArea * liquidDensity
                        * Math.sqrt(2 * (containerPressure - envPressure) / liquidDensity + 2 * g * h);
                //泄漏量＝泄漏频率＊泄漏时间＊泄漏速率
                leakage += leakageFreq * leakageTime * leakageRate;
            } else {
                double cleftArea;
                if (pipeDiameter <= 500) {
                    cleftArea = Math.pow(pipeDiameter * 0.1 / 2, 2) * Math.PI / 1000000;
                } else {
                    cleftArea = Math.pow((50 / 2), 2) * Math.PI / 1000000;
                }
                //QL6=Cdl*A*ρ*sqrt(2*(P-P0)/ρ+2*g*h)
                double leakageRate = leakCoefficient * cleftArea * liquidDensity
                        * Math.sqrt(2 * (containerPressure - envPressure) / liquidDensity + 2 * g * h);
                //泄漏量＝泄漏频率＊泄漏时间＊泄漏速率
                leakage += leakageFreq * leakageTime * leakageRate;
            }
        }
        return leakage;
    }

    private Map<String, String> getScenarioParamMap(AccidentScenarioResultParam param) {
        List<AccidentScenarioParamVO> scenarioParamList = param.getScenarioParamList();
        Map<String, String> paramMap = new HashMap<>();
        for (AccidentScenarioParamVO scenarioParam : scenarioParamList) {
            paramMap.put(scenarioParam.getCode(), scenarioParam.getValue());
        }
        return paramMap;
    }

    private ScenarioSelectionInfo getScenarioSelectionInfo(AccidentScenarioResultParam param) {
        return scenarioSelectionInfoMapper.selectOne(
                new QueryWrapper<ScenarioSelectionInfo>()
                        .eq("id", param.getScenarioSelectionInfoId())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private List<TechMappingLeakageModel> listTechMappingLeakageModel(String scenarioId, String partType) {
        return techMappingLeakageModelMapper.selectList(
                new QueryWrapper<TechMappingLeakageModel>()
                        .eq("scenario_id", scenarioId)
                        .eq("technique_component_name", partType)
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private String getSurveyResult(String entId, List<String> surveyItemId) {
        List<EntSurveyPlan> surveyPlans = entSurveyPlanService.list(new QueryWrapper<EntSurveyPlan>()
                .eq("ent_id", entId).eq("valid", BaseEnum.VALID_YES.getCode())
                .in("survey_item_id", surveyItemId));
        if (CollectionUtils.isEmpty(surveyPlans)) {
            throw new BusinessException("ent_survey_plan table not exit " + surveyItemId + " survey item of this enterprise");
        }
        List<String> planIds = surveyPlans.stream().map(EntSurveyPlan::getId).collect(Collectors.toList());
        List<EntSurveyResult> surveyResults = entSurveyResultService.list(new QueryWrapper<EntSurveyResult>()
                .in("survey_plan_id", planIds).eq("valid", BaseEnum.VALID_YES.getCode()));
        if (CollectionUtils.isEmpty(surveyResults)) {
            throw new BusinessException("surveyResults not exit survey_plan_id" + planIds);
        }
        String value = surveyResults.get(0).getResult();
        return value;
    }

    /**
     * 获取泄露量
     */
    private double getLeakage(Map<String, Double> leakTimeMap, Double onlineContent, String scenarioId,
                              Map<String, String> paramMap, List<TechMappingLeakageModel> techMappingLeakageModels) {
        double leakage = 0.0;
        for (TechMappingLeakageModel techMappingLeakageModel : techMappingLeakageModels) {
            Double leakageFreq = techMappingLeakageModel.getLeakageFreq();
            double γ = Double.parseDouble(paramMap.get(ScenarioParamEnum.ADIABATIC_EXPONENT_OF_GAS.getCode()));
            double cdg = Double.parseDouble(paramMap.get(ScenarioParamEnum.LEAKAGE_COEFFICIENT_OF_GAS.getCode()));
            double m = Double.parseDouble(paramMap.get(ScenarioParamEnum.MOLECULAR_WEIGHT_OF_MATTER.getCode()));
            double r = Double.parseDouble(paramMap.get(ScenarioParamEnum.GAS_CONSTANT.getCode()));
            double y = Double.parseDouble(paramMap.get(ScenarioParamEnum.OUTFLOW_COEFFICIENT.getCode()));
            double p = Double.parseDouble(paramMap.get(ScenarioParamEnum.CONTAINER_PRESSURE.getCode()));
            double tg = Double.parseDouble(paramMap.get(ScenarioParamEnum.GAS_TEMPERATURE.getCode()));
            String pipeInternalDiameter = paramMap.get(ScenarioParamEnum.INTERNAL_DIAMETER_OF_PIPELINE.getCode());
            Double pipeDiameter = 0.0;
            if (!ObjectUtil.isEmpty(pipeInternalDiameter)) {
                pipeDiameter = Double.valueOf(pipeInternalDiameter);
            }
            String detectionSystemClassification = paramMap.get(ScenarioParamEnum.DETECTION_SYSTEM_CLASSIFICATION.getCode());
            String isolationSystemClassification = paramMap.get(ScenarioParamEnum.ISOLATION_SYSTEM_CLASSIFICATION.getCode());
            Double leakageTime = leakTimeMap.get(scenarioId + detectionSystemClassification + isolationSystemClassification);
            Integer leakageModelCode = techMappingLeakageModel.getLeakageModelCode();
            if (leakageModelCode == LEAKAGE_MODEL_CODE_ONE) {
                //QG1=sqrt(1/1000)*Y*Cd*A*P*sqrt(M*γ/R/TG*（2/(γ+1))^((γ+1)/（γ-1）））
                double leakageRate = Math.sqrt(1.0 / 1000) * y * cdg * A * p
                        * Math.sqrt(m * γ / r / tg * Math.pow(2 / (γ + 1), ((γ + 1) / (γ - 1))));
                //泄漏量＝泄漏频率＊泄漏时间＊泄漏速率
                leakage += leakageFreq * leakageTime * leakageRate;
            } else if (leakageModelCode == LEAKAGE_MODEL_CODE_TWO) {
                //QG2=Q1*1000/Tmax
                double leakageRate = onlineContent * 1000 / T_MAX;
                //泄漏量＝泄漏频率＊泄漏时间＊泄漏速率
                leakage += leakageFreq * leakageTime * leakageRate;
            } else if (leakageModelCode == LEAKAGE_MODEL_CODE_THREE || leakageModelCode == LEAKAGE_MODEL_CODE_FIVE) {
                //泄漏量＝泄漏频率＊物质在线量 Q1*1000
                leakage += leakageFreq * onlineContent * 1000;
            } else if (leakageModelCode == LEAKAGE_MODEL_CODE_FOUR) {
                //(Pipe Diameter*10%/2)*(Pipe Diameter*10%/2)*PI()/1000000
                double cleftArea = Math.pow(pipeDiameter * 0.1 / 2, 2) * Math.PI / 1000000;
                //QG4=sqrt(1/1000)*Y*Cd*A*P*sqrt(M*γ/R/TG*（2/(γ+1))^((γ+1)/（γ-1）））
                double leakageRate = Math.sqrt(1.0 / 1000) * y * cdg * cleftArea * p
                        * Math.sqrt(m * γ / r / tg * Math.pow(2 / (γ + 1), ((γ + 1) / (γ - 1))));
                //泄漏量＝泄漏频率＊泄漏时间＊泄漏速率
                leakage += leakageFreq * leakageTime * leakageRate;
            } else {
                double cleftArea;
                if (pipeDiameter <= 500) {
                    cleftArea = Math.pow(pipeDiameter * 0.1 / 2, 2) * Math.PI / 1000000;
                } else {
                    cleftArea = Math.pow((50 / 2), 2) * Math.PI / 1000000;
                }
                //QG6=sqrt(1/1000)*Y*Cd*A*P*sqrt(M*γ/R/TG*（2/(γ+1))^((γ+1)/（γ-1）））
                double leakageRate = Math.sqrt(1.0 / 1000) * y * cdg * cleftArea * p
                        * Math.sqrt(m * γ / r / tg * Math.pow(2 / (γ + 1), ((γ + 1) / (γ - 1))));
                //泄漏量＝泄漏频率＊泄漏时间＊泄漏速率
                leakage += leakageFreq * leakageTime * leakageRate;
            }
        }
        return leakage;
    }

    /**
     * 获取泄露时间Map
     */
    private Map<String, Double> getLeakageTime() {
        List<LeakageTime> leakageTimes = leakageTimeService.list(new QueryWrapper<LeakageTime>()
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        Map<String, Double> map = new HashMap<>(leakageTimes.size());
        for (LeakageTime leakageTime : leakageTimes) {
            map.put(leakageTime.getScenarioId() + leakageTime.getDetectionSystemLevelName() + leakageTime.getShieldingSystemLevelName(),
                    leakageTime.getLeakageTime());
        }
        return map;
    }

}
