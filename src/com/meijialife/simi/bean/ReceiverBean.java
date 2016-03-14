package com.meijialife.simi.bean;

import java.io.Serializable;

public class ReceiverBean implements Serializable{

    private String is_show;
    private String action;
    private String card_id;
    private String card_type;
    private String service_time;
    private String remind_time;
    private String remind_title;
    private String remind_content;
    
    private String car_no;//车牌号
    private String car_color;
    private String mobile;
    private String ocx_time;
    private String order_money;
    private String order_type;//
    private String rest_money;
    private String cap_img;
    
    
    
    
    public String getOrder_type() {
        return order_type;
    }
    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }
    public String getOrder_money() {
        return order_money;
    }
    public void setOrder_money(String order_money) {
        this.order_money = order_money;
    }
    public String getRest_money() {
        return rest_money;
    }
    public void setRest_money(String rest_money) {
        this.rest_money = rest_money;
    }
    public String getCap_img() {
        return cap_img;
    }
    public void setCap_img(String cap_img) {
        this.cap_img = cap_img;
    }
    public String getOcx_time() {
        return ocx_time;
    }
    public void setOcx_time(String ocx_time) {
        this.ocx_time = ocx_time;
    }
    public String getCar_no() {
        return car_no;
    }
    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }
    public String getCar_color() {
        return car_color;
    }
    public void setCar_color(String car_color) {
        this.car_color = car_color;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getIs_show() {
        return is_show;
    }
    public void setIs_show(String is_show) {
        this.is_show = is_show;
    }
    public String getCard_id() {
        return card_id;
    }
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }
    public String getCard_type() {
        return card_type;
    }
    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }
    public String getService_time() {
        return service_time;
    }
    public void setService_time(String service_time) {
        this.service_time = service_time;
    }
    public String getRemind_time() {
        return remind_time;
    }
    public void setRemind_time(String remind_time) {
        this.remind_time = remind_time;
    }
    public String getRemind_title() {
        return remind_title;
    }
    public void setRemind_title(String remind_title) {
        this.remind_title = remind_title;
    }
    public String getRemind_content() {
        return remind_content;
    }
    public void setRemind_content(String remind_content) {
        this.remind_content = remind_content;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    
    
}