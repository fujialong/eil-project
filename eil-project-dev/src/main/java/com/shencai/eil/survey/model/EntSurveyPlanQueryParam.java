package com.shencai.eil.survey.model;

import com.shencai.eil.common.model.PageParam;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by fanhj on 2018/9/27.
 */
@Data
public class EntSurveyPlanQueryParam extends PageParam implements Serializable {
    private String enterpriseId;
    private String categoryCode;
}
