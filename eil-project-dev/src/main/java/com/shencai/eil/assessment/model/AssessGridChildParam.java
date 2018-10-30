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

    private Double pop;

    private Double sensitiveArea;

    private Double acreage;

    private Double ecoValue;

    private String waterType;

    private Double fisheryArea;

    private Double forestryArea;

    private Double agriculturalArea;

    private Double waterQuality;

    private int pointSortNum;

    private Integer gridCode;
}
