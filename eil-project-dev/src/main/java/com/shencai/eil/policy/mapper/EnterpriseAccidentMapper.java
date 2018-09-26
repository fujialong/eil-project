package com.shencai.eil.policy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.policy.entity.EnterpriseAccident;
import com.shencai.eil.policy.model.EnterpriseAccidentQueryParam;
import com.shencai.eil.policy.model.EnterpriseAccidentVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-18
 */
public interface EnterpriseAccidentMapper extends BaseMapper<EnterpriseAccident> {

    List<EnterpriseAccidentVO> listEnterpriseAccident(EnterpriseAccidentQueryParam enterpriseAccidentQueryParam);
}
