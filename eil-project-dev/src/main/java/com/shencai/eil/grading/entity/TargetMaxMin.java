package com.shencai.eil.grading.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author fujl
 * @since 2018-10-13
 */
@Data
public class TargetMaxMin extends Model<TargetMaxMin> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("target_id")
    private String targetId;

    @TableField("max_value")
    private Double maxParamValue;

    @TableField("min_value")
    private Double minParamValue;

    @TableField("industry_id")
    private String industryId;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Boolean valid;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
