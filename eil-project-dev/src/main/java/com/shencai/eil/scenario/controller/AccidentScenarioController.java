package com.shencai.eil.scenario.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.scenario.model.AccidentScenarioQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioResultQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioResultVO;
import com.shencai.eil.scenario.model.ScenarioResultParam;
import com.shencai.eil.scenario.service.IAccidentScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
@Controller
@RequestMapping("/accidentScenario")
public class AccidentScenarioController {

    @Autowired
    private IAccidentScenarioService scenarioService;

    @ResponseBody
    @RequestMapping("listAccidentScenario")
    public Result listAccidentScenario(AccidentScenarioQueryParam queryParam) {
        return Result.ok(scenarioService.listAccidentScenario(queryParam));
    }

    @ResponseBody
    @RequestMapping("saveEntAccidentScenarioResult")
    public Result saveEntAccidentScenarioResult(@RequestBody ScenarioResultParam param) {
        scenarioService.saveEntAccidentScenarioResult(param);
        return Result.ok();
    }

    @ResponseBody
    @RequestMapping("deleteEntAccidentScenarioResult")
    public Result deleteEntAccidentScenarioResult(@RequestBody ScenarioResultParam param) {
        scenarioService.deleteEntAccidentScenarioResult(param);
        return Result.ok();
    }

    @ResponseBody
    @RequestMapping("listEntAccidentScenarioResult")
    public Result listEntAccidentScenarioResult(AccidentScenarioResultQueryParam queryParam) {
        List<AccidentScenarioResultVO> scenarioResults = scenarioService.listEntAccidentScenarioResult(queryParam);
        return Result.ok(scenarioResults);
    }

}

