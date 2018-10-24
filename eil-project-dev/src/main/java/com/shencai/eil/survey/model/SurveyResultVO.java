package com.shencai.eil.survey.model;

import lombok.Data;

/**
 * Created by fanhj on 2018/10/17.
 */
@Data
public class SurveyResultVO {
    private Integer rowIndex;
    private String componentType;
    private String componentLocation;
    private String wasteCollection;
    private String wastePrevent;
    private String hasFireAlarmSystem;
    private String hasOutFireSystem;
}
