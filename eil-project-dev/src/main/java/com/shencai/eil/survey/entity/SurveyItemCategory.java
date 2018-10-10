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
public class SurveyItemCategory extends Model<SurveyItemCategory> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("survey_item_id")
    private String surveyItemId;
    @TableField("category_code")
    private String categoryCode;
    @TableField("target_bis_code")
    private String targetBisCode;

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
        return "SurveyItemCategory{" +
        "id=" + id +
        ", surveyItemId=" + surveyItemId +
        ", categoryCode=" + categoryCode +
        ", targetBisCode=" + targetBisCode +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
