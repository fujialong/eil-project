package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/9/19.
 */
public enum TemplateEnum {
    /**
     * Raw material
     */
    RAW_OR_AUXILIARY_MATERIALS("raw_material", "原辅料"),
    PRODUCTION_STATUS_OF_PRODUCTS("production", "主要产品生产情况"),
    OTHER_EMISSION_INTENSITY("other_emission_intensity", "其他污染物废气排放强度"),
    OTHER_EFFLUENT_INTENSITY("other_effluent_intensity", "其他污染物废水排放强度"),
    EMISSION_INTENSITY("emission_intensity", "重金属废气排放强度"),
    EFFLUENT_INTENSITY("effluent_intensity", "重金属废水排放强度"),
    SCENARIO_SELECTION("scenario_selection", "情景和源项"),
    MODEL_MATCHING("model_matching", "模型匹配"),
    /**
     * A6
     */
    CRITICAL_QUANTITY("A7", "临界量"),

    /**
     * A1
     */
    BIOAVAILABILITY("A1", "生物可利用性"),

    /**
     * A2
     */
    BIOLOGICAL_ENRICHMENT("A2", "生物富集性"),

    /**
     * A4
     */
    CARCINOGENICITY("A4", "致癌性"),

    /**
     * A5
     */
    STABILITY("A5", "稳定性（是否稳定）"),

    TEMPLATE_ID_OF_GAS("L", "废气排放强度模板id"),
    TEMPLATE_ID_OF_WATER("N", "废水排放强度模板id"),
    TEMPLATE_ID_OF_HEAVY_METAL_GAS("K", "重金属废气排放强度模板id"),
    TEMPLATE_ID_OF_HEAVY_METAL_WATER("M", "重金属废水排放强度模板id");

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
