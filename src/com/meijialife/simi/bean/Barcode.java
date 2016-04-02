package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：条形码详细信息实体
 * @author： kerryg
 * @date:2016年3月31日 
 */
public class Barcode implements Serializable {

    private String barcode;//条形码
    
    private String name;//名称
    
    private String unit;//规格
    
    private String price;//单价

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    
    
    
}
