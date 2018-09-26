package com.shencai.eil.policy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.policy.entity.EnterpriseAccident;
import com.shencai.eil.policy.mapper.EnterpriseAccidentMapper;
import com.shencai.eil.policy.model.EnterpriseAccidentQueryParam;
import com.shencai.eil.policy.model.EnterpriseAccidentVO;
import com.shencai.eil.policy.service.IEnterpriseAccidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-18
 */
@Service
public class EnterpriseAccidentServiceImpl extends ServiceImpl<EnterpriseAccidentMapper, EnterpriseAccident> implements IEnterpriseAccidentService {

    @Autowired
    private EnterpriseAccidentMapper enterpriseAccidentMapper;

    @Override
    public List<EnterpriseAccidentVO> listEnterpriseAccident(EnterpriseAccidentQueryParam enterpriseAccidentQueryParam) {
        List<EnterpriseAccidentVO> enterpriseAccidentVOList =
                enterpriseAccidentMapper.listEnterpriseAccident(enterpriseAccidentQueryParam);
        return enterpriseAccidentVOList;
    }
}
