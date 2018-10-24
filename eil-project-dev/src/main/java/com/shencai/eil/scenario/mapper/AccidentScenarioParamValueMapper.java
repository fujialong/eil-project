package com.shencai.eil.scenario.mapper;

import com.shencai.eil.scenario.entity.AccidentScenarioParamValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.scenario.model.AccidentScenarioParamVO;
import com.shencai.eil.scenario.model.AccidentScenarioResultQueryParam;

import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
public interface AccidentScenarioParamValueMapper extends BaseMapper<AccidentScenarioParamValue> {

    List<AccidentScenarioParamVO> listScenarioParamValue(AccidentScenarioResultQueryParam queryParam);
}
