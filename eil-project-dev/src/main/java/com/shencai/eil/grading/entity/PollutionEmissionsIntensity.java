package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author fujl
 * @since 2018-09-21
 */
public class PollutionEmissionsIntensity extends Model<PollutionEmissionsIntensity> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("pollution_type")
    private String pollutionType;

    @TableField("pollution_category")
    private String pollutionCategory;

    @TableField("direct_value")
    private Double directValue;

    @TableField("indirect_value")
    private Double indirectValue;

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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPollutionType() {
        return pollutionType;
    }

    public void setPollutionType(String pollutionType) {
        this.pollutionType = pollutionType;
    }

    public String getPollutionCategory() {
        return pollutionCategory;
    }

    public void setPollutionCategory(String pollutionCategory) {
        this.pollutionCategory = pollutionCategory;
    }

    public Double getDirectValue() {
        return directValue;
    }

    public void setDirectValue(Double directValue) {
        this.directValue = directValue;
    }

    public Double getIndirectValue() {
        return indirectValue;
    }

    public void setIndirectValue(Double indirectValue) {
        this.indirectValue = indirectValue;
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
        return "PollutionEmissionsIntensity{" +
        "id=" + id +
        ", pollutionType=" + pollutionType +
        ", pollutionCategory=" + pollutionCategory +
        ", directValue=" + directValue +
        ", indirectValue=" + indirectValue +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
