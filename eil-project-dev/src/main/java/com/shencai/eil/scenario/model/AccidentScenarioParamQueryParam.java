package com.shencai.eil.scenario.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/10/17.
 */
@Data
public class AccidentScenarioParamQueryParam implements Serializable {

    private String scenarioId;

    private String scenarioSelectionInfoId;

    private Double medianLethalConcentration;

    private Double onlineQuantity;

    private Boolean isStorageTank;

    private List<String> pipeParamCodeList;

}
