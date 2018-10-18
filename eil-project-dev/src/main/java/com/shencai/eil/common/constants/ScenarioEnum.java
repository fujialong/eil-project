package com.shencai.eil.common.constants;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-16 19:37
 **/
public enum ScenarioEnum {
    /**
     * S1
     */
    S_ONE("S1", "废物排放"),

    /**
     * S2
     */
    S_TWO("S2", "火灾爆炸"),

    /**
     * S3
     */
    S_THREE("S3", "气体泄漏"),

    /**
     * S4
     */
    S_FOUR("S4", "液体泄漏"),

    /**
     * S5
     */
    S_FIVE("S5", "液体滴漏");
    private String code;
    private String name;

    ScenarioEnum(String code, String name) {
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
