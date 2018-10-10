package com.shencai.eil.survey.service;

import com.shencai.eil.survey.entity.EntSurveyResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import com.shencai.eil.survey.model.EntSurveyResultVO;

import java.util.List;

/**
 * @author fujl
 * @since 2018-09-27
 */
public interface IEntSurveyResultService extends IService<EntSurveyResult> {

    List<EntSurveyResultVO> listEntSurveyResult(EntSurveyResultQueryParam queryParam);
}
