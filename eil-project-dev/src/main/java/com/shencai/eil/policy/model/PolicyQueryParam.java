package com.shencai.eil.policy.model;

import com.shencai.eil.common.model.PageParam;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/9/13.
 */
@Data
public class PolicyQueryParam extends PageParam implements Serializable {

    private String enterpriseId;

    private String status;

    private String comparatorFlag;

    private String riskLevel;

    private String enterpriseName;

    private String socialCreditCode;

    private String cantonCode;

    private String industryId;

    private String techniqueId;

    private String mainProductId;

}
