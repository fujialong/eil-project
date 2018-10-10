package com.shencai.eil.survey.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/10/9.
 */
@Data
public class EntSurveyResultQueryParam implements Serializable {

    private String enterpriseId;

    private String categoryCode;

    private String sheetName;

}
