package com.shencai.eil.common.constants;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-28 11:34
 **/
public enum GisValueEnum {

    /**
     * R2_2_01
     */
    R_TWO_TWO_ZERO_ONE("R2_2_01", "从圆心向外第一个块缓冲区中覆盖园区的面积", "R2_2_01"),

    /**
     * R2_2_02
     */
    R_TWO_TWO_ZERO_TWO("R2_2_02", "从圆心向外第二块缓冲区中覆盖园区的面积", "R2_2_02"),

    /**
     * R3_3_01
     */
    R_THREE_THREE_ZERO_ONE("R3_3_01", "企业的海拔", "R3_3_01"),

    /**
     * R3_3_02
     */
    R_THREE_THREE_ZERO_TWO("R3_3_02", "企业周围10km范围内1km缓冲区的海拔", "R3_3_01"),

    /**
     * R3_3_03
     */
    R_THREE_THREE_ZERO_THREE("R3_3_03", "企业周围10km范围内5km缓冲区的海拔", "R3_3_03"),

    /**
     * R3_3_04
     */
    R_THREE_THREE_ZERO_FOUR("R3_3_04", "企业周围10km范围内10km缓冲区的海拔", "R3_3_04"),

    /**
     * R3_3_05
     */
    R_THREE_THREE_ZERO_FIVE("R3_3_05", "企业周围10km范围内海拔数据的方差", "R3_3_05"),

    /**
     * R3_3_1_01
     */
    R_THREE_THREE_ONE_ZERO_ONE("R3_3_1_01", "企业所处位置的水流量情况", "Water_sou"),

    /**
     * R3_3_2_01
     */
    R_THREE_THREE_TWO_ZERO_ONE("R3_3_2_01", "土壤质地", "Soil_clay"),

    /**
     * R3_3_2_02
     */
    R_THREE_THREE_TWO_ZERO_TWO("R3_3_2_02", "土壤质地", "Soil_clay"),

    /**
     * R3_3_2_03
     */
    R_THREE_THREE_TWO_ZERO_THREE("R3_3_2_03", "土壤质地", "Soil_silt");

    private String code;
    private String name;
    private String englishName;

    GisValueEnum(String code, String name, String englishName) {
        this.code = code;
        this.name = name;
        this.englishName = englishName;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getEnglishName() {
        return englishName;
    }
}
