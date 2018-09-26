package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author fujl
 * @since 2018-09-20
 */
public class EntViolationHistory extends Model<EntViolationHistory> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("ent_name")
    private String entName;
    @TableField("social_credit_code")
    private String socialCreditCode;
    @TableField("industry_small_class")
    private String industrySmallClass;
    @TableField("province_canton_code")
    private String provinceCantonCode;
    @TableField("water_related_violation_qty")
    private Double waterRelatedViolationQty;
    @TableField("gas_related_violation_qty")
    private Double gasRelatedViolationQty;
    @TableField("soil_related_violation_qty")
    private Double soilRelatedViolationQty;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Integer valid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getSocialCreditCode() {
        return socialCreditCode;
    }

    public void setSocialCreditCode(String socialCreditCode) {
        this.socialCreditCode = socialCreditCode;
    }

    public String getIndustrySmallClass() {
        return industrySmallClass;
    }

    public void setIndustrySmallClass(String industrySmallClass) {
        this.industrySmallClass = industrySmallClass;
    }

    public String getProvinceCantonCode() {
        return provinceCantonCode;
    }

    public void setProvinceCantonCode(String provinceCantonCode) {
        this.provinceCantonCode = provinceCantonCode;
    }

    public Double getWaterRelatedViolationQty() {
        return waterRelatedViolationQty;
    }

    public void setWaterRelatedViolationQty(Double waterRelatedViolationQty) {
        this.waterRelatedViolationQty = waterRelatedViolationQty;
    }

    public Double getGasRelatedViolationQty() {
        return gasRelatedViolationQty;
    }

    public void setGasRelatedViolationQty(Double gasRelatedViolationQty) {
        this.gasRelatedViolationQty = gasRelatedViolationQty;
    }

    public Double getSoilRelatedViolationQty() {
        return soilRelatedViolationQty;
    }

    public void setSoilRelatedViolationQty(Double soilRelatedViolationQty) {
        this.soilRelatedViolationQty = soilRelatedViolationQty;
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
        return "EntViolationHistory{" +
        "id=" + id +
        ", entName=" + entName +
        ", socialCreditCode=" + socialCreditCode +
        ", industrySmallClass=" + industrySmallClass +
        ", provinceCantonCode=" + provinceCantonCode +
        ", waterRelatedViolationQty=" + waterRelatedViolationQty +
        ", gasRelatedViolationQty=" + gasRelatedViolationQty +
        ", soilRelatedViolationQty=" + soilRelatedViolationQty +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
