package com.shencai.eil.survey.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/10/9.
 */
@Data
public class EntSurveyResultVO implements Serializable {

    private String surveyItemName;

    private String result;

    private Integer excelRowIndex;

    private String optionCode;

    private String materialTypeOptionName;

    private String materialTypeOptionCode;

    private String materialName;

    private String output;

}
