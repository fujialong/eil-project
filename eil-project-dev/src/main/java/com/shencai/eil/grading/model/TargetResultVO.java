package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/20.
 */
@Data
public class TargetResultVO implements Serializable {

    private String targetId;

    private String parentId;

    private String targetType;

    private String targetCode;

    private String targetName;

    private List<TargetResultVO> childTargetResultList;

    private Double targetResult;

    private String riskGrade;

    private Double lowerLimitOfRiskGrade;

    private Double upperLimitOfRiskGrade;

    private String normalizationCode;

    private Double max;

    private Double min;

    private Double weight;
}
