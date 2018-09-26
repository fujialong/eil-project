package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/20.
 */
@Data
public class TargetWeightQueryParam implements Serializable {

    private String targetType;

    private String targetCode;

}
