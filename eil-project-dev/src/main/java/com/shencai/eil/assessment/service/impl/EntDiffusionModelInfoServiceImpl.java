package com.shencai.eil.assessment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.assessment.constants.DiffusionModelBisCode;
import com.shencai.eil.assessment.entity.*;
import com.shencai.eil.assessment.mapper.EntAssessInfoMapper;
import com.shencai.eil.assessment.mapper.EntDiffusionModelInfoMapper;
import com.shencai.eil.assessment.mapper.SoilModelPointMapper;
import com.shencai.eil.assessment.mapper.WaterDiffModelConditionMapper;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoQueryParam;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoVO;
import com.shencai.eil.assessment.service.IAssessGirdService;
import com.shencai.eil.assessment.service.IEntDiffusionModelInfoService;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.grading.entity.ComputeConstant;
import com.shencai.eil.grading.mapper.ComputeConstantMapper;
import com.shencai.eil.grading.mapper.RiskMaterialParamValueMapper;
import com.shencai.eil.grading.model.RiskMaterialParamValueQueryParam;
import com.shencai.eil.grading.model.RiskMaterialParamValueVO;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.scenario.entity.AccidentScenario;
import com.shencai.eil.scenario.mapper.AccidentScenarioMapper;
import com.shencai.eil.scenario.mapper.ScenarioSelectionInfoMapper;
import com.shencai.eil.survey.mapper.EntSurveyResultMapper;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import com.shencai.eil.survey.model.EntSurveyResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author zhoujx
 * @since 2018-10-20
 */
@Service
public class EntDiffusionModelInfoServiceImpl extends ServiceImpl<EntDiffusionModelInfoMapper, EntDiffusionModelInfo> implements IEntDiffusionModelInfoService {

    @Autowired
    private ScenarioSelectionInfoMapper scenarioSelectionInfoMapper;
    @Autowired
    private AccidentScenarioMapper accidentScenarioMapper;
    @Autowired
    private EntSurveyResultMapper entSurveyResultMapper;
    @Autowired
    private RiskMaterialParamValueMapper riskMaterialParamValueMapper;
    @Autowired
    private WaterDiffModelConditionMapper waterDiffModelConditionMapper;
    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    @Autowired
    private EntDiffusionModelInfoMapper entDiffusionModelInfoMapper;
    @Autowired
    private SoilModelPointMapper soilModelPointMapper;
    @Autowired
    private ComputeConstantMapper constantMapper;
    @Autowired
    private IAssessGirdService assessGirdService;
    @Autowired
    private EntAssessInfoMapper entAssessInfoMapper;

    //survey item code
    private static final String CODE_FOR_EMISSION_MODE_TYPE = "S206";
    private static final String CODE_FOR_TYPE_OF_SURFACE_WATER_INTO_DISCHARGE_PORT = "S207";
    private static final String CODE_FOR_DISTANCE = "S208";
    private static final String CODE_FOR_WATER_WIDTH = "S302";
    private static final String CODE_FOR_RIVER_DIRECTION = "S313";
    private static final String CODE_FOR_IS_THERE_ANY_SURFACE_WATER = "S265";
    private static final String CODE_FOR_SURFACE_WATER_TYPE_WITHIN_1KM = "S266";
    private static final String CODE_FOR_WATER_WIDTH_WITHIN_1KM = "S306";
    private static final String CODE_FOR_RIVER_DIRECTION_WITHIN_1KM = "S311";

    private static final String MAP_KEY_FOR_EMISSION_MODE_TYPE = "emissionModeType";
    private static final String MAP_KEY_FOR_EXIST_WATER_ENV = "existWaterEnv";
    private static final String MAP_KEY_FOR_SURFACE_WATER_INTO_DISCHARGE_PORT_TYPE = "typeOfSurfaceWaterIntoDischargePort";
    private static final String MAP_KEY_FOR_SURFACE_WATER_TYPE_WITHIN_1KM = "surfaceWaterTypeWithin1KM";
    private static final String MAP_KEY_FOR_ACCESS_TYPE = "accessType";
    private static final String MAP_KEY_FOR_DISTANCE = "distanceOfOutlet";
    private static final String MAP_KEY_FOR_WATER_WIDTH = "waterWidth";
    private static final String MAP_KEY_FOR_RIVER_DIRECTION = "riverDirection";
    private static final String MAP_KEY_FOR_WIDTH_WITHIN_1KM = "waterWidthWithin1KM";
    private static final String MAP_KEY_FOR_DIRECTION_WITHIN_1KM = "riverDirectionWithin1KM";

    private static final String SURVEY_RESULT_INDIRECT_EMISSIONS = "接管";
    private static final String LIQUID_STATE = "液态";
    private static final double DISTANCE_ZERO = 0;
    private static final double DENSITY_OF_WATER = 1.0;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void matchingWaterQualityModel(String enterpriseId) {
        updateEnterpriseStatus(enterpriseId);
        String scenarioIdForScenarioOne = getScenarioIdForScenarioOne();
        List<EntDiffusionModelInfo> entDiffusionModelInfoList = listEntDiffusionModelInfo(enterpriseId);
        List<EntSurveyResultVO> entSurveyResultVOS = listEntSurveyResult(enterpriseId);
        List<RiskMaterialParamValueVO> riskMaterialParamValueVOS = listRiskMaterialParamValue(entDiffusionModelInfoList);
        Map<String, String> surveyResultMap = new HashMap<>();
        disposeEntSurveyResult(entSurveyResultVOS, surveyResultMap);
        setPropertyOfEntDiffusionModelInfo(scenarioIdForScenarioOne, entDiffusionModelInfoList,
                riskMaterialParamValueVOS, surveyResultMap);
        matchingModel(entDiffusionModelInfoList);
        saveEntDiffusionModelInfoList(entDiffusionModelInfoList);
        List<SoilModelPoint> soilModelPointList = listSoilModelPoint();
        Double t = getT();
        List<AssessGird> assessGirdList = listAssessGird(enterpriseId, soilModelPointList, t);
        saveAssessGird(assessGirdList);
        saveEntAssessInfo(enterpriseId, surveyResultMap);
    }

    @Override
    public Page<EntDiffusionModelInfoVO> pageDiffusionModelInfo(EntDiffusionModelInfoQueryParam queryParam) {
        Page<EntDiffusionModelInfoVO> page = getPageParam(queryParam);
        pageEntDiffusionModelInfo(queryParam, page);
        return page;
    }

    private void updateEnterpriseStatus(String enterpriseId) {
        EnterpriseInfo updateParam = new EnterpriseInfo();
        updateParam.setId(enterpriseId);
        updateParam.setStatus(StatusEnum.PENDING_DAMAGE_ASSESSMENT.getCode());
        updateParam.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseInfoMapper.updateById(updateParam);
    }

    private void saveEntDiffusionModelInfoList(List<EntDiffusionModelInfo> entDiffusionModelInfoList) {
        if (!CollectionUtils.isEmpty(entDiffusionModelInfoList)) {
            this.saveBatch(entDiffusionModelInfoList);
        }
    }

    private void matchingModel(List<EntDiffusionModelInfo> entDiffusionModelInfoList) {
        List<WaterDiffModelCondition> waterDiffModelConditionList = waterDiffModelConditionMapper.selectList(
                new QueryWrapper<WaterDiffModelCondition>()
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        for (EntDiffusionModelInfo entDiffusionModelInfo : entDiffusionModelInfoList) {
            String surfaceWaterCondition = entDiffusionModelInfo.getSurfaceWaterCondition();
            String pollutantType = entDiffusionModelInfo.getPollutantType();
            String accessType = entDiffusionModelInfo.getAccessType();
            for (WaterDiffModelCondition waterDiffModelCondition : waterDiffModelConditionList) {
                if (waterDiffModelCondition.getLandmarkWaterType().equals(surfaceWaterCondition)
                        && waterDiffModelCondition.getPollutantType().equals(pollutantType)
                        && waterDiffModelCondition.getAccessType().equals(accessType)) {
                    entDiffusionModelInfo.setModelId(waterDiffModelCondition.getModelId());
                    break;
                }
            }
        }
    }

    private String getScenarioIdForScenarioOne() {
        AccidentScenario accidentScenario = accidentScenarioMapper.selectOne(new QueryWrapper<AccidentScenario>()
                .eq("code", ScenarioEnum.S_ONE.getCode())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        return accidentScenario.getId();
    }

    private void setPropertyOfEntDiffusionModelInfo(String scenarioIdForS1, List<EntDiffusionModelInfo> entDiffusionModelInfoList,
                                                    List<RiskMaterialParamValueVO> riskMaterialParamValueVOS,
                                                    Map<String, String> surveyResultMap) {
        for (EntDiffusionModelInfo entDiffusionModelInfo : entDiffusionModelInfoList) {
            entDiffusionModelInfo.setId(StringUtil.getUUID());
            entDiffusionModelInfo.setEmissionModeType(surveyResultMap.get(MAP_KEY_FOR_EMISSION_MODE_TYPE));
            entDiffusionModelInfo.setExistWaterEnv(Integer.valueOf(surveyResultMap.get(MAP_KEY_FOR_EXIST_WATER_ENV)));
            int isConsideringWaterEnvRisk = getIsConsideringWaterEnvRisk(scenarioIdForS1, surveyResultMap, entDiffusionModelInfo);
            entDiffusionModelInfo.setConsideringWaterEnvRisk(isConsideringWaterEnvRisk);
            if (isConsideringWaterEnvRisk == (Integer) BaseEnum.YES.getCode()) {
                if (scenarioIdForS1.equals(entDiffusionModelInfo.getScenarioId())
                        && EmissionModeType.DIRECT.getCode().equals(surveyResultMap.get(MAP_KEY_FOR_EMISSION_MODE_TYPE))) {
                    entDiffusionModelInfo.setSurfaceWaterCondition(surveyResultMap.get(MAP_KEY_FOR_SURFACE_WATER_INTO_DISCHARGE_PORT_TYPE));
                    entDiffusionModelInfo.setAccessType(surveyResultMap.get(MAP_KEY_FOR_ACCESS_TYPE));
                } else {
                    entDiffusionModelInfo.setSurfaceWaterCondition(surveyResultMap.get(MAP_KEY_FOR_SURFACE_WATER_TYPE_WITHIN_1KM));
                    entDiffusionModelInfo.setAccessType(DictionaryEnum.ACCESS_SURFACE_WATER_TYPE.getCode());
                }
                String material = entDiffusionModelInfo.getMaterial();
                for (RiskMaterialParamValueVO riskMaterialParamValue : riskMaterialParamValueVOS) {
                    if (material.equals(riskMaterialParamValue.getMaterialName())) {
                        String paramCode = riskMaterialParamValue.getParamCode();
                        String paramValue = riskMaterialParamValue.getParamValue();
                        if (ParamEnum.STABILITY.getCode().equals(paramCode)) {
                            entDiffusionModelInfo.setStability(paramValue);
                            if (Integer.valueOf(paramValue) == BaseEnum.YES.getCode()) {
                                entDiffusionModelInfo.setPollutantType(DictionaryEnum.POLLUTANT_TYPE_STABILITY.getCode());
                            } else {
                                entDiffusionModelInfo.setPollutantType(DictionaryEnum.POLLUTANT_TYPE_NO_STABILITY.getCode());
                            }
                        } else if (ParamEnum.DENSITY.getCode().equals(paramCode)) {
                            entDiffusionModelInfo.setDensity(paramValue);
                        } else if (ParamEnum.PHASE_STATE_AT_ROOM_TEMPERATURE.getCode().equals(paramCode)) {
                            entDiffusionModelInfo.setState(paramValue);
                        } else {
                            entDiffusionModelInfo.setOctanWaterPartition(paramValue);
                        }
                    }
                }
                if (LIQUID_STATE.equals(entDiffusionModelInfo.getState())
                        && Double.valueOf(entDiffusionModelInfo.getDensity()) < DENSITY_OF_WATER
                        && Integer.valueOf(entDiffusionModelInfo.getOctanWaterPartition()) == BaseEnum.NO.getCode()) {
                    entDiffusionModelInfo.setPollutantType(DictionaryEnum.POLLUTANT_TYPE_OILS.getCode());
                }
            }
            entDiffusionModelInfo.setValid((Integer) BaseEnum.VALID_YES.getCode());
            entDiffusionModelInfo.setCreateTime(DateUtil.getNowTimestamp());
            entDiffusionModelInfo.setUpdateTime(DateUtil.getNowTimestamp());
        }
    }

    private int getIsConsideringWaterEnvRisk(String scenarioIdForS1, Map<String, String> surveyResultMap, EntDiffusionModelInfo entDiffusionModelInfo) {
        int isConsideringWaterEnvRisk = (Integer) BaseEnum.YES.getCode();
        if (entDiffusionModelInfo.getWading() == BaseEnum.NO.getCode()) {
            isConsideringWaterEnvRisk = (Integer) BaseEnum.NO.getCode();
        } else {
            if (scenarioIdForS1.equals(entDiffusionModelInfo.getScenarioId())
                    && EmissionModeType.INDIRECT.getCode().equals(surveyResultMap.get(MAP_KEY_FOR_EMISSION_MODE_TYPE))) {
                isConsideringWaterEnvRisk = (Integer) BaseEnum.NO.getCode();
            }
            if (!scenarioIdForS1.equals(entDiffusionModelInfo.getScenarioId())
                    && Integer.valueOf(surveyResultMap.get(MAP_KEY_FOR_EXIST_WATER_ENV)) == 0) {
                isConsideringWaterEnvRisk = (Integer) BaseEnum.NO.getCode();
            }
        }
        return isConsideringWaterEnvRisk;
    }

    private void disposeEntSurveyResult(List<EntSurveyResultVO> entSurveyResultVOS, Map<String, String> surveyResultMap) {
        String emissionModeType = null;
        String existWaterEnv = null;
        String typeOfSurfaceWaterIntoDischargePort = null;
        String surfaceWaterTypeWithin1KM = null;
        String accessType = null;
        for (EntSurveyResultVO entSurveyResultVO : entSurveyResultVOS) {
            String result = entSurveyResultVO.getResult();
            switch (entSurveyResultVO.getSurveyItemCode()) {
                case CODE_FOR_EMISSION_MODE_TYPE:
                    if (SURVEY_RESULT_INDIRECT_EMISSIONS.equals(result)) {
                        emissionModeType = EmissionModeType.INDIRECT.getCode();
                    } else {
                        emissionModeType = EmissionModeType.DIRECT.getCode();
                    }
                    break;
                case CODE_FOR_IS_THERE_ANY_SURFACE_WATER:
                    if (BaseEnum.YES.getChineseName().equals(result)) {
                        existWaterEnv = String.valueOf(BaseEnum.YES.getCode());
                    } else {
                        existWaterEnv = String.valueOf(BaseEnum.NO.getCode());
                    }
                    break;
                case CODE_FOR_TYPE_OF_SURFACE_WATER_INTO_DISCHARGE_PORT:
                    if (DictionaryEnum.SURFACE_WATER_CONDITION_RIVER.getName().equals(result)) {
                        typeOfSurfaceWaterIntoDischargePort = DictionaryEnum.SURFACE_WATER_CONDITION_RIVER.getCode();
                    } else if (DictionaryEnum.SURFACE_WATER_CONDITION_LAKE.getName().equals(result)) {
                        typeOfSurfaceWaterIntoDischargePort = DictionaryEnum.SURFACE_WATER_CONDITION_LAKE.getCode();
                    } else if (DictionaryEnum.SURFACE_WATER_CONDITION_ESTUARY.getName().equals(result)) {
                        typeOfSurfaceWaterIntoDischargePort = DictionaryEnum.SURFACE_WATER_CONDITION_ESTUARY.getCode();
                    }
                    break;
                case CODE_FOR_SURFACE_WATER_TYPE_WITHIN_1KM:
                    if (DictionaryEnum.SURFACE_WATER_CONDITION_RIVER.getName().equals(result)) {
                        surfaceWaterTypeWithin1KM = DictionaryEnum.SURFACE_WATER_CONDITION_RIVER.getCode();
                    } else if (DictionaryEnum.SURFACE_WATER_CONDITION_LAKE.getName().equals(result)) {
                        surfaceWaterTypeWithin1KM = DictionaryEnum.SURFACE_WATER_CONDITION_LAKE.getCode();
                    } else if (DictionaryEnum.SURFACE_WATER_CONDITION_ESTUARY.getName().equals(result)) {
                        surfaceWaterTypeWithin1KM = DictionaryEnum.SURFACE_WATER_CONDITION_ESTUARY.getCode();
                    }
                    break;
                case CODE_FOR_DISTANCE:
                    if (DISTANCE_ZERO == Double.valueOf(result)) {
                        accessType = DictionaryEnum.ACCESS_SURFACE_WATER_TYPE_SHORE.getCode();
                    } else {
                        accessType = DictionaryEnum.ACCESS_SURFACE_WATER_TYPE_NON_SHORE.getCode();
                    }
                    surveyResultMap.put(MAP_KEY_FOR_DISTANCE, result);
                    break;
                case CODE_FOR_WATER_WIDTH:
                    surveyResultMap.put(MAP_KEY_FOR_WATER_WIDTH, result);
                    break;
                case CODE_FOR_RIVER_DIRECTION:
                    surveyResultMap.put(MAP_KEY_FOR_RIVER_DIRECTION, DictionaryEnum.getCodeByName(result));
                    break;
                case CODE_FOR_WATER_WIDTH_WITHIN_1KM:
                    surveyResultMap.put(MAP_KEY_FOR_WIDTH_WITHIN_1KM, result);
                    break;
                case CODE_FOR_RIVER_DIRECTION_WITHIN_1KM:
                    surveyResultMap.put(MAP_KEY_FOR_DIRECTION_WITHIN_1KM, DictionaryEnum.getCodeByName(result));
                    break;
                default:
                    break;
            }
        }
        surveyResultMap.put(MAP_KEY_FOR_EMISSION_MODE_TYPE, emissionModeType);
        surveyResultMap.put(MAP_KEY_FOR_EXIST_WATER_ENV, existWaterEnv);
        surveyResultMap.put(MAP_KEY_FOR_SURFACE_WATER_INTO_DISCHARGE_PORT_TYPE, typeOfSurfaceWaterIntoDischargePort);
        surveyResultMap.put(MAP_KEY_FOR_SURFACE_WATER_TYPE_WITHIN_1KM, surfaceWaterTypeWithin1KM);
        surveyResultMap.put(MAP_KEY_FOR_ACCESS_TYPE, accessType);
    }

    private List<RiskMaterialParamValueVO> listRiskMaterialParamValue(List<EntDiffusionModelInfo> entDiffusionModelInfoList) {
        List<String> materialNameList = new ArrayList<>();
        for (EntDiffusionModelInfo entDiffusionModelInfo : entDiffusionModelInfoList) {
            String material = entDiffusionModelInfo.getMaterial();
            if (!materialNameList.contains(material)) {
                materialNameList.add(material);
            }
        }
        RiskMaterialParamValueQueryParam queryParam = new RiskMaterialParamValueQueryParam();
        queryParam.setTemplateCode(TemplateEnum.MODEL_MATCHING.getCode());
        queryParam.setMaterialNameList(materialNameList);
        return riskMaterialParamValueMapper.listRiskMaterialParamValue(queryParam);
    }

    private List<EntDiffusionModelInfo> listEntDiffusionModelInfo(String enterpriseId) {
        return scenarioSelectionInfoMapper.listScenarioSelectInfoAndResult(enterpriseId);
    }

    private List<EntSurveyResultVO> listEntSurveyResult(String enterpriseId) {
        List<String> surveyItemCodeList = new ArrayList<>();
        surveyItemCodeList.add(CODE_FOR_EMISSION_MODE_TYPE);
        surveyItemCodeList.add(CODE_FOR_TYPE_OF_SURFACE_WATER_INTO_DISCHARGE_PORT);
        surveyItemCodeList.add(CODE_FOR_DISTANCE);
        surveyItemCodeList.add(CODE_FOR_IS_THERE_ANY_SURFACE_WATER);
        surveyItemCodeList.add(CODE_FOR_SURFACE_WATER_TYPE_WITHIN_1KM);
        surveyItemCodeList.add(CODE_FOR_WATER_WIDTH);
        surveyItemCodeList.add(CODE_FOR_RIVER_DIRECTION);
        surveyItemCodeList.add(CODE_FOR_WATER_WIDTH_WITHIN_1KM);
        surveyItemCodeList.add(CODE_FOR_RIVER_DIRECTION_WITHIN_1KM);
        EntSurveyResultQueryParam queryParam = new EntSurveyResultQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        queryParam.setSurveyItemCodeList(surveyItemCodeList);
        return entSurveyResultMapper.listEntSurveyResultForModelMatching(queryParam);
    }

    private Page<EntDiffusionModelInfoVO> getPageParam(EntDiffusionModelInfoQueryParam queryParam) {
        Page<EntDiffusionModelInfoVO> page = new Page<>();
        page.setCurrent(queryParam.getCurrent());
        page.setSize(queryParam.getSize());
        return page;
    }

    private void pageEntDiffusionModelInfo(EntDiffusionModelInfoQueryParam queryParam, Page<EntDiffusionModelInfoVO> page) {
        List<EntDiffusionModelInfoVO> entDiffusionModelInfoVOList =
                entDiffusionModelInfoMapper.pageEntDiffusionModelInfo(page, queryParam);
        page.setRecords(entDiffusionModelInfoVOList);
    }

    private List<SoilModelPoint> listSoilModelPoint() {
        return soilModelPointMapper.selectList(
                new QueryWrapper<SoilModelPoint>()
                        .eq("valid", BaseEnum.VALID_YES.getCode())
                        .orderByAsc("z", "x"));
    }

    private void saveAssessGird(List<AssessGird> assessGirdList) {
        if (!CollectionUtils.isEmpty(assessGirdList)) {
            assessGirdService.saveBatch(assessGirdList);
        }
    }

    private List<AssessGird> listAssessGird(String enterpriseId, List<SoilModelPoint> soilModelPointList, Double t) {
        List<AssessGird> assessGirdList = new ArrayList<>();
        Date nowTime = DateUtil.getNowTimestamp();
        double x0 = 0;
        double acreage = 0;
        for (SoilModelPoint soilModelPoint : soilModelPointList) {
            Double x = soilModelPoint.getX();
            if (x > x0) {
                acreage = Math.PI * Math.pow((x - x0) * 2, 2) - acreage;
            } else {
                acreage = Math.PI * Math.pow(x * 2, 2);
            }
            x0 = x;
            AssessGird assessGird = new AssessGird();
            assessGird.setId(StringUtil.getUUID());
            assessGird.setEntId(enterpriseId);
            assessGird.setX(x);
            assessGird.setY(soilModelPoint.getY());
            assessGird.setZ(soilModelPoint.getZ());
            assessGird.setT(t);
            assessGird.setBisCode(DiffusionModelBisCode.SOIL.getCode());
            assessGird.setPointSortNum(soilModelPointList.indexOf(soilModelPoint) + 1);
            assessGird.setAcreage(acreage);
            assessGird.setCreateTime(nowTime);
            assessGird.setUpdateTime(nowTime);
            assessGird.setValid((Integer) BaseEnum.VALID_YES.getCode());
            assessGirdList.add(assessGird);
        }
        return assessGirdList;
    }

    private Double getT() {
        ComputeConstant computeConstant = constantMapper.selectOne(
                new QueryWrapper<ComputeConstant>()
                        .eq("code", ComputeConstantEnum.T.getCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        return Double.valueOf(computeConstant.getValue());
    }

    private void saveEntAssessInfo(String enterpriseId, Map<String, String> surveyResultMap) {
        EntAssessInfo entAssessInfo = new EntAssessInfo();
        entAssessInfo.setId(StringUtil.getUUID());
        entAssessInfo.setEntId(enterpriseId);
        if (EmissionModeType.DIRECT.getCode().equals(surveyResultMap.get(MAP_KEY_FOR_EMISSION_MODE_TYPE))) {
            entAssessInfo.setDistanceOfOutlet(Double.valueOf(surveyResultMap.get(MAP_KEY_FOR_DISTANCE)));
        }
        if (Integer.valueOf(surveyResultMap.get(MAP_KEY_FOR_EXIST_WATER_ENV)) == 0) {
            String waterWidth = surveyResultMap.get(MAP_KEY_FOR_WATER_WIDTH);
            Double riverWidth = ObjectUtil.isEmpty(waterWidth) ? null : Double.valueOf(waterWidth);
            entAssessInfo.setRiverWidth(riverWidth);
            entAssessInfo.setWaterDirection(surveyResultMap.get(MAP_KEY_FOR_RIVER_DIRECTION));
        } else {
            String waterWidth = surveyResultMap.get(MAP_KEY_FOR_WIDTH_WITHIN_1KM);
            Double riverWidth = ObjectUtil.isEmpty(waterWidth) ? null : Double.valueOf(waterWidth);
            entAssessInfo.setRiverWidth(riverWidth);
            entAssessInfo.setWaterDirection(surveyResultMap.get(MAP_KEY_FOR_DIRECTION_WITHIN_1KM));
        }
        entAssessInfo.setCreateTime(DateUtil.getNowTimestamp());
        entAssessInfo.setUpdateTime(DateUtil.getNowTimestamp());
        entAssessInfo.setValid((Integer) BaseEnum.VALID_YES.getCode());
        entAssessInfoMapper.insert(entAssessInfo);
    }

}
