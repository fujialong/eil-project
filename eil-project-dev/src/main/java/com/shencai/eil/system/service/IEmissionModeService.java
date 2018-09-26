package com.shencai.eil.system.service;

import com.shencai.eil.system.entity.EmissionMode;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.system.model.EmissionModeQueryParam;
import com.shencai.eil.system.model.EmissionModeVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-18
 */
public interface IEmissionModeService extends IService<EmissionMode> {

    List<EmissionModeVO> listEmissionMode(EmissionModeQueryParam queryParam);
}
