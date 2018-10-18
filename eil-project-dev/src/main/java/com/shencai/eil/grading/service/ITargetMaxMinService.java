package com.shencai.eil.grading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.grading.entity.TargetMaxMin;
import com.shencai.eil.grading.model.TargetMaxValueVO;

import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author fujl
 * @since 2018-10-13
 */
public interface ITargetMaxMinService extends IService<TargetMaxMin> {

    List<TargetMaxValueVO> listMaxMinMap();
}
