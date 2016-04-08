package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：公司资产列表实体
 * @author： kerryg
 * @date:2016年3月31日 
 */
public class AssetData implements Serializable {
    
    
    private Long companyId;//公司Id
    
    private Long asset_id;//资产Id
    
    private int asset_type_id;//资产Id
    
    private String barcode;//条形码
    
    private String name;//资产名称
    
    private String stock;//库存
    
    private String price;//单价
    
    private String total_price;//总价
    
    private String unit;//单位/规格
    
    private String place;//存放位置
    
    private String seq;//编号
    
    private Long add_time;
    
    private Long update_time;

    
    private int count;
   
    
    
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public Long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(Long asset_id) {
        this.asset_id = asset_id;
    }

    public int getAsset_type_id() {
        return asset_type_id;
    }

    public void setAsset_type_id(int asset_type_id) {
        this.asset_type_id = asset_type_id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

  
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    
    
    
}
