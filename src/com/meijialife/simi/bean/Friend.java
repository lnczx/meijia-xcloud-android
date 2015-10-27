package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 好友数据
 */
@Table(name = "Friend")
public class Friend implements Serializable {

    public String id;

    /** 用户ID **/
    public String friend_id;
    /** 用户名称 **/
    public String name;
    /** 性别 **/
    public String sex;
    /** 头像 **/
    public String head_img;
    /** 环信账号名称 **/
    public String im_username;

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

    public String getIm_username() {
        return im_username;
    }

    public void setIm_username(String im_username) {
        this.im_username = im_username;
    }

}
