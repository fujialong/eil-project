package com.shencai.eil.gis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.gis.entity.GisValue;
import com.shencai.eil.gis.mapper.GisValueMapper;
import com.shencai.eil.gis.model.ContainsCodeValue;
import com.shencai.eil.gis.model.GisValueParam;
import com.shencai.eil.gis.service.IGisValueService;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * GisValueServiceImpl
 * </p>
 *
 * @author fujl
 * @since 2018-09-22
 */
@Service
@Slf4j
public class GisValueServiceImpl extends ServiceImpl<GisValueMapper, GisValue> implements IGisValueService {


    @Override
    public void saveGisValue(GisValueParam requestParam) {
        List<GisValue> preList = new ArrayList<>();
        List<ContainsCodeValue> containsCodeValues = requestParam.getParamList();

        if (CollectionUtils.isEmpty(containsCodeValues)) {
            log.error("The list in the request parameter is empty ,In the saveGisValue method!");
            return;
        }

        for (ContainsCodeValue codeAndValue : containsCodeValues) {
            saveEntityToList(preList, requestParam, codeAndValue);
        }

        this.saveBatch(preList);
    }

    @Override
    public List<String> getCodesByEntId(String entId) {
        return this.baseMapper.getCodesByEntId(entId);
    }

    @Override
    public double getValueByEntIdAndCode(String entId, String code) {
        return this.baseMapper.getValueByEntIdAndCode(entId,code);
    }

    @Override
    public List<CodeAndValueUseDouble> getCodeValueByEntId(String entId) {
        return this.baseMapper.getCodeValueByEntId(entId);
    }


    private void saveEntityToList(List<GisValue> preList, GisValueParam requestParam, ContainsCodeValue codeAndValue) {
        GisValue gisValue = new GisValue();
        Date date = DateUtil.getNowTimestamp();

        gisValue.setEntId(requestParam.getEntId());
        gisValue.setCreateTime(date);
        gisValue.setId(StringUtil.getUUID());
        gisValue.setValid((Integer) BaseEnum.VALID_YES.getCode());
        gisValue.setUpdateTime(date);
        gisValue.setCode(codeAndValue.getCode());
        gisValue.setValue(codeAndValue.getValue());

        preList.add(gisValue);
    }
}
