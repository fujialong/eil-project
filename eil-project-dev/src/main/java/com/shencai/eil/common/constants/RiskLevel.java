package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/9/29.
 */
public enum RiskLevel {
    /**
     * low risk
     */
    LOW("001", "低风险"),

    /**
     * high risk
     */
    HIGH("002", "高风险");
    private String code;
    private String name;

    RiskLevel(String code, String name) {
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
