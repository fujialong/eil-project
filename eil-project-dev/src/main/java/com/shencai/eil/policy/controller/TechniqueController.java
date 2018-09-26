package com.shencai.eil.policy.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.policy.model.TechniqueQueryParam;
import com.shencai.eil.policy.model.TechniqueVO;
import com.shencai.eil.policy.service.ITechniqueService;
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
@RequestMapping("/technique")
public class TechniqueController {

    @Autowired
    private ITechniqueService techniqueService;

    @ResponseBody
    @RequestMapping("/listTechnique")
    public Result listTechnique(TechniqueQueryParam queryParam) {
        List<TechniqueVO> techniqueList = techniqueService.listTechnique(queryParam);
        return Result.ok(techniqueList);
    }
}

