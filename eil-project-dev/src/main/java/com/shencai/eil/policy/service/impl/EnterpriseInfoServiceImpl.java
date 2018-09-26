package com.shencai.eil.policy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.gis.model.GisValueVO;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.policy.service.IEnterpriseInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Service
public class EnterpriseInfoServiceImpl extends ServiceImpl<EnterpriseInfoMapper, EnterpriseInfo> implements IEnterpriseInfoService {

    @Override
    public void saveEnterpriseInfo(EnterpriseInfo param) {
        this.updateById(param);
    }

    @Override
    public GisValueVO getEntLocation(String entId) {
        return this.baseMapper.getEntLocation(entId);
    }

    @Override
    public List<GisValueVO> listOtherEntLocation(String entId) {
        return this.baseMapper.listOtherEntLocation(entId);
    }

    @Override
    public List<GisValueVO> listAllEntLocation() {
        return this.baseMapper.listAllEntLocation();
    }
}
