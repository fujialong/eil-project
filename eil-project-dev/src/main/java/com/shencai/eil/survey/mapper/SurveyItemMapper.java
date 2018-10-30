package com.shencai.eil.survey.mapper;

import com.shencai.eil.survey.entity.SurveyItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.survey.model.SurveyItemQueryParam;
import com.shencai.eil.survey.model.SurveyItemVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fujl
 * @since 2018-09-27
 */
public interface SurveyItemMapper extends BaseMapper<SurveyItem> {
    List<SurveyItemVO> listSurveyItem(SurveyItemQueryParam queryParam);
    List<SurveyItemVO> listSurveyItemByCategory(SurveyItemQueryParam queryParam);
    void deleteIntensivePlan(SurveyItemVO surveyItem);
}
