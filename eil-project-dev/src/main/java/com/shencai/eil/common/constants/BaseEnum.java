package com.shencai.eil.common.constants;

/**
 * Created by zhoujx on 2018/9/13.
 */
public enum BaseEnum {
    /**
     * Whether it is effective
     */
    VALID_YES(1, "effective", "有效"),

    /**
     * Level
     */
    LEVEL_ONE(1, "Level 1", "层级1"),

    /**
     * Is the main product
     */
    IS_MAIN_PRODUCT_YES(1, "Is the main product", "是主产品"),

    /**
     * Not the main product
     */
    IS_MAIN_PRODUCT_NO(0, "Not the main product", "不是主产品"),

    /**
     * The year of first record
     */
    RECORD_BEGIN_YEAR(2014, "The year of first record", "记录最初年份（2014）"),

    CONSTANT_K1("k1", "Computing coefficient for R1.1", "R1.1计算系数k1");

    private Object code;
    private String englishName;
    private String chineseName;

    BaseEnum(Object code, String englishName, String chineseName) {
        this.code = code;
        this.englishName = englishName;
        this.chineseName = chineseName;
    }

    public Object getCode() {
        return code;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getChineseName() {
        return chineseName;
    }
}
