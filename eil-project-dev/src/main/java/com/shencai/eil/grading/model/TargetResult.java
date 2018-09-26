package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/20.
 */
@Data
public class TargetResult implements Serializable {

    private String targetId;

    private String targetCode;

    private String targetName;

    private List<EntRiskAssessResultVO> entRiskAssessResultList;

    private Double targetResult;

}
