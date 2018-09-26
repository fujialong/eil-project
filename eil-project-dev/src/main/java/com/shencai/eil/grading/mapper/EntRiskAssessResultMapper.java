package com.shencai.eil.grading.mapper;

import com.shencai.eil.grading.entity.EntRiskAssessResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.model.EntRiskAssessResultQueryParam;
import com.shencai.eil.grading.model.EntRiskAssessResultVO;
import com.shencai.eil.grading.model.TargetResult;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
public interface EntRiskAssessResultMapper extends BaseMapper<EntRiskAssessResult> {

    List<TargetResult> listRootTargetResult(EntRiskAssessResultQueryParam queryParam);

    List<EntRiskAssessResultVO> listEntRiskAssessResult(EntRiskAssessResultQueryParam queryParam);
}
