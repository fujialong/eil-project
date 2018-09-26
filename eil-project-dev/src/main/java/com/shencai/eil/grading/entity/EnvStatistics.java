package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-09-21
 */
public class EnvStatistics extends Model<EnvStatistics> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("province_canton_code")
    private String provinceCantonCode;

    @TableField("industry_id")
    private String industryId;

    @TableField("technique_code")
    private String techniqueCode;

    @TableField("product_id")
    private String productId;

    @TableField("ent_scale")
    private String entScale;

    @TableField("industrial_output")
    private Double industrialOutput;

    @TableField("main_product_qty")
    private Double mainProductQty;

    private String remark;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Integer valid;

    @TableField("emission_mode_type_code")
    private String emissionModeTypeCode;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvinceCantonCode() {
        return provinceCantonCode;
    }

    public void setProvinceCantonCode(String provinceCantonCode) {
        this.provinceCantonCode = provinceCantonCode;
    }

    public String getIndustryId() {
        return industryId;
    }

    public void setIndustryId(String industryId) {
        this.industryId = industryId;
    }

    public String getTechniqueCode() {
        return techniqueCode;
    }

    public void setTechniqueCode(String techniqueCode) {
        this.techniqueCode = techniqueCode;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getEntScale() {
        return entScale;
    }

    public void setEntScale(String entScale) {
        this.entScale = entScale;
    }

    public Double getIndustrialOutput() {
        return industrialOutput;
    }

    public void setIndustrialOutput(Double industrialOutput) {
        this.industrialOutput = industrialOutput;
    }

    public Double getMainProductQty() {
        return mainProductQty;
    }

    public void setMainProductQty(Double mainProductQty) {
        this.mainProductQty = mainProductQty;
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

    public String getEmissionModeTypeCode() {
        return emissionModeTypeCode;
    }

    public void setEmissionModeTypeCode(String emissionModeTypeCode) {
        this.emissionModeTypeCode = emissionModeTypeCode;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "EnvStatistics{" +
        "id=" + id +
        ", provinceCantonCode=" + provinceCantonCode +
        ", industryId=" + industryId +
        ", techniqueCode=" + techniqueCode +
        ", productId=" + productId +
        ", entScale=" + entScale +
        ", industrialOutput=" + industrialOutput +
        ", mainProductQty=" + mainProductQty +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        ", emissionModeTypeCode=" + emissionModeTypeCode +
        "}";
    }
}
