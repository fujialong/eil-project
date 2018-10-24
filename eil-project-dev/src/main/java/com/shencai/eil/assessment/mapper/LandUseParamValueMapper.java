package com.shencai.eil.assessment.mapper;

import com.shencai.eil.assessment.entity.LandUseParamValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.assessment.model.LandUseParamValueQueryParam;
import com.shencai.eil.assessment.model.LandUseParamValueVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
public interface LandUseParamValueMapper extends BaseMapper<LandUseParamValue> {
    List<LandUseParamValueVO> listLandUseParam(LandUseParamValueQueryParam queryParam);
}
