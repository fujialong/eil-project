package com.shencai.eil.scenario.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoQueryParam;
import com.shencai.eil.scenario.service.IScenarioSelectionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fanhj
 * @since 2018-10-12
 */
@Controller
@RequestMapping("/scenarioSelection")
public class ScenarioSelectionController {
    @Autowired
    private IScenarioSelectionInfoService scenarioSelectionInfoService;

    @ResponseBody
    @RequestMapping("pageScenarioSelectionInfo")
    public Result pageScenarioSelectionInfo(ScenarioSelectionInfoQueryParam queryParam) {
        return Result.ok(scenarioSelectionInfoService.pageScenarioSelectionInfo(queryParam));
    }
}

