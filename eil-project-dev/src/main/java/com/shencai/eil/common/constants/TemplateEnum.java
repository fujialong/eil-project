package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/9/19.
 */
public enum TemplateEnum {
    /**
     * Raw material
     */
    RAW_OR_AUXILIARY_MATERIALS("raw_or_auxiliary_materials", "原辅料"),

    /**
     * Heavy metal
     */
    HEAVY_METAL("heavy_metal", "重金属"),

    /**
     * A6
     */
    CRITICAL_QUANTITY("A6", "临界量"),

    /**
     * A1
     */
    BIOAVAILABILITY("A1", "生物可利用性"),

    /**
     * A2
     */
    BIOLOGICAL_ENRICHMENT("A2", "生物富集性"),

    /**
     * D5
     */
    CARCINOGENICITY("D5", "致癌性");

    private String code;
    private String name;

    TemplateEnum(String code, String name) {
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
