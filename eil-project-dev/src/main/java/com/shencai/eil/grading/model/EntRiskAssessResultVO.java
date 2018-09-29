package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/20.
 */
@Data
public class EntRiskAssessResultVO implements Serializable {

    private String id;

    private String targetId;

    private String targetCode;

    private String targetName;

    private String targetResult;

    private String parentId;

    private String gradeLineCode;

    private String targetType;
}
