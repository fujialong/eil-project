package com.shencai.eil.survey.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.survey.entity.EntSurveyPlan;
import com.shencai.eil.survey.mapper.EntSurveyPlanMapper;
import com.shencai.eil.survey.model.EntSurveyPlanQueryParam;
import com.shencai.eil.survey.model.EntSurveyPlanVO;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fujl
 * @since 2018-09-27
 */
@Service
public class EntSurveyPlanServiceImpl extends ServiceImpl<EntSurveyPlanMapper, EntSurveyPlan> implements IEntSurveyPlanService {

    @Autowired
    private EntSurveyPlanMapper entSurveyPlanMapper;

    @Override
    public Page<EntSurveyPlanVO> pageBasicSurveyPlan(EntSurveyPlanQueryParam queryParam) {
        Page<EntSurveyPlanVO> page = new Page<>();
        page.setCurrent(queryParam.getCurrent());
        page.setSize(queryParam.getSize());
        page.setRecords(entSurveyPlanMapper.pageBasicSurveyPlan(page, queryParam));
        return page;
    }

    @Override
    public Page<EntSurveyPlanVO> pageIntensiveSurveyPlan(EntSurveyPlanQueryParam queryParam) {
        Page<EntSurveyPlanVO> page = new Page<>();
        page.setCurrent(queryParam.getCurrent());
        page.setSize(queryParam.getSize());
        page.setRecords(entSurveyPlanMapper.pageIntensiveSurveyPlan(page, queryParam));
        return page;
    }
}
