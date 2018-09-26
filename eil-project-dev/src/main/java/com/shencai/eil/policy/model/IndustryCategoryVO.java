package com.shencai.eil.policy.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/14.
 */
@Data
public class IndustryCategoryVO implements Serializable {

    private String id;

    private String code;

    private String name;

    private List<IndustryCategoryVO> children;

    private Integer level;

    private String parentId;

}
