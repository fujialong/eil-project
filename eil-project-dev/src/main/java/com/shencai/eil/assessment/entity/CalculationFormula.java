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
public class CalculationFormula extends Model<CalculationFormula> {

    private static final long serialVersionUID = 1L;

    private String id;

    private String code;

    private String name;

    private String formula;
    @TableField("bis_code")
    private String bisCode;
    @TableField("sort_num")
    private Integer sortNum;
    @TableField("sort_num")
    private Boolean needSum;

    private String description;

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
        return "CalculationFormula{" +
        "id=" + id +
        ", code=" + code +
        ", name=" + name +
        ", formula=" + formula +
        ", bisCode=" + bisCode +
        ", sortNum=" + sortNum +
        ", needSum=" + needSum +
        ", description=" + description +
        ", remark=" + remark +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
