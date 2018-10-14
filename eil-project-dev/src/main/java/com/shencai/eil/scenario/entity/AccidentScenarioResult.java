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
public class AccidentScenarioResult extends Model<AccidentScenarioResult> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("info_id")
    private String infoId;
    @TableField("scenario_id")
    private String scenarioId;
    @TableField("scenario_result")
    private Double scenarioResult;

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
        return "AccidentScenarioResult{" +
        "id=" + id +
        ", infoId=" + infoId +
        ", scenarioId=" + scenarioId +
        ", scenarioResult=" + scenarioResult +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
