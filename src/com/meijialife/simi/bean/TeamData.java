package com.meijialife.simi.bean;

import java.io.Serializable;


/**
 * @description团建类实体
 * @author： kerryg
 * @date:2016年3月1日 
 */
public class TeamData implements Serializable {
    
    private String order_id;//订单Id
    
    private String order_no;//订单号
    
    private String user_id;//用户id
    
    private String name;//用户名称
    
    private String city_name;//所在城市名称
    
    private String service_days;//服务天数
    
    private String order_ext_status;//
   
    private String order_status_name;//订单名称
    
    private String link_man;//联系人
    
    private String link_tel;//联系电话
    
    private String team_type;//团建类型
    
    private String team_type_name;//团建类型名称
    
    private String attend_num;//参加人数
    
    private String add_time_str;//下单时间

    
    
    public String getTeam_type_name() {
        return team_type_name;
    }

    public void setTeam_type_name(String team_type_name) {
        this.team_type_name = team_type_name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getService_days() {
        return service_days;
    }

    public void setService_days(String service_days) {
        this.service_days = service_days;
    }

    public String getOrder_ext_status() {
        return order_ext_status;
    }

    public void setOrder_ext_status(String order_ext_status) {
        this.order_ext_status = order_ext_status;
    }

    public String getOrder_status_name() {
        return order_status_name;
    }

    public void setOrder_status_name(String order_status_name) {
        this.order_status_name = order_status_name;
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

    public String getTeam_type() {
        return team_type;
    }

    public void setTeam_type(String team_type) {
        this.team_type = team_type;
    }

    public String getAttend_num() {
        return attend_num;
    }

    public void setAttend_num(String attend_num) {
        this.attend_num = attend_num;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    
    
    
}
