package com.shencai.eil.survey.constants;

/**
 * Created by fanhj on 2018/10/17.
 */
public enum FinalReportParam {
    ENTERPRISE_NAME("${enterpriseName}", "企业名称", ""),
    SOCIAL_CREDIT_CODE("${socialCreditCode}", "社会信用代码", ""),
    ADDRESS("${address}", "地址", ""),
    CONTACTS("${contacts}", "联系人", ""),
    LINK_PHONE("${linkPhone}", "联系电话", ""),
    INDUSTRY("${industry}", "行业", ""),
    EMPLOYEES_NUMBER("${employeesNumber}", "员工数", ""),
    INCOME("${income}", "收入", ""),
    SURVEY_INFO("${surveyInfo}", "现场勘查内容", ""),
    SURVEY_TIME("${surveyTime}", "查勘时间", ""),
    AND_GRADUAL_RISK("${andGradualRisk}", "判断是否渐进风险", ""),
    TOTAL_RISK("${totalRisk}", "风险数", ""),
    TOTAL_RISK_TYPE("${totalRiskType}", "风险类型数", ""),
    SCENARIO("${scenario}", "情景", ""),
    DAMAGE_ASSESSMENT("${damageAssessment}", "损害评估模型", "${waterModels}${soilModels}"),
    WATER_MODELS("${waterModels}", "水模型", ""),
    SOIL_MODELS("${soilModels}", "土壤模型", "，土壤污染扩散方程"),
    WATER_SIMULATION("${waterSimulation}", "水模拟"
            , "，进行水中污染物浓度模拟${waterSimulationTimes}次"),
    WATER_SIMULATION_TIMES("${waterSimulationTimes}", "水模拟次数", ""),
    SOIL_SIMULATION("${soilSimulation}", "土壤模拟"
            , "，土壤中污染物浓度模拟${soilSimulationTimes}次"),
    SOIL_SIMULATION_TIMES("${soilSimulationTimes}", "土壤模拟次数", ""),
    HAS_SIMULATION("${hasSimulation}", "模拟后的文字描述"
            , "在污染物浓度模拟的基础上，针对不同事故情景下第三者人身伤害、第三者财产损失、生态系统损害、清污与应急处置进行损失评估，"),
    SURVEY_ATTACHMENT("${surveyAttachment}", "现场拍摄图片", ""),
    EXIST_STORAGE("${existStorage}", "是否存在地下储罐", ""),
    EXIST_PIPE("${existPipe}", "是否存在地下管道", ""),
    UNLOAD_SPECIFY("${unloadSpecify}", "装卸货区域风险防控措施是否规范", ""),
    HAS_FIRE_DEVICE("${hasFireDevice}", "装卸货区域风险防控措施是否规范", ""),
    CHAIN_EFFECTIVE("${chainEffective}", "装卸货区域风险防控措施是否规范", ""),
    CHAIN_EFFECTIVE_REASON("${chainEffectiveReason}", "装卸货区域风险防控措施是否规范", ""),
    COMMON_RISK_LEVEL("${commonRiskLevel}", "装卸货区域风险防控措施是否规范", ""),
    HIGH_RISK_LEVEL_REASON("${highRiskLevelReason}", "装卸货区域风险防控措施是否规范", ""),
    CONTROL_RISK_LEVEL("${controlRiskLevel}", "装卸货区域风险防控措施是否规范", ""),
    HIGH_CONTROL_RISK_LEVEL_REASON("${highControlRiskLevelReason}", "装卸货区域风险防控措施是否规范", ""),
    RECEPTOR_RISK_LEVEL("${receptorRiskLevel}", "装卸货区域风险防控措施是否规范", ""),
    HIGH_RECEPTOR_RISK_LEVEL_REASON("${highReceptorRiskLevelReason}", "装卸货区域风险防控措施是否规范", ""),
    TECHNIQUE_STEP_SUGGESTION("${techniqueStepSuggestion}", "装卸货区域风险防控措施是否规范", ""),
    PIPELINE_STEP_SUGGESTION("${pipelineStepSuggestion}", "装卸货区域风险防控措施是否规范", ""),
    DANGER_STEP_SUGGESTION("${dangerStepSuggestion}", "装卸货区域风险防控措施是否规范", ""),
    HANDLING_PROCESS_SUGGESTION("${handlingProcessSuggestion}", "装卸货区域风险防控措施是否规范", ""),
    EMERGENCY_FAC_SUGGESTION("${emergencyFacSuggestion}", "装卸货区域风险防控措施是否规范", ""),
    EXIST_GRADUAL_RISK("${existGradualRisk}", "存在渐进风险", "渐进环境风险相较其他企业处于${gradualRiskLevel}水平，"),
    GRADUAL_RISK_LEVEL("${gradualRiskLevel}", "渐进风险级别", ""),
    SUDDEN_RISK_LEVEL("${suddenRiskLevel}", "突发风险级别", ""),
    RANKING_OF_RISK("${rankingOfRisk}", "风险排名", ""),
    IMPORTANCE_ON("${importanceOn}", "重视内容", ""),
    NEED_UPGRADE("${needUpgrade}", "显示升级内容", "经过风险查勘，企业在产品原辅料和污染物排放方面的渐进环境风险潜势高于快速分级中的预期。"),
    COST("${cost}", "应急处置与清污费用", ""),
    ESV("${esv}", "生态环境损害", ""),
    BI("${bi}", "第三者财产损失", ""),
    PI("${pi}", "第三者人身损害", ""),
    LOSS_CHART("${lossChart}", "四类损失金额图表", ""),
    GREATEST_LOSS("${greatestLoss}", "四类损失中占比最大", ""),
    GREATEST_MEDIUM_LOSS("${greatestMediumLoss}", "介质角度来看损失最大", ""),
    MEDIUM_LOSS_CHART("${mediumLossChart}", "四类损失分介质比较图表", ""),
    MEDIUM("${medium}", "需重视的环境风险", ""),
    ;

    private String code;
    private String name;
    private String defaultValue;

    FinalReportParam(String code, String name, String defaultValue) {
        this.code = code;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
