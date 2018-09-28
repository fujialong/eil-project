package com.shencai.eil.grading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.entity.TargetWeight;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import com.shencai.eil.grading.model.EntRiskAssessResultQueryParam;
import com.shencai.eil.grading.model.TargetResultVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
public interface TargetWeightMapper extends BaseMapper<TargetWeight> {

    List<TargetResultVO> listAllTarget(EntRiskAssessResultQueryParam queryParam);

    /**
     * list Code Type And Assess Results
     *
     * @param entId
     * @return
     */
    List<TargetResultVO> listCodeTypeAndAssessResult(String entId);

    /**
     *Lis target weight type and weight
     *
     * @param code
     * @return
     */
    List<CodeAndValueUseDouble> lisTargetWeightTypeAndWight(String code);

    /**
     * get all code and wight
     *
     * @return
     */
    List<CodeAndValueUseDouble> getAllCodeAndWight();
}
