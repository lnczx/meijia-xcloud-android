package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：左侧栏--我的订单
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MyOrder implements Serializable{
    
    private Long order_id;//订单Id
    private String order_no;//订单号
    private Long partner_user_id;//服务人员Id
    private String partner_user_name;//服务人员名称
    private String partner_user_head_img;//服务人员头像
    private Long user_id;//用户Id
    private String mobile;//手机号
    private String name;//用户名称
    private String service_type_name;//服务类别名称
    private Long service_type_id;//服务类别Id
    private Long service_price_id;//服务报价Id
    private String service_price_name;//服务报价名称
    private String addr_name;//服务地址
    private String order_status_name;//订单状态
    private String add_time_str;//下单时间
    private String order_money;//订单金额
    private String order_pay;//订单实际支付金额
    private int order_status;//订单状态Id 1 = 未支付 2 = 已支付
    private int is_addr;//是否需要地址   0 = 不需要 1 = 需要
    
    
    public Long getService_type_id() {
        return service_type_id;
    }
    public void setService_type_id(Long service_type_id) {
        this.service_type_id = service_type_id;
    }
    public Long getService_price_id() {
        return service_price_id;
    }
    public void setService_price_id(Long service_price_id) {
        this.service_price_id = service_price_id;
    }
    public String getService_price_name() {
        return service_price_name;
    }
    public void setService_price_name(String service_price_name) {
        this.service_price_name = service_price_name;
    }
    public int getIs_addr() {
        return is_addr;
    }
    public void setIs_addr(int is_addr) {
        this.is_addr = is_addr;
    }
    public int getOrder_status() {
        return order_status;
    }
    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }
    public Long getOrder_id() {
        return order_id;
    }
    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }
    public String getOrder_no() {
        return order_no;
    }
    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }
    public Long getPartner_user_id() {
        return partner_user_id;
    }
    public void setPartner_user_id(Long partner_user_id) {
        this.partner_user_id = partner_user_id;
    }
    public String getPartner_user_name() {
        return partner_user_name;
    }
    public void setPartner_user_name(String partner_user_name) {
        this.partner_user_name = partner_user_name;
    }
    public String getPartner_user_head_img() {
        return partner_user_head_img;
    }
    public void setPartner_user_head_img(String partner_user_head_img) {
        this.partner_user_head_img = partner_user_head_img;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getService_type_name() {
        return service_type_name;
    }
    public void setService_type_name(String service_type_name) {
        this.service_type_name = service_type_name;
    }
    public String getAddr_name() {
        return addr_name;
    }
    public void setAddr_name(String addr_name) {
        this.addr_name = addr_name;
    }
    public String getOrder_status_name() {
        return order_status_name;
    }
    public void setOrder_status_name(String order_status_name) {
        this.order_status_name = order_status_name;
    }
    public String getAdd_time_str() {
        return add_time_str;
    }
    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    public String getOrder_money() {
        return order_money;
    }
    public void setOrder_money(String order_money) {
        this.order_money = order_money;
    }
    public String getOrder_pay() {
        return order_pay;
    }
    public void setOrder_pay(String order_pay) {
        this.order_pay = order_pay;
    }
    
    
    
    
    
    
    
    
    

}
