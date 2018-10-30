package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/10/5.
 */
public enum SurveyItemCategoryCode {

    DEFAULT_VALUE("default_value", "默认值"),
    WEB_VIEW("web_view", "前台显示"),
    GO_BACK("go_back", "数据回流"),
    EXCEL_VIEW_BASIC_SURVEY_TABLE("Sheet0", "excel基础表sheet"),
    EXCEL_VIEW_INTENSIVE_TOP5("tableA0", "excel加强表Top5"),
    EXCEL_VIEW_INTENSIVE_TOP10("tableA1", "excel加强表Top10"),
    EXCEL_VIEW_INTENSIVE_S1("Sheet1", "excel加强表其他1"),
    EXCEL_VIEW_INTENSIVE_S2("Sheet2", "excel加强表其他2"),
    EXCEL_VIEW_INTENSIVE_S3("Sheet3", "excel加强表其他3"),
    SCENARIO_SELECTION("scenario_selection", "情景选择信息来源"),
    DIFFUSION_MODEL("diffusion_model","扩散模型使用数据")
    ;

    private String code;
    private String name;

    SurveyItemCategoryCode(String code, String name) {
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
