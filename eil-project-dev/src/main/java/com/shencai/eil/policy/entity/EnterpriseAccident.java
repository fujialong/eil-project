package com.shencai.eil.policy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhoujx
 * @since 2018-09-18
 */
public class EnterpriseAccident extends Model<EnterpriseAccident> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("happen_time")
    private Date happenTime;

    private Integer disposed;

    @TableField("ent_id")
    private String entId;

    private String level;

    private String remark;

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

    public Date getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Date happenTime) {
        this.happenTime = happenTime;
    }

    public String getEntId() {
        return entId;
    }

    public void setEntId(String entId) {
        this.entId = entId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Integer getDisposed() {
        return disposed;
    }

    public void setDisposed(Integer disposed) {
        this.disposed = disposed;
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
        return "EnterpriseAccident{" +
                "id=" + id +
                ", happenTime=" + happenTime +
                ", disposed=" + disposed +
                ", entId=" + entId +
                ", level=" + level +
                ", remark=" + remark +
                ", createUserId=" + createUserId +
                ", createTime=" + createTime +
                ", updateUserId=" + updateUserId +
                ", updateTime=" + updateTime +
                ", valid=" + valid +
                "}";
    }
}
