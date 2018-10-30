package com.shencai.eil.scenario.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/10/13.
 */
@Data
public class AccidentScenarioQueryParam {

    private String partType;

    private Boolean isStorageTank;

    private List<String> pipeScenarioCodeList;

}
