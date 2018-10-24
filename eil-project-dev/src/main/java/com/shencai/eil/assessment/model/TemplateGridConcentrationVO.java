package com.shencai.eil.assessment.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/10/22.
 */
@Data
public class TemplateGridConcentrationVO {
    private String gridId;
    private Integer sensitiveArea;
    private Double x;
    private Double y;
    private Double z;
    private Double pop;
    private Double concentration;
    private String waterZoning;
    private String gridUseType;
    private Double acreage;
    private Double ecoValue;
}
