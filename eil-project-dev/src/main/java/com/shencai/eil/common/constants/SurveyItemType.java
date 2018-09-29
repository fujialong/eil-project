package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/9/28.
 */
public enum SurveyItemType {
    /**
     * basic survey item
     */
    BASIC("basic", "基础表"),

    /**
     * intensive survey item
     */
    INTENSIVE("intensive", "强化表");
    private String code;
    private String name;

    SurveyItemType(String code, String name) {
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
