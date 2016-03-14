package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：废品回收实体
 * @author： kerryg
 * @date:2016年3月7日 
 */
public class WasterData implements Serializable {

    private String order_id;//订单Id
    
    private String order_no;//订单号
    
    private String user_id;//用户Id
    
    private String mobile;//手机号
    
    private String name;//用户名称
    
    private String recycle_type_name;//废品回收类型名称
    
    private String service_type_name;//服务类型名称
    
    private String addr_name;//服务地址
    
    private String link_man;//联系人
    
    private String link_tel;//联系电话
    
    private String order_status_name;//订单状态
    
    private String add_time_str;//下单时间
    
    

    public String getRecycle_type_name() {
        return recycle_type_name;
    }

    public void setRecycle_type_name(String recycle_type_name) {
        this.recycle_type_name = recycle_type_name;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
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

    public String getLink_man() {
        return link_man;
    }

    public void setLink_man(String link_man) {
        this.link_man = link_man;
    }

    public String getLink_tel() {
        return link_tel;
    }

    public void setLink_tel(String link_tel) {
        this.link_tel = link_tel;
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
    
    
}
