package com.shencai.eil.common.constants;

/**
 * Created by zhoujx on 2018/10/11.
 */
public enum ParamEnum {

    SUFFIX_OF_EMISSION_INTENSITY("排放强度（吨/万元）", "排放强度后缀"),
    MOLECULAR_WEIGHT("B4", "分子量"),
    DENSITY("C5", "密度"),
    PHASE_STATE_AT_ROOM_TEMPERATURE("C3", "常温下相态"),
    STABILITY("A5", "稳定性（是否稳定）"),
    LOGP("LogP", "油水分配系数"),
    LC50("LC50", "物质的LC50");

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
