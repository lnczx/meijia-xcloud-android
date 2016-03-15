package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：会议室实体对象
 * @author： kerryg
 * @date:2016年3月15日 
 */
public class MeetingData implements Serializable {
    
    private String setting_id;
    
    private String name;//名称
    
    private String setting_json;//设置扩展信息
    
    private String add_time_str;//添加时间 yyyy-MM-dd

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

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    
    
}
