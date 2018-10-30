package com.shencai.eil.assessment.controller;

import com.shencai.eil.assessment.model.AssessGirdParam;
import com.shencai.eil.assessment.model.AssessGridVO;
import com.shencai.eil.assessment.service.IAssessGirdService;
import com.shencai.eil.assessment.service.IWaterModelCalculateService;
import com.shencai.eil.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-24 10:58
 **/
@Controller
@RequestMapping("/assessGrid")
@Slf4j
public class AssessGridController {

    @Autowired
    private IAssessGirdService assessGirdService;
    @Autowired
    private IWaterModelCalculateService waterModelCalculateService;

    @ResponseBody
    @RequestMapping("/save")
    public Result save(@RequestBody List<AssessGirdParam> paramList) {
        assessGirdService.saveAssessGrid(paramList);
        waterModelCalculateService.saveToGridConcentration(paramList.get(0).getEntId());
        return Result.ok(paramList);
    }

    @ResponseBody
    @RequestMapping("/gisBaseInfo")
    public Result<AssessGridVO> gisBaseInfo(String entId) {
        return Result.ok(assessGirdService.gisBaseInfo(entId));
    }

}
