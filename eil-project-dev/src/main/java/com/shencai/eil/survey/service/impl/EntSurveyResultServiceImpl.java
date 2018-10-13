package com.shencai.eil.survey.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.survey.entity.EntSurveyResult;
import com.shencai.eil.survey.mapper.EntSurveyResultMapper;
import com.shencai.eil.survey.model.EntSurveyResultParam;
import com.shencai.eil.survey.service.IEntSurveyResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fujl
 * @since 2018-09-27
 */
@Service
public class EntSurveyResultServiceImpl extends ServiceImpl<EntSurveyResultMapper, EntSurveyResult> implements IEntSurveyResultService {

    @Autowired
    private EntSurveyResultMapper entSurveyResultMapper;

    @Override
    public void deleteEntSurveyResults(EntSurveyResultParam param) {
        entSurveyResultMapper.logDelete(param);
    }

}
