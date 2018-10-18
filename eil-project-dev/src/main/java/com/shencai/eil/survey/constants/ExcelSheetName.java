package com.shencai.eil.survey.constants;

/**
 * Created by fanhj on 2018/10/12.
 */
public enum ExcelSheetName {
    Sheet0("Sheet0", "基础表"),
    TableA0("tableA0", "强化表Top5"),
    TableA1("tableA1", "强化表Top10"),
    Sheet1("Sheet1", "附加表1"),
    Sheet2("Sheet2", "附加表2"),
    Sheet3("Sheet3", "附加表3");

    private String code;
    private String name;

    ExcelSheetName(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getNameByCode(String code) {
        for(ExcelSheetName enumItem : ExcelSheetName.values()){
            if(enumItem.getCode().equals(code)){
                return enumItem.getName();
            }
        }
        return null;
    }

    public static String getCodeByName(String name) {
        for(ExcelSheetName enumItem : ExcelSheetName.values()){
            if(enumItem.getName().equals(name)){
                return enumItem.getCode();
            }
        }
        return null;
    }
}
