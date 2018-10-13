package com.shencai.eil.survey.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by zhoujx on 2018/10/11.
 */
@Data
public class EntSurveyResultParam implements Serializable {

    private List<String> categoryCodeList;

    private String enterpriseId;

    private Date updateTime;

}
