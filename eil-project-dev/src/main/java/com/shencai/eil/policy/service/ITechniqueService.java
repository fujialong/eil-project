package com.shencai.eil.policy.service;

import com.shencai.eil.policy.entity.Technique;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.policy.model.TechniqueQueryParam;
import com.shencai.eil.policy.model.TechniqueVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface ITechniqueService extends IService<Technique> {

    List<TechniqueVO> listTechnique(TechniqueQueryParam queryParam);
}
