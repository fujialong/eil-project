package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/19.
 */
@Data
public class EnvStatisticsParamValueQueryParam implements Serializable {

    private String envStatisticsId;

    private String templateType;

}
