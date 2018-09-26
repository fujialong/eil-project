package com.shencai.eil.policy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.model.Result;
import com.shencai.eil.policy.model.PolicyParam;
import com.shencai.eil.policy.model.PolicyQueryParam;
import com.shencai.eil.policy.model.PolicyVO;
import com.shencai.eil.policy.service.IPolicyAdministrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhoujx on 2018/9/13.
 */
@Controller
@RequestMapping("/policyAdministrate")
public class PolicyAdministrateController {

    @Autowired
    private IPolicyAdministrateService policyAdministrateService;

    @ResponseBody
    @RequestMapping("/savePolicy")
    public Result savePolicy(@RequestBody PolicyParam param) {
        policyAdministrateService.savePolicy(param);
        return Result.ok();
    }

    @ResponseBody
    @RequestMapping("/pagePolicy")
    public Result pagePolicy(PolicyQueryParam queryParam) {
        Page<PolicyVO> policyPage = policyAdministrateService.pagePolicy(queryParam);
        return Result.ok(policyPage);
    }

    @ResponseBody
    @RequestMapping("/getPolicy")
    public Result getPolicy(PolicyQueryParam queryParam) {
        PolicyVO policy = policyAdministrateService.getPolicy(queryParam);
        return Result.ok(policy);
    }
}
