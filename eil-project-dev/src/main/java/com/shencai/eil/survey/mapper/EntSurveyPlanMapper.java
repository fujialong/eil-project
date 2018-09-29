package com.shencai.eil.survey.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.survey.entity.EntSurveyPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.survey.model.EntSurveyPlanQueryParam;
import com.shencai.eil.survey.model.EntSurveyPlanVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fujl
 * @since 2018-09-27
 */
public interface EntSurveyPlanMapper extends BaseMapper<EntSurveyPlan> {
    List<EntSurveyPlanVO> pageBasicSurveyPlan(Page<EntSurveyPlanVO> page, @Param("param") EntSurveyPlanQueryParam queryParam);

    List<EntSurveyPlanVO> pageIntensiveSurveyPlan(Page<EntSurveyPlanVO> page, @Param("param") EntSurveyPlanQueryParam queryParam);
}
