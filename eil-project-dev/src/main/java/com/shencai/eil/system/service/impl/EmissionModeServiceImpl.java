package com.shencai.eil.system.service.impl;

import com.shencai.eil.system.entity.EmissionMode;
import com.shencai.eil.system.mapper.EmissionModeMapper;
import com.shencai.eil.system.model.EmissionModeQueryParam;
import com.shencai.eil.system.model.EmissionModeVO;
import com.shencai.eil.system.service.IEmissionModeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-18
 */
@Service
public class EmissionModeServiceImpl extends ServiceImpl<EmissionModeMapper, EmissionMode> implements IEmissionModeService {

    @Autowired
    private EmissionModeMapper emissionModeMapper;

    @Override
    public List<EmissionModeVO> listEmissionMode(EmissionModeQueryParam queryParam) {
        List<EmissionModeVO> emissionModeList = emissionModeMapper.listEmissionMode(queryParam);
        return emissionModeList;
    }
}
