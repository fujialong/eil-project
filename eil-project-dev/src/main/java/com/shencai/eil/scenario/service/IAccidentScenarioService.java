package com.shencai.eil.scenario.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.scenario.entity.AccidentScenario;
import com.shencai.eil.scenario.model.*;

import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
public interface IAccidentScenarioService extends IService<AccidentScenario> {

    List<AccidentScenarioVO> listAccidentScenario(AccidentScenarioQueryParam queryParam);

    void saveEntAccidentScenarioResult(ScenarioResultParam param);

    void deleteEntAccidentScenarioResult(ScenarioResultParam param);

    List<AccidentScenarioResultVO> listEntAccidentScenarioResult(AccidentScenarioResultQueryParam queryParam);
}
