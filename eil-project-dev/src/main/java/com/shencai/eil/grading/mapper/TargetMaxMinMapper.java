package com.shencai.eil.grading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.entity.TargetMaxMin;
import com.shencai.eil.grading.model.TargetMaxValueVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fujl
 * @since 2018-10-13
 */
public interface TargetMaxMinMapper extends BaseMapper<TargetMaxMin> {

    List<TargetMaxValueVO> listMaxMinMap();
}
