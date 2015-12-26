package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 秘书服务
 */
@Table(name = "SecretarySenior")
public class SecretarySenior implements Serializable {

    public String id;//私秘服务ID
    private String name;
    private String senior_pay;
    private String valid_day;
    private String description;
    private String add_time;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSenior_pay() {
        return senior_pay;
    }
    public void setSenior_pay(String senior_pay) {
        this.senior_pay = senior_pay;
    }
    public String getValid_day() {
        return valid_day;
    }
    public void setValid_day(String valid_day) {
        this.valid_day = valid_day;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAdd_time() {
        return add_time;
    }
    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }
    
    
    
    
    
}
