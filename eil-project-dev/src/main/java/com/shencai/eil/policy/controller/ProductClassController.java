package com.shencai.eil.policy.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.policy.model.ProductClassQueryParam;
import com.shencai.eil.policy.model.ProductClassVO;
import com.shencai.eil.policy.service.IProductClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Controller
@RequestMapping("/productClass")
public class ProductClassController {

    @Autowired
    private IProductClassService productClassService;

    @ResponseBody
    @RequestMapping("/listProductClass")
    public Result listProductClass(ProductClassQueryParam queryParam) {
        List<ProductClassVO> productClassList = productClassService.listProductClass(queryParam);
        return Result.ok(productClassList);
    }
}

