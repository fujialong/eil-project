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
 * @author fanhj
 * @since 2018-10-21
 */
@Data
public class EntAssessInfo extends Model<EntAssessInfo> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("ent_id")
    private String entId;

    @TableField("distance_of_outlet")
    private Double distanceOfOutlet;

    @TableField("river_width")
    private Double riverWidth;

    @TableField("water_direction")
    private String waterDirection;

    private Double bi;

    private Double esv;

    private Double pi;

    private Double cost;

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
