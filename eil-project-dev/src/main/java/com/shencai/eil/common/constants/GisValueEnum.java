package com.shencai.eil.common.constants;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-28 11:34
 **/
public enum GisValueEnum {


    CLASS_ONE("1","初级控制机制相关gis数据","R2.2"),

    CLASS_TWO("2","区域环境背景水平相关gis数据","R3.2.1"),

    CLASS_THREE("3","是否有利于污染物扩散相关gis数据","R3.3"),

    CLASS_FOUR("4","第三者人生损害相关gis数据","R4.1"),

    CLASS_FIVE("5","第三者财产损失","R4.2"),

    CLASS_SIX("6","生态环境损害和应急处置与清污费用（地表水）相关gis数据","R4.3_R4.4"),

    DEM("dem","企业周围海拔","dem"),

    POP("pop","企业周围的人口总量","pop"),

    GDP("gpd","企业周围的GDP总产值","gdp"),

    /**
     * R2_2_01
     */
    R_TWO_TWO_ZERO_ONE("R2_2_01", "从圆心向外第一个块缓冲区中覆盖园区的面积", "R2_2_01"),

    /**
     * R2_2_02
     */
    R_TWO_TWO_ZERO_TWO("R2_2_02", "从圆心向外第二块缓冲区中覆盖园区的面积", "R2_2_02"),

    /**
     * R3_2_1
     */
    R_THREE_TWO_ONE("R3_2_1", "R3_2_1", "R3_2_1"),

    /**
     * R3_2_2
     */
    R_THREE_TWO_TWO("R3_2_2", "R3_2_2", "R3_2_2"),

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
    R_THREE_THREE_TWO_ZERO_THREE("R3_3_2_03", "土壤质地", "Soil_silt"),

    /**
     * R4_1_01
     */
    R_FOUR_ONE_ZERO_ONE("R4_1_01", "企业1km缓冲区覆盖范围的总人口", "R4_1_01"),

    /**
     * R4_1_02
     */
    R_FOUR_ONE_ZERO_TWO("R4_1_02", "企业1-5km缓冲区覆盖范围的总人口", "R4_1_02"),

    /**
     * R4_1_03
     */
    R_FOUR_ONE_ZERO_THREE("R4_1_03", "企业5-10km缓冲区覆盖范围的总人口", "R4_1_03"),

    /**
     * R4_1_1_01
     */
    R_FOUR_ONE_ONE_ZERO_ONE("R4_1_1_01", "企业1km缓冲区覆盖医院和教育机构个数", "R4_1_1_01"),

    /**
     * R4_1_1_02
     */
    R_FOUR_ONE_ONE_ZERO_TWO("R4_1_1_02", "企业1-5km缓冲区覆盖医院和教育机构个数", "R4_1_1_02"),


    /**
     * R4_1_1_03
     */
    R_FOUR_ONE_ONE_ZERO_THREE("R4_1_1_03", "企业5-10km缓冲区覆盖医院和教育机构个数", "R4_1_1_03"),

    /**
     * R4_1_2_01
     */
    R_FOUR_ONE_TWO_ZERO_ONE("R4_1_2_01", "企业1km缓冲区覆盖范围的饮用水源地的个数", "R4_1_2_01"),

    /**
     * R4_1_2_02
     */
    R_FOUR_ONE_TWO_ZERO_TWO("R4_1_2_02", "企业1-5km缓冲区覆盖范围的饮用水源地的个数", "R4_1_2_02"),


    /**
     * R4_1_2_03
     */
    R_FOUR_ONE_TWO_ZERO_THREE("R4_1_2_03", "企业5-10km缓冲区覆盖范围的饮用水源地的个数", "R4_1_2_03"),

    /**
     * R4_2_01
     */
    R_FOUR_TWO_ZERO_ONE("R4_2_01", "企业1km缓冲区覆盖范围的GDP", "R4_2_01"),

    /**
     * R4_2_02
     */
    R_FOUR_TWO_ZERO_TWO("R4_2_02", "企业1-5km缓冲区覆盖范围的GDP", "R4_2_02"),

    /**
     * R4_2_03
     */
    R_FOUR_TWO_ZERO_THREE("R4_2_03", "企业5-10km缓冲区覆盖范围的GDP", "R4_2_03"),

    /**
     * R4_2_1_01
     */
    R_FOUR_TWO_ONE_ZERO_ONE("R4_2_1_01", "企业1km缓冲区覆盖范围的水田和旱田面积", "R4_2_1_01"),

    /**
     * R4_2_1_02
     */
    R_FOUR_TWO_ONE_ZERO_TWO("R4_2_1_02", "企业1-5km缓冲区覆盖范围的水田和旱田面积", "R4_2_1_02"),


    /**
     * R4_2_1_03
     */
    R_FOUR_TWO_ONE_ZERO_THREE("R4_2_1_03", "企业5-10km缓冲区覆盖范围的水田和旱田面积", "R4_2_1_03"),

    /**
     * R4_2_2_01
     */
    R_FOUR_TWO_TWO_ZERO_ONE("R4_2_2_01", "企业1km缓冲区覆盖范围的农渔业用水区个数", "R4_2_2_01"),

    /**
     * R4_2_2_02
     */
    R_FOUR_TWO_TWO_ZERO_TWO("R4_2_2_02", "企业1-5km缓冲区覆盖范围的农渔业用水区个数", "R4_2_2_02"),


    /**
     * R4_2_2_03
     */
    R_FOUR_TWO_TWO_ZERO_THREE("R4_2_2_03", "企业5-10km缓冲区覆盖范围的农渔业用水区个数", "R4_2_2_03"),

    /**
     * R4_3_01
     */
    R_FOUR_THREE_ZERO_ONE("R4_3_01", "企业1km缓冲区覆盖范围的生态系统服务价值", "R4_3_01"),

    /**
     * R4_3_02
     */
    R_FOUR_THREE_ZERO_TWO("R4_3_02", "企业1-5km缓冲区覆盖范围的生态系统服务价值", "R4_3_02"),

    /**
     * R4_3_03
     */
    R_FOUR_THREE_ZERO_THREE("R4_3_03", "企业5-10km缓冲区覆盖范围的生态系统服务价值", "R4_3_03"),


    /**
     * R4_4
     */
    R_FOUR_FOUR("R4_4", "10km范围里距离最近的网格，以“目标水质”为输出；", "R4_4");


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
