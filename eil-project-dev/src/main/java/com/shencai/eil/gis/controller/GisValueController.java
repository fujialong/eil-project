package com.shencai.eil.gis.controller;


import com.shencai.eil.gis.model.GisValueParam;
import com.shencai.eil.gis.service.IGisValueService;
import com.shencai.eil.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  About Gis Controller
 * </p>
 *
 * @author fujl
 * @since 2018-09-22
 */
@Controller
@RequestMapping("/gis")
public class GisValueController {

    @Autowired
    private IGisValueService gisValueService;

    @RequestMapping("/saveGisValue")
    @ResponseBody
    public Result saveGisValue(GisValueParam requestParam) {
        gisValueService.saveGisValue(requestParam);

        return Result.ok();
    }
}

