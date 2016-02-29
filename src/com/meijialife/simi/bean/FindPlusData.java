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
    
    private Long no;//序号
    
    private String name;//导航标题
    
    private String open_type;//操作类型 app/h5
    
    private String action ;//动作标识
    
    private String params ;//操作相关参数
    
    private String url;//跳转路径
    
    private String logo;//图标

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

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpen_type() {
        return open_type;
    }

    public void setOpen_type(String open_type) {
        this.open_type = open_type;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
