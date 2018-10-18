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
    DAMAGE_ASSESSMENT("${damageAssessment}", "损害评估", ""),
    WATER_SIMULATION("${waterSimulation}", "水模拟"
            , "，进行水中污染物浓度模拟${waterSimulationTimes}次"),
    WATER_SIMULATION_TIMES("${waterSimulationTimes}", "水模拟次数", ""),
    SOIL_SIMULATION("${soilSimulation}", "土壤模拟"
            , "，土壤中污染物浓度模拟${soilSimulationTimes}次"),
    SOIL_SIMULATION_TIMES("${soilSimulationTimes}", "土壤模拟次数", ""),
    SURVEY_ATTACHMENT("${surveyAttachment}", "现场拍摄图片", ""),
    EXIST_STORAGE("${existStorage}", "是否存在地下储罐", ""),
    EXIST_PIPE("${existPipe}", "是否存在地下管道", ""),
    UNLOAD_SPECIFY("${unloadSpecify}", "装卸货区域风险防控措施是否规范", ""),
    ;

    private String code;
    private String name;

    FinalReportParam(String code, String name, String defaultValue) {
        this.code = code;
        this.name = name;
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
}
