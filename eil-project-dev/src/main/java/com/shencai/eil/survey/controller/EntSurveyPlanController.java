package com.shencai.eil.survey.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.policy.model.EnterpriseParam;
import com.shencai.eil.survey.model.EntSurveyPlanQueryParam;
import com.shencai.eil.survey.service.IEntSurveyFileService;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
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
 * @author fujl
 * @since 2018-09-27
 */
@Controller
@RequestMapping("/entSurveyPlan")
public class EntSurveyPlanController {
    @Autowired
    private IEntSurveyPlanService entSurveyPlanService;
    @Autowired
    private IEntSurveyFileService entSurveyFileService;

    /**
     * get survey info of enterprise
     * include enterprise status, risk level, need upgrade or not
     */
    @ResponseBody
    @RequestMapping("getEntSurveyInfo")
    public Result getEntSurveyInfo(String enterpriseId) {
        return Result.ok(entSurveyPlanService.getEntSurveyInfo(enterpriseId));
    }

    /**
     * upgrade survey
     */
    @ResponseBody
    @RequestMapping("surveyUpgrade")
    public Result surveyUpgrade(@RequestBody EnterpriseParam param) {
        entSurveyPlanService.surveyUpgrade(param);
        return Result.ok();
    }

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

    @ResponseBody
    @RequestMapping("getFastGradingResult")
    public Result getFastGradingResult(String enterpriseId) {
        return Result.ok(entSurveyFileService.getFastGradingResult(enterpriseId));
    }

    @ResponseBody
    @RequestMapping("getExcelOfSurveyPlan")
    public Result getExcelOfSurveyPlan(String enterpriseId, String sourceType) {
        return Result.ok(entSurveyFileService.generateExcel(enterpriseId, sourceType));
    }

    @ResponseBody
    @RequestMapping("getSurveyUploadFileStatus")
    public Result getSurveyUploadFileStatus(String enterpriseId) {
       return Result.ok(entSurveyPlanService.getSurveyUploadFileStatus(enterpriseId));
    }

    @ResponseBody
    @RequestMapping("finishSurvey")
    public Result finishSurvey(String enterpriseId) {
        entSurveyPlanService.finishSurvey(enterpriseId);
        return Result.ok();
    }
}

