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
 * @author fanhj
 * @since 2018-10-12
 */
@Data
public class AccidentScenarioParam extends Model<AccidentScenarioParam> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("scenario_id")
    private String scenarioId;

    private String code;

    private String name;

    private String type;

    @TableField("value_type")
    private String valueType;

    @TableField("unit_name")
    private String unitName;

    @TableField("default_value")
    private Double defaultValue;

    private Integer revisable;

    private Integer calculative;

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
        return "AccidentScenarioParam{" +
        "id=" + id +
        ", scenarioId=" + scenarioId +
        ", code=" + code +
        ", name=" + name +
        ", type=" + type +
        ", valueType=" + valueType +
        ", unitName=" + unitName +
        ", defaultValue=" + defaultValue +
        ", revisable=" + revisable +
        ", calculative=" + calculative +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
