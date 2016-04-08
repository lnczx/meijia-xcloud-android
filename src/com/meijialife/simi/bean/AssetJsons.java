package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：用户选择的领用类型实体
 * @author： kerryg
 * @date:2016年4月5日 
 */
public class AssetJsons implements Serializable {

    private Long asset_id;
    
    private int total;

    
    

    
    public AssetJsons() {
        super();
    }

   



    public void setAsset_id(Long asset_id) {
        this.asset_id = asset_id;
    }





    public Long getAsset_id() {
        return asset_id;
    }





    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }





    public AssetJsons(Long asset_id, int total) {
        super();
        this.asset_id = asset_id;
        this.total = total;
    }



    

    
    
    
}
