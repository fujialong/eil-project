package com.shencai.eil.policy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
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

    @TableField("emission_mode_id")
    private String emissionModeId;

    private String status;

    public String getIndustryId() {
        return industryId;
    }

    public void setIndustryId(String industryId) {
        this.industryId = industryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmissionModeId() {
        return emissionModeId;
    }

    public void setEmissionModeId(String emissionModeId) {
        this.emissionModeId = emissionModeId;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCantonCode() {
        return cantonCode;
    }

    public void setCantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
    }

    public String getSocialCreditCode() {
        return socialCreditCode;
    }

    public void setSocialCreditCode(String socialCreditCode) {
        this.socialCreditCode = socialCreditCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getHasRisksReport() {
        return hasRisksReport;
    }

    public void setHasRisksReport(Integer hasRisksReport) {
        this.hasRisksReport = hasRisksReport;
    }

    public String getLinkPhone() {
        return linkPhone;
    }

    public void setLinkPhone(String linkPhone) {
        this.linkPhone = linkPhone;
    }

    public Double getYield() {
        return yield;
    }

    public void setYield(Double yield) {
        this.yield = yield;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Integer getEmployeesNumber() {
        return employeesNumber;
    }

    public void setEmployeesNumber(Integer employeesNumber) {
        this.employeesNumber = employeesNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

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
