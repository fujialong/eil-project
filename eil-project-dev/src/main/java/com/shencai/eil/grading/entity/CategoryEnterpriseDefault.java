package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author fujl
 * @since 2018-09-20
 */
public class CategoryEnterpriseDefault extends Model<CategoryEnterpriseDefault> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("industry_id")
    private String industryId;

    @TableField("canton_code")
    private String cantonCode;

    @TableField("enterprise_scale")
    private Integer enterpriseScale;

    @TableField("product_type")
    private String productType;

    @TableField("water_related_default")
    private Double waterRelatedDefault;

    @TableField("gas_related_default")
    private Double gasRelatedDefault;

    @TableField("soil_related_default")
    private Double soilRelatedDefault;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Integer valid;

    public String getIndustryId() {
        return industryId;
    }

    public void setIndustryId(String industryId) {
        this.industryId = industryId;
    }

    public String getCantonCode() {
        return cantonCode;
    }

    public void setCantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getEnterpriseScale() {
        return enterpriseScale;
    }

    public void setEnterpriseScale(Integer enterpriseScale) {
        this.enterpriseScale = enterpriseScale;
    }

    public Double getWaterRelatedDefault() {
        return waterRelatedDefault;
    }

    public void setWaterRelatedDefault(Double waterRelatedDefault) {
        this.waterRelatedDefault = waterRelatedDefault;
    }

    public Double getGasRelatedDefault() {
        return gasRelatedDefault;
    }

    public void setGasRelatedDefault(Double gasRelatedDefault) {
        this.gasRelatedDefault = gasRelatedDefault;
    }

    public Double getSoilRelatedDefault() {
        return soilRelatedDefault;
    }

    public void setSoilRelatedDefault(Double soilRelatedDefault) {
        this.soilRelatedDefault = soilRelatedDefault;
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
        return "CategoryEnterpriseDefault{" +
        "id=" + id +
        ", industryId=" + industryId +
        ", cantonCode=" + cantonCode +
        ", enterpriseScale=" + enterpriseScale +
        ", productType=" + productType +
        ", waterRelatedDefault=" + waterRelatedDefault +
        ", gasRelatedDefault=" + gasRelatedDefault +
        ", soilRelatedDefault=" + soilRelatedDefault +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
