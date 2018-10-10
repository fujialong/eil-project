package com.shencai.eil.grading.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.grading.entity.EntRiskParamValue;
import com.shencai.eil.grading.mapper.EntRiskParamValueMapper;
import com.shencai.eil.grading.model.EntRiskParamValueParam;
import com.shencai.eil.grading.service.IEntRiskParamValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhoujx
 * @since 2018-09-19
 */
@Service
public class EnvRiskParamValueServiceImpl extends ServiceImpl<EntRiskParamValueMapper, EntRiskParamValue> implements IEntRiskParamValueService {

    @Autowired
    private EntRiskParamValueMapper entRiskParamValueMapper;

    @Override
    public void deleteEntRiskParamValue(EntRiskParamValueParam param) {
        entRiskParamValueMapper.logicDeleteEntRiskParamValue(param);
    }
}
