package com.shencai.eil.common.constants;

/**
 * Created by fanhj on 2018/10/7.
 */
public enum FileSourceType {
    ENT_INFO("ent_info", "保单附件"),
    BASIC_FAST_GRADING_FILE("basic_fast_grading_file", "基础快速分级报告"),
    FAST_GRADING_FILE("fast_grading_file", "快速分级报告"),
    BASIC_SURVEY_PLAN_EXCEL("basic_survey_plan_excel", "基础勘查模板"),
    BASIC_SURVEY_PLAN_UPLOAD("basic_survey_plan_upload", "基础勘查模板上传"),
    INTENSIVE_SURVEY_PLAN_EXCEL("intensive_survey_plan_excel", "强化勘查模板"),
    INTENSIVE_SURVEY_PLAN_UPLOAD("intensive_survey_plan_upload", "强化勘查模板上传");
    private String code;
    private String name;

    FileSourceType(String code, String name) {
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
