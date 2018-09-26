package com.shencai.eil.policy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public class IndustryCategory extends Model<IndustryCategory> {

    private static final long serialVersionUID = 1L;

    private String id;

    private String code;

    private String name;

    private String description;

    @TableField("parent_id")
    private String parentId;

    private Integer level;

    private String remark;

    @TableField("level_one_id")
    private String levelOneId;

    @TableField("level_two_id")
    private String levelTwoId;

    @TableField("level_three_id")
    private String levelThreeId;

    @TableField("level_four_id")
    private String levelFourId;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Integer valid;

    public String getLevelOneId() {
        return levelOneId;
    }

    public void setLevelOneId(String levelOneId) {
        this.levelOneId = levelOneId;
    }

    public String getLevelTwoId() {
        return levelTwoId;
    }

    public void setLevelTwoId(String levelTwoId) {
        this.levelTwoId = levelTwoId;
    }

    public String getLevelThreeId() {
        return levelThreeId;
    }

    public void setLevelThreeId(String levelThreeId) {
        this.levelThreeId = levelThreeId;
    }

    public String getLevelFourId() {
        return levelFourId;
    }

    public void setLevelFourId(String levelFourId) {
        this.levelFourId = levelFourId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
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
        return "IndustryCategory{" +
                "id=" + id +
                ", code=" + code +
                ", name=" + name +
                ", description=" + description +
                ", parentId=" + parentId +
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
