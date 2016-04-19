package com.meijialife.simi.database.bean;

import java.io.Serializable;

/**
 * @description：城市数据库表
 * @author： kerryg
 * @date:2016年4月18日 
 */
public class City implements Serializable {
    
    public String city_id;
    
    public String zip_code;
    
    public String name;
    
    public String province_id;
    
    public int is_enable;
    
    public long add_time;

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public int getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(int is_enable) {
        this.is_enable = is_enable;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    
    
}
