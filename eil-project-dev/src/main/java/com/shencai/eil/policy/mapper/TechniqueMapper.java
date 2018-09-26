package com.shencai.eil.policy.mapper;

import com.shencai.eil.policy.entity.Technique;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.policy.model.TechniqueQueryParam;
import com.shencai.eil.policy.model.TechniqueVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface TechniqueMapper extends BaseMapper<Technique> {

    List<TechniqueVO> listTechnique(TechniqueQueryParam queryParam);
}
