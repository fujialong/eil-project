package com.shencai.eil.assessment.mapper;

import com.shencai.eil.assessment.entity.GridCalculationValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.assessment.model.GridCalculationValueVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
public interface GridCalculationValueMapper extends BaseMapper<GridCalculationValue> {
    List<GridCalculationValueVO> listStatisticsValueByBisType(String entId);
}
