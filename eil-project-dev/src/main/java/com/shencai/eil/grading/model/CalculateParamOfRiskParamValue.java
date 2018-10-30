package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-30 14:16
 **/
@Data
public class CalculateParamOfRiskParamValue implements Serializable {

    private String id;

    private String entId;

    private String targetType;

    private String riskType;

    private Double value;

    private Double criticalQuantity;
}
