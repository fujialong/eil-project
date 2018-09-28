package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/20.
 */
@Data
public class RiskMaterialVO implements Serializable {

    private String name;

    private String value;

    private String templateParamName;

    private String templateParamCode;

}
