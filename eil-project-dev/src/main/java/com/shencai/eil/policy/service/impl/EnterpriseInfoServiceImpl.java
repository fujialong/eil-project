package com.shencai.eil.policy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.gis.model.EntGisInfo;
import com.shencai.eil.gis.service.IGisValueClassService;
import com.shencai.eil.gis.service.IGisValueService;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.policy.model.PolicyQueryParam;
import com.shencai.eil.policy.service.IEnterpriseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.shencai.eil.common.utils.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Service
public class EnterpriseInfoServiceImpl extends ServiceImpl<EnterpriseInfoMapper, EnterpriseInfo> implements IEnterpriseInfoService {

    @Autowired
    private IGisValueService gisValueService;

    @Autowired
    private IGisValueClassService gisValueClassService;


    @Override
    public void saveEnterpriseInfo(EnterpriseInfo param) {
        param.setUpdateTime(DateUtil.getNowTimestamp());
        this.updateById(param);
    }

    @Override
    public EntGisInfo getEntLocation(String entId) {
        //This code category has been saved
        List<String> codes = gisValueService.getCodesByEntId(entId);


        List<String> classCodes = CollectionUtils.isEmpty(codes) ? new ArrayList<>() : gisValueClassService.getClassCodesByCodes(codes);
        EntGisInfo entGisInfo = this.baseMapper.getEntLocation(entId);

        if (!ObjectUtil.isEmpty(entGisInfo)) {
            entGisInfo.setHasSavedParamList(classCodes);
        }
        return entGisInfo;
    }

    @Override
    public List<EntGisInfo> listOtherEntLocation(String entId) {
        return this.baseMapper.listOtherEntLocation(entId);
    }

    @Override
    public List<EntGisInfo> listAllEntLocation() {
        return this.baseMapper.listAllEntLocation();
    }

    @Override
    public EnterpriseInfo getEnterpriseInfo(PolicyQueryParam queryParam) {
        EnterpriseInfo enterpriseInfo = this.getOne(new QueryWrapper<EnterpriseInfo>()
                .eq("id", queryParam.getEnterpriseId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (ObjectUtil.isEmpty(enterpriseInfo)) {
            throw new BusinessException("The enterprise has been removed!");
        }
        return enterpriseInfo;
    }
}
