package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/20.
 */
@Data
public class EntRiskParamValueQueryParam implements Serializable {

    private String enterpriseId;

    private String templateType;

}
