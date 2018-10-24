package com.shencai.eil.assessment.mapper;

import com.shencai.eil.assessment.entity.CantonParamValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.assessment.model.CantonParamQueryParam;
import com.shencai.eil.assessment.model.CantonParamVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
public interface CantonParamValueMapper extends BaseMapper<CantonParamValue> {
    List<CantonParamVO> listCantonParam(CantonParamQueryParam queryParam);
}
