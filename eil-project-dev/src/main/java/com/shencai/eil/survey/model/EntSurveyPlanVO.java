package com.shencai.eil.survey.model;

import com.shencai.eil.system.entity.BaseFileupload;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
    private Double assessValue;
    private String defaultResult;
    private Integer hasAttachment;
    List<BaseFileupload> attachmentList;
}
