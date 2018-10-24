package com.shencai.eil.common.constants;

/**
 * Created by zhoujx on 2018/10/21.
 */
public enum DictionaryEnum {

    ACCESS_SURFACE_WATER_TYPE("100", "不考虑排放方式"),
    ACCESS_SURFACE_WATER_TYPE_SHORE("110", "岸边排放"),
    ACCESS_SURFACE_WATER_TYPE_NON_SHORE("120", "非岸边排放"),
    POLLUTANT_TYPE_STABILITY("stability", "持久性污染物"),
    POLLUTANT_TYPE_NO_STABILITY("no_stability", "非持久性污染物"),
    POLLUTANT_TYPE_OILS("oils", "油类"),
    SURFACE_WATER_CONDITION_RIVER("river", "河流"),
    SURFACE_WATER_CONDITION_LAKE("lake", "湖库"),
    SURFACE_WATER_CONDITION_ESTUARY("estuary", "河口、海湾");

    private String code;
    private String name;

    DictionaryEnum(String code, String name) {
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
