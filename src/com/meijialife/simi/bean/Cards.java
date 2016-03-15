package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Cards implements Serializable{
    private String ticket_from_city_id;
    private String set_sec_remarks;
    private String create_user_id;
    private String set_remind;
    private String create_user_name;
    private String ticket_to_city_id;
    private String name;
    private String user_id;
    private String user_name;//新增
    private String user_head_img;//新增
    private String ticket_from_city_name;
    private String ticket_type;
    private String ticket_to_city_name;
    private String set_sec_do;
    private String update_time;
    private String set_now_send;
    private ArrayList<CardZan> zan_top10;
    private String add_time;
    
    private String card_id;
    private String card_type_name;
    private String card_type;
    private String title;
    private ArrayList<CardAttend> attends;
    private String service_time;
    private String service_addr;
    private String service_content;
    private int total_zan;
    private String total_comment;
    private String add_time_str;
    private String status;
    private String card_extra;
    private String head_img_create_user;
    
    
    public String getHead_img_create_user() {
        return head_img_create_user;
    }
    public void setHead_img_create_user(String head_img_create_user) {
        this.head_img_create_user = head_img_create_user;
    }
    public String getTitle() {
        return title;
    }
    public String getCard_extra() {
        return card_extra;
    }
    public void setCard_extra(String card_extra) {
        this.card_extra = card_extra;
    }
    public void setTitle(String title) {
        this.title = title;
    }
 
    public int getTotal_zan() {
        return total_zan;
    }
    public void setTotal_zan(int total_zan) {
        this.total_zan = total_zan;
    }
    public String getTicket_from_city_id() {
        return ticket_from_city_id;
    }
    public void setTicket_from_city_id(String ticket_from_city_id) {
        this.ticket_from_city_id = ticket_from_city_id;
    }
    public String getSet_sec_remarks() {
        return set_sec_remarks;
    }
    public void setSet_sec_remarks(String set_sec_remarks) {
        this.set_sec_remarks = set_sec_remarks;
    }
    public String getCreate_user_id() {
        return create_user_id;
    }
    public void setCreate_user_id(String create_user_id) {
        this.create_user_id = create_user_id;
    }
    public String getSet_remind() {
        return set_remind;
    }
    public void setSet_remind(String set_remind) {
        this.set_remind = set_remind;
    }
    public String getCard_id() {
        return card_id;
    }
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }
    public String getCreate_user_name() {
        return create_user_name;
    }
    public void setCreate_user_name(String create_user_name) {
        this.create_user_name = create_user_name;
    }
    public String getService_time() {
        return service_time;
    }
    public void setService_time(String service_time) {
        this.service_time = service_time;
    }
    public String getService_content() {
        return service_content;
    }
    public void setService_content(String service_content) {
        this.service_content = service_content;
    }
    public String getCard_type_name() {
        return card_type_name;
    }
    public void setCard_type_name(String card_type_name) {
        this.card_type_name = card_type_name;
    }
    public String getTotal_comment() {
        return total_comment;
    }
    public void setTotal_comment(String total_comment) {
        this.total_comment = total_comment;
    }
    public String getTicket_to_city_id() {
        return ticket_to_city_id;
    }
    public void setTicket_to_city_id(String ticket_to_city_id) {
        this.ticket_to_city_id = ticket_to_city_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getUser_head_img() {
        return user_head_img;
    }
    public void setUser_head_img(String user_head_img) {
        this.user_head_img = user_head_img;
    }
    public String getTicket_from_city_name() {
        return ticket_from_city_name;
    }
    public void setTicket_from_city_name(String ticket_from_city_name) {
        this.ticket_from_city_name = ticket_from_city_name;
    }
    public String getTicket_type() {
        return ticket_type;
    }
    public void setTicket_type(String ticket_type) {
        this.ticket_type = ticket_type;
    }
    public String getAdd_time_str() {
        return add_time_str;
    }
    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    public ArrayList<CardAttend> getAttends() {
        return attends;
    }
    public void setAttends(ArrayList<CardAttend> attends) {
        this.attends = attends;
    }
    public String getTicket_to_city_name() {
        return ticket_to_city_name;
    }
    public void setTicket_to_city_name(String ticket_to_city_name) {
        this.ticket_to_city_name = ticket_to_city_name;
    }
    public String getSet_sec_do() {
        return set_sec_do;
    }
    public void setSet_sec_do(String set_sec_do) {
        this.set_sec_do = set_sec_do;
    }
    public String getService_addr() {
        return service_addr;
    }
    public void setService_addr(String service_addr) {
        this.service_addr = service_addr;
    }
    public String getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
    public String getCard_type() {
        return card_type;
    }
    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }
    public String getSet_now_send() {
        return set_now_send;
    }
    public void setSet_now_send(String set_now_send) {
        this.set_now_send = set_now_send;
    }
    
    public ArrayList<CardZan> getZan_top10() {
        return zan_top10;
    }
    public void setZan_top10(ArrayList<CardZan> zan_top10) {
        this.zan_top10 = zan_top10;
    }
    public String getAdd_time() {
        return add_time;
    }
    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }


}