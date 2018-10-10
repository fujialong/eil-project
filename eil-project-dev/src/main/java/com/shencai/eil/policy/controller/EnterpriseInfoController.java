package com.shencai.eil.policy.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.model.PolicyQueryParam;
import com.shencai.eil.policy.service.IEnterpriseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Controller
@RequestMapping("/enterpriseInfo")
public class EnterpriseInfoController {

    @Autowired
    private IEnterpriseInfoService enterpriseInfoService;

    @ResponseBody
    @RequestMapping("/saveEnterpriseInfo")
    public Result saveEnterpriseInfo(EnterpriseInfo param) {
        enterpriseInfoService.saveEnterpriseInfo(param);
        return Result.ok();
    }

    @ResponseBody
    @RequestMapping("/getEnterpriseInfo")
    public Result getEnterpriseInfo(PolicyQueryParam queryParam) {
        EnterpriseInfo enterpriseInfo = enterpriseInfoService.getEnterpriseInfo(queryParam);
        return Result.ok(enterpriseInfo);
    }

}