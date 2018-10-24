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
    @TableField("grid_id")
    private String gridId;
    @TableField("bis_code")
    private String bisCode;
    @TableField("cr_cwg")
    private Double crCwg;
    @TableField("cr_dgw")
    private Double crDgw;
    @TableField("hq_cwg")
    private Double hqCwg;
    @TableField("hq_dgw")
    private Double hqDgw;

    private Double cr;

    private Double hi;
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

    @Override
    public String toString() {
        return "GridCalculationValue{" +
        "id=" + id +
        ", gridId=" + gridId +
        ", bisCode=" + bisCode +
        ", crCwg=" + crCwg +
        ", crDgw=" + crDgw +
        ", hqCwg=" + hqCwg +
        ", hqDgw=" + hqDgw +
        ", cr=" + cr +
        ", hi=" + hi +
        ", lossCr=" + lossCr +
        ", lossHi=" + lossHi +
        ", esv=" + esv +
        ", bi=" + bi +
        ", cost=" + cost +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
