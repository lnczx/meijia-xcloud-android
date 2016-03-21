package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：公司配置信息实体接口
 * @author： kerryg
 * @date:2016年3月17日 
 */
public class CompanySetting implements Serializable {

    private String setting_id;
    
    private String name;//名称
    
    private String setting_json;//设置扩展信息
    
    private String add_timeStr;//yyyy-MM-dd

    public String getSetting_id() {
        return setting_id;
    }

    public void setSetting_id(String setting_id) {
        this.setting_id = setting_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSetting_json() {
        return setting_json;
    }

    public void setSetting_json(String setting_json) {
        this.setting_json = setting_json;
    }

    public String getAdd_timeStr() {
        return add_timeStr;
    }

    public void setAdd_timeStr(String add_timeStr) {
        this.add_timeStr = add_timeStr;
    }
    
}
    
    
