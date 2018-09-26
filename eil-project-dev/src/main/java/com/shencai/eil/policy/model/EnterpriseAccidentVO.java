package com.shencai.eil.policy.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/19.
 */
@Data
public class EnterpriseAccidentVO implements Serializable {

    private String id;

    private String happenTime;

    private String levelName;

    private Integer disposed;

}
