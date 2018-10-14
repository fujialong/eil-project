package com.shencai.eil.scenario.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/10/12.
 */
@Data
public class ScenarioSelectionInfoVO {
    private String id;
    private String entId;
    private String matterName;
    private String matterType;
    private String techniqueCell;
    private String techniqueComponent;
    private String componentLocation;
    private Double onlineContent;
    private String matterProperty;
    private String explosiveCharacteristic;
    private String combustionCharacteristic;
    private String meltingPoint;
    private String boilingPoint;
    private String phaseState;
    private String flashingPoint;
    private String density;
    private String remark;

    private List<AccidentScenarioResultVO> accidentScenarioResultList;
}
