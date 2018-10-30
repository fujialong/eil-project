package com.shencai.eil.scenario.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.grading.mapper.RiskMaterialParamValueMapper;
import com.shencai.eil.grading.model.RiskMaterialParamValueQueryParam;
import com.shencai.eil.scenario.entity.AccidentScenario;
import com.shencai.eil.scenario.entity.AccidentScenarioParam;
import com.shencai.eil.scenario.entity.HazardousReleaseRate;
import com.shencai.eil.scenario.entity.ScenarioSelectionInfo;
import com.shencai.eil.scenario.mapper.AccidentScenarioMapper;
import com.shencai.eil.scenario.mapper.AccidentScenarioParamMapper;
import com.shencai.eil.scenario.mapper.ScenarioSelectionInfoMapper;
import com.shencai.eil.scenario.model.AccidentScenarioParamQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioParamVO;
import com.shencai.eil.scenario.service.IAccidentScenarioParamService;
import com.shencai.eil.scenario.service.IAccidentScenarioService;
import com.shencai.eil.scenario.service.IHazardousReleaseRateService;
import com.shencai.eil.survey.constants.ExcelSheetName;
import com.shencai.eil.survey.mapper.EntSurveyResultMapper;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import com.shencai.eil.survey.model.EntSurveyResultVO;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
@Service
public class AccidentScenarioParamServiceImpl extends ServiceImpl<AccidentScenarioParamMapper, AccidentScenarioParam> implements IAccidentScenarioParamService {

    @Autowired
    private ScenarioSelectionInfoMapper scenarioSelectionInfoMapper;
    @Autowired
    private AccidentScenarioParamMapper accidentScenarioParamMapper;
    @Autowired
    private RiskMaterialParamValueMapper riskMaterialParamValueMapper;
    @Autowired
    private EntSurveyResultMapper entSurveyResultMapper;
    @Autowired
    private AccidentScenarioMapper accidentScenarioMapper;
    @Autowired
    private IHazardousReleaseRateService hazardousReleaseRateService;
    @Autowired
    private IEntSurveyPlanService entSurveyPlanService;
    @Autowired
    private IAccidentScenarioService accidentScenarioService;

    private static final double BASE_TEMPERATURE = 273.15;
    private static final List<String> PART_TYPE_BELONGING_TO_STORAGE_TANK =
            Arrays.asList("反应釜、工艺储罐、气体储罐、塔器", "常压单包容储罐", "常压储罐");
    private static final List<String> SURVEY_ITEM_CODE_OF_PIPE_INTERNAL_PRESSURE = Arrays.asList("S6", "S56");
    private static final List<String> SURVEY_ITEM_CODE_OF_TANK_INTERNAL_PRESSURE = Arrays.asList("S11", "S61");
    private static final List<String> SURVEY_ITEM_CODE_OF_TANK_HEIGHT = Arrays.asList("S10", "S60");
    private static final List<String> SURVEY_ITEM_CODE_OF_TANK_INTERNAL_TEMPERATURE = Arrays.asList("S12", "S62");
    private static final List<String> SURVEY_ITEM_CODE_OF_PIPE_INTERNAL_TEMPERATURE = Arrays.asList("S7", "S57");
    private static final List<String> SURVEY_ITEM_CODE_OF_TECHNIQUE_CELL = Arrays.asList("S1", "S51");
    private static final List<String> SURVEY_ITEM_CODE_OF_MATERIAL_NAME = Arrays.asList("S248", "S254");
    private static final List<String> SURVEY_ITEM_CODE_OF_MATERIAL_TYPE = Arrays.asList("S247", "S253");
    private static final List<String> SURVEY_ITEM_CODE_OF_DETECTION_SYSTEM = Arrays.asList("S14", "S67");
    private static final List<String> SURVEY_ITEM_CODE_OF_ISOLATION_SYSTEM = Arrays.asList("S16", "S69");
    private static final List<String> SURVEY_ITEM_CODE_OF_PIPE_INTERNAL_DIAMETER = Arrays.asList("S263", "S264");
    private static final List<String> SURVEY_ITEM_CODE_OF_EMISSIONS = Arrays.asList("S252", "S258");
    private static final List<String> SURVEY_ITEM_CODE_OF_PIPE_VALVE_NUMBER = Arrays.asList("S4", "S54");
    private static final List<String> SURVEY_ITEM_CODE_OF_PIPE_LENGTH = Arrays.asList("S5", "S55");
    private static final List<String> SURVEY_ITEM_CODE_OF_PIPE_LIFE = Arrays.asList("S259", "S261");
    private static final List<String> SURVEY_ITEM_CODE_OF_HAS_DETECTION_SYSTEM = Arrays.asList("S13", "S66");
    private static final List<String> SURVEY_ITEM_CODE_OF_HAS_ISOLATION_SYSTEM = Arrays.asList("S15", "S68");
    private static final String DETECTION_SYSTEM_DEFAULTED_VALUE = "外观检查、照相机，或带远距功能的探测器";
    private static final String ISOLATION_SYSTEM_DEFAULTED_VALUE = "手动操作阀启动的隔离系统;";

    @Override
    public List<AccidentScenarioParamVO> listScenarioParam(AccidentScenarioParamQueryParam queryParam) {
        ScenarioSelectionInfo scenarioSelectionInfo = getScenarioSelectionInfo(queryParam.getScenarioSelectionInfoId());
        String scenarioId = queryParam.getScenarioId();
        String scenarioCode = getScenarioCode(scenarioId);
        return listAccidentScenarioParamVO(scenarioSelectionInfo, scenarioId, scenarioCode);
    }

    @Override
    public List<AccidentScenarioParamVO> listScenarioParam(ScenarioSelectionInfo scenarioSelectionInfo, String scenarioId) {
        String scenarioCode = getScenarioCode(scenarioId);
        return listAccidentScenarioParamVO(scenarioSelectionInfo, scenarioId, scenarioCode);
    }

    @Override
    public String calculateRelatedParam(AccidentScenarioParamQueryParam queryParam) {
        Double medianLethalConcentration = queryParam.getMedianLethalConcentration();
        Double onlineQuantity = queryParam.getOnlineQuantity();
        HazardousReleaseRate hazardousReleaseRate = hazardousReleaseRateService.getOne(new QueryWrapper<HazardousReleaseRate>()
                .le("lower_limit_of_lc50", medianLethalConcentration)
                .gt("upper_limit_of_lc50", medianLethalConcentration)
                .lt("lower_limit_of_q1", onlineQuantity)
                .ge("upper_limit_of_q1", onlineQuantity)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (ObjectUtil.isEmpty(hazardousReleaseRate)) {
            throw new BusinessException("hazardous_release_rate not exits");
        }
        return hazardousReleaseRate.getRate();
    }

    private List<AccidentScenarioParamVO> listAccidentScenarioParamVO(ScenarioSelectionInfo scenarioSelectionInfo, String scenarioId, String scenarioCode) {
        switch (scenarioCode) {
            case ScenarioCode.S_ONE:
                return trashDischarge(scenarioSelectionInfo);
            case ScenarioCode.S_TWO:
                return fireOrexplosion(scenarioSelectionInfo, scenarioCode, scenarioId);
            case ScenarioCode.S_FIVE:
                return liquidDrip(scenarioSelectionInfo, scenarioId);
            default:
                return listScenarioParamForGasOrLiquidLeakage(scenarioSelectionInfo, scenarioCode, scenarioId);
        }
    }

    /**
     * query scenario parameter for S3/S4
     *
     * @param scenarioSelectionInfo
     * @param scenarioCode
     * @param scenarioId
     * @return
     */
    private List<AccidentScenarioParamVO> listScenarioParamForGasOrLiquidLeakage(ScenarioSelectionInfo scenarioSelectionInfo,
                                                                                 String scenarioCode, String scenarioId) {
        String paramCode = getRiskMaterialParamCode(scenarioCode);
        String riskMaterialParamValue = getRiskMaterialParamValue(scenarioSelectionInfo.getMatterName(), paramCode);
        boolean isStorageTank = judgeIsStorageTank(scenarioSelectionInfo);
        List<AccidentScenarioParamVO> scenarioParamVOS = listScenarioParam(scenarioId, isStorageTank);
        List<EntSurveyResultVO> surveyResultVOList = listEntSurveyResult(scenarioSelectionInfo);
        List<Integer> rowIndexList = listAllRowIndex(surveyResultVOList);
        EntSurveyResultVO entSurveyResultVO =
                getEntSurveyResultOfRow(scenarioSelectionInfo, surveyResultVOList, rowIndexList, isStorageTank);
        setScenarioParamValue(scenarioSelectionInfo, scenarioParamVOS, riskMaterialParamValue, entSurveyResultVO);
        return scenarioParamVOS;
    }

    private boolean judgeIsStorageTank(ScenarioSelectionInfo scenarioSelectionInfo) {
        String techniqueComponent = scenarioSelectionInfo.getTechniqueComponent();
        return PART_TYPE_BELONGING_TO_STORAGE_TANK.contains(techniqueComponent) ? true : false;
    }

    private void setScenarioParamValue(ScenarioSelectionInfo scenarioSelectionInfo,
                                       List<AccidentScenarioParamVO> scenarioParamVOS,
                                       String riskMaterialParamValue, EntSurveyResultVO entSurveyResultVO) {
        for (AccidentScenarioParamVO scenarioParam : scenarioParamVOS) {
            String code = scenarioParam.getCode();
            if (code.equals(ScenarioParamEnum.PART_TYPE.getCode())) {
                scenarioParam.setValue(scenarioSelectionInfo.getTechniqueComponent());
            } else if (code.equals(ScenarioParamEnum.MOLECULAR_WEIGHT_OF_MATTER.getCode())) {
                scenarioParam.setValue(riskMaterialParamValue);
            } else if (code.equals(ScenarioParamEnum.LIQUID_DENSITY.getCode())) {
                scenarioParam.setValue(riskMaterialParamValue);
            } else if (code.equals(ScenarioParamEnum.CONTAINER_PRESSURE.getCode())) {
                scenarioParam.setValue(entSurveyResultVO.getContainerPressure());
            } else if (code.equals(ScenarioParamEnum.GAS_TEMPERATURE.getCode())) {
                scenarioParam.setValue(String.valueOf(entSurveyResultVO.getTemperature() + BASE_TEMPERATURE));
            } else if (code.equals(ScenarioParamEnum.DETECTION_SYSTEM_CLASSIFICATION.getCode())) {
                if (entSurveyResultVO.getHasDetectionSystem().equals(BaseEnum.YES.getChineseName())) {
                    scenarioParam.setValue(entSurveyResultVO.getDetectionSystemClassification());
                } else {
                    scenarioParam.setValue(DETECTION_SYSTEM_DEFAULTED_VALUE);
                }
            } else if (code.equals(ScenarioParamEnum.ISOLATION_SYSTEM_CLASSIFICATION.getCode())) {
                if (entSurveyResultVO.getHasIsolationSystem().equals(BaseEnum.YES.getChineseName())) {
                    scenarioParam.setValue(entSurveyResultVO.getIsolationSystemClassification());
                } else {
                    scenarioParam.setValue(ISOLATION_SYSTEM_DEFAULTED_VALUE);
                }
            } else if (code.equals(ScenarioParamEnum.LIQUID_HEIGHT.getCode())) {
                scenarioParam.setValue(entSurveyResultVO.getHeight());
            } else if (code.equals(ScenarioParamEnum.INTERNAL_DIAMETER_OF_PIPELINE.getCode())) {
                scenarioParam.setValue(entSurveyResultVO.getInternalDiameterOfPipeline());
            }
        }
    }

    private EntSurveyResultVO getEntSurveyResultOfRow(ScenarioSelectionInfo scenarioSelectionInfo,
                                                      List<EntSurveyResultVO> surveyResultVOList,
                                                      List<Integer> rowIndexList, boolean isStorageTank) {
        List<EntSurveyResultVO> entSurveyResultVOS = new ArrayList<>();
        for (Integer rowIndex : rowIndexList) {
            EntSurveyResultVO surveyResult = new EntSurveyResultVO();
            for (EntSurveyResultVO entSurveyResult : surveyResultVOList) {
                if (entSurveyResult.getExcelRowIndex() == rowIndex) {
                    String surveyItemCode = entSurveyResult.getSurveyItemCode();
                    String result = entSurveyResult.getResult();
                    if (SURVEY_ITEM_CODE_OF_TECHNIQUE_CELL.contains(surveyItemCode)) {
                        surveyResult.setTechniqueCell(result);
                    } else if (SURVEY_ITEM_CODE_OF_MATERIAL_NAME.contains(surveyItemCode)) {
                        surveyResult.setMaterialName(result);
                    } else if (SURVEY_ITEM_CODE_OF_MATERIAL_TYPE.contains(surveyItemCode)) {
                        surveyResult.setMaterialTypeOptionName(result);
                    } else if (SURVEY_ITEM_CODE_OF_PIPE_INTERNAL_PRESSURE.contains(surveyItemCode)) {
                        if (!isStorageTank) {
                            surveyResult.setContainerPressure(result);
                        }
                    } else if (SURVEY_ITEM_CODE_OF_TANK_INTERNAL_PRESSURE.contains(surveyItemCode)) {
                        if (isStorageTank) {
                            surveyResult.setContainerPressure(result);
                        }
                    } else if (SURVEY_ITEM_CODE_OF_TANK_HEIGHT.contains(surveyItemCode)) {
                        if (isStorageTank) {
                            surveyResult.setHeight(result);
                        }
                    } else if (SURVEY_ITEM_CODE_OF_TANK_INTERNAL_TEMPERATURE.contains(surveyItemCode)) {
                        if (isStorageTank) {
                            surveyResult.setTemperature(Double.valueOf(result));
                        }
                    } else if (SURVEY_ITEM_CODE_OF_PIPE_INTERNAL_TEMPERATURE.contains(surveyItemCode)) {
                        if (!isStorageTank) {
                            surveyResult.setTemperature(Double.valueOf(result));
                        }
                    } else if (SURVEY_ITEM_CODE_OF_HAS_DETECTION_SYSTEM.contains(surveyItemCode)) {
                        surveyResult.setHasDetectionSystem(result);
                    } else if (SURVEY_ITEM_CODE_OF_HAS_ISOLATION_SYSTEM.contains(surveyItemCode)) {
                        surveyResult.setHasIsolationSystem(result);
                    } else if (SURVEY_ITEM_CODE_OF_DETECTION_SYSTEM.contains(surveyItemCode)) {
                        surveyResult.setDetectionSystemClassification(result);
                    } else if (SURVEY_ITEM_CODE_OF_ISOLATION_SYSTEM.contains(surveyItemCode)) {
                        surveyResult.setIsolationSystemClassification(result);
                    } else if (SURVEY_ITEM_CODE_OF_PIPE_INTERNAL_DIAMETER.contains(surveyItemCode)) {
                        if (!isStorageTank) {
                            surveyResult.setInternalDiameterOfPipeline(result);
                            surveyResult.setHeight(String.valueOf(Double.valueOf(result) / 1000.0));
                        }
                    } else if (SURVEY_ITEM_CODE_OF_EMISSIONS.contains(surveyItemCode)) {
                        surveyResult.setEmission(Double.valueOf(result));
                    } else if (SURVEY_ITEM_CODE_OF_PIPE_VALVE_NUMBER.contains(surveyItemCode)) {
                        if (!isStorageTank) {
                            surveyResult.setPipeValveNumber(Integer.valueOf(result));
                        }
                    } else if (SURVEY_ITEM_CODE_OF_PIPE_LENGTH.contains(surveyItemCode)) {
                        if (!isStorageTank) {
                            surveyResult.setPipeLength(Double.valueOf(result));
                        }
                    } else if (SURVEY_ITEM_CODE_OF_PIPE_LIFE.contains(surveyItemCode)) {
                        if (!isStorageTank) {
                            surveyResult.setPipeLife(Double.valueOf(result));
                        }
                    }
                }
            }
            if (!ObjectUtil.isEmpty(surveyResult)) {
                entSurveyResultVOS.add(surveyResult);
            }
        }
        String techniqueCell = scenarioSelectionInfo.getTechniqueCell();
        String matterType = scenarioSelectionInfo.getMatterType();
        String matterName = scenarioSelectionInfo.getMatterName();
        for (EntSurveyResultVO entSurveyResultVO : entSurveyResultVOS) {
            if (techniqueCell.equals(entSurveyResultVO.getTechniqueCell())
                    && matterType.equals(entSurveyResultVO.getMaterialTypeOptionName())
                    && matterName.equals(entSurveyResultVO.getMaterialName())) {
                return entSurveyResultVO;
            }
        }
        throw new BusinessException("No matching survey results");
    }

    private List<EntSurveyResultVO> listEntSurveyResult(ScenarioSelectionInfo scenarioSelectionInfo) {
        List<String> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(ExcelSheetName.TableA0.getCode());
        categoryCodeList.add(ExcelSheetName.TableA1.getCode());
        EntSurveyResultQueryParam queryParam = new EntSurveyResultQueryParam();
        queryParam.setEnterpriseId(scenarioSelectionInfo.getEntId());
        queryParam.setCategoryCodeList(categoryCodeList);
        return entSurveyResultMapper.listEntSurveyResult(queryParam);
    }

    private String getRiskMaterialParamValue(String materialName, String paramCode) {
        if (!ObjectUtil.isEmpty(paramCode)) {
            RiskMaterialParamValueQueryParam queryParam = new RiskMaterialParamValueQueryParam();
            queryParam.setMaterialName(materialName);
            queryParam.setParamCode(paramCode);
            return riskMaterialParamValueMapper.getParamValue(queryParam);
        }
        return null;
    }

    private List<AccidentScenarioParamVO> listScenarioParam(String scenarioId, boolean isStorageTank) {
        List<String> pipeParamCodeList = Arrays.asList(ScenarioParamEnum.INTERNAL_DIAMETER_OF_PIPELINE.getCode(),
                ScenarioParamEnum.PIPE_LENGTH.getCode(), ScenarioParamEnum.PIPE_VALVE_NUMBER.getCode(),
                ScenarioParamEnum.PIPE_LIFE.getCode());
        AccidentScenarioParamQueryParam queryParam = new AccidentScenarioParamQueryParam();
        queryParam.setScenarioId(scenarioId);
        queryParam.setIsStorageTank(isStorageTank);
        queryParam.setPipeParamCodeList(pipeParamCodeList);
        return accidentScenarioParamMapper.listScenarioParam(queryParam);
    }

    private String getRiskMaterialParamCode(String scenarioCode) {
        if (ScenarioEnum.S_THREE.getCode().equals(scenarioCode)) {
            return ParamEnum.MOLECULAR_WEIGHT.getCode();
        } else if (ScenarioEnum.S_FOUR.getCode().equals(scenarioCode)) {
            return ParamEnum.DENSITY.getCode();
        } else if (ScenarioEnum.S_TWO.getCode().equals(scenarioCode)) {
            return ParamEnum.LC50.getCode();
        }
        return null;
    }

    private String getScenarioCode(String scenarioId) {
        AccidentScenario accidentScenario = accidentScenarioMapper.selectOne(new QueryWrapper<AccidentScenario>()
                .eq("id", scenarioId)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        return accidentScenario.getCode();
    }

    private ScenarioSelectionInfo getScenarioSelectionInfo(String scenarioSelectionInfoId) {
        return scenarioSelectionInfoMapper.selectOne(
                new QueryWrapper<ScenarioSelectionInfo>()
                        .eq("id", scenarioSelectionInfoId)
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private List<Integer> listAllRowIndex(List<EntSurveyResultVO> surveyResultVOList) {
        List<Integer> rowIndexList = new ArrayList<>();
        for (EntSurveyResultVO entSurveyResultVO : surveyResultVOList) {
            Integer excelRowIndex = entSurveyResultVO.getExcelRowIndex();
            if (!rowIndexList.contains(excelRowIndex)) {
                rowIndexList.add(entSurveyResultVO.getExcelRowIndex());
            }
        }
        return rowIndexList;
    }

    /**
     * trash discharge
     *
     * @author fujl
     */
    public List<AccidentScenarioParamVO> trashDischarge(ScenarioSelectionInfo scenarioSelectionInfo) {
        AccidentScenario accidentScenario = getAccidentScenario(ScenarioEnum.S_ONE.getCode());
        List<AccidentScenarioParamVO> accidentScenarioParamList = getAccidentScenarioParams(accidentScenario);
        String entId = scenarioSelectionInfo.getEntId();
        for (AccidentScenarioParamVO accidentScenarioParam : accidentScenarioParamList) {
            switch (accidentScenarioParam.getCode()) {
                case ScenarioCode.TWD:
                    //Pollutant discharge time
                    String sValue = getValue(entId, Arrays.asList("S156"));
                    double twd;
                    if (ObjectUtil.isEmpty(sValue)) {
                        twd = 1;
                    } else {
                        twd = 1 - (Double.parseDouble(sValue) / 365);
                    }
                    accidentScenarioParam.setValue(twd + "");
                    break;
                case ScenarioCode.QWD:
                    List<EntSurveyResultVO> surveyResultVOList = listEntSurveyResult(scenarioSelectionInfo);
                    List<Integer> rowIndexList = listAllRowIndex(surveyResultVOList);
                    EntSurveyResultVO entSurveyResultVO =
                            getEntSurveyResultOfRow(scenarioSelectionInfo, surveyResultVOList, rowIndexList, false);
                    String qwd = entSurveyResultVO.getEmission() + "";
                    accidentScenarioParam.setValue(qwd);
                    break;
                default:
                    break;
            }
        }
        return accidentScenarioParamList;
    }

    /**
     * @author fujl
     */
    public List<AccidentScenarioParamVO> fireOrexplosion(ScenarioSelectionInfo scenarioSelectionInfo,
                                                         String scenarioCode, String scenarioId) {
        List<AccidentScenarioParamVO> accidentScenarioParamList = listScenarioParam(scenarioId, false);
        Double onlineContent = scenarioSelectionInfo.getOnlineContent();
        String paramCode = getRiskMaterialParamCode(scenarioCode);
        String riskMaterialParamValue = getRiskMaterialParamValue(scenarioSelectionInfo.getMatterName(), paramCode);
        for (AccidentScenarioParamVO accidentScenarioParam : accidentScenarioParamList) {
            switch (accidentScenarioParam.getCode()) {
                case ScenarioCode.Q_ONE:
                    accidentScenarioParam.setValue(String.valueOf(onlineContent));
                    break;
                case ScenarioCode.LC_FIVE_ZERO:
                    accidentScenarioParam.setValue(riskMaterialParamValue);
                    break;
                case ScenarioCode.R_ONE:
                    String r1Value = getR1Value(onlineContent, Double.valueOf(riskMaterialParamValue));
                    accidentScenarioParam.setValue(r1Value);
                    break;
                default:
                    break;
            }
        }
        return accidentScenarioParamList;
    }

    /**
     * @author fujl
     */
    public List<AccidentScenarioParamVO> liquidDrip(ScenarioSelectionInfo scenarioSelectionInfo, String scenarioId) {
        boolean isStorageTank = judgeIsStorageTank(scenarioSelectionInfo);
        List<AccidentScenarioParamVO> accidentScenarioParamList = listScenarioParam(scenarioId, isStorageTank);
        List<EntSurveyResultVO> surveyResultVOList = listEntSurveyResult(scenarioSelectionInfo);
        List<Integer> rowIndexList = listAllRowIndex(surveyResultVOList);
        EntSurveyResultVO entSurveyResultVO =
                getEntSurveyResultOfRow(scenarioSelectionInfo, surveyResultVOList, rowIndexList, isStorageTank);
        for (AccidentScenarioParamVO accidentScenarioParam : accidentScenarioParamList) {
            switch (accidentScenarioParam.getCode()) {
                case ScenarioCode.L:
                    accidentScenarioParam.setValue(entSurveyResultVO.getPipeLength() + "");
                    break;
                case ScenarioCode.T:
                    accidentScenarioParam.setValue(entSurveyResultVO.getPipeLife() + "");
                    break;
                case ScenarioCode.N:
                    accidentScenarioParam.setValue(entSurveyResultVO.getPipeValveNumber() + "");
                    break;
                default:
                    break;
            }
        }
        return accidentScenarioParamList;
    }


    private String getValue(String entId, List<String> surveyItemId) {
        return entSurveyPlanService.getValue(entId, surveyItemId);
    }


    private AccidentScenario getAccidentScenario(String code) {
        return accidentScenarioService.getOne(new QueryWrapper<AccidentScenario>()
                .eq("code", code));
    }

    private List<AccidentScenarioParamVO> getAccidentScenarioParams(AccidentScenario accidentScenario) {
        AccidentScenarioParamQueryParam queryParam = new AccidentScenarioParamQueryParam();
        queryParam.setScenarioId(accidentScenario.getId());
        List<AccidentScenarioParamVO> accidentScenarioParams =
                accidentScenarioParamMapper.listScenarioParam(queryParam);
        return accidentScenarioParams;
    }

    private String getR1Value(Double q1, Double lc50Value) {
        HazardousReleaseRate hazardousReleaseRate = hazardousReleaseRateService.getOne(new QueryWrapper<HazardousReleaseRate>()
                .le("lower_limit_of_lc50", lc50Value)
                .gt("upper_limit_of_lc50", lc50Value)
                .lt("lower_limit_of_q1", q1)
                .ge("upper_limit_of_q1", q1)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (ObjectUtil.isEmpty(hazardousReleaseRate)) {
            throw new BusinessException("hazardous_release_rate not exits");
        }
        return hazardousReleaseRate.getRate();
    }

}
