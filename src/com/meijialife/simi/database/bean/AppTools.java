package com.meijialife.simi.database.bean;

import java.io.Serializable;

/**
 * @description：应用中心数据库实体
 * @author： kerryg
 * @date:2016年4月18日 
 */
public class AppTools implements Serializable {
    
    private String t_id;
    
    private String no;
    
    private String name;//名称
    
    private String logo;//图标
    
    private String app_type;//云行政=xcloud,时光机=timerchick
   
    private String menu_type;//工具与服务 =t,成长与赚钱=d
  
    private String open_type;//跳转类型 h5/app
   
    private String url;//跳转url
    
    private String action;//动作标识
    
    private String params;//
    
    private short is_default;//0=不默认 1=默认
    
    private short is_del;//0=可以删除，1=不可删除
   
    private String is_partner;//是否服务商 0=否，1=是
    
    private String is_online;
    
    private String app_provider;//应用提供者
    
    private String app_describe;//应用描述
    
    private String auth_url;//不满足条件时跳转页面
    
    private String qr_code; 
    
    private Long add_time;//时间戳
    
    private Long update_time;//时间戳

    
    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public short getIs_default() {
        return is_default;
    }

    public void setIs_default(short is_default) {
        this.is_default = is_default;
    }

    public short getIs_del() {
        return is_del;
    }

    public void setIs_del(short is_del) {
        this.is_del = is_del;
    }

    public String getIs_partner() {
        return is_partner;
    }

    public void setIs_partner(String is_partner) {
        this.is_partner = is_partner;
    }

    public String getIs_online() {
        return is_online;
    }

    public void setIs_online(String is_online) {
        this.is_online = is_online;
    }

    public String getApp_provider() {
        return app_provider;
    }

    public void setApp_provider(String app_provider) {
        this.app_provider = app_provider;
    }

    public String getApp_describe() {
        return app_describe;
    }

    public void setApp_describe(String app_describe) {
        this.app_describe = app_describe;
    }

    public String getAuth_url() {
        return auth_url;
    }

    public void setAuth_url(String auth_url) {
        this.auth_url = auth_url;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public Long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }
    
    
    
}
