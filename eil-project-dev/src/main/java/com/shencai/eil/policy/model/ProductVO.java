package com.shencai.eil.policy.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/13.
 */
@Data
public class ProductVO implements Serializable {

    private String largeClassName;

    private String smallClassName;

    private String largeClassId;

    private String smallClassId;

    private String name;

    private String id;

    private String parentId;

    private List<ProductVO> children;

    private String enterpriseId;

    private List<String> productIdForVue;

}
