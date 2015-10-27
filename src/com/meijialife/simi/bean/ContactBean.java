package com.meijialife.simi.bean;

public class ContactBean {

    private String mobile;

    private String name;
    
    
    

    public ContactBean(String mobile, String name) {
        super();
        this.mobile = mobile;
        this.name = name;
    }

    public ContactBean() {
        super();
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
    
    

}