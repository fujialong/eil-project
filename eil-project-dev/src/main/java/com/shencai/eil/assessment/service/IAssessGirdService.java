package com.shencai.eil.assessment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.assessment.entity.AssessGird;
import com.shencai.eil.assessment.model.AssessGirdParam;
import com.shencai.eil.assessment.model.AssessGridVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
public interface IAssessGirdService extends IService<AssessGird> {

    void saveAssessGrid(List<AssessGirdParam> paramList);

    AssessGridVO gisBaseInfo(String entId);

    List<AssessGird> entAssessGirdList(String entId, String bisCode);
}
