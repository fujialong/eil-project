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
 * @since 2018-10-04
 */
@Data
public class GradeTemplateParam extends Model<GradeTemplateParam> {

    private static final long serialVersionUID = 1L;

    private String id;

    private String code;
    @TableField("target_weight_id")
    private String targetWeightId;
    @TableField("result_code")
    private String resultCode;
    @TableField("param_code")
    private String paramCode;
    @TableField("param_content")
    private String paramContent;
    private String type;

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
        return "GradeTemplateParam{" +
        "id=" + id +
        ", code=" + code +
        ", targetWeightId=" + targetWeightId +
        ", resultCode=" + resultCode +
        ", paramCode=" + paramCode +
        ", paramContent=" + paramContent +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
