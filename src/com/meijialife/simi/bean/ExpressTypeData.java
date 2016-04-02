package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：快递数据库实体
 * @author： kerryg
 * @date:2016年4月1日 
 */
public class ExpressTypeData implements Serializable {
    
    private String express_id;
    
    private String ecode;//快递名称拼音
    
    private String name;//快递名称
    
    private short is_hot;
    
    private Long add_time;
    
    private Long update_time;

    public String getExpress_id() {
        return express_id;
    }

    public void setExpress_id(String express_id) {
        this.express_id = express_id;
    }

    public String getEcode() {
        return ecode;
    }

    public void setEcode(String ecode) {
        this.ecode = ecode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(short is_hot) {
        this.is_hot = is_hot;
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
