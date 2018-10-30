package com.shencai.eil.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * @author fanhj
 * @since 2018-10-21
 */
@Data
public class AssessGird extends Model<AssessGird> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("ent_id")
    private String entId;

    private Double x;

    private Double y;

    private Double z;

    private Double t;

    @TableField("bis_code")
    private String bisCode;

    @TableField("point_sort_num")
    private Integer pointSortNum;

    @TableField("grid_code")
    private Integer gridCode;

    @TableField("water_type")
    private String waterType;

    @TableField("sand_rate")
    private Double sandRate;

    @TableField("clay_rate")
    private Double clayRate;

    @TableField("powder_rate")
    private Double powderRate;

    @TableField("fishery_area")
    private Double fisheryArea;

    @TableField("agricultural_area")
    private Double agriculturalArea;

    @TableField("forestry_area")
    private Double forestryArea;

    @TableField("water_quality")
    private String waterQuality;

    private Double lon;

    private Double lat;

    private Double pop;
    @TableField("sensitive_area")
    private Double sensitiveArea;

    @TableField("acreage")
    private Double acreage;
    @TableField("eco_value")
    private Double ecoValue;
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
}
