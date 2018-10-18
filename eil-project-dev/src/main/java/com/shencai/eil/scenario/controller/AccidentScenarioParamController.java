package com.shencai.eil.scenario.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.scenario.model.AccidentScenarioParamQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioParamVO;
import com.shencai.eil.scenario.service.IAccidentScenarioParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
@Controller
@RequestMapping("/accidentScenarioParam")
public class AccidentScenarioParamController {

    @Autowired
    private IAccidentScenarioParamService accidentScenarioParamService;

    @ResponseBody
    @RequestMapping("listScenarioParam")
    public Result listScenarioParam(AccidentScenarioParamQueryParam queryParam) {
        List<AccidentScenarioParamVO> scenarioParamVOS = accidentScenarioParamService.listScenarioParam(queryParam);
        return Result.ok(scenarioParamVOS);
    }



}

