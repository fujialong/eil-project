package com.shencai.eil.policy.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhoujx on 2018/9/19.
 */
@Data
public class EnterpriseVO implements Serializable {

    private String id;

    private String name;

    private String status;

    private String statusName;

    private String provinceCode;

    private String scale;

    private String mainProductId;

    private String mainProductName;

    private String mainProductType;

    private Double mainProductQty;

    private String techniqueId;

    private String techniqueCode;

    private String emissionModeTypeCode;

    private Double yield;

    private String industryId;

    private Date startTime;

    private String socialCreditCode;

    private String cantonCode;

}
