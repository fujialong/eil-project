package com.shencai.eil.policy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.policy.entity.EnterpriseAccident;
import com.shencai.eil.policy.model.EnterpriseAccidentQueryParam;
import com.shencai.eil.policy.model.EnterpriseAccidentVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-18
 */
public interface IEnterpriseAccidentService extends IService<EnterpriseAccident> {

    List<EnterpriseAccidentVO> listEnterpriseAccident(EnterpriseAccidentQueryParam enterpriseAccidentQueryParam);

}
