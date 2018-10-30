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
    SOUTH("southToNorth", "向北"),
    SOUTH_TO_NORTH("southToNorth", "向东北"),
    WEST("westToEast", "向东"),
    WEST_TO_EAST("westToEast", "向东南"),
    NORTH("northToSouth", "向南"),
    NORTH_TO_SOUTH("northToSouth", "向西南"),
    EAST("eastToWest", "向西"),
    EAST_TO_WEST("eastToWest", "向西北"),
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

    public static String getCodeByName(String name) {
        for(DictionaryEnum enumItem : DictionaryEnum.values()){
            if(enumItem.getName().equals(name)){
                return enumItem.getCode();
            }
        }
        return null;
    }

}
