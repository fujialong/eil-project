package com.shencai.eil.scenario.service;

import com.shencai.eil.scenario.model.AccidentScenarioParamVO;

import java.util.List;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-16 15:15
 **/
public interface IScenarioSelectionService {

    List<AccidentScenarioParamVO> trashDischarge(String entId);

    /**
     * fire / explosion
     * @param entId
     */
    List<AccidentScenarioParamVO> fireOrexplosion(String entId);

    /**
     *
     * @param entId
     */
    List<AccidentScenarioParamVO> liquidDrip(String entId);

}
