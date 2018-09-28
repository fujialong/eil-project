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
public class EntSurveyResult extends Model<EntSurveyResult> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("survey_plan_id")
    private String surveyPlanId;
    @TableField("parent_id")
    private String parentId;

    private String result;
    @TableField("excel_row_index")
    private Integer excelRowIndex;

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
        return "EntSurveyResult{" +
        "id=" + id +
        ", surveyPlanId=" + surveyPlanId +
        ", parentId=" + parentId +
        ", result=" + result +
        ", excelRowIndex=" + excelRowIndex +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
