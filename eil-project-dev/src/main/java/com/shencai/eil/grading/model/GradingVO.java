package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/19.
 */
@Data
public class GradingVO implements Serializable {

    private String enterpriseName;

    private String enterpriseStatus;

    private List<TargetResult> progressiveTargetResultList;

    private List<TargetResult> suddenTargetResultList;

}
