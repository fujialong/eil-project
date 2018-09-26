package com.shencai.eil.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-09-21
 */
public class BaseFileupload extends Model<BaseFileupload> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("source_id")
    private String sourceId;

    private String stype;

    @TableField("file_name")
    private String fileName;

    @TableField("file_desc")
    private String fileDesc;

    private String flag;

    @TableField("file_type")
    private String fileType;

    @TableField("file_adress")
    private String fileAdress;

    @TableField("filere_lcode")
    private String filereLcode;

    @TableField("deal_state")
    private String dealState;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Integer valid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileAdress() {
        return fileAdress;
    }

    public void setFileAdress(String fileAdress) {
        this.fileAdress = fileAdress;
    }

    public String getFilereLcode() {
        return filereLcode;
    }

    public void setFilereLcode(String filereLcode) {
        this.filereLcode = filereLcode;
    }

    public String getDealState() {
        return dealState;
    }

    public void setDealState(String dealState) {
        this.dealState = dealState;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "BaseFileupload{" +
        "id=" + id +
        ", sourceId=" + sourceId +
        ", stype=" + stype +
        ", fileName=" + fileName +
        ", fileDesc=" + fileDesc +
        ", flag=" + flag +
        ", fileType=" + fileType +
        ", fileAdress=" + fileAdress +
        ", filereLcode=" + filereLcode +
        ", dealState=" + dealState +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
