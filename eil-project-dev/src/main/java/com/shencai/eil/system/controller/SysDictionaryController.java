package com.shencai.eil.system.controller;


import com.shencai.eil.model.Result;
import com.shencai.eil.system.model.DictionaryQueryParam;
import com.shencai.eil.system.model.DictionaryVO;
import com.shencai.eil.system.service.ISysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-15
 */
@Controller
@RequestMapping("/sysDictionary")
public class SysDictionaryController {

    @Autowired
    private ISysDictionaryService sysDictionaryService;

    @ResponseBody
    @RequestMapping("/listSysDictionary")
    public Result listSysDictionary(DictionaryQueryParam queryParam) {
        List<DictionaryVO> dictionaryList = sysDictionaryService.listSysDictionary(queryParam);
        return Result.ok(dictionaryList);
    }
}

