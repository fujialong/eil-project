package com.shencai.eil.policy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Data
public class EnterpriseInfo extends Model<EnterpriseInfo> {

    private static final long serialVersionUID = 1L;

    private String id;

    private String category;

    private String type;

    private String code;

    private String name;

    @TableField("canton_code")
    private String cantonCode;

    @TableField("industry_id")
    private String industryId;

    @TableField("social_credit_code")
    private String socialCreditCode;

    private String postcode;

    private Double longitude;

    private Double latitude;

    private String address;

    @TableField("branch_code")
    private String branchCode;

    @TableField("has_risks_report")
    private Integer hasRisksReport;

    @TableField("link_phone")
    private String linkPhone;

    private Double yield;

    private Double income;

    @TableField("employees_number")
    private Integer employeesNumber;

    private String remark;

    private String scale;

    @TableField("emission_mode_id")
    private String emissionModeId;

    private String status;
    @TableField("need_survey_upgrade")
    private Integer needSurveyUpgrade;
    @TableField("risk_level")
    private String riskLevel;

    @TableField("audit_remark")
    private String auditRemark;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    @TableField("start_time")
    private Date startTime;

    private Integer valid;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "EnterpriseInfo{" +
        "id=" + id +
        ", category=" + category +
        ", type=" + type +
        ", code=" + code +
        ", name=" + name +
        ", cantonCode=" + cantonCode +
        ", industryId=" + industryId +
        ", socialCreditCode=" + socialCreditCode +
        ", address=" + address +
        ", postcode=" + postcode +
        ", longitude=" + longitude +
        ", latitude=" + latitude +
        ", branchCode=" + branchCode +
        ", hasRisksReport=" + hasRisksReport +
        ", linkPhone=" + linkPhone +
        ", yield=" + yield +
        ", income=" + income +
        ", employeesNumber=" + employeesNumber +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
