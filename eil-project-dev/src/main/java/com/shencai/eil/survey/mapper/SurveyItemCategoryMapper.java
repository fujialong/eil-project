package com.shencai.eil.survey.mapper;

import com.shencai.eil.survey.entity.SurveyItemCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.survey.model.SurveyItemCategoryQueryParam;
import com.shencai.eil.survey.model.SurveyItemVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-06
 */
public interface SurveyItemCategoryMapper extends BaseMapper<SurveyItemCategory> {
    List<SurveyItemVO> listSurveyItem(SurveyItemCategoryQueryParam queryParam);
}
