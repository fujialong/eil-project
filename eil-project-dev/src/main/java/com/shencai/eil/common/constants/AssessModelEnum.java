package com.shencai.eil.common.constants;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-28 11:34
 **/
public enum AssessModelEnum {


    ONE("1","河流完全混合模式","River_Completely_Mixed"),

    TWO("2","二维稳态混合模式（岸边排放）","Two_Steady_Shore"),

    THREE("3","二维稳态混合模型（非岸边排放）","Two_Steady_Nonshore"),

    FOUR("4","二维稳态混合衰减模式（岸边排放）","Two_Steady_Attenuation_Shore"),

    FIVE("5","二维稳态混合衰减模式（非岸边排放）","Two_Steady_Attenuation_Nonshore"),

    SIX("6","托马斯模式","Thomas_Mode"),

    SEVEN("7","欧康纳河口衰减模式（上溯时&下泄时）","OConnor_Attenuation"),

    EIGHT("8","湖泊稳态二维环流混合模式（岸边排放）","Lake_Two_Steady_Shore"),

    NINE("9","湖泊稳态二维环流混合模式（非岸边排放）","Lake_Two_Steady_Nonshore"),

    TEN("10","湖泊稳态二维混合衰减混合模式（岸边排放）","Lake_Two_Steady_Attenuation_Shore"),

    ELEVEN("11","湖泊稳态二维混合衰减混合模式（非岸边排放）","Lake_Two_Steady_Attenuation_Nonshore"),

    TWELVE("12","费伊(Fay)油膜扩延公式","Fay_Expansion");

    private String code;
    private String name;
    private String englishName;

    AssessModelEnum(String code, String name, String englishName) {
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
