package com.shencai.eil.policy.model;

import com.shencai.eil.system.entity.BaseFileupload;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/13.
 */
@Data
public class PolicyVO implements Serializable {

    private String id;

    private String auditRemark;

    private String enterpriseName;

    private String status;

    private String statusName;

    private String socialCreditCode;

    private String scaleName;

    private String linkPhone;

    private String contacts;

    private String industryCategoryName;

    private String industryLargeCategoryName;

    private String industrySmallCategoryName;

    private String industryName;

    private String industryCategoryId;

    private String industryLargeCategoryId;

    private String industrySmallCategoryId;

    private String industryId;

    private List<String> industryIdDefault;

    private String techniqueName;

    private String techniqueId;

    private List<ProductVO> productList;

    private String gradingResult;

    private Integer hasRisksReport;

    private String postcode;

    private Double yield;

    private Double income;

    private Integer employeesNumber;

    private String address;

    private String provinceName;

    private String cityName;

    private String provinceCode;

    private Double longitude;

    private Double latitude;

    private String cityCode;

    private List<String> cantonCodeDefault;

    private String cantonName;

    private Date startTime;

    private String emissionModeName;

    private String emissionModeId;

    private List<String> productNameList;

    private List<EnterpriseAccidentVO> enterpriseAccidentList;

    private Integer hasDamageEvent;

    private Integer enterpriseAccidentCount;

    private List<BaseFileupload> evaluationReportAttachmentList;

    private List<BaseFileupload> licenceAttachmentList;

    private Date depthEvaluationTime;

    private String riskLevel;

}
