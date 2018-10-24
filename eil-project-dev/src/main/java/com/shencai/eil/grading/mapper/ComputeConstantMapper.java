package com.shencai.eil.grading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.entity.ComputeConstant;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
public interface ComputeConstantMapper extends BaseMapper<ComputeConstant> {


    /**
     * list code value
     *
     * @return
     */
    List<CodeAndValueUseDouble> listCodeValue(String bisCode);
}
