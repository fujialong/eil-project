package com.shencai.eil.login.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author fujl
 * @since 2018-09-21
 */
public class SystemBaseUser extends Model<SystemBaseUser> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("userId")
    private String userId;

    @TableField("userName")
    private String userName;

    private String password;

    private String tel;

    private String email;

    @TableField("userType")
    private String userType;

    @TableField("tenantId")
    private String tenantId;

    @TableField("busiStatus")
    private String busiStatus;

    private String remark;

    @TableField("createTime")
    private String createTime;

    @TableField("createBy")
    private String createBy;

    @TableField("editTime")
    private String editTime;

    @TableField("editBy")
    private String editBy;

    @TableField("deleteFlag")
    private String deleteFlag;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getBusiStatus() {
        return busiStatus;
    }

    public void setBusiStatus(String busiStatus) {
        this.busiStatus = busiStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "SystemBaseUser{" +
        "id=" + id +
        ", userId=" + userId +
        ", userName=" + userName +
        ", password=" + password +
        ", tel=" + tel +
        ", email=" + email +
        ", userType=" + userType +
        ", tenantId=" + tenantId +
        ", busiStatus=" + busiStatus +
        ", remark=" + remark +
        ", createTime=" + createTime +
        ", createBy=" + createBy +
        ", editTime=" + editTime +
        ", editBy=" + editBy +
        ", deleteFlag=" + deleteFlag +
        "}";
    }
}
