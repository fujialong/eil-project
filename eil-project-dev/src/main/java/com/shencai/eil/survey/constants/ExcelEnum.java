package com.shencai.eil.survey.constants;

/**
 * Created by zhoujx on 2018/10/5.
 */
public enum ExcelEnum {

    MATERIAL_TYPE("物料类型"),
    PRODUCT_OPTION_CODE("B"),
    MATERIAL_NAME("物质名称"),
    INPUT_OR_OUTPUT("每年投入／产出量（吨）"),
    MAIN_POLLUTANT_OF_WATER_CODE("M38"),
    EMISSION_OF_WATER_CODE("M39"),
    WATER_POLLUTANT_EMISSION_CONCENTRATION_CODE("M40"),
    MAIN_POLLUTANT_OF_GAS_CODE("M41"),
    EMISSION_OF_GAS_CODE("M42"),
    AIR_POLLUTANT_EMISSION_CONCENTRATION_CODE("M43"),
    END_OF_SHEET("end");

    private String value;

    ExcelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
