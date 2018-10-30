package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/10/23.
 */
public enum DataModelType {
    WATER_TYPE("water", "水模型"),
    SOIL_TYPE("soil", "土壤模型"),
    SINGLE("single", "single"),
    DOUBLE("double", "double"),
    LAKER("lake", "lake");

    private String code;
    private String name;

    DataModelType(String code, String name) {
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
