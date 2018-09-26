package com.shencai.eil.policy.model;

import com.shencai.eil.system.entity.BaseFileupload;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/13.
 */
@Data
public class PolicyVO implements Serializable {

    private String id;

    private String enterpriseName;

    private String status;

    private String statusName;

    private String socialCreditCode;

    private String scaleName;

    private String linkPhone;

    private String industryCategoryName;

    private String industryLargeCategoryName;

    private String industrySmallCategoryName;

    private String industryName;

    private String techniqueName;

    private List<ProductVO> productList;

    private String gradingResult;

    private String hasRisksReport;

    private String postcode;

    private Double yield;

    private Double income;

    private Integer employeesNumber;

    private String address;

    private String provinceName;

    private String cityName;

    private String cantonName;

    private String startTime;

    private String emissionModeName;

    private List<String> productNameList;

    private List<EnterpriseAccidentVO> enterpriseAccidentList;

    private List<BaseFileupload> attachmentList;

}
