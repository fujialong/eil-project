package com.shencai.eil.assessment.constants;

/**
 * Created by fanhj on 2018/10/22.
 */
public enum GridUseType {
    AQUL("Aqul", "水产养殖"),
    AL("AL", "耕地"),
    FL("FL", "林地"),
    ;
    private String code;
    private String name;

    GridUseType(String code, String name) {
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
