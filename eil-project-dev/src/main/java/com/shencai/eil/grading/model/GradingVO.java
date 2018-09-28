package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/19.
 */
@Data
public class GradingVO implements Serializable {

    private String enterpriseName;

    private String enterpriseStatus;

    private TargetResultVO progressiveTargetResultOfRu;

    private TargetResultVO suddenTargetResultOfRu;

}
