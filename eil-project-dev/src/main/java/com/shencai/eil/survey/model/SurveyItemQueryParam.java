package com.shencai.eil.survey.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fanhj on 2018/9/28.
 */
@Data
public class SurveyItemQueryParam implements Serializable {
    private String type;
    private String categoryCode;
    private List<String> categoryCodeList;
}
