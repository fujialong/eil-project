package com.shencai.eil.scenario.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/10/18.
 */
@Data
public class AccidentScenarioResultParam implements Serializable {

    private String scenarioId;

    private String scenarioCode;

    private List<AccidentScenarioParamVO> scenarioParamList;

}
