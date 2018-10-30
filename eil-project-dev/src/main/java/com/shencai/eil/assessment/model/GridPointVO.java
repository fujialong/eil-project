package com.shencai.eil.assessment.model;

import lombok.Data;

/**
 * Created by fanhj on 2018/10/26.
 */
@Data
public class GridPointVO {
    private String material;
    private String gridId;
    private Double sensitiveArea;
    private String waterQuality;
    private Double x;
    private Double y;
    private Double z;
    private Double pop;
    private Double concentration;
    private Double avgConcentration;
    private String gridUseType;
    private Double acreage;
    private Double ecoValue;
    private Double fisheryArea;
    private Double agriculturalArea;
    private Double forestryArea;
    private String scenarioCode;
    private String emissionModeType;
    private String gridCode;
    private String waterHeightType;
}
