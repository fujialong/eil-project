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
 * @author fujl
 * @since 2018-09-27
 */
@Data
public class EntSurveyPlan extends Model<EntSurveyPlan> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("ent_id")
    private String entId;
    @TableField("survey_item_id")
    private String surveyItemId;
    @TableField("default_result")
    private String defaultResult;
    @TableField("excel_col_index")
    private Integer excelColIndex;
    @TableField("ent_target_result_id")
    private String entTargetResultId;

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
        return "EntSurveyPlan{" +
        "id=" + id +
        ", entId=" + entId +
        ", surveyItemId=" + surveyItemId +
        ", defaultResult=" + defaultResult +
        ", excelColIndex=" + excelColIndex +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
