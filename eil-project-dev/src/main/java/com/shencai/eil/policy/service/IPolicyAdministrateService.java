package com.shencai.eil.policy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.policy.model.PolicyParam;
import com.shencai.eil.policy.model.PolicyQueryParam;
import com.shencai.eil.policy.model.PolicyVO;

/**
 * Created by zhoujx on 2018/9/13.
 */
public interface IPolicyAdministrateService {

    String savePolicy(PolicyParam param);

    Page<PolicyVO> pagePolicy(PolicyQueryParam queryParam);

    PolicyVO getPolicy(PolicyQueryParam queryParam);

    void deletePolicy(PolicyParam param);
}
