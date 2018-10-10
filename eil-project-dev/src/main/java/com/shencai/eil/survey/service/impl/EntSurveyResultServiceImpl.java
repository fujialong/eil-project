package com.shencai.eil.survey.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.survey.entity.EntSurveyResult;
import com.shencai.eil.survey.mapper.EntSurveyResultMapper;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import com.shencai.eil.survey.model.EntSurveyResultVO;
import com.shencai.eil.survey.service.IEntSurveyResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fujl
 * @since 2018-09-27
 */
@Service
public class EntSurveyResultServiceImpl extends ServiceImpl<EntSurveyResultMapper, EntSurveyResult> implements IEntSurveyResultService {

    @Autowired
    private EntSurveyResultMapper entSurveyResultMapper;

    @Override
    public List<EntSurveyResultVO> listEntSurveyResult(EntSurveyResultQueryParam queryParam) {
        List<EntSurveyResultVO> surveyResultVOList = entSurveyResultMapper.listEntSurveyResult(queryParam);
        return surveyResultVOList;
    }

}
