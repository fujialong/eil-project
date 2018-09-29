package com.shencai.eil.common.constants;

/**
 * Compute constant enum
 *
 * @author fujl
 */
public enum ComputeConstantEnum {


    /**
     * d1
     */
    D_ONE("d1", "R3.3.2计算土壤质地在扩散方面的性质时使用", "d1"),

    /**
     * d2
     */
    D_TWO("d2", "R3.3.2计算土壤质地在扩散方面的性质时使用", "d2"),

    /**
     * d3
     */
    D_THREE("d2", "R3.3.2计算土壤质地在扩散方面的性质时使用", "d3"),

    /**
     * p1
     */
    P_ONE("p1", "R4.5计算土壤质地在扩散方面的性质时使用", "p1"),

    /**
     * p2
     */
    P_TWO("p2", "R4.5计算土壤质地在扩散方面的性质时使用", "p2"),

    /**
     * p3
     */
    P_THREE("p3", "R4.5计算土壤质地在扩散方面的性质时使用", "p3"),
    /**
     * he
     */
    R_HE("he", "he", "he"),

    /**
     * sw1
     */
    S_W_ONE("sw1", "空间权重1km", "sw1"),

    /**
     * sw2
     */
    S_W_TWO("sw2", "空间权重1km", "sw2"),

    /**
     * sw3
     */
    S_W_THREE("sw3", "空间权重5-10km", "sw3"),

    /**
     * ba1
     */
    B_A_ONE("ba1", "缓冲区总面积1km", "ba1"),

    /**
     * ba2
     */
    B_A_TWO("ba2", "缓冲区总面积1km", "ba2"),

    /**
     * ba3
     */
    B_A_THREE("ba3", "缓冲区总面积1km", "ba3"),

    /**
     * R2_2_max
     */
    R_TWO_TWO_MAX("R2_2_max", "R2.2极大法标准化最大值", "R2_2_max"),

    /**
     * R1_1_max1
     */
    R_ONE_ONE_MAX_ONE("R1_1_max1", "R1.1极大法标准化最大值（突发环境风险)", "R1_1_max1"),

    /**
     * R1_1_max2
     */
    R_ONE_ONE_MAX_TWO("R1_1_max2", "R1.1极大法标准化最大值（渐进环境风险）", "R1_1_max2"),

    /**
     * R1_2_max
     */
    R_ONE_TWO_MAX("R1_2_max", "R1_2_max", "R1_2_max"),


    /**
     * R2_1_max
     */
    R_TWO_ONE_MAX("R2_1_max", "R2_1_max", "R2_1_max"),

    /**
     *R3_1max
     */
    R_THREE_ONE_MAX("R3_1max", "R3_1max", "R3_1max"),

    /**
     * R3_3_1_min
     */
    R_THREE_THREE_ONE_MIN("R3_3_1_min", "R3.3.1极差法标准化最小值", "R3_3_1_min"),

    /**
     * R3_3_1_max
     */
    R_THREE_THREE_ONE_MAX("R3_3_1_max", "R3.3.1极差法标准化最大值", "R3_3_1_max"),

    /**
     * R3_3_2_min
     */
    R_THREE_THREE_TWO_MIN("R3_3_2_min", "R3.3.2极差法标准化最小值", "R3_3_2_min"),

    /**
     * R3_3_1_max
     */
    R_THREE_THREE_TWO_MAX("R3_3_2_max", "R3.3.2极差法标准化最大值", "R3_3_2_max"),

    /**
     * R4_3_max
     */
    R_FOUR_THREE_MAX("R4_3_max", "R4_3_max", "R4_3_max"),

    /**
     * R4_4_max
     */
    R_FOUR_FOUR_MAX("R4_4_max", "R4_4_max", "R4_4_max"),

    /**
     * R4_5_max
     */
    R_FOUR_FIVE_MAX("R4_5_max", "R4_5_max", "R4_5_max"),

    /**
     * ws
     */
    R_WS("ws", "ws", "ws"),


    /**
     * k1
     */
    K_ONE("k1", "贮存系数", "k1");

    private String code;
    private String name;
    private String englishName;

    ComputeConstantEnum(String code, String name, String englishName) {
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
