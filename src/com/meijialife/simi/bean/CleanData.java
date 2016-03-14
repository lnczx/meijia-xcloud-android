package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：保洁订单实体
 * @author： kerryg
 * @date:2016年3月7日 
 */
public class CleanData implements Serializable {
    
    private String order_id;//订单Id
    
    private String order_no;//订单号
    
    private String user_id;//用户Id
    
    private String name;//用户名称
    
    private String service_type_name;//服务类型名称
    
    private String company_name;//公司名称
    
    private String clean_area_name;//企业面积
    
    private String clean_type_name;//保洁类型
    
    private String order_status_name;//订单状态

    private String addr_name;//订单状态
    
    private String add_time_str;//下单时间

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

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getClean_area_name() {
        return clean_area_name;
    }

    public void setClean_area_name(String clean_area_name) {
        this.clean_area_name = clean_area_name;
    }

    public String getClean_type_name() {
        return clean_type_name;
    }

    public void setClean_type_name(String clean_type_name) {
        this.clean_type_name = clean_type_name;
    }

    public String getOrder_status_name() {
        return order_status_name;
    }

    public void setOrder_status_name(String order_status_name) {
        this.order_status_name = order_status_name;
    }

    public String getAddr_name() {
        return addr_name;
    }

    public void setAddr_name(String addr_name) {
        this.addr_name = addr_name;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }

    
    
}
