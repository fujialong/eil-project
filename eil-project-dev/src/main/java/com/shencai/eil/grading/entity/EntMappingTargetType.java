package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * @author fujl
 * @since 2018-09-19
 */
@Data
public class EntMappingTargetType extends Model<EntMappingTargetType> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("ent_id")
    private String entId;
    @TableField("target_weight_type")
    private String targetWeightType;
    @TableField("grade_line_id")
    private String gradeLineId;

    @TableField("create_user_id")
    private String createUserId;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_user_id")
    private String updateUserId;
    @TableField("update_time")
    private Date updateTime;

    private Integer valid;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "EntMappingTargetType{" +
        "id=" + id +
        ", entId=" + entId +
        ", targetWeightType=" + targetWeightType +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
