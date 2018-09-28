package com.shencai.eil.gis.service.impl;

import com.shencai.eil.gis.entity.GisValueClass;
import com.shencai.eil.gis.mapper.GisValueClassMapper;
import com.shencai.eil.gis.service.IGisValueClassService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fujl
 * @since 2018-09-27
 */
@Service
public class GisValueClassServiceImpl extends ServiceImpl<GisValueClassMapper, GisValueClass> implements IGisValueClassService {

    @Override
    public List<String> getClassCodesByCodes(List<String> codes) {
        return this.baseMapper.getClassCodesByCodes(codes);
    }
}
