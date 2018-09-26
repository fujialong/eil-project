package com.shencai.eil.policy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.policy.service.IEnterpriseInfoService;
import org.springframework.stereotype.Service;

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
}
