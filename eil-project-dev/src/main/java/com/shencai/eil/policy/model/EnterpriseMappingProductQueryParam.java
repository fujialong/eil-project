package com.shencai.eil.policy.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/14.
 */
@Data
public class EnterpriseMappingProductQueryParam implements Serializable {

    private List<String> enterpriseIdList;

    private String enterpriseId;

    private Integer isMainProduct;
}
