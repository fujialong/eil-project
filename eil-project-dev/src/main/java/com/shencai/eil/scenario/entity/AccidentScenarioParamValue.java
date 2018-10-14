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
public class AccidentScenarioParamValue extends Model<AccidentScenarioParamValue> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("info_id")
    private String infoId;
    @TableField("scenario_param_id")
    private String scenarioParamId;
    @TableField("scenario_param_value")
    private Double scenarioParamValue;

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
        return "AccidentScenarioParamValue{" +
        "id=" + id +
        ", infoId=" + infoId +
        ", scenarioParamId=" + scenarioParamId +
        ", scenarioParamValue=" + scenarioParamValue +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
