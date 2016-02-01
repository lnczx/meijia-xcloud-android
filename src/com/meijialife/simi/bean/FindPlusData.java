package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：加号中图标列表实体
 * @author： kerryg
 * @date:2016年1月30日 
 */
public class FindPlusData implements Serializable {
    private Long id;//主键

    private String app_type;//应用类型xcloud/timechicken/simi
    
    private Long serial_no;//序号
    
    private String title;//导航标题
    
    private String category;//操作类型 app/h5
    
    private String action ;//动作标识
    
    private String params ;//操作相关参数
    
    private String goto_url;//跳转路径
    
    private String icon_url;//图标

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

    public Long getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(Long serial_no) {
        this.serial_no = serial_no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getGoto_url() {
        return goto_url;
    }

    public void setGoto_url(String goto_url) {
        this.goto_url = goto_url;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
    







}
