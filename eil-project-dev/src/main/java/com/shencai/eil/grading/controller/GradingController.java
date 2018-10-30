package com.shencai.eil.grading.controller;

import com.shencai.eil.grading.model.GradingQueryParam;
import com.shencai.eil.grading.model.GradingVO;
import com.shencai.eil.grading.service.IGradingService;
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

    @ResponseBody
    @RequestMapping("/getGradingResult")
    public Result getGradingResult(GradingQueryParam queryParam) throws ExecutionException, InterruptedException {
        GradingVO gradingVO = gradingService.getGradingResult(queryParam);
        return Result.ok(gradingVO);
    }
}
