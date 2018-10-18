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
 * @since 2018-10-16
 */
public class HazardousReleaseRate extends Model<HazardousReleaseRate> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("upper_limit_of_lc50")
    private Double upperLimitOfLc50;

    @TableField("lower_limit_of_lc50")
    private Double lowerLimitOfLc50;

    @TableField("upper_limit_of_q1")
    private Double upperLimitOfQ1;

    @TableField("lower_limit_of_q1")
    private Double lowerLimitOfQ1;

    private String rate;

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

    public Double getUpperLimitOfLc50() {
        return upperLimitOfLc50;
    }

    public void setUpperLimitOfLc50(Double upperLimitOfLc50) {
        this.upperLimitOfLc50 = upperLimitOfLc50;
    }

    public Double getLowerLimitOfLc50() {
        return lowerLimitOfLc50;
    }

    public void setLowerLimitOfLc50(Double lowerLimitOfLc50) {
        this.lowerLimitOfLc50 = lowerLimitOfLc50;
    }

    public Double getUpperLimitOfQ1() {
        return upperLimitOfQ1;
    }

    public void setUpperLimitOfQ1(Double upperLimitOfQ1) {
        this.upperLimitOfQ1 = upperLimitOfQ1;
    }

    public Double getLowerLimitOfQ1() {
        return lowerLimitOfQ1;
    }

    public void setLowerLimitOfQ1(Double lowerLimitOfQ1) {
        this.lowerLimitOfQ1 = lowerLimitOfQ1;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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
        return "HazardousReleaseRate{" +
                "id=" + id +
                ", upperLimitOfLc50=" + upperLimitOfLc50 +
                ", lowerLimitOfLc50=" + lowerLimitOfLc50 +
                ", upperLimitOfQ1=" + upperLimitOfQ1 +
                ", lowerLimitOfQ1=" + lowerLimitOfQ1 +
                ", rate=" + rate +
                ", remark=" + remark +
                ", createUserId=" + createUserId +
                ", createTime=" + createTime +
                ", updateUserId=" + updateUserId +
                ", updateTime=" + updateTime +
                ", valid=" + valid +
                "}";
    }
}
