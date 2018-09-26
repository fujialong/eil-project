package com.shencai.eil.policy.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/14.
 */
@Data
public class ProductClassQueryParam implements Serializable {

    private String parentId;

    private String industryId;

}
