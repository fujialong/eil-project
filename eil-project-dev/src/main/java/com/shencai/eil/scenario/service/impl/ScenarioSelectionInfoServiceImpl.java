package com.shencai.eil.scenario.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.TemplateEnum;
import com.shencai.eil.common.utils.CommonsUtil;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.grading.mapper.RiskMaterialParamValueMapper;
import com.shencai.eil.grading.model.RiskMaterialParamValueQueryParam;
import com.shencai.eil.grading.model.RiskMaterialParamValueVO;
import com.shencai.eil.scenario.entity.AccidentScenarioParamValue;
import com.shencai.eil.scenario.entity.AccidentScenarioResult;
import com.shencai.eil.scenario.entity.ScenarioMappingMatter;
import com.shencai.eil.scenario.entity.ScenarioSelectionInfo;
import com.shencai.eil.scenario.mapper.ScenarioMappingMatterMapper;
import com.shencai.eil.scenario.mapper.ScenarioSelectionInfoMapper;
import com.shencai.eil.scenario.model.AccidentScenarioParamVO;
import com.shencai.eil.scenario.model.AccidentScenarioResultParam;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoQueryParam;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoVO;
import com.shencai.eil.scenario.service.IAccidentScenarioParamService;
import com.shencai.eil.scenario.service.IAccidentScenarioParamValueService;
import com.shencai.eil.scenario.service.IAccidentScenarioResultService;
import com.shencai.eil.scenario.service.IScenarioSelectionInfoService;
import com.shencai.eil.survey.mapper.EntSurveyResultMapper;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import com.shencai.eil.survey.model.EntSurveyResultVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-12
 */
@Service
public class ScenarioSelectionInfoServiceImpl extends ServiceImpl<ScenarioSelectionInfoMapper, ScenarioSelectionInfo> implements IScenarioSelectionInfoService {
    @Autowired
    private ScenarioSelectionInfoMapper scenarioSelectionInfoMapper;
    @Autowired
    private EntSurveyResultMapper entSurveyResultMapper;
    @Autowired
    private RiskMaterialParamValueMapper riskMaterialParamValueMapper;
    @Autowired
    private ScenarioMappingMatterMapper scenarioMappingMatterMapper;
    @Autowired
    private IAccidentScenarioParamService accidentScenarioParamService;
    @Autowired
    private IAccidentScenarioResultService accidentScenarioResultService;
    @Autowired
    private IAccidentScenarioParamValueService accidentScenarioParamValueService;

    private static final List<String> MAIN_PROCESS_LIST = Arrays.asList(new String[]{"S1", "S51"});
    private static final List<String> PART_TYPE_LIST = Arrays.asList(new String[]{"S2", "S52"});
    private static final List<String> COMPONENT_LOCATION_LIST = Arrays.asList(new String[]{"S3", "S53"});
    private static final List<String> MATERIAL_TYPE_LIST = Arrays.asList(new String[]{"S247", "S253"});
    private static final List<String> MATERIAL_NAME_LIST = Arrays.asList(new String[]{"S248", "S254"});
    private static final List<String> MATERIAL_PROPERTY_LIST = Arrays.asList(new String[]{"S249", "S255"});
    private static final List<String> ONLINE_LIST = Arrays.asList(new String[]{"S251", "S257"});
    private static final String EXPLOSIVE_CHARACTERISTIC = "B2";
    private static final String COMBUSTION_CHARACTERISTIC = "B3";
    private static final String MELTING_POINT = "C1";
    private static final String BOILING_POINT = "C2";
    private static final String PHASE_STATE = "C3";
    private static final String FLASHING_POINT = "C4";
    private static final String DENSITY = "C5";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initScenarioSelectionInfo(String enterpriseId, boolean isComputeScenario) {
        List<EntSurveyResultVO> resultList = listEntSurveyResult(enterpriseId);
        List<String> materialNameList = new ArrayList<>();
        HashMap<Integer, ScenarioSelectionInfo> infoMap = new HashMap<>();
        setScenarioInfoMap(enterpriseId, resultList, infoMap, materialNameList);
        List<RiskMaterialParamValueVO> materialParamValueList = listRiskMaterialParamValue(materialNameList);
        HashMap<String, ScenarioSelectionInfo> materialHashMap = classifyMaterialProperties(materialParamValueList);
        List<ScenarioSelectionInfo> infoList = setPropertiesToScenarioInfo(infoMap, materialHashMap);
        if (CollectionUtil.isNotEmpty(infoList)) {
            saveBatch(infoList);
        }
        if (isComputeScenario) {
            computeAndSaveEntScenarioResults(infoList);
        }
    }

    private void computeAndSaveEntScenarioResults(List<ScenarioSelectionInfo> infoList) {
        Map<String, String> scenarioMappingMatterMap = getScenarioMappingMatterMap();
        List<AccidentScenarioResult> scenarioResults = new ArrayList<>();
        List<AccidentScenarioParamValue> scenarioParamValues = new ArrayList<>();
        for (ScenarioSelectionInfo scenarioSelectionInfo : infoList) {
            String scenarioId = scenarioMappingMatterMap.get(scenarioSelectionInfo.getTechniqueCell() + scenarioSelectionInfo.getMatterName());
            List<AccidentScenarioParamVO> accidentScenarioParamVOS = accidentScenarioParamService.listScenarioParam(scenarioSelectionInfo, scenarioId);
            double scenarioResult = getScenarioResult(scenarioSelectionInfo, scenarioId, accidentScenarioParamVOS);
            buildAccidentScenarioResult(scenarioResults, scenarioSelectionInfo, scenarioId, scenarioResult);
            buildAccidentScenarioParamValues(scenarioParamValues, scenarioSelectionInfo, accidentScenarioParamVOS);
        }
        if (CollectionUtils.isNotEmpty(scenarioResults)) {
            accidentScenarioResultService.saveBatch(scenarioResults);
            accidentScenarioParamValueService.saveBatch(scenarioParamValues);
        }
    }

    private void buildAccidentScenarioParamValues(List<AccidentScenarioParamValue> scenarioParamValues,
                                                  ScenarioSelectionInfo scenarioSelectionInfo, List<AccidentScenarioParamVO> accidentScenarioParamVOS) {
        for (AccidentScenarioParamVO paramVO : accidentScenarioParamVOS) {
            AccidentScenarioParamValue paramValue = new AccidentScenarioParamValue();
            paramValue.setId(StringUtil.getUUID());
            paramValue.setInfoId(scenarioSelectionInfo.getId());
            paramValue.setScenarioParamId(paramVO.getId());
            paramValue.setScenarioParamValue(paramVO.getValue());
            paramValue.setCreateTime(DateUtil.getNowTimestamp());
            paramValue.setUpdateTime(DateUtil.getNowTimestamp());
            paramValue.setValid((Integer) BaseEnum.VALID_YES.getCode());

            scenarioParamValues.add(paramValue);
        }
    }

    private void buildAccidentScenarioResult(List<AccidentScenarioResult> scenarioResults, ScenarioSelectionInfo scenarioSelectionInfo,
                                             String scenarioId, double scenarioResult) {
        AccidentScenarioResult result = new AccidentScenarioResult();
        result.setId(StringUtil.getUUID());
        result.setInfoId(scenarioSelectionInfo.getId());
        result.setScenarioId(scenarioId);
        result.setScenarioResult(scenarioResult);
        result.setCreateTime(DateUtil.getNowTimestamp());
        result.setUpdateTime(DateUtil.getNowTimestamp());
        result.setValid((Integer) BaseEnum.VALID_YES.getCode());

        scenarioResults.add(result);
    }

    private double getScenarioResult(ScenarioSelectionInfo scenarioSelectionInfo, String scenarioId, List<AccidentScenarioParamVO> accidentScenarioParamVOS) {
        AccidentScenarioResultParam param = new AccidentScenarioResultParam();
        param.setScenarioId(scenarioId);
        param.setScenarioParamList(accidentScenarioParamVOS);
        param.setScenarioSelectionInfo(scenarioSelectionInfo);
        return accidentScenarioResultService.calculateScenarioResult(param);
    }

    private Map<String, String> getScenarioMappingMatterMap() {
        List<ScenarioMappingMatter> scenarioMappingMatterList = scenarioMappingMatterMapper.selectList(
                new QueryWrapper<ScenarioMappingMatter>()
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        Map<String, String> scenarioMappingMatterMap = new HashMap<>();
        for (ScenarioMappingMatter scenarioMappingMatter : scenarioMappingMatterList) {
            scenarioMappingMatterMap.put(scenarioMappingMatter.getTechniqueCell() + scenarioMappingMatter.getMatterName(), scenarioMappingMatter.getScenarioId());
        }
        return scenarioMappingMatterMap;
    }

    private List<ScenarioSelectionInfo> setPropertiesToScenarioInfo(HashMap<Integer, ScenarioSelectionInfo> infoMap, HashMap<String, ScenarioSelectionInfo> materialHashMap) {
        List<ScenarioSelectionInfo> infoList = new ArrayList<>();
        Iterator iterator = infoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ScenarioSelectionInfo info = (ScenarioSelectionInfo) entry.getValue();
            ScenarioSelectionInfo infoProperty = materialHashMap.get(info.getMatterName());
            if (ObjectUtil.isNotNull(infoProperty)) {
                CommonsUtil.cloneObj(infoProperty, info);
            }
            infoList.add(info);
        }
        return infoList;
    }

    private HashMap<String, ScenarioSelectionInfo> classifyMaterialProperties(List<RiskMaterialParamValueVO> materialParamValueList) {
        HashMap<String, ScenarioSelectionInfo> materialHashMap = new HashMap<>();
        for (int i = 0; i < materialParamValueList.size(); i++) {
            RiskMaterialParamValueVO valueVO = materialParamValueList.get(i);
            ScenarioSelectionInfo info;
            if (materialHashMap.containsKey(valueVO.getMaterialName())) {
                info = materialHashMap.get(valueVO.getMaterialName());
            } else {
                info = new ScenarioSelectionInfo();
                materialHashMap.put(valueVO.getMaterialName(), info);
            }
            if (EXPLOSIVE_CHARACTERISTIC.equals(valueVO.getParamCode())) {
                info.setExplosiveCharacteristic(valueVO.getParamValue());
            } else if (COMBUSTION_CHARACTERISTIC.equals(valueVO.getParamCode())) {
                info.setCombustionCharacteristic(valueVO.getParamValue());
            } else if (MELTING_POINT.equals(valueVO.getParamCode())) {
                info.setMeltingPoint(valueVO.getParamValue());
            } else if (BOILING_POINT.equals(valueVO.getParamCode())) {
                info.setBoilingPoint(valueVO.getParamValue());
            } else if (PHASE_STATE.equals(valueVO.getParamCode())) {
                info.setPhaseState(valueVO.getParamValue());
            } else if (FLASHING_POINT.equals(valueVO.getParamCode())) {
                info.setFlashingPoint(valueVO.getParamValue());
            } else if (DENSITY.equals(valueVO.getParamCode())) {
                info.setDensity(valueVO.getParamValue());
            }
        }
        return materialHashMap;
    }

    private List<RiskMaterialParamValueVO> listRiskMaterialParamValue(List<String> materialNameList) {
        RiskMaterialParamValueQueryParam paramValueQueryParam = new RiskMaterialParamValueQueryParam();
        paramValueQueryParam.setMaterialNameList(materialNameList);
        paramValueQueryParam.setTemplateCode(TemplateEnum.SCENARIO_SELECTION.getCode());
        return riskMaterialParamValueMapper.listRiskMaterialParamValue(paramValueQueryParam);
    }

    private void setScenarioInfoMap(String enterpriseId, List<EntSurveyResultVO> resultList, HashMap<Integer
            , ScenarioSelectionInfo> infoMap, List<String> materialNameList) {
        for (EntSurveyResultVO resultInfo : resultList) {
            ScenarioSelectionInfo info;
            if (infoMap.containsKey(resultInfo.getExcelRowIndex())) {
                info = infoMap.get(resultInfo.getExcelRowIndex());
            } else {
                info = new ScenarioSelectionInfo();
                info.setId(StringUtil.getUUID());
                info.setEntId(enterpriseId);
                info.setCreateTime(DateUtil.getNowTimestamp());
                info.setValid((Integer) BaseEnum.VALID_YES.getCode());
                infoMap.put(resultInfo.getExcelRowIndex(), info);
            }
            if (MAIN_PROCESS_LIST.contains(resultInfo.getSurveyItemCode())) {
                info.setTechniqueCell(resultInfo.getResult());
            } else if (PART_TYPE_LIST.contains(resultInfo.getSurveyItemCode())) {
                info.setTechniqueComponent(resultInfo.getResult());
            } else if (COMPONENT_LOCATION_LIST.contains(resultInfo.getSurveyItemCode())) {
                info.setComponentLocation(resultInfo.getResult());
            } else if (MATERIAL_NAME_LIST.contains(resultInfo.getSurveyItemCode())) {
                info.setMatterName(resultInfo.getResult());
                if (!materialNameList.contains(resultInfo.getResult())) {
                    materialNameList.add(resultInfo.getResult());
                }
            } else if (MATERIAL_TYPE_LIST.contains(resultInfo.getSurveyItemCode())) {
                info.setMatterType(resultInfo.getResult());
            } else if (MATERIAL_PROPERTY_LIST.contains(resultInfo.getSurveyItemCode())) {
                info.setMatterProperty(resultInfo.getResult());
            } else if (ONLINE_LIST.contains(resultInfo.getSurveyItemCode())) {
                info.setOnlineContent(Double.valueOf(resultInfo.getResult()));
            }
        }
    }

    private List<EntSurveyResultVO> listEntSurveyResult(String enterpriseId) {
        EntSurveyResultQueryParam queryParam = new EntSurveyResultQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        return entSurveyResultMapper.listEntSurveyResultForScenario(queryParam);
    }

    @Override
    public Page<ScenarioSelectionInfoVO> pageScenarioSelectionInfo(ScenarioSelectionInfoQueryParam queryParam) {
        Page<ScenarioSelectionInfoVO> page = new Page<>();
        page.setCurrent(queryParam.getCurrent());
        page.setSize(queryParam.getSize());
        page.setRecords(scenarioSelectionInfoMapper.pageScenarioSelectionInfo(page, queryParam));
        return page;
    }
}
