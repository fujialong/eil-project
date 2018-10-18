package com.shencai.eil.grading.mapper;

import com.shencai.eil.grading.entity.RiskMaterialParamValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.model.RiskMaterialParamValueQueryParam;
import com.shencai.eil.grading.model.RiskMaterialParamValueVO;

import java.util.List;
import com.shencai.eil.grading.model.RiskMaterialParamValueQueryParam;

/**
 * @author fujl
 * @since 2018-09-19
 */
public interface RiskMaterialParamValueMapper extends BaseMapper<RiskMaterialParamValue> {

    String getParamValue(RiskMaterialParamValueQueryParam queryParam);
    List<RiskMaterialParamValueVO> listRiskMaterialParamValue(RiskMaterialParamValueQueryParam queryParam);
}
