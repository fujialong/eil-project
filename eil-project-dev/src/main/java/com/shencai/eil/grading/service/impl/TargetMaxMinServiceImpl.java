package com.shencai.eil.grading.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.grading.entity.TargetMaxMin;
import com.shencai.eil.grading.mapper.TargetMaxMinMapper;
import com.shencai.eil.grading.model.TargetMaxValueVO;
import com.shencai.eil.grading.service.ITargetMaxMinService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author fujl
 * @since 2018-10-13
 */
@Service
public class TargetMaxMinServiceImpl extends ServiceImpl<TargetMaxMinMapper, TargetMaxMin> implements ITargetMaxMinService {

    @Override
    public List<TargetMaxValueVO> listMaxMinMap() {
        return this.baseMapper.listMaxMinMap();
    }
}
