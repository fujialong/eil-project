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
 * @since 2018-10-18
 */
@Data
public class EntSurveyPhoto extends Model<EntSurveyPhoto> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("survey_info_id")
    private String surveyInfoId;
    @TableField("time_of_photo")
    private Date timeOfPhoto;

    private String location;
    @TableField("file_id")
    private String fileId;

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
        return "EntSurveyPhoto{" +
        "id=" + id +
        ", surveyInfoId=" + surveyInfoId +
        ", timeOfPhoto=" + timeOfPhoto +
        ", location=" + location +
        ", fileId=" + fileId +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
