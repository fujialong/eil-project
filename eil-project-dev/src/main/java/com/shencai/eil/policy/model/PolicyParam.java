package com.shencai.eil.policy.model;

import com.shencai.eil.policy.entity.EnterpriseAccident;
import com.shencai.eil.system.entity.BaseFileupload;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/13.
 */
@Data
public class PolicyParam implements Serializable {

    private String enterpriseId;

    private String status;

    private String enterpriseName;

    private String socialCreditCode;

    private String postcode;

    private String cantonCode;

    private String address;

    private Double longitude;

    private Double latitude;

    private String linkPhone;

    private String industryId;

    private String industryLargeCategoryCode;

    private String techniqueId;

    private List<String> productionIdList;

    private List<EnterpriseAccident> enterpriseAccidentList;

    private Double yield;

    private Double income;

    private Integer employeesNumber;

    private Integer hasRisksReport;

    private String scale;

    private Date startTime;

    private String emissionModeId;

    private List<BaseFileupload> evaluationReportAttachmentList;

    private List<BaseFileupload> licenceAttachmentList;

}
