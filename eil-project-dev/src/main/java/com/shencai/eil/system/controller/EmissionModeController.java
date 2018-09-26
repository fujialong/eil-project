package com.shencai.eil.system.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.system.model.EmissionModeQueryParam;
import com.shencai.eil.system.model.EmissionModeVO;
import com.shencai.eil.system.service.IEmissionModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-18
 */
@Controller
@RequestMapping("/emissionMode")
public class EmissionModeController {

    @Autowired
    private IEmissionModeService emissionModeService;

    @ResponseBody
    @RequestMapping("/listEmissionMode")
    public Result listEmissionMode(EmissionModeQueryParam queryParam) {
        List<EmissionModeVO> emissionModeList = emissionModeService.listEmissionMode(queryParam);
        return Result.ok(emissionModeList);
    }
}

