package com.shencai.eil.survey.controller;

import com.shencai.eil.model.Result;
import com.shencai.eil.survey.service.IEntSurveyFileService;
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

    @ResponseBody
    @RequestMapping("importSurveyResults")
    public Result importSurveyResults(String baseFileuploadId, String enterpriseId) {
        entSurveyFileService.importSurveyResults(baseFileuploadId, enterpriseId);
        return Result.ok();
    }
}
