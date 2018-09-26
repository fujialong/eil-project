package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/9/19.
 */
public enum TemplateCategory {
    /**
     * Gradual risk
     */
    GRADUAL_RISK("gradual_risk", "企业渐进性风险判断"),

    /**
     * Progressive judgment - Stability
     */
    STABILITY("stability", "渐进性判断-稳定性"),

    /**
     * Archive all data templates
     */
    ENV_BASE_TEMPLE("env_base", "环统档案所有数据模板"),

    /**
     * Emissions intensity
     */
    EMISSIONS_INTENSITY("emissions_intensity", "排放强度"),

    /**
     * Fast grading
     */
    FAST_GRADING("fast_grading", "快速分级模板"),

    /**
     * Raw and auxiliary material input coefficient template
     */
    RAW_MATERIAL("raw_material", "原辅料投入系数模板");

    private String code;
    private String name;

    TemplateCategory(String code, String name) {
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
