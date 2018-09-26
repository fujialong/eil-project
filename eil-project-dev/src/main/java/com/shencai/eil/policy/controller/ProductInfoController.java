package com.shencai.eil.policy.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.policy.model.ProductInfoParam;
import com.shencai.eil.policy.model.ProductInfoQueryParam;
import com.shencai.eil.policy.model.ProductVO;
import com.shencai.eil.policy.service.IProductInfoService;
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
@RequestMapping("/productInfo")
public class ProductInfoController {

    @Autowired
    private IProductInfoService productInfoService;

    @ResponseBody
    @RequestMapping("/saveProductInfo")
    public Result saveProductInfo(ProductInfoParam param) {
        ProductVO productVO = productInfoService.saveProductInfo(param);
        return Result.ok(productVO);
    }

    @ResponseBody
    @RequestMapping("/listProduct")
    public Result listProduct(ProductInfoQueryParam queryParam) {
        List<ProductVO> productList = productInfoService.listProduct(queryParam);
        return Result.ok(productList);
    }
}

