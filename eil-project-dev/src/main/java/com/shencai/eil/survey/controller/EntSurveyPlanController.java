package com.shencai.eil.survey.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.survey.model.EntSurveyPlanQueryParam;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fujl
 * @since 2018-09-27
 */
@Controller
@RequestMapping("/entSurveyPlan")
public class EntSurveyPlanController {
    @Autowired
    private IEntSurveyPlanService entSurveyPlanService;

    @ResponseBody
    @RequestMapping("pageBasicSurveyPlan")
    public Result pageBasicSurveyPlan(EntSurveyPlanQueryParam queryParam) {
        return Result.ok(entSurveyPlanService.pageBasicSurveyPlan(queryParam));
    }

    @ResponseBody
    @RequestMapping("pageIntensiveSurveyPlan")
    public Result pageIntensiveSurveyPlan(EntSurveyPlanQueryParam queryParam) {
        return Result.ok(entSurveyPlanService.pageIntensiveSurveyPlan(queryParam));
    }
}

