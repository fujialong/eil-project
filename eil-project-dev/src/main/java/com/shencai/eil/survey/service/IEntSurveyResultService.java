package com.shencai.eil.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.survey.entity.EntSurveyResult;
import com.shencai.eil.survey.model.EntSurveyResultParam;

/**
 * @author fujl
 * @since 2018-09-27
 */
public interface IEntSurveyResultService extends IService<EntSurveyResult> {

    void deleteEntSurveyResults(EntSurveyResultParam param);

}
