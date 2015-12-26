package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 秘书
 */
@Table(name = "Secretary")
public class SecUserBean implements Serializable {

    public String id;

    /** 秘书id **/
    private String sec_id;
    /**  **/
    private String mobile;
    /** 手机号归属地 **/
    private String province_name;
    /** 用户名称 **/
    private String name;
    /** 性别   男 女 **/
    private String sex;
    /** 头像url **/
    private String head_img;
    /** 用户类型 1 = 秘书 **/
    private String user_type;
    /** 环信账号名称 **/
    private String im_user_name;
    /** 秘书描述 **/
    private String description;
    
    public String getSec_id() {
        return sec_id;
    }
    public void setSec_id(String sec_id) {
        this.sec_id = sec_id;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getProvince_name() {
        return province_name;
    }
    public void setProvince_name(String province_name) {
        this.province_name = province_name;
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
    public String getUser_type() {
        return user_type;
    }
    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
    public String getIm_user_name() {
        return im_user_name;
    }
    public void setIm_user_name(String im_user_name) {
        this.im_user_name = im_user_name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    
    
}
