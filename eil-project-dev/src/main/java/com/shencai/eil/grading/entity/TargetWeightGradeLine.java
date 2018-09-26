package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
public class TargetWeightGradeLine extends Model<TargetWeightGradeLine> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("target_weight_id")
    private String targetWeightId;

    @TableField("percent_start")
    private Integer percentStart;

    @TableField("percent_end")
    private Integer percentEnd;

    private String type;

    @TableField("result_code")
    private String resultCode;

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

    public String getTargetWeightId() {
        return targetWeightId;
    }

    public void setTargetWeightId(String targetWeightId) {
        this.targetWeightId = targetWeightId;
    }

    public Integer getPercentStart() {
        return percentStart;
    }

    public void setPercentStart(Integer percentStart) {
        this.percentStart = percentStart;
    }

    public Integer getPercentEnd() {
        return percentEnd;
    }

    public void setPercentEnd(Integer percentEnd) {
        this.percentEnd = percentEnd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
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
        return "TargetWeightGradeLine{" +
        "id=" + id +
        ", targetWeightId=" + targetWeightId +
        ", percentStart=" + percentStart +
        ", percentEnd=" + percentEnd +
        ", type=" + type +
        ", resultCode=" + resultCode +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
