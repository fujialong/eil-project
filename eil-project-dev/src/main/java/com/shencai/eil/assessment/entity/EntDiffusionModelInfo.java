package com.shencai.eil.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhoujx
 * @since 2018-10-20
 */
public class EntDiffusionModelInfo extends Model<EntDiffusionModelInfo> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("ent_id")
    private String entId;

    private String material;

    @TableField("scenario_id")
    private String scenarioId;

    @TableField("release_amount")
    private Double releaseAmount;

    @TableField("release_concentration")
    private Double releaseConcentration;

    @TableField("release_time")
    private Integer releaseTime;

    private Integer wading;

    @TableField("emission_mode_type")
    private String emissionModeType;

    @TableField("exist_water_env")
    private Integer existWaterEnv;

    @TableField("considering_water_env_risk")
    private Integer consideringWaterEnvRisk;

    @TableField("surface_water_condition")
    private String surfaceWaterCondition;

    @TableField("access_type")
    private String accessType;

    private String stability;

    private String density;

    private String state;

    @TableField("octan_water_partition")
    private String octanWaterPartition;

    @TableField("pollutant_type")
    private String pollutantType;

    @TableField("model_id")
    private String modelId;

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

    public String getEntId() {
        return entId;
    }

    public void setEntId(String entId) {
        this.entId = entId;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Double getReleaseAmount() {
        return releaseAmount;
    }

    public void setReleaseAmount(Double releaseAmount) {
        this.releaseAmount = releaseAmount;
    }

    public Double getReleaseConcentration() {
        return releaseConcentration;
    }

    public void setReleaseConcentration(Double releaseConcentration) {
        this.releaseConcentration = releaseConcentration;
    }

    public Integer getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Integer releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getEmissionModeType() {
        return emissionModeType;
    }

    public void setEmissionModeType(String emissionModeType) {
        this.emissionModeType = emissionModeType;
    }

    public String getSurfaceWaterCondition() {
        return surfaceWaterCondition;
    }

    public void setSurfaceWaterCondition(String surfaceWaterCondition) {
        this.surfaceWaterCondition = surfaceWaterCondition;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getStability() {
        return stability;
    }

    public void setStability(String stability) {
        this.stability = stability;
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOctanWaterPartition() {
        return octanWaterPartition;
    }

    public void setOctanWaterPartition(String octanWaterPartition) {
        this.octanWaterPartition = octanWaterPartition;
    }

    public String getPollutantType() {
        return pollutantType;
    }

    public void setPollutantType(String pollutantType) {
        this.pollutantType = pollutantType;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
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

    public Integer getWading() {
        return wading;
    }

    public void setWading(Integer wading) {
        this.wading = wading;
    }

    public Integer getExistWaterEnv() {
        return existWaterEnv;
    }

    public void setExistWaterEnv(Integer existWaterEnv) {
        this.existWaterEnv = existWaterEnv;
    }

    public Integer getConsideringWaterEnvRisk() {
        return consideringWaterEnvRisk;
    }

    public void setConsideringWaterEnvRisk(Integer consideringWaterEnvRisk) {
        this.consideringWaterEnvRisk = consideringWaterEnvRisk;
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
        return "EntDiffusionModelInfo{" +
                "id=" + id +
                ", entId=" + entId +
                ", material=" + material +
                ", scenarioId=" + scenarioId +
                ", releaseAmount=" + releaseAmount +
                ", releaseConcentration=" + releaseConcentration +
                ", releaseTime=" + releaseTime +
                ", wading=" + wading +
                ", emissionModeType=" + emissionModeType +
                ", existWaterEnv=" + existWaterEnv +
                ", consideringWaterEnvRisk=" + consideringWaterEnvRisk +
                ", surfaceWaterCondition=" + surfaceWaterCondition +
                ", accessType=" + accessType +
                ", stability=" + stability +
                ", density=" + density +
                ", state=" + state +
                ", octanWaterPartition=" + octanWaterPartition +
                ", pollutantType=" + pollutantType +
                ", modelId=" + modelId +
                ", remark=" + remark +
                ", createUserId=" + createUserId +
                ", createTime=" + createTime +
                ", updateUserId=" + updateUserId +
                ", updateTime=" + updateTime +
                ", valid=" + valid +
                "}";
    }
}
