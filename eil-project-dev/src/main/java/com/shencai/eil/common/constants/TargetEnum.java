package com.shencai.eil.common.constants;

/**
 * Created by zhoujx on 2018/9/20.
 */
public enum TargetEnum {

    /**
     * Risk factors
     */
    RISK_FACTOR("R1", "风险因子", "Risk factors"),

    /**
     * Sudden risk
     */
    SUDDEN_RISK("sudden_risk", "突发风险类型", "Sudden risk"),


    /**
     * Progressive risk
     */
    PROGRESSIVE_RISK("progressive_risk", "突发风险类型", "Progressive risk"),

    /**
     * Primary control mechanism
     */
    PRIMARY_CONTROL_MECHANISM("R2", "初级控制机制", "Primary control mechanism"),

    /**
     * Compliance capability
     */
    ENTERPRISE_COMPLIANCE("R2.1", "企业合规能力", "Compliance capability"),

    /**
     * R2.2
     */
    R_TWO_POINT_TWO("R2.2", "群发链发效应", "Cluster chain effect"),

    /**
     * Secondary control mechanism
     */
    SECONDARY_CONTROL_MECHANISM("R3", "次级控制机制", "Secondary control mechanism"),

    /**
     * Receptor sensitivity
     */
    RECEPTOR_SENSITIVITY("R4", "受体敏感性", "Receptor sensitivity"),

    /**
     * Overall rating
     */
    GENERAL_COMMENT("Ru", "总评", "Overall rating"),

    /**
     * R3.1
     */
    R_THREE_ONE("R3.1", "省市风险管控能力", "Provincial and municipal risk control ability"),

    /**
     * R3.2
     */
    R_THREE_TWO("R3.2", "区域环境背景水平", "Regional environmental background level"),

    /**
     * R3.3
     */
    R_THREE_THREE("R3.3", "周边环境", "surroundings"),

    /**
     * R4.1
     */
    R_FOUR_ONE("R4.1", "第三者人身损害", "R4.1"),

    /**
     * R4.1.1
     */
    R_FOUR_ONE_ONE("R4.1.1", "R4_1_1_w", "R4_1_1_w"),

    /**
     * R4.1.2
     */
    R_FOUR_ONE_TWO("R4.1.2", "R4_1_2_w", "R4_1_2_w"),

    /**
     * R4.2
     */
    R_FOUR_TWO("R4.2", "第三者财产损失", "R4.2"),

    /**
     * R4.2.1
     */
    R_FOUR_TWO_ONE("R4.2.1", "R4.2.1w", "R4.2.1"),

    /**
     * R4.2.2
     */
    R_FOUR_TWO_TWO("R4.2.2", "R4.2.2_w", "R4.2.2_w"),

    /**
     * R4.3
     */
    R_FOUR_THREE("R4.3", "生态环境损害", "R4.3"),

    /**
     * R4.4
     */
    R_FOUR_FOUR("R4.4", "应急处置与清污费用（地表水）", "R4.4"),

    /**
     * R4.5
     */
    R_FOUR_FIVE("R4.5", "应急处置与清污费用（地表水）", "R4.5"),

    /**
     * R1.1
     */
    SECONDARY_INDICATORS_OF_RISK_FACTORS("R1.1", "风险因子二级指标R1.1", "R1.1"),

    /**
     * R1.2
     */
    RISK_FACTORS_SECONDARY_STEP("R1.2", "风险因子二级指标R1.2", "R1.2");

    private String code;
    private String name;
    private String englishName;

    TargetEnum(String code, String name, String englishName) {
        this.code = code;
        this.name = name;
        this.englishName = englishName;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getEnglishName() {
        return englishName;
    }
}
