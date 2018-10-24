package com.shencai.eil.scenario.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/10/19.
 */
@Data
public class AccidentScenarioResultQueryParam implements Serializable {

    private String scenarioSelectionInfoId;

    private String enterpriseId;

}
