package com.shencai.eil.survey.mapper;

import com.shencai.eil.survey.entity.EntSurveyExtendInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.survey.model.EntSurveyExtendInfoVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-18
 */
public interface EntSurveyExtendInfoMapper extends BaseMapper<EntSurveyExtendInfo> {
    List<EntSurveyExtendInfoVO> listEntSurveyExtendInfo(String enterpriseId);
}
