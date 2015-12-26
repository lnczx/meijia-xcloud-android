package com.meijialife.simi.bean;

import java.io.Serializable;

public class CardAttend implements Serializable {
    
    private Long id;
    private Long card_id;
    private String user_id;
    private String mobile;
    private String name;
    private String add_time;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getCard_id() {
        return card_id;
    }
    public void setCard_id(Long card_id) {
        this.card_id = card_id;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
    public String getAdd_time() {
        return add_time;
    }
    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    
}