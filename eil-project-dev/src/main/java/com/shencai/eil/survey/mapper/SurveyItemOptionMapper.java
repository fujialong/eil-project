package com.shencai.eil.survey.mapper;

import com.shencai.eil.survey.entity.SurveyItemOption;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.survey.model.SurveyItemOptionQueryParam;
import com.shencai.eil.survey.model.SurveyItemOptionVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-06
 */
public interface SurveyItemOptionMapper extends BaseMapper<SurveyItemOption> {
    List<SurveyItemOptionVO> listSurveyItemOption(SurveyItemOptionQueryParam queryParam);
}
