package com.shencai.eil.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhoujx
 * @since 2018-09-18
 */
public class EnterpriseScaleDivision extends Model<EnterpriseScaleDivision> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("industry_id")
    private String industryId;

    @TableField("industry_code")
    private String industryCode;

    @TableField("employees_number_lower_limit")
    private Integer employeesNumberLowerLimit;

    @TableField("scale_code")
    private String scaleCode;

    @TableField("income_lower_limit")
    private Integer incomeLowerLimit;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Boolean valid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndustryId() {
        return industryId;
    }

    public void setIndustryId(String industryId) {
        this.industryId = industryId;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    public Integer getEmployeesNumberLowerLimit() {
        return employeesNumberLowerLimit;
    }

    public void setEmployeesNumberLowerLimit(Integer employeesNumberLowerLimit) {
        this.employeesNumberLowerLimit = employeesNumberLowerLimit;
    }

    public String getScaleCode() {
        return scaleCode;
    }

    public void setScaleCode(String scaleCode) {
        this.scaleCode = scaleCode;
    }

    public Integer getIncomeLowerLimit() {
        return incomeLowerLimit;
    }

    public void setIncomeLowerLimit(Integer incomeLowerLimit) {
        this.incomeLowerLimit = incomeLowerLimit;
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

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "EnterpriseScaleDivision{" +
                "id=" + id +
                ", industryId=" + industryId +
                ", industryCode=" + industryCode +
                ", employeesNumberLowerLimit=" + employeesNumberLowerLimit +
                ", scaleCode=" + scaleCode +
                ", incomeLowerLimit=" + incomeLowerLimit +
                ", createUserId=" + createUserId +
                ", createTime=" + createTime +
                ", updateUserId=" + updateUserId +
                ", updateTime=" + updateTime +
                ", valid=" + valid +
                "}";
    }
}
