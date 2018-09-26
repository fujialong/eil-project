package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/20.
 */
@Data
public class EntRiskParamValueVO implements Serializable {

    private String name;

    private Double value;

    private String remark;

    private Double criticalQuantity;

    private Double bioavailability;

    private Double biologicalEnrichment;

    private Double carcinogenicity;

}
