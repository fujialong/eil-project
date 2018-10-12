package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/10/6.
 */
public enum ValueType {
    OPTION("option", "单选"),
    OPTION_AND_OTHER("option_and_other", "单选及其他"),
    MULTIPLE_OPTION("multiple_option", "多选"),
    MULTIPLE_OPTION_AND_OTHER("multiple_option_and_other", "多选及其他"),
    MULTIPLE_INPUT_TEXT("multiple_input_text", "多文本"),
    TEXT("text", "文本"),
    NUMBER("number", "数字"),
    DOUBLE("numerical_value", "小数");

    private String code;
    private String name;

    ValueType(String code, String name) {
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
}
