package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.transaction.Transactional;
import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author fujl
 * @since 2018-10-13
 */
public class TargetMaxMin extends Model<TargetMaxMin> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("target_id")
    private String targetId;

    @TableField("max_value")
    private Double maxValue;

    @TableField("min_value")
    private Double minValue;

    @TableField("industry_id")
    private String industryId;

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

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public String getIndustryId() {
        return industryId;
    }

    public void setIndustryId(String industryId) {
        this.industryId = industryId;
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
        return "TargetMaxMin{" +
                "id=" + id +
                ", targetId=" + targetId +
                ", maxValue=" + maxValue +
                ", minValue=" + minValue +
                ", industryId=" + industryId +
                ", createUserId=" + createUserId +
                ", createTime=" + createTime +
                ", updateUserId=" + updateUserId +
                ", updateTime=" + updateTime +
                ", valid=" + valid +
                "}";
    }
}
