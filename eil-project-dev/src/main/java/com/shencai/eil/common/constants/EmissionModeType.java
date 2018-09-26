package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/9/21.
 */
public enum EmissionModeType {

    /**
     * Direct emissions
     */
    DIRECT("1", "直接排放","Direct emissions"),

    /**
     * Indirect emissions
     */
    INDIRECT("0", "间接排放","Indirect emissions");

    private String code;
    private String name;
    private String englishName;

    EmissionModeType(String code, String name,String englishName) {
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
