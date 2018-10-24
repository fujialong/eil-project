package com.shencai.eil.scenario.mapper;

import com.shencai.eil.scenario.entity.AccidentScenarioResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.scenario.model.AccidentScenarioResultQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioResultVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-12
 */
public interface AccidentScenarioResultMapper extends BaseMapper<AccidentScenarioResult> {

    List<AccidentScenarioResultVO> listScenarioResult(AccidentScenarioResultQueryParam queryParam);

    int countScenarioResult(AccidentScenarioResultQueryParam queryParam);
}
