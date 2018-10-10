package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by fanhj on 2018/9/19.
 */
@Data
public class ParamVO implements Serializable {

    private String id;

    private String name;

    private String value;

    private String templateCode;

    private String templateCategoryType;
}
