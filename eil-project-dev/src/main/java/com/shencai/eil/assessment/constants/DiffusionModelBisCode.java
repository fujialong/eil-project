package com.shencai.eil.assessment.constants;

/**
 * Created by fanhj on 2018/10/21.
 */
public enum DiffusionModelBisCode {
    WATER("water", "水模型"),
    SOIL("soil", "土壤模型");
    private String code;
    private String name;

    DiffusionModelBisCode(String code, String name) {
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
