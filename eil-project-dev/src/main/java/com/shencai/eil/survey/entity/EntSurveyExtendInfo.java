package com.shencai.eil.survey.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author fanhj
 * @since 2018-10-18
 */
@Data
public class EntSurveyExtendInfo extends Model<EntSurveyExtendInfo> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("ent_id")
    private String entId;
    @TableField("survey_time")
    private Date surveyTime;

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
    @TableField("chain_effective")
    private String chainEffective;
    @TableField("chain_effective_reason")
    private String chainEffectiveReason;
    @TableField("risk_level")
    private String riskLevel;
    @TableField("high_risk_level_reason")
    private String highRiskLevelReason;
    @TableField("control_risk_level")
    private String controlRiskLevel;
    @TableField("high_control_risk_level_reason")
    private String highControlRiskLevelReason;
    @TableField("receptor_risk_level")
    private String receptorRiskLevel;
    @TableField("high_receptor_risk_level")
    private String highReceptorRiskLevel;
    @TableField("technique_step_suggestion")
    private String techniqueStepSuggestion;
    @TableField("pipeline_step_suggestion")
    private String pipelineStepSuggestion;
    @TableField("danger_step_suggestion")
    private String dangerStepSuggestion;
    @TableField("handling_process_suggestion")
    private String handlingProcessSuggestion;
    @TableField("emergency_fac_suggestion")
    private String emergencyFacSuggestion;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "EntSurveyExtendInfo{" +
        "id=" + id +
        ", entId=" + entId +
        ", surveyTime=" + surveyTime +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        ", chainEffective=" + chainEffective +
        ", chainEffectiveReason=" + chainEffectiveReason +
        ", riskLevel=" + riskLevel +
        ", highRiskLevelReason=" + highRiskLevelReason +
        ", controlRiskLevel=" + controlRiskLevel +
        ", highControlRiskLevelReason=" + highControlRiskLevelReason +
        ", receptorRiskLevel=" + receptorRiskLevel +
        ", highReceptorRiskLevel=" + highReceptorRiskLevel +
        ", techniqueStepSuggestion=" + techniqueStepSuggestion +
        ", pipelineStepSuggestion=" + pipelineStepSuggestion +
        ", dangerStepSuggestion=" + dangerStepSuggestion +
        ", handlingProcessSuggestion=" + handlingProcessSuggestion +
        ", emergencyFacSuggestion=" + emergencyFacSuggestion +
        "}";
    }
}
