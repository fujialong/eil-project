package com.shencai.eil.scenario.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.scenario.entity.OsmoticCoefficient;
import com.shencai.eil.scenario.service.IOsmoticCoefficientService;
import com.shencai.eil.scenario.service.ISoilModelCalculateService;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-21 14:44
 **/
@Service
public class SoilModelCalculateServiceImpl implements ISoilModelCalculateService {

    public static final String HEAVY_METAL = "heavyMetal";
    public static final String ORGANISM = "organism";
    @Autowired
    private IOsmoticCoefficientService osmoticCoefficientService;

    @Autowired
    private IEntSurveyPlanService entSurveyPlanService;
    private static final Double SANDY_SOIL_VALUE = 0.0126;
    private static final Double CLAYEY_SOIL_VALUE = 2 * Math.pow(10, -10);
    private static final Double LOAM_VALUE = 2 * Math.pow(10, -10);
    private double soilMoisture, dij, density, kd, k, uc, us, t;
    private String xi, xj;

    public void getSoliModelConstants(String entId, String soilType, String compoundType, String x, String y) {
        initParams(entId, soilType, compoundType, x, y);
    }

    /**
     * @param entId
     * @param soilType
     * @param compoundType
     * @param x
     * @param y
     */
    public void initParams(String entId, String soilType, String compoundType, String x, String y) {
        //θ
        soilMoisture = Double.valueOf(entSurveyPlanService.getValue(entId, Arrays.asList("S212")));

        OsmoticCoefficient osmoticCoefficient = osmoticCoefficientService.getOne(new QueryWrapper<OsmoticCoefficient>()
                .eq("soil_type", soilType).eq("valid", BaseEnum.VALID_YES.getCode()));

        if (ObjectUtil.isEmpty(osmoticCoefficient)) {
            throw new BusinessException("no records in table osmotic_coefficient when query use soil_type:" + soilType);
        }
        dij = osmoticCoefficient.getValue();
        //密度
        density = Double.valueOf(entSurveyPlanService.getValue(entId, Arrays.asList("S213")));
        //　Kd 土壤-水分配系数 重金属0.63；有机物1.2 　k 一阶吸附解吸速率常数 有机物0.06~0.084；重金属6.94*10^-6
        switch (compoundType) {
            case HEAVY_METAL:
                kd = 0.63;
                k = RandomUtil.randomDouble(0.06, 0.084);
                break;
            case ORGANISM:
                kd = 1.2;
                k = 6.94 * 0.000001;
        }
        //μc 水相一阶微生物降解速率常数
        uc = 0.08;
        //us
        us = 0.004;
        //t 时间 假定
        t = 1;
        //qi 达西流速 =K*I
        xi = x;
        xj = y;

    }
}
