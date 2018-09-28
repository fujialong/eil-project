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
public class SurveyItem extends Model<SurveyItem> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("target_weight_code")
    private String targetWeightCode;
    @TableField("data_source")
    private String dataSource;

    private Double importance;

    private Double cost;

    private byte[] description;
    @TableField("has_attachment")
    private Boolean hasAttachment;

    private String code;
    @TableField("sort_num")
    private Integer sortNum;

    private String name;
    @TableField("value_type")
    private String valueType;
    @TableField("unit_code")
    private String unitCode;

    private String type;
    @TableField("parent_id")
    private String parentId;
    @TableField("one_for_one_parent")
    private Boolean oneForOneParent;

    private Boolean isleaf;
    @TableField("value_required")
    private Boolean valueRequired;
    @TableField("need_selected")
    private Boolean needSelected;

    private Boolean dependent;
    @TableField("dependent_result_code")
    private String dependentResultCode;

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
        return "SurveyItem{" +
        "id=" + id +
        ", targetWeightCode=" + targetWeightCode +
        ", dataSource=" + dataSource +
        ", importance=" + importance +
        ", cost=" + cost +
        ", description=" + description +
        ", hasAttachment=" + hasAttachment +
        ", code=" + code +
        ", sortNum=" + sortNum +
        ", name=" + name +
        ", valueType=" + valueType +
        ", unitCode=" + unitCode +
        ", type=" + type +
        ", parentId=" + parentId +
        ", oneForOneParent=" + oneForOneParent +
        ", isleaf=" + isleaf +
        ", valueRequired=" + valueRequired +
        ", needSelected=" + needSelected +
        ", dependent=" + dependent +
        ", dependentResultCode=" + dependentResultCode +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
