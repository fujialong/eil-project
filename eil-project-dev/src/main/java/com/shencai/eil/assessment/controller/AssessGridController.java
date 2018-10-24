package com.shencai.eil.assessment.controller;

import com.shencai.eil.assessment.model.AssessGirdParam;
import com.shencai.eil.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @ResponseBody
    @RequestMapping("/save")
    public Result save(@RequestBody AssessGirdParam param) {
        log.info("assessGrid/save method param:" + param);
        return Result.ok(param);
    }
}
