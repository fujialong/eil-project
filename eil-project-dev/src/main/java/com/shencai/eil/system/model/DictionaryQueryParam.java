package com.shencai.eil.system.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/15.
 */
@Data
public class DictionaryQueryParam implements Serializable {

    private String dictionaryTypeCode;

    private String parentCode;

}
