package com.shencai.eil.policy.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/14.
 */
@Data
public class ProductClassVO implements Serializable {

    private String id;

    private String name;

    private String parentId;

}
