package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by zhoujx on 2018/10/10.
 */
@Data
public class EntRiskParamValueParam implements Serializable {

    private String enterpriseId;

    private List<String> templateCategoryTypeList;

    private Date updateTime;

}
