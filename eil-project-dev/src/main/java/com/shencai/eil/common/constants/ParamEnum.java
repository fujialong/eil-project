package com.shencai.eil.common.constants;

/**
 * Created by zhoujx on 2018/10/11.
 */
public enum ParamEnum {

    SUFFIX_OF_EMISSION_INTENSITY("排放强度（吨/万元）", "排放强度后缀"),
    MOLECULAR_WEIGHT("B4", "分子量"),
    DENSITY("C5", "密度");

    private String code;
    private String name;

    ParamEnum(String code, String name) {
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
