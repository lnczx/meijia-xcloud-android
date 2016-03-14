package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：审批人信息实体
 * @author： kerryg
 * @date:2016年3月10日 
 */
public class PassUsersData implements Serializable {

    private String user_id;//审批人Id
    
    private String name;//审批人姓名
    
    private String head_img;//审批人头像
    
    private String status;//审批状态
    
    private String status_name;//审批状态名称
    
    private String remarks;//审批备注
    
    private String add_time_str;//审批时间

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    
    
    
    
}
