package com.shencai.eil.survey.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.policy.model.EnterpriseParam;
import com.shencai.eil.policy.model.EnterpriseVO;
import com.shencai.eil.survey.entity.EntSurveyPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.survey.model.EntSurveyPlanQueryParam;
import com.shencai.eil.survey.model.EntSurveyPlanVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fujl
 * @since 2018-09-27
 */
public interface IEntSurveyPlanService extends IService<EntSurveyPlan> {
    EnterpriseVO getEntSurveyInfo(String enterpriseId);

    void surveyUpgrade(EnterpriseParam param);

    Page<EntSurveyPlanVO> pageBasicSurveyPlan(EntSurveyPlanQueryParam queryParam);

    Page<EntSurveyPlanVO> pageIntensiveSurveyPlan(EntSurveyPlanQueryParam queryParam);

    void finishSurvey(String enterpriseId);
}
