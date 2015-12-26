package com.meijialife.simi.bean;

import com.meijialife.simi.bean.MyOrder;

/**
 * @description：订单详情
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MyOrderDetail extends MyOrder {
    
    private String city_name;//所在城市
    private String pay_type_name;//支付方式
    private String remarks;//备注
    private String service_content;//服务内容
    private String service_conpon_id;//用户优惠券Id
    private String user_coupon_name;//优惠券名称
    private String user_coupon_value;//优惠券金额
    
    public String getCity_name() {
        return city_name;
    }
    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
    public String getPay_type_name() {
        return pay_type_name;
    }
    public void setPay_type_name(String pay_type_name) {
        this.pay_type_name = pay_type_name;
    }
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public String getService_content() {
        return service_content;
    }
    public void setService_content(String service_content) {
        this.service_content = service_content;
    }
    public String getService_conpon_id() {
        return service_conpon_id;
    }
    public void setService_conpon_id(String service_conpon_id) {
        this.service_conpon_id = service_conpon_id;
    }
    public String getUser_coupon_name() {
        return user_coupon_name;
    }
    public void setUser_coupon_name(String user_coupon_name) {
        this.user_coupon_name = user_coupon_name;
    }
    public String getUser_coupon_value() {
        return user_coupon_value;
    }
    public void setUser_coupon_value(String user_coupon_value) {
        this.user_coupon_value = user_coupon_value;
    }
    
    
    

}
