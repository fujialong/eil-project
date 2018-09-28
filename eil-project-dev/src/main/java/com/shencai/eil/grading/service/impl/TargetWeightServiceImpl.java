package com.shencai.eil.grading.service.impl;

import com.shencai.eil.grading.entity.TargetWeight;
import com.shencai.eil.grading.mapper.TargetWeightMapper;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import com.shencai.eil.grading.model.TargetResultVO;
import com.shencai.eil.grading.service.ITargetWeightService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
@Service
public class TargetWeightServiceImpl extends ServiceImpl<TargetWeightMapper, TargetWeight> implements ITargetWeightService {

    @Override
    public List<TargetResultVO> listCodeTypeAndAssessResult(String entId) {
        return this.baseMapper.listCodeTypeAndAssessResult(entId);
    }

    @Override
    public List<CodeAndValueUseDouble> lisTargetWeightTypeAndWeight(String code) {
        return this.baseMapper.lisTargetWeightTypeAndWight(code);
    }
}
