package com.shencai.eil.assessment.entity;

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
 * @since 2018-10-26
 */
@Data
public class SoilType extends Model<SoilType> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("soil_type")
    private String soilType;

    @TableField("sand_rate_upper")
    private Double sandRateUpper;

    @TableField("sand_rate_lower")
    private Double sandRateLower;

    @TableField("clay_rate_upper")
    private Double clayRateUpper;

    @TableField("clay_rate_lower")
    private Double clayRateLower;

    @TableField("powder_rate_upper")
    private Double powderRateUpper;

    @TableField("powder_rate_lower")
    private Double powderRateLower;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Boolean valid;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
