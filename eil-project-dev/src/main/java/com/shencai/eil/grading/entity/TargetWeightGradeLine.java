package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-09-20
 */
@Data
public class TargetWeightGradeLine extends Model<TargetWeightGradeLine> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("target_weight_id")
    private String targetWeightId;

    @TableField("percent_start")
    private Double percentStart;

    @TableField("percent_end")
    private Double percentEnd;

    private String type;

    @TableField("result_code")
    private String resultCode;

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
        return "TargetWeightGradeLine{" +
        "id=" + id +
        ", targetWeightId=" + targetWeightId +
        ", percentStart=" + percentStart +
        ", percentEnd=" + percentEnd +
        ", type=" + type +
        ", resultCode=" + resultCode +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
