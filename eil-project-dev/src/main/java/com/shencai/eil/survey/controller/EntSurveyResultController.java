package com.shencai.eil.survey.controller;

import com.shencai.eil.model.Result;
import com.shencai.eil.survey.service.IEntSurveyFileService;
import com.shencai.eil.survey.service.IFinalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhoujx on 2018/10/11.
 */
@Controller
@RequestMapping("/entSurveyResult")
public class EntSurveyResultController {

    @Autowired
    private IEntSurveyFileService entSurveyFileService;
    @Autowired
    private IFinalReportService finalReportService;

    @ResponseBody
    @RequestMapping("importSurveyResults")
    public Result importSurveyResults(String baseFileuploadId, String enterpriseId) {
        entSurveyFileService.importSurveyResults(baseFileuploadId, enterpriseId);
        return Result.ok();
    }

    @ResponseBody
    @RequestMapping("downloadBasicSurveyResult")
    public Result downloadBasicSurveyResult(String enterpriseId) {
        return Result.ok(entSurveyFileService.downloadBasicSurveyResult(enterpriseId));
    }

    @ResponseBody
    @RequestMapping("downloadIntensiveSurveyResult")
    public Result downloadIntensiveSurveyResult(String enterpriseId) {
        return Result.ok(entSurveyFileService.downloadIntensiveSurveyResult(enterpriseId));
    }

    @ResponseBody
    @RequestMapping("getFinalSurveyReport")
    public Result getFinalSurveyReport(String enterpriseId) throws Exception {
        return Result.ok(finalReportService.getFinalReport(enterpriseId));
    }
}
