package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-30 10:25
 **/
@Data
public class GradingCalculateParamHeadVO implements Serializable {

    private String targetType;

    private String riskType;

    private String entId;

}
