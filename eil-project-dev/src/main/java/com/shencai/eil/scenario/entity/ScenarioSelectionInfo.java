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
public class ScenarioSelectionInfo extends Model<ScenarioSelectionInfo> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("ent_id")
    private String entId;
    @TableField("matter_name")
    private String matterName;
    @TableField("matter_type")
    private String matterType;
    @TableField("technique_cell")
    private String techniqueCell;
    @TableField("technique_component")
    private String techniqueComponent;
    @TableField("component_location")
    private String componentLocation;
    @TableField("online_content")
    private Double onlineContent;
    @TableField("matter_property")
    private String matterProperty;
    @TableField("explosive_characteristic")
    private String explosiveCharacteristic;
    @TableField("combustion_characteristic")
    private String combustionCharacteristic;
    @TableField("melting_point")
    private String meltingPoint;
    @TableField("boiling_point")
    private String boilingPoint;
    @TableField("phase_state")
    private String phaseState;
    @TableField("flashing_point")
    private String flashingPoint;

    private String density;

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
        return "ScenarioSelectionInfo{" +
                "id='" + id + '\'' +
                ", entId='" + entId + '\'' +
                ", matterName='" + matterName + '\'' +
                ", matterType='" + matterType + '\'' +
                ", techniqueCell='" + techniqueCell + '\'' +
                ", techniqueComponent='" + techniqueComponent + '\'' +
                ", componentLocation='" + componentLocation + '\'' +
                ", onlineContent=" + onlineContent +
                ", matterProperty='" + matterProperty + '\'' +
                ", explosiveCharacteristic='" + explosiveCharacteristic + '\'' +
                ", combustionCharacteristic='" + combustionCharacteristic + '\'' +
                ", meltingPoint='" + meltingPoint + '\'' +
                ", boilingPoint='" + boilingPoint + '\'' +
                ", phaseState='" + phaseState + '\'' +
                ", flashingPoint='" + flashingPoint + '\'' +
                ", density='" + density + '\'' +
                ", remark='" + remark + '\'' +
                ", createUserId='" + createUserId + '\'' +
                ", createTime=" + createTime +
                ", updateUserId='" + updateUserId + '\'' +
                ", updateTime=" + updateTime +
                ", valid=" + valid +
                '}';
    }
}
