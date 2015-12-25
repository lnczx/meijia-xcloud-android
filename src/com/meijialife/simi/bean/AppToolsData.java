package com.meijialife.simi.bean;

import java.io.Serializable;

public class AppToolsData implements Serializable{

    private String t_id;
    private String name;//名称
    private String logo;//图标
    private String app_type;//云行政=xcloud,时光机=timerchick
    private String menu_type;//工具与服务 =t,成长与赚钱=d
    private String open_type;//跳转类型 h5/app
    private String url;//跳转url
    private String is_partner;//是否服务商 0=否，1=是
    private String auth_url;//不满足条件时跳转页面
    private String add_timeStr;//添加时间
    private Long add_time;//时间戳
    public String getT_id() {
        return t_id;
    }
    public void setT_id(String t_id) {
        this.t_id = t_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }
    public String getApp_type() {
        return app_type;
    }
    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }
    public String getMenu_type() {
        return menu_type;
    }
    public void setMenu_type(String menu_type) {
        this.menu_type = menu_type;
    }
    public String getOpen_type() {
        return open_type;
    }
    public void setOpen_type(String open_type) {
        this.open_type = open_type;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getIs_partner() {
        return is_partner;
    }
    public void setIs_partner(String is_partner) {
        this.is_partner = is_partner;
    }
    public String getAuth_url() {
        return auth_url;
    }
    public void setAuth_url(String auth_url) {
        this.auth_url = auth_url;
    }
    public String getAdd_timeStr() {
        return add_timeStr;
    }
    public void setAdd_timeStr(String add_timeStr) {
        this.add_timeStr = add_timeStr;
    }
    public Long getAdd_time() {
        return add_time;
    }
    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }

    
}
