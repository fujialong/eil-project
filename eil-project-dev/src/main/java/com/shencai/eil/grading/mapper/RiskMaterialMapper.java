package com.shencai.eil.grading.mapper;

import com.shencai.eil.grading.entity.RiskMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.model.RiskMaterialQueryParam;
import com.shencai.eil.grading.model.RiskMaterialVO;

import java.util.List;

/**
 * @author fujl
 * @since 2018-09-19
 */
public interface RiskMaterialMapper extends BaseMapper<RiskMaterial> {

    List<RiskMaterialVO> listRiskMaterial(RiskMaterialQueryParam queryParam);
}
