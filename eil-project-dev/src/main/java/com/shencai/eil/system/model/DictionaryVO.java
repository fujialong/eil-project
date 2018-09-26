package com.shencai.eil.system.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/15.
 */
@Data
public class DictionaryVO implements Serializable {

    private String id;

    private String code;

    private String name;

    private String parentCode;

}
