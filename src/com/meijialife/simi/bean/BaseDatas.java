package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：基础数据类型实体
 * @author： kerryg
 * @date:2016年3月31日 
 */
public class BaseDatas implements Serializable {

    private String apptools;
    
    private String express;
    
    private String asset_types;
    
    private String city;

    public String getApptools() {
        return apptools;
    }

    public void setApptools(String apptools) {
        this.apptools = apptools;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getAsset_types() {
        return asset_types;
    }

    public void setAsset_types(String asset_types) {
        this.asset_types = asset_types;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BaseDatas(String apptools, String express, String asset_types, String city) {
        super();
        this.apptools = apptools;
        this.express = express;
        this.asset_types = asset_types;
        this.city = city;
    }

    public BaseDatas() {
        super();
    }
    
    
    
}
