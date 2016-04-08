package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * 用户数据（个人主页接口）
 *
 */
public class UserIndexData implements Serializable {

    private String id;//
    private String mobile;//
    private String head_img;//
    private String name;//
    private String sex;//
    private String province_name;//
    private float rest_money;//
    private int user_type;// 用户类型 0 = 普通用户 1= 秘书
    private String poi_distance;//
    private String im_user_name;//
    private int total_card;// 卡片数量
    private int total_coupon;// 优惠券数量
    private int total_friends;// 好友总数
    private int is_friend;// 0=不是好友，1=是好友
    private int total_feed;// 动态数量
    private int score;// 积分

    public UserIndexData() {
    }

    public int getTotal_feed() {
        return total_feed;
    }

    public void setTotal_feed(int total_feed) {
        this.total_feed = total_feed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(int is_friend) {
        this.is_friend = is_friend;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
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

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public float getRest_money() {
        return rest_money;
    }

    public void setRest_money(float rest_money) {
        this.rest_money = rest_money;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getPoi_distance() {
        return poi_distance;
    }

    public void setPoi_distance(String poi_distance) {
        this.poi_distance = poi_distance;
    }

    public String getIm_user_name() {
        return im_user_name;
    }

    public void setIm_user_name(String im_user_name) {
        this.im_user_name = im_user_name;
    }

    public int getTotal_card() {
        return total_card;
    }

    public void setTotal_card(int total_card) {
        this.total_card = total_card;
    }

    public int getTotal_coupon() {
        return total_coupon;
    }

    public void setTotal_coupon(int total_coupon) {
        this.total_coupon = total_coupon;
    }

    public int getTotal_friends() {
        return total_friends;
    }

    public void setTotal_friends(int total_friends) {
        this.total_friends = total_friends;
    }

}
