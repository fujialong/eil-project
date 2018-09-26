package com.shencai.eil.policy.service.impl;

import com.shencai.eil.policy.entity.Technique;
import com.shencai.eil.policy.mapper.TechniqueMapper;
import com.shencai.eil.policy.model.TechniqueQueryParam;
import com.shencai.eil.policy.model.TechniqueVO;
import com.shencai.eil.policy.service.ITechniqueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Service
public class TechniqueServiceImpl extends ServiceImpl<TechniqueMapper, Technique> implements ITechniqueService {

    @Autowired
    private TechniqueMapper techniqueMapper;

    @Override
    public List<TechniqueVO> listTechnique(TechniqueQueryParam queryParam) {
        List<TechniqueVO> techniqueList = techniqueMapper.listTechnique(queryParam);
        return techniqueList;
    }
}
