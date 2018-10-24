package com.shencai.eil.grading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.grading.entity.ComputeConstant;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
public interface IComputeConstantService extends IService<ComputeConstant> {


    /**
     * list code value
     * @return
     */
    List<CodeAndValueUseDouble> listCodeValue(String bisCode);
}
