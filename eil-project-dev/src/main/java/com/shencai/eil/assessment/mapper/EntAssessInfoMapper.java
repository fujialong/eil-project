package com.shencai.eil.assessment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.assessment.entity.EntAssessInfo;
import com.shencai.eil.assessment.model.AssessGridVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
public interface EntAssessInfoMapper extends BaseMapper<EntAssessInfo> {

   AssessGridVO gisBaseInfo(String entId);
}
