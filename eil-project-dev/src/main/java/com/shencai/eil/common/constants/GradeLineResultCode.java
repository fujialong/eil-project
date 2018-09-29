package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/9/29.
 */
public enum GradeLineResultCode {
    /**
     * low level
     */
    LOW("low", "低"),
    /**
     * medium level
     */
    MEDIUM("medium", "中"),

    /**
     * high level
     */
    HIGH("high", "高");
    private String code;
    private String name;

    GradeLineResultCode(String code, String name) {
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
