package com.shencai.eil.grading.controller;

import com.shencai.eil.grading.model.GradingParam;
import com.shencai.eil.grading.model.GradingQueryParam;
import com.shencai.eil.grading.model.GradingVO;
import com.shencai.eil.grading.service.IGradingService;
import com.shencai.eil.grading.service.IGradingService2;
import com.shencai.eil.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutionException;

/**
 * Created by zhoujx on 2018/9/19.
 */
@Controller
@RequestMapping("/grading")
public class GradingController {

    @Autowired
    private IGradingService gradingService;

    @Autowired
    private IGradingService2 gradingService2;

    @ResponseBody
    @RequestMapping("/execute")
    public Result execute(GradingParam param) throws ExecutionException, InterruptedException {
        gradingService.execute(param);
        return Result.ok();
    }

    @ResponseBody
    @RequestMapping("/getGradingResult")
    public Result getGradingResult(GradingQueryParam queryParam) throws ExecutionException, InterruptedException {
        GradingVO gradingVO = gradingService2.getGradingResult2(queryParam);
        return Result.ok(gradingVO);
    }
}
