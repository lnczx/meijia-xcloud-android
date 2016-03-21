package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：好友申请实体
 * @author： kerryg
 * @date:2016年3月16日 
 */
public class FriendApplyData implements Serializable {
    
    private String friend_id;//用户Id
    
    private String name;//用户名称
    
    private String sex;//姓名
    
    private String head_img;//头像url
    
    private String mobile;//手机号
    
    private int status;//申请状态 0 = 申请  1 = 同意 2 = 拒绝
    
    private int req_type;//申请类型 0 = 我申请的 1 = 别人申请的    注： 当 status = 0 和 req_type = 1 的情况下，出现同意按钮
    
    private String add_time_str;//申请时间

    
    
    public int getReq_type() {
        return req_type;
    }

    public void setReq_type(int req_type) {
        this.req_type = req_type;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    
    
}
