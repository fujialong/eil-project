package com.shencai.eil.policy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public class EnterpriseMappingProduct extends Model<EnterpriseMappingProduct> {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("prod_id")
    private String prodId;

    @TableField("ent_id")
    private String entId;

    private String ingredients;

    @TableField("waste_water")
    private Double wasteWater;

    @TableField("heavy_metal")
    private Double heavyMetal;

    @TableField("waste_gas")
    private Double wasteGas;

    @TableField("waste_qty")
    private Double wasteQty;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField("update_time")
    private Date updateTime;

    private Integer valid;

    @TableField("is_main_product")
    private Integer isMainProduct;

    public Integer getIsMainProduct() {
        return isMainProduct;
    }

    public void setIsMainProduct(Integer isMainProduct) {
        this.isMainProduct = isMainProduct;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getEntId() {
        return entId;
    }

    public void setEntId(String entId) {
        this.entId = entId;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Double getWasteWater() {
        return wasteWater;
    }

    public void setWasteWater(Double wasteWater) {
        this.wasteWater = wasteWater;
    }

    public Double getHeavyMetal() {
        return heavyMetal;
    }

    public void setHeavyMetal(Double heavyMetal) {
        this.heavyMetal = heavyMetal;
    }

    public Double getWasteGas() {
        return wasteGas;
    }

    public void setWasteGas(Double wasteGas) {
        this.wasteGas = wasteGas;
    }

    public Double getWasteQty() {
        return wasteQty;
    }

    public void setWasteQty(Double wasteQty) {
        this.wasteQty = wasteQty;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "EnterpriseMappingProduct{" +
        "id=" + id +
        ", prodId=" + prodId +
        ", entId=" + entId +
        ", ingredients=" + ingredients +
        ", wasteWater=" + wasteWater +
        ", heavyMetal=" + heavyMetal +
        ", wasteGas=" + wasteGas +
        ", wasteQty=" + wasteQty +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        ", valid=" + valid +
        "}";
    }
}
