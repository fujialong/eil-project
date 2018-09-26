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
     * Primary control mechanism
     */
    PRIMARY_CONTROL_MECHANISM("R2", "初级控制机制", "Primary control mechanism"),

    /**
     * Compliance capability
     */
    ENTERPRISE_COMPLIANCE("R2.1", "企业合规能力", "Compliance capability"),

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
     * Provincial and municipal risk control ability
     */
    R_THREE_ONE("R3.1", "省市风险管控能力", "Provincial and municipal risk control ability"),

    /**
     * Regional environmental background level
     */
    R_THREE_TWO("R3.2", "区域环境背景水平", "Regional environmental background level"),

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
