package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;


/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-30 15:55
 **/
@Data
public class GradingCalculateParamLineVO implements Serializable {

    private Integer id;

    private String paramName;

    private String paramValue;

}
