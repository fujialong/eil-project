package com.shencai.eil.policy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.policy.entity.EnterpriseInfo;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface IEnterpriseInfoService extends IService<EnterpriseInfo> {

    void saveEnterpriseInfo(EnterpriseInfo param);
}
