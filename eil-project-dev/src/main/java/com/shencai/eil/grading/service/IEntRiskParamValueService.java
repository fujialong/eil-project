package com.shencai.eil.grading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.grading.entity.EntRiskParamValue;
import com.shencai.eil.grading.model.EntRiskParamValueParam;

/**
 * @author zhoujx
 * @since 2018-09-19
 */
public interface IEntRiskParamValueService extends IService<EntRiskParamValue> {

    void deleteEntRiskParamValue(EntRiskParamValueParam param);
}
