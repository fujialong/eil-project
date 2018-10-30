package com.shencai.eil.scenario.entity;

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
public class PollutantParamValue extends Model<PollutantParamValue> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("pollutant_name")
    private String pollutantName;

    @TableField("param_id")
    private String paramId;

    @TableField("param_value")
    private Double paramValue;

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
