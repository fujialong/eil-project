package com.shencai.eil.survey.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author fanhj
 * @since 2018-10-06
 */
@Data
public class SurveyItemOption extends Model<SurveyItemOption> {

    private static final long serialVersionUID = 1L;

    private String id;

    private String code;
    @TableField("option_value")
    private String optionValue;
    @TableField("source_id")
    private String sourceId;
    @TableField("source_type")
    private String sourceType;

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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "SurveyItemOption{" +
        "id=" + id +
        ", code=" + code +
        ", optionValue=" + optionValue +
        ", sourceId=" + sourceId +
        ", sourceType=" + sourceType +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
