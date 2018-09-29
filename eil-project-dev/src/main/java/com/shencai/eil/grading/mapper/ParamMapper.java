package com.shencai.eil.grading.mapper;

import com.shencai.eil.grading.entity.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.model.ParamQueryParam;
import com.shencai.eil.grading.model.ParamVO;

import java.util.List;

/**
 * @author fujl
 * @since 2018-09-19
 */
public interface ParamMapper extends BaseMapper<Param> {
    List<ParamVO> listEntParamValue(ParamQueryParam queryParam);
}
