package com.shencai.eil.assessment.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-24 11:20
 **/
@Data
public class AssessGridChildParam implements Serializable {
    private Double lon;

    private Double lat;

    private Double len;

    private String sandRate;

    private String clayRate;

    private String powderRate;

    private Double pop;

    private Integer sensitiveArea;

    private String waterZoning;

    private Double acreage;

    private String ecoValue;

    private String waterType;
}
