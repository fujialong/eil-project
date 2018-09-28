package com.shencai.eil.grading.service;

import com.shencai.eil.grading.entity.TargetWeight;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import com.shencai.eil.grading.model.TargetResultVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
public interface ITargetWeightService extends IService<TargetWeight> {

    /**
     * code type assess result
     *
     * @param entId
     * @return
     */
    List<TargetResultVO> listCodeTypeAndAssessResult(String entId);

    /**
     *Lis target weight type and weight
     * @param code
     * @return
     */
    List<CodeAndValueUseDouble> lisTargetWeightTypeAndWeight(String code);


    /**
     * get all code and weight
     *
     * @return
     */
    List<CodeAndValueUseDouble> getAllCodeAndWeight();
}
