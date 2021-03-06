package com.shencai.eil.survey.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by fanhj on 2018/9/28.
 */
@Data
public class SurveyItemVO implements Serializable {
    private String id;
    private String entId;
    private String code;
    private String name;
    private String valueType;
    private String targetWeightCode;
    private String categoryCode;
    private Integer needSelected;
    private Date updateTime;
}
