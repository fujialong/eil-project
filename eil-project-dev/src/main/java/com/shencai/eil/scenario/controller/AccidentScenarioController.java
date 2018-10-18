package com.shencai.eil.scenario.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.scenario.model.AccidentScenarioQueryParam;
import com.shencai.eil.scenario.service.IAccidentScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fanhj
 * @since 2018-10-12
 */
@Controller
@RequestMapping("/accidentScenario")
public class AccidentScenarioController {

    @Autowired
    private IAccidentScenarioService accidentScenarioService;

    @ResponseBody
    @RequestMapping("listAccidentScenario")
    public Result listAccidentScenario(AccidentScenarioQueryParam queryParam) {
        return Result.ok(accidentScenarioService.listAccidentScenario(queryParam));
    }
}

