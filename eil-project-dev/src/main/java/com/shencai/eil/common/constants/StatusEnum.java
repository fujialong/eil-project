package com.shencai.eil.common.constants;

/**
 * Created by zhoujx on 2018/9/20.
 */
public enum StatusEnum {
    /**
     * Provided
     */
    PROVIDED("provided", "已填报"),

    /**
     * Verified
     */
    VERIFIED("verified", "已核实"),

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
