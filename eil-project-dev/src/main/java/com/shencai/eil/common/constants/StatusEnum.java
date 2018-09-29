package com.shencai.eil.common.constants;

/**
 * Created by zhoujx on 2018/9/20.
 */
public enum StatusEnum {
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
     * Wait for the survey
     */
    W_SURVEY("1050", "待查勘"),

    /**
     * Wait for the survey
     */
    I_SURVEY("1060", "查勘中"),

    /**
     * Checked
     */
    CHECKED("checked", "已审核");

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
