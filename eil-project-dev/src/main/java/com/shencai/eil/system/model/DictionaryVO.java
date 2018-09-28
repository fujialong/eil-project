package com.shencai.eil.system.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/15.
 */
@Data
public class DictionaryVO implements Serializable {

    private String id;

    private String code;

    private String name;

    private String parentCode;

    private List<DictionaryVO> children;

}
