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
public class CantonParamValue extends Model<CantonParamValue> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("area_code")
    private String areaCode;
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

    private Integer valid;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "CantonParamValue{" +
        "id=" + id +
        ", areaCode=" + areaCode +
        ", paramId=" + paramId +
        ", paramValue=" + paramValue +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
