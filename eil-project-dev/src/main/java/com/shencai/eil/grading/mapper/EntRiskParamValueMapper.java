package com.shencai.eil.grading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.entity.EntRiskParamValue;
import com.shencai.eil.grading.model.EntRiskParamValueParam;
import com.shencai.eil.grading.model.EntRiskParamValueQueryParam;
import com.shencai.eil.grading.model.EntRiskParamValueVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-19
 */
public interface EntRiskParamValueMapper extends BaseMapper<EntRiskParamValue> {

    List<EntRiskParamValueVO> listEntRiskParamValue(EntRiskParamValueQueryParam queryParam);

    int countGradualRiskParams(EntRiskParamValueQueryParam queryParam);

    void logicDeleteEntRiskParamValue(EntRiskParamValueParam param);
}
