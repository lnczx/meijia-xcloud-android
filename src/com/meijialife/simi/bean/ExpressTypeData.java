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
    
    private String website;
    
    private String api_order_url;
    
    private String api_search_url;
    
    private Long add_time;
    
    private Long update_time;
    
    
    

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getApi_order_url() {
        return api_order_url;
    }

    public void setApi_order_url(String api_order_url) {
        this.api_order_url = api_order_url;
    }

    public String getApi_search_url() {
        return api_search_url;
    }

    public void setApi_search_url(String api_search_url) {
        this.api_search_url = api_search_url;
    }

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
