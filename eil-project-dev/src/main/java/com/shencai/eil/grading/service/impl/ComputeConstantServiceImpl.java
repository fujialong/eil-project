package com.shencai.eil.grading.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.grading.entity.ComputeConstant;
import com.shencai.eil.grading.mapper.ComputeConstantMapper;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import com.shencai.eil.grading.service.IComputeConstantService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
@Service
public class ComputeConstantServiceImpl extends ServiceImpl<ComputeConstantMapper, ComputeConstant> implements IComputeConstantService {

    @Override
    public double getValueByCode(String code) {
        return this.baseMapper.getValueByCode(code);
    }

    @Override
    public List<CodeAndValueUseDouble> listCodeValue() {

        return this.baseMapper.listCodeValue();
    }
}
