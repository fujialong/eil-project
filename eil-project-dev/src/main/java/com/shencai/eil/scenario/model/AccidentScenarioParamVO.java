package com.shencai.eil.scenario.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/10/17.
 */
@Data
public class AccidentScenarioParamVO implements Serializable {

    private String id;

    private String code;

    private String name;

    private String unitName;

    private String value;

    private Integer revisable;

    private Integer calculative;

}
