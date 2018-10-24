package com.shencai.eil.survey.constants;

/**
 * Created by fanhj on 2018/10/18.
 */
public enum ExcelImportStatus {
    NOT_UPLOAD("not_upload", "未上传"),
    UPLOADED("uploaded", "已上传"),
    COMPLETED("completed", "已完善"),
    ;

    private String code;
    private String desc;

    ExcelImportStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
