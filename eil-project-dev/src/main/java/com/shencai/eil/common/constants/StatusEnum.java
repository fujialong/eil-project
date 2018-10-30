package com.shencai.eil.common.constants;

/**
 * Created by zhoujx on 2018/9/20.
 */
public enum StatusEnum {

    /**
     * Provided
     */
    TEMPORARY_STORAGE("1000", "暂存"),

    /**
     * Provided
     */
    PROVIDED("1010", "已填报"),

    /**
     * Checked
     */
    REJECTED("1020", "已驳回"),

    /**
     * Verified
     */
    VERIFIED("1030", "已核实"),

    /**
     * fasting grading ing
     */
    FASTING("1040", "快速分级中"),

    /**
     * Wait for the survey
     */
    W_SURVEY("1050", "待查勘"),

    /**
     * in doing survey
     */
    I_SURVEY("1060", "查勘中"),
    /**
     * finish survey
     */
    IN_DEPTH_EVALUATION("1070", "待深度评估"),

    SCENARIO_SELECTION("1080", "情景选择中"),

    PENDING_DAMAGE_ASSESSMENT("1090", "待损害评估"),

    IN_DAMAGE_ASSESSMENT("1100", "损害评估中"),

    FINISH_ASSESSMENT("1200", "评估完成"),
    ;


    private String code;
    private String name;

    StatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
