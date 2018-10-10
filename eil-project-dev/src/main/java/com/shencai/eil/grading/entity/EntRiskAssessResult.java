package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
public class EntRiskAssessResult extends Model<EntRiskAssessResult> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("ent_id")
    private String entId;

    @TableField("target_weight_id")
    private String targetWeightId;

    @TableField("assess_value")
    private Double assessValue;

    @TableField("grade_line_id")
    private String gradeLineId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Integer valid;

    @TableField("create_user_id")
    private String createUserId;


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

    public String getTargetWeightId() {
        return targetWeightId;
    }

    public void setTargetWeightId(String targetWeightId) {
        this.targetWeightId = targetWeightId;
    }

    public Double getAssessValue() {
        return assessValue;
    }

    public void setAssessValue(Double assessValue) {
        this.assessValue = assessValue;
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

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getGradeLineId() {
        return gradeLineId;
    }

    public void setGradeLineId(String gradeLineId) {
        this.gradeLineId = gradeLineId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "EntRiskAssessResult{" +
        "id=" + id +
        ", entId=" + entId +
        ", targetWeightId=" + targetWeightId +
        ", assessValue=" + assessValue +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        ", gradLineId=" + gradeLineId +
        ", createUserId=" + createUserId +
        "}";
    }
}
