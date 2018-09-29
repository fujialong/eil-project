package com.shencai.eil.survey.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by fanhj on 2018/9/27.
 */
@Data
public class EntSurveyPlanVO implements Serializable {
    private String id;
    private String code;
    private String name;
    private String description;
    private Double importance;
    private Double cost;
    private String targetWeightCode;
    private String assessValue;
    private String defaultResult;
}
