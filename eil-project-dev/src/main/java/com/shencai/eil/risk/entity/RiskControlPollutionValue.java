package com.shencai.eil.risk.entity;

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
 * @since 2018-09-20
 */
@Data
public class RiskControlPollutionValue extends Model<RiskControlPollutionValue> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("area_name")
    private String areaName;

    @TableField("area_code")
    private String areaCode;

    @TableField("land_pollution_index")
    private Double landPollutionIndex;

    @TableField("risk_control")
    private Double riskControl;

    @TableField("enforcement_intensity")
    private Double enforcementIntensity;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private int valid;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
