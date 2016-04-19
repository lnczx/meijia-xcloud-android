package com.meijialife.simi.database.bean;

import java.io.Serializable;
import java.util.List;

import com.meijialife.simi.bean.ExpressTypeData;
import com.meijialife.simi.bean.XcompanySetting;


/**
 * @description：基础数据实体
 * @author： kerryg
 * @date:2016年4月16日
 */
public class BaseData implements Serializable {
    
    

    

    private List<XcompanySetting> asset_types;
   
    private List<ExpressTypeData> express;
   
    private List<AppTools> apptools;

    private List<OpAd> opads;
    
    private List<City> city;
    
    

    
    

    public List<City> getCity() {
        return city;
    }

    public void setCity(List<City> city) {
        this.city = city;
    }

    public List<OpAd> getOpads() {
        return opads;
    }

    public void setOpads(List<OpAd> opads) {
        this.opads = opads;
    }

    public List<AppTools> getApptools() {
        return apptools;
    }

    public void setApptools(List<AppTools> apptools) {
        this.apptools = apptools;
    }
    
    

    public List<XcompanySetting> getAsset_types() {
        return asset_types;
    }

    public void setAsset_types(List<XcompanySetting> asset_types) {
        this.asset_types = asset_types;
    }

    public List<ExpressTypeData> getExpress() {
        return express;
    }

    public void setExpress(List<ExpressTypeData> express) {
        this.express = express;
    }
    
 
    
    
   

}
