package com.meijialife.simi.bean;

import java.io.Serializable;

public class ReceiverBean implements Serializable{

    private String is;//是否在通知栏展现
    private String ac;//动作标识
    private String ca;//类型app/h5
    private String ci;//卡片Id
    private String ct;//卡片类型
    private String st;//卡片发生时间，精确到秒
    private Long re;//卡片提醒时间，精确到秒
    private String rt;//消息栏展示标题
    private String rc;//消息展示内容
    
    
    
    
    
    
    
    
    
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
    public String getIs() {
        return is;
    }
    public void setIs(String is) {
        this.is = is;
    }
    public String getAc() {
        return ac;
    }
    public void setAc(String ac) {
        this.ac = ac;
    }
    public String getCa() {
        return ca;
    }
    public void setCa(String ca) {
        this.ca = ca;
    }
    public String getCi() {
        return ci;
    }
    public void setCi(String ci) {
        this.ci = ci;
    }
    public String getCt() {
        return ct;
    }
    public void setCt(String ct) {
        this.ct = ct;
    }
    public String getSt() {
        return st;
    }
    public void setSt(String st) {
        this.st = st;
    }
   
    public Long getRe() {
        return re;
    }
    public void setRe(Long re) {
        this.re = re;
    }
    public String getRt() {
        return rt;
    }
    public void setRt(String rt) {
        this.rt = rt;
    }
    public String getRc() {
        return rc;
    }
    public void setRc(String rc) {
        this.rc = rc;
    }
   
    
    
}