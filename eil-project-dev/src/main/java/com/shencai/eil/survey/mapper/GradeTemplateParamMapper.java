package com.shencai.eil.survey.mapper;

import com.shencai.eil.survey.entity.GradeTemplateParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.survey.model.GradeTemplateParamQueryParam;
import com.shencai.eil.survey.model.GradeTemplateParamVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-04
 */
public interface GradeTemplateParamMapper extends BaseMapper<GradeTemplateParam> {
    List<GradeTemplateParamVO> listGradeTemplateParam(GradeTemplateParamQueryParam queryParam);
}
