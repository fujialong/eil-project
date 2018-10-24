package com.shencai.eil.assessment.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoQueryParam;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoVO;
import com.shencai.eil.assessment.service.IEntDiffusionModelInfoService;
import com.shencai.eil.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-10-20
 */
@Controller
@RequestMapping("/entDiffusionModelInfo")
public class EntDiffusionModelInfoController {

    @Autowired
    private IEntDiffusionModelInfoService entDiffusionModelInfoService;

    @ResponseBody
    @RequestMapping("/matchingWaterQualityModel")
    public Result matchingWaterQualityModel(String enterpriseId) {
        entDiffusionModelInfoService.matchingWaterQualityModel(enterpriseId);
        return Result.ok();
    }

    @ResponseBody
    @RequestMapping("/pageDiffusionModelInfo")
    public Result pageDiffusionModelInfo(EntDiffusionModelInfoQueryParam queryParam) {
        Page<EntDiffusionModelInfoVO> entDiffusionModelInfoPage =
                entDiffusionModelInfoService.pageDiffusionModelInfo(queryParam);
        return Result.ok(entDiffusionModelInfoPage);
    }

}

