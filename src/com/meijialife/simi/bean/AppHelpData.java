package com.meijialife.simi.bean;

import java.io.Serializable;
/**
 * app帮助实体
 * @description：
 * @author： kerryg
 * @date:2016年2月1日
 */
public class AppHelpData implements Serializable {
    
    private Long id;//主键
  
    private String app_type;//应用类型
    
    private String action;//动作标识
    
    private String goto_url;//
    
    private String img_url;//图标
    
    private String content;//帮助说明
    
    private Long add_time;//
    
    private String title;//

    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getGoto_url() {
        return goto_url;
    }

    public void setGoto_url(String goto_url) {
        this.goto_url = goto_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }
    
    
    
    
}
