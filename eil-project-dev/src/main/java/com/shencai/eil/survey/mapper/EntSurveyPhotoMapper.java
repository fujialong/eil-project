package com.shencai.eil.survey.mapper;

import com.shencai.eil.survey.entity.EntSurveyPhoto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.survey.model.EntSurveyPhotoQueryParam;
import com.shencai.eil.survey.model.EntSurveyPhotoVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-18
 */
public interface EntSurveyPhotoMapper extends BaseMapper<EntSurveyPhoto> {
    List<EntSurveyPhotoVO> listSurveyPhoto(EntSurveyPhotoQueryParam queryParam);
}
