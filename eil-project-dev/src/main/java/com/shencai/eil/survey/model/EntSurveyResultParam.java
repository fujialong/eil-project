package com.shencai.eil.survey.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhoujx on 2018/10/11.
 */
@Data
public class EntSurveyResultParam implements Serializable {

    private String surveyItemCategoryCode;

    private String enterpriseId;

    private Date updateTime;

}
