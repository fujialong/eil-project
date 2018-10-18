package com.shencai.eil.scenario.mapper;

import com.shencai.eil.scenario.entity.AccidentScenarioParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.scenario.model.AccidentScenarioParamQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioParamVO;

import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
public interface AccidentScenarioParamMapper extends BaseMapper<AccidentScenarioParam> {

    List<AccidentScenarioParamVO> listScenarioParam(AccidentScenarioParamQueryParam queryParam);
}
