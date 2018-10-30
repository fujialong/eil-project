package com.shencai.eil.survey.model;

import lombok.Data;

/**
 * Created by fanhj on 2018/10/18.
 */
@Data
public class SurveyExtendItemVO {
    private boolean hasChainEffective;
    private boolean hasRiskLevel;
    private boolean hasControlRiskLevel;
    private boolean hasReceptorRiskLevel;
    private boolean hasLicence;
    private boolean hasEvaluationReport;
}
