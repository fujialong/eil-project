package com.shencai.eil.scenario.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.scenario.model.AccidentScenarioResultParam;
import com.shencai.eil.scenario.service.IAccidentScenarioResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fanhj
 * @since 2018-10-12
 */
@Controller
@RequestMapping("/accidentScenarioResult")
public class AccidentScenarioResultController {

    @Autowired
    private IAccidentScenarioResultService accidentScenarioResultService;

    @ResponseBody
    @RequestMapping("calculateScenarioResult")
    public Result calculateScenarioResult(@RequestBody AccidentScenarioResultParam param) {
        double resultValue = accidentScenarioResultService.calculateScenarioResult(param);
        return Result.ok(resultValue);
    }

}

