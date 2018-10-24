package com.shencai.eil.survey.constants;

/**
 * Created by zhoujx on 2018/10/5.
 */
public enum ExcelEnum {

    PRODUCT_OPTION_CODE("B"),
    MATERIAL_OPTION_CODE("A"),
    EMISSIONS_FORM_OPTION_CODE_WATER("B"),
    EMISSIONS_FORM_OPTION_CODE_GAS("A"),
    MAIN_POLLUTANT_OF_WATER_CODE("M38"),
    EMISSION_OF_WATER_CODE("M39"),
    WATER_POLLUTANT_EMISSION_CONCENTRATION_CODE("M40"),
    MAIN_POLLUTANT_OF_GAS_CODE("M41"),
    EMISSION_OF_GAS_CODE("M42"),
    AIR_POLLUTANT_EMISSION_CONCENTRATION_CODE("M43"),
    MAIN_HEAVY_METAL_POLLUTANT_OF_CODE("M45"),
    EMISSIONS_FORM_CODE("M46"),
    ANNUAL_EMISSION_CODE("M47"),
    END_OF_SHEET("end");

    private String value;

    ExcelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
