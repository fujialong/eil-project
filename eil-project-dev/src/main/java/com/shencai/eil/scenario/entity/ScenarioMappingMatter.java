package com.shencai.eil.scenario.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-10-30
 */
@Data
public class ScenarioMappingMatter extends Model<ScenarioMappingMatter> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("scenario_id")
    private String scenarioId;

    @TableField("technique_cell")
    private String techniqueCell;

    @TableField("matter_name")
    private String matterName;

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
        return "ScenarioMappingMatter{" +
        "id=" + id +
        ", scenarioId=" + scenarioId +
        ", techniqueCell=" + techniqueCell +
        ", matterName=" + matterName +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
