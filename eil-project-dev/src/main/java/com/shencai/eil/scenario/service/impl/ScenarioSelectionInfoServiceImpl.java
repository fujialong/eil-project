package com.shencai.eil.scenario.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.TemplateEnum;
import com.shencai.eil.common.utils.CommonsUtil;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.grading.mapper.RiskMaterialParamValueMapper;
import com.shencai.eil.grading.model.RiskMaterialParamValueQueryParam;
import com.shencai.eil.grading.model.RiskMaterialParamValueVO;
import com.shencai.eil.scenario.entity.ScenarioSelectionInfo;
import com.shencai.eil.scenario.mapper.ScenarioSelectionInfoMapper;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoQueryParam;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoVO;
import com.shencai.eil.scenario.service.IScenarioSelectionInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.survey.mapper.EntSurveyResultMapper;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import com.shencai.eil.survey.model.EntSurveyResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 *  服务实现类
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

    private static final String MAIN_PROCESS = "S1";
    private static final String PART_TYPE = "S2";
    private static final String COMPONENT_LOCATION = "S3";
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
    public void initScenarioSelectionInfo(String enterpriseId) {
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
    }

    private List<ScenarioSelectionInfo> setPropertiesToScenarioInfo(HashMap<Integer, ScenarioSelectionInfo> infoMap, HashMap<String, ScenarioSelectionInfo> materialHashMap) {
        List<ScenarioSelectionInfo> infoList = new ArrayList<>();
        Iterator iterator = infoMap.entrySet().iterator();
        while(iterator.hasNext()) {
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
        for (EntSurveyResultVO resultInfo: resultList) {
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
            if (MAIN_PROCESS.equals(resultInfo.getSurveyItemCode())) {
                info.setTechniqueCell(resultInfo.getResult());
            } else if (PART_TYPE.equals(resultInfo.getSurveyItemCode())) {
                info.setTechniqueComponent(resultInfo.getResult());
            } else if (COMPONENT_LOCATION.equals(resultInfo.getSurveyItemCode())) {
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
