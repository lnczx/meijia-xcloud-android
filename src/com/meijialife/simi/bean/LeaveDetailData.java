package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description：请假详情实体
 * @author： kerryg
 * @date:2016年3月10日 
 */
public class LeaveDetailData implements Serializable {

    private String leave_id;//请假Id
    
    private String company_id;//公司Id
    
    private String user_id;//用户Id
    
    private String name;//用户名称
    
    private String head_img;//用户头像url
    
    private String leave_type;//请假类型
    
    private String start_date;//开始日期 yyyy-MM-dd
    
    private String end_date;//结束日期yyyy-MM-dd
    
    private String total_days;//总天数
    
    private String remarks;//请假理由
    
    private ArrayList<PassUsersData> pass_users;//审批人员
    
    private List<String> imgs;//图片url
    
    private int status;//状态 0 = 审批中 1 = 审批通过 2 = 审批不通过 3 = 撤销
    
    private String status_name;//状态名称
    
    private String add_time_str;//格式为 yyyy-MM-dd HH:mm

    
    
    
    public String getLeave_type() {
        return leave_type;
    }

    public void setLeave_type(String leave_type) {
        this.leave_type = leave_type;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
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


    public ArrayList<PassUsersData> getPass_users() {
        return pass_users;
    }

    public void setPass_users(ArrayList<PassUsersData> pass_users) {
        this.pass_users = pass_users;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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
