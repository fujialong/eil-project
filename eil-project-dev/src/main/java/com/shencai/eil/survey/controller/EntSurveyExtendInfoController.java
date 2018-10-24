package com.shencai.eil.survey.controller;

import com.shencai.eil.model.Result;
import com.shencai.eil.survey.model.EntSurveyExtendInfoVO;
import com.shencai.eil.survey.service.IEntSurveyExtendInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by fanhj on 2018/10/18.
 */
@Controller
@RequestMapping("/entSurveyExtendInfo")
public class EntSurveyExtendInfoController {
    @Autowired
    private IEntSurveyExtendInfoService entSurveyExtendInfoService;

    @ResponseBody
    @RequestMapping("getDisplayStatus")
    public Result getSurveyExtendItemDisplayStatus(String enterpriseId) {
        return Result.ok(entSurveyExtendInfoService.getSurveyExtendItemDisplayStatus(enterpriseId));
    }

    @ResponseBody
    @RequestMapping("saveSurveyExtendInfo")
    public Result saveSurveyExtendItem(@RequestBody EntSurveyExtendInfoVO info) {
        entSurveyExtendInfoService.saveSurveyExtendItem(info);
        return Result.ok();
    }

    @ResponseBody
    @RequestMapping("getSurveyExtendItem")
    public Result getSurveyExtendItem(String enterpriseId) {
        return Result.ok(entSurveyExtendInfoService.getSurveyExtendItem(enterpriseId));
    }
}
