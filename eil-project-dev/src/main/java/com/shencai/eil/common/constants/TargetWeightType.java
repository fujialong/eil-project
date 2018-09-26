package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/9/19.
 */
public enum TargetWeightType {
    /**
     * Progressive risk
     */
    PROGRESSIVE_RISK("progressive_risk", "渐进性风险"),

    /**
     * Sudden risk
     */
    SUDDEN_RISK("sudden_risk", "突发性风险");
    private String code;
    private String name;

    TargetWeightType(String code, String name) {
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
