package com.shencai.eil.assessment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.assessment.entity.EntAssessInfo;
import com.shencai.eil.assessment.model.AssessGridVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
public interface IEntAssessResultService extends IService<EntAssessInfo> {

    AssessGridVO gisBaseInfo(String entId);
}
