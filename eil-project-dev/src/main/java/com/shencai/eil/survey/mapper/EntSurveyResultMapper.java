package com.shencai.eil.survey.mapper;

import com.shencai.eil.survey.entity.EntSurveyResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.survey.model.EntSurveyResultParam;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import com.shencai.eil.survey.model.EntSurveyResultVO;

import java.util.List;

/**
 * @author fujl
 * @since 2018-09-27
 */
public interface EntSurveyResultMapper extends BaseMapper<EntSurveyResult> {

    List<EntSurveyResultVO> listEntSurveyResult(EntSurveyResultQueryParam queryParam);

    void logDelete(EntSurveyResultParam param);
}
