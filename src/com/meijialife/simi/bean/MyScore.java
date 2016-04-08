package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：积分明细实体
 * @author： kerryg
 * @date:2016年4月8日 
 */
public class MyScore implements Serializable {

    private Long id;
    
    private Long user_id;
    
    private String mobile;
    
    private String score;
    
    private String action;//操作标识
    
    private String params;//参数
    
    private short is_consume;//0 = 获得 + 1 = 使用 -
    
    private String remarks;
    
    private Long add_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
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

    public short getIs_consume() {
        return is_consume;
    }

    public void setIs_consume(short is_consume) {
        this.is_consume = is_consume;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }
    
    
    
    
    
}
