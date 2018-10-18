package com.shencai.eil.common.constants;

/**
 * Created by zhoujx on 2018/10/16.
 */
public enum ScenarioParamEnum {

    PART_TYPE("Type1", "工艺单元部件类型"),
    ADIABATIC_EXPONENT_OF_GAS("γ", "气体绝热指数"),
    LEAKAGE_COEFFICIENT_OF_GAS("Cdg", "气体泄漏系数"),
    LEAKAGE_COEFFICIENT_OF_LIQUID("Cdl", "液体泄漏系数"),
    MOLECULAR_WEIGHT_OF_MATTER("M", "物质分子量"),
    GAS_CONSTANT("R", "气体常数"),
    OUTFLOW_COEFFICIENT("Y", "流出系数"),
    CONTAINER_PRESSURE("P", "容器压力"),
    GAS_TEMPERATURE("TG", "气体温度"),
    INTERNAL_DIAMETER_OF_PIPELINE("Pipe diameter", "管道内径"),
    DETECTION_SYSTEM_CLASSIFICATION("Type2", "探测系统分级"),
    ISOLATION_SYSTEM_CLASSIFICATION("Type3", "隔离系统分级"),
    LIQUID_DENSITY("ρ", "液体密度"),
    ENVIRONMENT_PRESSURE("P0", "环境压力"),
    GRAVITATIONAL_ACCELERATION("g", "重力加速度"),
    LIQUID_HEIGHT("h", "裂口之上液位高度");

    private String code;
    private String name;

    ScenarioParamEnum(String code, String name) {
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
