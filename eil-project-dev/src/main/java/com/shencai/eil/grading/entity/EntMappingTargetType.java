package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author fujl
 * @since 2018-09-19
 */
public class EntMappingTargetType extends Model<EntMappingTargetType> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("ent_id")
    private String entId;
    @TableField("target_weight_type")
    private String targetWeightType;

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

    public String getEntId() {
        return entId;
    }

    public void setEntId(String entId) {
        this.entId = entId;
    }

    public String getTargetWeightType() {
        return targetWeightType;
    }

    public void setTargetWeightType(String targetWeightType) {
        this.targetWeightType = targetWeightType;
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
        return "EntMappingTargetType{" +
        "id=" + id +
        ", entId=" + entId +
        ", targetWeightType=" + targetWeightType +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
