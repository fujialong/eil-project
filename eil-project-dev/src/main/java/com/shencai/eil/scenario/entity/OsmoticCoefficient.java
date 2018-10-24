package com.shencai.eil.scenario.entity;

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
 * @since 2018-10-23
 */
public class OsmoticCoefficient extends Model<OsmoticCoefficient> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("soil_type")
    private String soilType;

    @TableField("moisture_content_upper")
    private Double moistureContentUpper;

    @TableField("moisture_content_lower")
    private Double moistureContentLower;

    private Double value;

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

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public Double getMoistureContentUpper() {
        return moistureContentUpper;
    }

    public void setMoistureContentUpper(Double moistureContentUpper) {
        this.moistureContentUpper = moistureContentUpper;
    }

    public Double getMoistureContentLower() {
        return moistureContentLower;
    }

    public void setMoistureContentLower(Double moistureContentLower) {
        this.moistureContentLower = moistureContentLower;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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
        return "OsmoticCoefficient{" +
        "id=" + id +
        ", soilType=" + soilType +
        ", moistureContentUpper=" + moistureContentUpper +
        ", moistureContentLower=" + moistureContentLower +
        ", value=" + value +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
