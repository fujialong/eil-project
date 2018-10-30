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
public class GridCalculationValue extends Model<GridCalculationValue> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("ent_id")
    private String entId;
    @TableField("grid_code")
    private String gridCode;
    @TableField("bis_code")
    private String bisCode;
    @TableField("cr_cwg_s")
    private Double crCwgS;
    @TableField("cr_cwg_i")
    private Double crCwgI;
    @TableField("cr_dgw_s")
    private Double crDgwS;
    @TableField("cr_dgw_i")
    private Double crDgwI;
    @TableField("hq_cwg_s")
    private Double hqCwgS;
    @TableField("hq_cwg_i")
    private Double hqCwgI;
    @TableField("hq_dgw_s")
    private Double hqDgwS;
    @TableField("hq_dgw_i")
    private Double hqDgwI;
    @TableField("cr_s")
    private Double crS;
    @TableField("cr_i")
    private Double crI;
    @TableField("hi_s")
    private Double hiS;
    @TableField("hi_i")
    private Double hiI;
    @TableField("loss_cr")
    private Double lossCr;
    @TableField("loss_hi")
    private Double lossHi;

    private Double esv;

    private Double bi;

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
