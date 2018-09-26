package com.shencai.eil.system.mapper;

import com.shencai.eil.system.entity.EmissionMode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.system.model.EmissionModeQueryParam;
import com.shencai.eil.system.model.EmissionModeVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-18
 */
public interface EmissionModeMapper extends BaseMapper<EmissionMode> {

    List<EmissionModeVO> listEmissionMode(EmissionModeQueryParam queryParam);
}
