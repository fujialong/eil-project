package com.shencai.eil.assessment.mapper;

import com.shencai.eil.assessment.entity.GridConcentration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.assessment.model.GridConcentrationQueryParam;
import com.shencai.eil.assessment.model.GridConcentrationVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
public interface GridConcentrationMapper extends BaseMapper<GridConcentration> {
    List<GridConcentrationVO> listGridConcentration(GridConcentrationQueryParam queryParam);
}
