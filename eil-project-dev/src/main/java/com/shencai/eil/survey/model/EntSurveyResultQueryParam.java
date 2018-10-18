package com.shencai.eil.survey.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/10/9.
 */
@Data
public class EntSurveyResultQueryParam implements Serializable {

    private String enterpriseId;

    private String categoryCode;

    private List<String> categoryCodeList;

    private String sheetCode;

}
