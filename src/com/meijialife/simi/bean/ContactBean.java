package com.meijialife.simi.bean;

import java.io.Serializable;

public class ContactBean implements Serializable{

    private String mobile;

    private String name;
    
    private String user_id;
    
       
    public ContactBean(String mobile, String name) {
        super();
        this.mobile = mobile;
        this.name = name;
    }
    
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public ContactBean(String mobile, String name, String user_id) {
        super();
        this.mobile = mobile;
        this.name = name;
        this.user_id = user_id;
    }

    public ContactBean() {
        super();
    }
    
    

}