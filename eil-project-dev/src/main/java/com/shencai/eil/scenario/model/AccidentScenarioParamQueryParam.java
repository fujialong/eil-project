package com.shencai.eil.scenario.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/10/17.
 */
@Data
public class AccidentScenarioParamQueryParam implements Serializable {

    private String scenarioId;

    private String scenarioSelectionInfoId;

}
