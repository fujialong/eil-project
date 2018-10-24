package com.shencai.eil.scenario.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/10/19.
 */
@Data
public class ScenarioResultParam implements Serializable {

    private String scenarioSelectionInfoId;

    private String scenarioId;;

    private List<AccidentScenarioResultParam> scenarioResultList;

    private List<String> paramIdList;

    private String enterpriseId;

}
