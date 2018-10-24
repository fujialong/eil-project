package com.shencai.eil.scenario.service;

import com.shencai.eil.scenario.entity.AccidentScenarioResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.scenario.model.AccidentScenarioResultParam;

/**
 * @author fanhj
 * @since 2018-10-12
 */
public interface IAccidentScenarioResultService extends IService<AccidentScenarioResult> {

    double calculateScenarioResult(AccidentScenarioResultParam param);

}
