package com.shencai.eil.scenario.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-10-18
 */
public class TechMappingLeakageModel extends Model<TechMappingLeakageModel> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("scenario_id")
    private String scenarioId;

    @TableField("technique_component_code")
    private String techniqueComponentCode;

    @TableField("technique_component_name")
    private String techniqueComponentName;

    @TableField("leakage_model_code")
    private Integer leakageModelCode;

    @TableField("leakage_model_name")
    private String leakageModelName;

    @TableField("leakage_freq")
    private Double leakageFreq;

    @TableField("leakage_freq_unit")
    private String leakageFreqUnit;

    @TableField("leakage_time")
    private Double leakageTime;

    @TableField("leakage_time_unit")
    private String leakageTimeUnit;

    @TableField("leakage_velocity_unit")
    private String leakageVelocityUnit;

    @TableField("compute_method_code")
    private String computeMethodCode;

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

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getTechniqueComponentCode() {
        return techniqueComponentCode;
    }

    public void setTechniqueComponentCode(String techniqueComponentCode) {
        this.techniqueComponentCode = techniqueComponentCode;
    }

    public String getTechniqueComponentName() {
        return techniqueComponentName;
    }

    public void setTechniqueComponentName(String techniqueComponentName) {
        this.techniqueComponentName = techniqueComponentName;
    }

    public Integer getLeakageModelCode() {
        return leakageModelCode;
    }

    public void setLeakageModelCode(Integer leakageModelCode) {
        this.leakageModelCode = leakageModelCode;
    }

    public String getLeakageModelName() {
        return leakageModelName;
    }

    public void setLeakageModelName(String leakageModelName) {
        this.leakageModelName = leakageModelName;
    }

    public Double getLeakageFreq() {
        return leakageFreq;
    }

    public void setLeakageFreq(Double leakageFreq) {
        this.leakageFreq = leakageFreq;
    }

    public String getLeakageFreqUnit() {
        return leakageFreqUnit;
    }

    public void setLeakageFreqUnit(String leakageFreqUnit) {
        this.leakageFreqUnit = leakageFreqUnit;
    }

    public Double getLeakageTime() {
        return leakageTime;
    }

    public void setLeakageTime(Double leakageTime) {
        this.leakageTime = leakageTime;
    }

    public String getLeakageTimeUnit() {
        return leakageTimeUnit;
    }

    public void setLeakageTimeUnit(String leakageTimeUnit) {
        this.leakageTimeUnit = leakageTimeUnit;
    }

    public String getLeakageVelocityUnit() {
        return leakageVelocityUnit;
    }

    public void setLeakageVelocityUnit(String leakageVelocityUnit) {
        this.leakageVelocityUnit = leakageVelocityUnit;
    }

    public String getComputeMethodCode() {
        return computeMethodCode;
    }

    public void setComputeMethodCode(String computeMethodCode) {
        this.computeMethodCode = computeMethodCode;
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
        return "TechMappingLeakageModel{" +
        "id=" + id +
        ", scenarioId=" + scenarioId +
        ", techniqueComponentCode=" + techniqueComponentCode +
        ", techniqueComponentName=" + techniqueComponentName +
        ", leakageModelCode=" + leakageModelCode +
        ", leakageModelName=" + leakageModelName +
        ", leakageFreq=" + leakageFreq +
        ", leakageFreqUnit=" + leakageFreqUnit +
        ", leakageTime=" + leakageTime +
        ", leakageTimeUnit=" + leakageTimeUnit +
        ", leakageVelocityUnit=" + leakageVelocityUnit +
        ", computeMethodCode=" + computeMethodCode +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
