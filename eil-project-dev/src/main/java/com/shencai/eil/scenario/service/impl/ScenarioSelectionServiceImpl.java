package com.shencai.eil.scenario.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.ScenarioCode;
import com.shencai.eil.common.constants.ScenarioEnum;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.grading.entity.RiskMaterial;
import com.shencai.eil.grading.entity.RiskMaterialParamValue;
import com.shencai.eil.grading.service.IRiskMaterialParamValueService;
import com.shencai.eil.grading.service.IRiskMaterialService;
import com.shencai.eil.scenario.entity.AccidentScenario;
import com.shencai.eil.scenario.entity.HazardousReleaseRate;
import com.shencai.eil.scenario.mapper.AccidentScenarioParamMapper;
import com.shencai.eil.scenario.model.AccidentScenarioParamQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioParamVO;
import com.shencai.eil.scenario.service.IAccidentScenarioParamService;
import com.shencai.eil.scenario.service.IAccidentScenarioService;
import com.shencai.eil.scenario.service.IHazardousReleaseRateService;
import com.shencai.eil.scenario.service.IScenarioSelectionService;
import com.shencai.eil.survey.entity.EntSurveyPlan;
import com.shencai.eil.survey.entity.EntSurveyResult;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import com.shencai.eil.survey.service.IEntSurveyResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shencai.eil.common.utils.ObjectUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-16 15:08
 **/
@Service
@Slf4j
public class ScenarioSelectionServiceImpl implements IScenarioSelectionService {

    @Autowired
    private IEntSurveyPlanService entSurveyPlanService;

    @Autowired
    private IEntSurveyResultService entSurveyResultService;

    @Autowired
    private IRiskMaterialService riskMaterialService;

    @Autowired
    private IRiskMaterialParamValueService riskMaterialParamValueService;

    @Autowired
    private IHazardousReleaseRateService hazardousReleaseRateService;

    @Autowired
    private IAccidentScenarioParamService accidentScenarioParamService;

    @Autowired
    private IAccidentScenarioService accidentScenarioService;

    @Autowired
    private AccidentScenarioParamMapper accidentScenarioParamMapper;


    /**
     * trash discharge
     */

    @Override
    public List<AccidentScenarioParamVO> trashDischarge(String entId) {
        AccidentScenario accidentScenario = getAccidentScenario(ScenarioEnum.S_ONE.getCode());
        List<AccidentScenarioParamVO> accidentScenarioParamList = getAccidentScenarioParams(accidentScenario);
        for (AccidentScenarioParamVO accidentScenarioParam : accidentScenarioParamList) {
            switch (accidentScenarioParam.getCode()) {
                case ScenarioCode.TWD:
                    //Pollutant discharge time
                    double twd = 1 - (Double.parseDouble(getValue(entId, Arrays.asList("S156"))) / 365);
                    accidentScenarioParam.setValue(twd + "");
                    break;
                case ScenarioCode.QWD:
                    String qwd = getValue(entId, Arrays.asList("S252", "S258"));
                    accidentScenarioParam.setValue(qwd);
                    break;
                default:
                    break;
            }
        }
        return accidentScenarioParamList;
    }

    @Override
    public List<AccidentScenarioParamVO> fireOrexplosion(String entId) {
        AccidentScenario accidentScenario = getAccidentScenario(ScenarioEnum.S_TWO.getCode());
        List<AccidentScenarioParamVO> accidentScenarioParamList = getAccidentScenarioParams(accidentScenario);
        for (AccidentScenarioParamVO accidentScenarioParam : accidentScenarioParamList) {
            switch (accidentScenarioParam.getCode()) {
                case ScenarioCode.Q_ONE:
                    String q1 = getValue(entId, Arrays.asList("S251", "S257"));
                    accidentScenarioParam.setValue(q1);
                    break;
                case ScenarioCode.LC_FIVE_ZERO:
                    //TODO LC50 数据库中暂时没有这个
                    String lc50Value = getLc50Value();
                    accidentScenarioParam.setValue(lc50Value);
                    break;
                default:
                    break;
            }
        }
        for (AccidentScenarioParamVO accidentScenarioParam : accidentScenarioParamList) {
            if (ScenarioCode.R_ONE.equals(accidentScenarioParam.getCode())) {
         /*       Map<String, String> map = accidentScenarioParamList.stream().collect(Collectors.toMap(AccidentScenarioParamVO::getCode,
                        AccidentScenarioParamVO::getValue));*/
                Map<String, String> map = new HashMap<>(accidentScenarioParamList.size());
                for (AccidentScenarioParamVO accidentScenarioParamVO : accidentScenarioParamList) {
                    map.put(accidentScenarioParamVO.getCode(), accidentScenarioParamVO.getValue());
                }
                String r1Value = getR1Value(map.get(ScenarioCode.Q_ONE), map.get(ScenarioCode.LC_FIVE_ZERO));
                accidentScenarioParam.setValue(r1Value);
            }
        }
        return accidentScenarioParamList;
    }


    @Override
    public List<AccidentScenarioParamVO> liquidDrip(String entId) {
        AccidentScenario accidentScenario = getAccidentScenario(ScenarioEnum.S_FIVE.getCode());
        List<AccidentScenarioParamVO> accidentScenarioParamList = getAccidentScenarioParams(accidentScenario);
        for (AccidentScenarioParamVO accidentScenarioParam : accidentScenarioParamList) {
            switch (accidentScenarioParam.getCode()) {
                case ScenarioCode.L:
                    String l = getValue(entId, Arrays.asList("S5", "S55"));
                    accidentScenarioParam.setValue(l);
                    break;
                case ScenarioCode.T:
                    String t = getValue(entId, Arrays.asList("S259", "S261"));
                    accidentScenarioParam.setValue(t);
                    break;
                case ScenarioCode.N:
                    String n = getValue(entId, Arrays.asList("S4", "S54"));
                    accidentScenarioParam.setValue(n);
                    break;
                default:
                    break;
            }
        }
        return accidentScenarioParamList;
    }


    private String getValue(String entId, List<String> surveyItemId) {
        String value;
        List<EntSurveyPlan> surveyPlans = entSurveyPlanService.list(new QueryWrapper<EntSurveyPlan>()
                .eq("ent_id", entId).eq("valid", BaseEnum.VALID_YES.getCode())
                .in("survey_item_id", surveyItemId));

        System.out.println("surveyPlans:" + surveyPlans);

        if (CollectionUtils.isEmpty(surveyPlans)) {
            throw new BusinessException("ent_survey_plan table not exit " + surveyItemId + " survey item of this enterprise");
        }

        List<String> planIds = surveyPlans.stream().map(EntSurveyPlan::getId).collect(Collectors.toList());

        List<EntSurveyResult> surveyResults = entSurveyResultService.list(new QueryWrapper<EntSurveyResult>()
                .in("survey_plan_id", planIds).eq("valid", BaseEnum.VALID_YES.getCode()));

        if (CollectionUtils.isEmpty(surveyResults)) {
            throw new BusinessException("surveyResults not exit survey_plan_id" + planIds);
        }

        value = surveyResults.get(0).getResult();
        return value;
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

    private String getR1Value(String q1, String lc50Value) {
        String r1Value;
        HazardousReleaseRate hazardousReleaseRate = hazardousReleaseRateService.getOne(new QueryWrapper<HazardousReleaseRate>()
                .le("lower_limit_of_lc50", lc50Value)
                .ge("upper_limit_of_lc50", lc50Value)
                .le("lower_limit_of_q1", q1)
                .ge("upper_limit_of_q1", q1).eq("valid", BaseEnum.VALID_YES.getCode()));

        if (ObjectUtil.isEmpty(hazardousReleaseRate)) {
            throw new BusinessException("hazardous_release_rate not exits of q1=>," + q1 + ",lc50=>" + lc50Value);
        }
        r1Value = hazardousReleaseRate.getRate();
        return r1Value;
    }

    private String getLc50Value() {
        String lc50Value;
        String riskMateriaName = "LC50";
        List<RiskMaterial> riskMaterials = riskMaterialService.list(new QueryWrapper<RiskMaterial>()
                .eq("chinese_generic_name", riskMateriaName)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (CollectionUtils.isEmpty(riskMaterials)) {
            throw new BusinessException("risk_material table not exits materName LC50");
        }

        List<RiskMaterialParamValue> riskMaterialParamValues = riskMaterialParamValueService.list(new QueryWrapper<RiskMaterialParamValue>()
                .eq("material_id", riskMaterials.get(0).getId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));

        if (CollectionUtils.isEmpty(riskMaterialParamValues)) {
            throw new BusinessException("risk_material_param_value table not exits material_id value>>" + riskMaterials.get(0).getId());
        }
        lc50Value = riskMaterialParamValues.get(0).getValue();
        return lc50Value;
    }

}
