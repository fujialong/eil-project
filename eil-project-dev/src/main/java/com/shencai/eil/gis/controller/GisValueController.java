package com.shencai.eil.gis.controller;


import com.shencai.eil.gis.model.GisValueParam;
import com.shencai.eil.gis.model.GisValueVO;
import com.shencai.eil.gis.service.IGisValueService;
import com.shencai.eil.model.Result;
import com.shencai.eil.policy.service.IEnterpriseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 * About Gis Controller
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

    @Autowired
    private IEnterpriseInfoService enterpriseInfoService;

    /**
     * @param requestParam
     * @return Result
     */
    @RequestMapping("/saveGisValue")
    @ResponseBody
    public Result saveGisValue(GisValueParam requestParam) {
        gisValueService.saveGisValue(requestParam);

        return Result.ok();
    }


    /**
     * Gets the coordinates of the current enterprise
     *
     * @param entId
     * @return Result<GisValueVO>
     */
    @RequestMapping("/getBaseInfo")
    @ResponseBody
    public Result<GisValueVO> getBaseInfo(String entId) {

        return Result.ok(enterpriseInfoService.getEntLocation(entId));
    }

    /**
     * Gets the coordinates of all existing enterprises in the system
     * Excluding the self enterprise
     *
     * @param entId
     * @return Result<List < GisValueVO>>
     */
    @RequestMapping("/listOtherEntLocation")
    @ResponseBody
    public Result<List<GisValueVO>> listOtherEntLocation(String entId) {

        return Result.ok(enterpriseInfoService.listOtherEntLocation(entId));
    }


    /**
     * Gets the coordinates of all existing enterprises in the system
     *
     * @return Result<List < GisValueVO>>
     */
    @RequestMapping("/listAllEntLocation")
    @ResponseBody
    public Result<List<GisValueVO>> listAllEntLocation() {

        return Result.ok(enterpriseInfoService.listAllEntLocation());
    }

}

