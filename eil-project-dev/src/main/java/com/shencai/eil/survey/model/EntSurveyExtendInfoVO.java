package com.shencai.eil.survey.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by fanhj on 2018/10/18.
 */
@Data
public class EntSurveyExtendInfoVO {
    private String id;
    private Date surveyTime;
    private String enterpriseId;
    private String chainEffective;
    private String chainEffectiveReason;
    private String riskLevel;
    private String highRiskLevelReason;
    private String controlRiskLevel;
    private String highControlRiskLevelReason;
    private String receptorRiskLevel;
    private String highReceptorRiskLevel;
    private String techniqueStepSuggestion;
    private String pipelineStepSuggestion;
    private String dangerStepSuggestion;
    private String handlingProcessSuggestion;
    private String emergencyFacSuggestion;
    private List<EntSurveyPhotoVO> photoList;
    private List<String> licenceFileIdList;
}
