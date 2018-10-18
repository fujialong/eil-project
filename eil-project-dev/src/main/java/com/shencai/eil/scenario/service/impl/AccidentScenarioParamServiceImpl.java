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
import com.shencai.eil.scenario.entity.ScenarioSelectionInfo;
import com.shencai.eil.scenario.mapper.AccidentScenarioMapper;
import com.shencai.eil.scenario.mapper.AccidentScenarioParamMapper;
import com.shencai.eil.scenario.mapper.ScenarioSelectionInfoMapper;
import com.shencai.eil.scenario.model.AccidentScenarioParamQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioParamVO;
import com.shencai.eil.scenario.service.IAccidentScenarioParamService;
import com.shencai.eil.scenario.service.IScenarioSelectionService;
import com.shencai.eil.survey.constants.ExcelEnum;
import com.shencai.eil.survey.constants.ExcelSheetName;
import com.shencai.eil.survey.mapper.EntSurveyResultMapper;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import com.shencai.eil.survey.model.EntSurveyResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private IScenarioSelectionService IScenarioSelectionService;

    private static final double BASE_TEMPERATURE = 273.15;

    @Override
    public List<AccidentScenarioParamVO> listScenarioParam(AccidentScenarioParamQueryParam queryParam) {
        ScenarioSelectionInfo scenarioSelectionInfo = getScenarioSelectionInfo(queryParam.getScenarioSelectionInfoId());
        String scenarioCode = getScenarioCode(queryParam.getScenarioId());
        switch (scenarioCode) {
            case ScenarioCode.S_ONE:
                return IScenarioSelectionService.trashDischarge(scenarioSelectionInfo.getEntId());
            case ScenarioCode.S_TWO:
                return IScenarioSelectionService.fireOrexplosion(scenarioSelectionInfo.getEntId());
            case ScenarioCode.S_FIVE:
                return IScenarioSelectionService.liquidDrip(scenarioSelectionInfo.getEntId());
            default:
                return listScenarioParamForGasOrLiquidLeakage(scenarioSelectionInfo, scenarioCode, queryParam.getScenarioId());
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
        List<AccidentScenarioParamVO> scenarioParamVOS = listScenarioParam(scenarioId);
        List<EntSurveyResultVO> surveyResultVOList = listEntSurveyResult(scenarioSelectionInfo);
        List<Integer> rowIndexList = listAllRowIndex(surveyResultVOList);
        EntSurveyResultVO entSurveyResultVO =
                getEntSurveyResultOfRow(scenarioSelectionInfo, surveyResultVOList, rowIndexList);
        setScenarioParamValue(scenarioSelectionInfo, scenarioParamVOS, riskMaterialParamValue, entSurveyResultVO);
        return scenarioParamVOS;
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
                scenarioParam.setValue(entSurveyResultVO.getDetectionSystemClassification());
            } else if (code.equals(ScenarioParamEnum.ISOLATION_SYSTEM_CLASSIFICATION.getCode())) {
                scenarioParam.setValue(entSurveyResultVO.getIsolationSystemClassification());
            } else if (code.equals(ScenarioParamEnum.LIQUID_HEIGHT.getCode())) {
                scenarioParam.setValue(entSurveyResultVO.getHeight());
            } else if (code.equals(ScenarioParamEnum.INTERNAL_DIAMETER_OF_PIPELINE.getCode())) {
                scenarioParam.setValue(entSurveyResultVO.getInternalDiameterOfPipeline());
            }
        }
    }

    private EntSurveyResultVO getEntSurveyResultOfRow(ScenarioSelectionInfo scenarioSelectionInfo,
                                                      List<EntSurveyResultVO> surveyResultVOList,
                                                      List<Integer> rowIndexList) {
        List<EntSurveyResultVO> entSurveyResultVOS = new ArrayList<>();
        for (Integer rowIndex : rowIndexList) {
            EntSurveyResultVO surveyResult = new EntSurveyResultVO();
            for (EntSurveyResultVO entSurveyResult : surveyResultVOList) {
                if (entSurveyResult.getExcelRowIndex() == rowIndex) {
                    String surveyItemName = entSurveyResult.getSurveyItemName();
                    String result = entSurveyResult.getResult();
                    if (surveyItemName.contains(ExcelEnum.TECHNIQUE_CELL.getValue())) {
                        surveyResult.setTechniqueCell(result);
                    } else if (surveyItemName.contains(ExcelEnum.MATERIAL_NAME.getValue())) {
                        surveyResult.setMaterialName(result);
                    } else if (surveyItemName.contains(ExcelEnum.MATERIAL_TYPE.getValue())) {
                        surveyResult.setMaterialTypeOptionName(result);
                    } else if (surveyItemName.contains(ExcelEnum.CONTAINER_PRESSURE.getValue())) {
                        surveyResult.setContainerPressure(result);
                    } else if (surveyItemName.contains(ExcelEnum.TEMPERATURE.getValue())) {
                        surveyResult.setTemperature(Double.valueOf(result));
                    } else if (surveyItemName.contains(ExcelEnum.DETECTION_SYSTEM_CLASSIFICATION.getValue())) {
                        surveyResult.setDetectionSystemClassification(result);
                    } else if (surveyItemName.contains(ExcelEnum.ISOLATION_SYSTEM_CLASSIFICATION.getValue())) {
                        surveyResult.setIsolationSystemClassification(result);
                    } else if (surveyItemName.contains(ExcelEnum.HEIGHT.getValue())) {
                        surveyResult.setHeight(result);
                    } else if (surveyItemName.contains(ExcelEnum.PIPELINE_INTERNAL_DIAMETER.getValue())) {
                        surveyResult.setInternalDiameterOfPipeline(result);
                        surveyResult.setHeight(result);
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

    private List<AccidentScenarioParamVO> listScenarioParam(String scenarioId) {
        AccidentScenarioParamQueryParam queryParam = new AccidentScenarioParamQueryParam();
        queryParam.setScenarioId(scenarioId);
        return accidentScenarioParamMapper.listScenarioParam(queryParam);
    }

    private String getRiskMaterialParamCode(String scenarioCode) {
        if (ScenarioEnum.S_THREE.getCode().equals(scenarioCode)) {
            return ParamEnum.MOLECULAR_WEIGHT.getCode();
        } else if (ScenarioEnum.S_FOUR.getCode().equals(scenarioCode)) {
            return ParamEnum.DENSITY.getCode();
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

}
