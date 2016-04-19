package com.meijialife.simi.database.bean;

import java.io.Serializable;

/**
 * @description：服务大厅数据表
 * @author： kerryg
 * @date:2016年4月18日 
 */
public class OpAd implements Serializable {

    private String id;
    
    private String no;
    
    private String title;
    
    private String ad_type;
    
    private String service_type_ids;
    
    private String img_url;
    
    private String goto_type;
    
    private String goto_url;
    
    private String add_time;
    
    private String update_time;
    
    private String enable;

    
    
    public String getGoto_url() {
        return goto_url;
    }

    public void setGoto_url(String goto_url) {
        this.goto_url = goto_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAd_type() {
        return ad_type;
    }

    public void setAd_type(String ad_type) {
        this.ad_type = ad_type;
    }

    public String getService_type_ids() {
        return service_type_ids;
    }

    public void setService_type_ids(String service_type_ids) {
        this.service_type_ids = service_type_ids;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getGoto_type() {
        return goto_type;
    }

    public void setGoto_type(String goto_type) {
        this.goto_type = goto_type;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
    
    
    
    
    
    
    
    
    
    
}
