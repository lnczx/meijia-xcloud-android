package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：请假实体
 * @author： kerryg
 * @date:2016年3月8日 
 */
public class LeaveData implements Serializable {

    private String leave_id;
    
    private String user_id;
    
    private String head_img;//图标
    
    private String start_date;//开始时间，yyyy-MM-dd

    private String end_date;//开始时间，yyyy-MM-dd
   
    private String total_days;//总天数
    
    private String remarks;//请假理由
    
    private String status;//状态 0 = 审批中  1 = 审批通过 2 = 审批不通过 3 = 撤销
    
    private String status_name;//状态中文名
    
    private String add_time_str;//yyyy-MM-dd HH:mm
    
    private String name;//用户名称
    
    
    

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

    public String getLeave_id() {
        return leave_id;
    }

    public void setLeave_id(String leave_id) {
        this.leave_id = leave_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getTotal_days() {
        return total_days;
    }

    public void setTotal_days(String total_days) {
        this.total_days = total_days;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    
    
    
    
    
}
