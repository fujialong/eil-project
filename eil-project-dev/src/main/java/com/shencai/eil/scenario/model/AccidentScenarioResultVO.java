package com.shencai.eil.scenario.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/10/12.
 */
@Data
public class AccidentScenarioResultVO {
    private String scenarioName;
    private Double scenarioResult;
    private String scenarioResultDesc;

    private String id;

    private String scenarioId;

    private List<AccidentScenarioParamVO> scenarioParamList;

}
