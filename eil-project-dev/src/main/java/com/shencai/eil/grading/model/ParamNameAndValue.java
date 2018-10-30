package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-30 10:38
 **/
@Data
public class ParamNameAndValue implements Serializable {

    private String paramName;

    private Double paramValue;
}
