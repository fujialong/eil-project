package com.shencai.eil.scenario.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.enterprise.inject.TransientReference;
import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author fujl
 * @since 2018-10-19
 */
public class LeakageTime extends Model<LeakageTime> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("scenario_id")
    private String scenarioId;

    @TableField("detection_system_level_code")
    private String detectionSystemLevelCode;

    @TableField("detection_system_level_name")
    private String detectionSystemLevelName;

    @TableField("shielding_system_level_code")
    private String shieldingSystemLevelCode;

    @TableField("shielding_system_level_name")
    private String shieldingSystemLevelName;

    @TableField("leakage_time")
    private Double leakageTime;

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

    public String getDetectionSystemLevelCode() {
        return detectionSystemLevelCode;
    }

    public void setDetectionSystemLevelCode(String detectionSystemLevelCode) {
        this.detectionSystemLevelCode = detectionSystemLevelCode;
    }

    public String getDetectionSystemLevelName() {
        return detectionSystemLevelName;
    }

    public void setDetectionSystemLevelName(String detectionSystemLevelName) {
        this.detectionSystemLevelName = detectionSystemLevelName;
    }

    public String getShieldingSystemLevelCode() {
        return shieldingSystemLevelCode;
    }

    public void setShieldingSystemLevelCode(String shieldingSystemLevelCode) {
        this.shieldingSystemLevelCode = shieldingSystemLevelCode;
    }

    public String getShieldingSystemLevelName() {
        return shieldingSystemLevelName;
    }

    public void setShieldingSystemLevelName(String shieldingSystemLevelName) {
        this.shieldingSystemLevelName = shieldingSystemLevelName;
    }

    public Double getLeakageTime() {
        return leakageTime;
    }

    public void setLeakageTime(Double leakageTime) {
        this.leakageTime = leakageTime;
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
        return "LeakageTime{" +
                "id=" + id +
                ", scenarioId=" + scenarioId +
                ", detectionSystemLevelCode=" + detectionSystemLevelCode +
                ", detectionSystemLevelName=" + detectionSystemLevelName +
                ", shieldingSystemLevelCode=" + shieldingSystemLevelCode +
                ", shieldingSystemLevelName=" + shieldingSystemLevelName +
                ", leakageTime=" + leakageTime +
                ", remark=" + remark +
                ", createUserId=" + createUserId +
                ", createTime=" + createTime +
                ", updateUserId=" + updateUserId +
                ", updateTime=" + updateTime +
                ", valid=" + valid +
                "}";
    }
}
