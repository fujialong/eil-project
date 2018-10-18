package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/10/17.
 */
public enum GradeTemplateParamType {
    FINAL_REPORT("final_report", "最终报告"),
    FAST_GRADE_REPORT("fast_grade_report", "快速分级报告");
    private String code;
    private String name;

    GradeTemplateParamType(String code, String name) {
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
