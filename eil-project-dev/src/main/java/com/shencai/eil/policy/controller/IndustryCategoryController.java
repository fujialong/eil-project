package com.shencai.eil.policy.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.policy.model.IndustryCategoryQueryParam;
import com.shencai.eil.policy.model.IndustryCategoryVO;
import com.shencai.eil.policy.service.IIndustryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Controller
@RequestMapping("/industryCategory")
public class IndustryCategoryController {

    @Autowired
    private IIndustryCategoryService industryCategoryService;

    @ResponseBody
    @RequestMapping("/listIndustryCategory")
    public Result listIndustryCategory(IndustryCategoryQueryParam queryParam) {
        List<IndustryCategoryVO> industryCategoryList = industryCategoryService.listIndustryCategory(queryParam);
        return Result.ok(industryCategoryList);
    }
}

