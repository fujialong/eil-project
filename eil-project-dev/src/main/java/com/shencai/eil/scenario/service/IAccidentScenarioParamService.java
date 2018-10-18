package com.shencai.eil.scenario.service;

import com.shencai.eil.scenario.entity.AccidentScenarioParam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.scenario.model.AccidentScenarioParamQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioParamVO;

import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
public interface IAccidentScenarioParamService extends IService<AccidentScenarioParam> {

    List<AccidentScenarioParamVO> listScenarioParam(AccidentScenarioParamQueryParam queryParam);

}
