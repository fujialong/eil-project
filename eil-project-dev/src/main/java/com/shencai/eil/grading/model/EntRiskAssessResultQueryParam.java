package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/20.
 */
@Data
public class EntRiskAssessResultQueryParam implements Serializable {

    private List<String> parentIdList;

    private String enterpriseId;

    private List<String> targetType;

}
