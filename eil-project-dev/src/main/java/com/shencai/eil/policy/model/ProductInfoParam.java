package com.shencai.eil.policy.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/13.
 */
@Data
public class ProductInfoParam implements Serializable {

    private String largeClassName;

    private String smallClassName;

    private String name;

}
