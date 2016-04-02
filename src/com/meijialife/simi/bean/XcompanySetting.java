package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * @description：资产类别
 * @author： kerryg
 * @date:2016年3月31日 
 */

public class XcompanySetting implements Serializable {
    
    @Id(column="id")
    private String id;//
    
    private String company_id;//
    
    private String name;
    
    private String setting_type;//
    
    private String setting_json;
    
    private short is_enable;
    
    private Long add_time;
    
    private Long update_time;
    
    private int count;//选择的数量

    
    
    
    public XcompanySetting(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public XcompanySetting() {
        super();
    }

    public XcompanySetting(String id, String company_id, String name, String setting_type, String setting_json, short is_enable, Long add_time,
            Long update_time) {
        super();
        this.id = id;
        this.company_id = company_id;
        this.name = name;
        this.setting_type = setting_type;
        this.setting_json = setting_json;
        this.is_enable = is_enable;
        this.add_time = add_time;
        this.update_time = update_time;
    }
    
    

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSetting_type() {
        return setting_type;
    }

    public void setSetting_type(String setting_type) {
        this.setting_type = setting_type;
    }

    public String getSetting_json() {
        return setting_json;
    }

    public void setSetting_json(String setting_json) {
        this.setting_json = setting_json;
    }

    public short getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(short is_enable) {
        this.is_enable = is_enable;
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
    
    

}
