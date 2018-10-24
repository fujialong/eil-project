package com.shencai.eil.scenario.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.StatusEnum;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.scenario.entity.AccidentScenario;
import com.shencai.eil.scenario.entity.AccidentScenarioParamValue;
import com.shencai.eil.scenario.entity.AccidentScenarioResult;
import com.shencai.eil.scenario.mapper.AccidentScenarioMapper;
import com.shencai.eil.scenario.mapper.AccidentScenarioParamValueMapper;
import com.shencai.eil.scenario.mapper.AccidentScenarioResultMapper;
import com.shencai.eil.scenario.model.*;
import com.shencai.eil.scenario.service.IAccidentScenarioParamValueService;
import com.shencai.eil.scenario.service.IAccidentScenarioResultService;
import com.shencai.eil.scenario.service.IAccidentScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
@Service
public class AccidentScenarioServiceImpl extends ServiceImpl<AccidentScenarioMapper, AccidentScenario> implements IAccidentScenarioService {

    @Autowired
    private AccidentScenarioMapper accidentScenarioMapper;
    @Autowired
    private IAccidentScenarioResultService accidentScenarioResultService;
    @Autowired
    private AccidentScenarioResultMapper accidentScenarioResultMapper;
    @Autowired
    private IAccidentScenarioParamValueService accidentScenarioParamValueService;
    @Autowired
    private AccidentScenarioParamValueMapper accidentScenarioParamValueMapper;
    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;

    @Override
    public List<AccidentScenarioVO> listAccidentScenario(AccidentScenarioQueryParam queryParam) {
        return accidentScenarioMapper.listAccidentScenario(queryParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEntAccidentScenarioResult(ScenarioResultParam param) {
        Date nowTime = DateUtil.getNowTimestamp();
        updateEnterpriseStatus(param, nowTime);
        String scenarioSelectionInfoId = param.getScenarioSelectionInfoId();
        List<AccidentScenarioResultParam> scenarioResultList = param.getScenarioResultList();
        List<AccidentScenarioResult> accidentScenarioResultsForInsert = new ArrayList<>();
        List<AccidentScenarioParamValue> scenarioParamValuesForInsert = new ArrayList<>();
        List<AccidentScenarioResult> accidentScenarioResultsForUpdate = new ArrayList<>();
        List<AccidentScenarioParamValue> scenarioParamValuesForUpdate = new ArrayList<>();
        for (AccidentScenarioResultParam resultParam : scenarioResultList) {
            String resultId = resultParam.getId();
            if (ObjectUtil.isEmpty(resultId)) {
                buildAccidentScenarioResultAndScenarioParamValuesForInsert(nowTime, scenarioSelectionInfoId,
                        accidentScenarioResultsForInsert, scenarioParamValuesForInsert, resultParam);
            } else {
                buildAccidentScenarioResultAndScenarioParamValuesForUpdate(nowTime, accidentScenarioResultsForUpdate,
                        scenarioParamValuesForUpdate, resultParam, resultId);
            }
        }
        insertAccidentScenarioResultAndScenarioParamValues(accidentScenarioResultsForInsert, scenarioParamValuesForInsert);
        updateAccidentScenarioResultAndScenarioParamValues(accidentScenarioResultsForUpdate, scenarioParamValuesForUpdate);
    }

    private void updateEnterpriseStatus(ScenarioResultParam param, Date nowTime) {
        AccidentScenarioResultQueryParam queryParam = new AccidentScenarioResultQueryParam();
        queryParam.setEnterpriseId(param.getEnterpriseId());
        int count = accidentScenarioResultMapper.countScenarioResult(queryParam);
        if (count == 0) {
            EnterpriseInfo updateParam = new EnterpriseInfo();
            updateParam.setId(param.getEnterpriseId());
            updateParam.setStatus(StatusEnum.SCENARIO_SELECTION.getCode());
            updateParam.setUpdateTime(nowTime);
            enterpriseInfoMapper.updateById(updateParam);
        }
    }

    private void updateAccidentScenarioResultAndScenarioParamValues(List<AccidentScenarioResult> accidentScenarioResultsForUpdate,
                                                                    List<AccidentScenarioParamValue> scenarioParamValuesForUpdate) {
        if (!CollectionUtils.isEmpty(accidentScenarioResultsForUpdate)) {
            accidentScenarioResultService.updateBatchById(accidentScenarioResultsForUpdate);
        }
        if (!CollectionUtils.isEmpty(scenarioParamValuesForUpdate)) {
            accidentScenarioParamValueService.updateBatchById(scenarioParamValuesForUpdate);
        }
    }

    private void insertAccidentScenarioResultAndScenarioParamValues(List<AccidentScenarioResult> accidentScenarioResultsForInsert,
                                                                    List<AccidentScenarioParamValue> scenarioParamValuesForInsert) {
        if (!CollectionUtils.isEmpty(accidentScenarioResultsForInsert)) {
            accidentScenarioResultService.saveBatch(accidentScenarioResultsForInsert);
        }
        if (!CollectionUtils.isEmpty(scenarioParamValuesForInsert)) {
            accidentScenarioParamValueService.saveBatch(scenarioParamValuesForInsert);
        }
    }

    private void buildAccidentScenarioResultAndScenarioParamValuesForUpdate(Date nowTime,
                                                                            List<AccidentScenarioResult> accidentScenarioResultsForUpdate,
                                                                            List<AccidentScenarioParamValue> scenarioParamValuesForUpdate,
                                                                            AccidentScenarioResultParam resultParam, String resultId) {
        AccidentScenarioResult result = new AccidentScenarioResult();
        result.setId(resultId);
        result.setScenarioResult(resultParam.getScenarioResult());
        result.setUpdateTime(nowTime);

        accidentScenarioResultsForUpdate.add(result);

        List<AccidentScenarioParamVO> scenarioParamList = resultParam.getScenarioParamList();
        for (AccidentScenarioParamVO paramVO : scenarioParamList) {
            AccidentScenarioParamValue paramValue = new AccidentScenarioParamValue();
            paramValue.setId(paramVO.getParamValueId());
            paramValue.setScenarioParamValue(paramVO.getValue());
            paramValue.setUpdateTime(nowTime);

            scenarioParamValuesForUpdate.add(paramValue);
        }
    }

    private void buildAccidentScenarioResultAndScenarioParamValuesForInsert(Date nowTime, String scenarioSelectionInfoId,
                                                                            List<AccidentScenarioResult> accidentScenarioResultsForInsert,
                                                                            List<AccidentScenarioParamValue> scenarioParamValuesForInsert,
                                                                            AccidentScenarioResultParam resultParam) {
        AccidentScenarioResult result = new AccidentScenarioResult();
        result.setId(StringUtil.getUUID());
        result.setInfoId(scenarioSelectionInfoId);
        result.setScenarioId(resultParam.getScenarioId());
        result.setScenarioResult(resultParam.getScenarioResult());
        result.setCreateTime(nowTime);
        result.setUpdateTime(nowTime);
        result.setValid((Integer) BaseEnum.VALID_YES.getCode());

        accidentScenarioResultsForInsert.add(result);

        List<AccidentScenarioParamVO> scenarioParamList = resultParam.getScenarioParamList();
        for (AccidentScenarioParamVO paramVO : scenarioParamList) {
            AccidentScenarioParamValue paramValue = new AccidentScenarioParamValue();
            paramValue.setId(StringUtil.getUUID());
            paramValue.setInfoId(scenarioSelectionInfoId);
            paramValue.setScenarioParamId(paramVO.getId());
            paramValue.setScenarioParamValue(paramVO.getValue());
            paramValue.setCreateTime(nowTime);
            paramValue.setUpdateTime(nowTime);
            paramValue.setValid((Integer) BaseEnum.VALID_YES.getCode());

            scenarioParamValuesForInsert.add(paramValue);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntAccidentScenarioResult(ScenarioResultParam param) {
        String scenarioSelectionInfoId = param.getScenarioSelectionInfoId();
        logicDeleteAccidentScenarioResult(param, scenarioSelectionInfoId);
        logicDeleteAccidentScenarioParamValue(param, scenarioSelectionInfoId);
    }

    @Override
    public List<AccidentScenarioResultVO> listEntAccidentScenarioResult(AccidentScenarioResultQueryParam queryParam) {
        List<AccidentScenarioResultVO> scenarioResults = listAccidentScenarioResult(queryParam);
        setScenarioParamList(queryParam, scenarioResults);
        return scenarioResults;
    }

    private void setScenarioParamList(AccidentScenarioResultQueryParam queryParam, List<AccidentScenarioResultVO> scenarioResults) {
        List<AccidentScenarioParamVO> scenarioParamValues =
                accidentScenarioParamValueMapper.listScenarioParamValue(queryParam);
        for (AccidentScenarioResultVO scenarioResultVO : scenarioResults) {
            String scenarioId = scenarioResultVO.getScenarioId();
            List<AccidentScenarioParamVO> scenarioParamList = new ArrayList<>();
            for (AccidentScenarioParamVO scenarioParamValue : scenarioParamValues) {
                if (scenarioId.equals(scenarioParamValue.getScenarioId())) {
                    scenarioParamList.add(scenarioParamValue);
                }
            }
            scenarioResultVO.setScenarioParamList(scenarioParamList);
        }
    }

    private List<AccidentScenarioResultVO> listAccidentScenarioResult(AccidentScenarioResultQueryParam queryParam) {
        return accidentScenarioResultMapper.listScenarioResult(queryParam);
    }

    private void logicDeleteAccidentScenarioParamValue(ScenarioResultParam param, String scenarioSelectionInfoId) {
        AccidentScenarioParamValue updateParam = new AccidentScenarioParamValue();
        updateParam.setValid((Integer) BaseEnum.VALID_NO.getCode());
        updateParam.setUpdateTime(DateUtil.getNowTimestamp());
        accidentScenarioParamValueService.update(updateParam, new QueryWrapper<AccidentScenarioParamValue>()
                .eq("info_id", scenarioSelectionInfoId)
                .in("scenario_param_id", param.getParamIdList())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private void logicDeleteAccidentScenarioResult(ScenarioResultParam param, String scenarioSelectionInfoId) {
        AccidentScenarioResult updateParam = new AccidentScenarioResult();
        updateParam.setValid((Integer) BaseEnum.VALID_NO.getCode());
        updateParam.setUpdateTime(DateUtil.getNowTimestamp());
        accidentScenarioResultService.update(updateParam, new QueryWrapper<AccidentScenarioResult>()
                .eq("info_id", scenarioSelectionInfoId)
                .eq("scenario_id", param.getScenarioId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }
}
