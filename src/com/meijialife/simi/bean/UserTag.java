package com.meijialife.simi.bean;

import java.io.Serializable;
/**
 * 
 * @description：标签类实体
 * @author： kerryg
 * @date:2015年11月12日
 */
public class UserTag implements Serializable{
    
    private Long tag_id;
    private String tag_name;
    private short tag_type;
    private Long add_time;
    private short is_enable;
    
    
    public Long getTag_id() {
        return tag_id;
    }
    public void setTag_id(Long tag_id) {
        this.tag_id = tag_id;
    }
    public String getTag_name() {
        return tag_name;
    }
    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }
    public short getTag_type() {
        return tag_type;
    }
    public void setTag_type(short tag_type) {
        this.tag_type = tag_type;
    }
    public Long getAdd_time() {
        return add_time;
    }
    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }
    public short getIs_enable() {
        return is_enable;
    }
    public void setIs_enable(short is_enable) {
        this.is_enable = is_enable;
    }
    
    
    
    
}
