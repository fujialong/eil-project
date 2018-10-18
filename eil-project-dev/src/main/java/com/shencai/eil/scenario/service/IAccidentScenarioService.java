package com.shencai.eil.scenario.service;

import com.shencai.eil.scenario.entity.AccidentScenario;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.scenario.model.AccidentScenarioQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioVO;

import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
public interface IAccidentScenarioService extends IService<AccidentScenario> {
    List<AccidentScenarioVO> listAccidentScenario(AccidentScenarioQueryParam queryParam);
}
