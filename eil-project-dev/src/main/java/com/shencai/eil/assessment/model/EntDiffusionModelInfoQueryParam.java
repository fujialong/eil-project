package com.shencai.eil.assessment.model;

import com.shencai.eil.common.model.PageParam;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/10/23.
 */
@Data
public class EntDiffusionModelInfoQueryParam extends PageParam implements Serializable {

    private String enterpriseId;

    private String modelType;

}
