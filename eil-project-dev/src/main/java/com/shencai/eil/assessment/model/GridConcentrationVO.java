package com.shencai.eil.assessment.model;

import lombok.Data;

/**
 * Created by fanhj on 2018/10/21.
 */
@Data
public class GridConcentrationVO {
    private String material;
    private String gridId;
    private Integer sensitiveArea;
    private String waterZoning;
    private Double x;
    private Double y;
    private Double z;
    private Double pop;
    private Double concentration;
    private String gridUseType;
    private Double acreage;
    private Double ecoValue;
    private String scenarioCode;
    private String emissionModeType;
}
