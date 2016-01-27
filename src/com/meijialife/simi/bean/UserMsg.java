package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：用户消息实体
 * @author： kerryg
 * @date:2016年1月27日 
 */
public class UserMsg implements Serializable {

    private Long msg_id;//消息Id
    
    private Long user_id;//用户Id
    
    private String title;//标题
    
    private String summary;//摘要
    
    private String icon_url;//图表地址
    
    private String msg_time;//时间 HH:mm
    
    private String category;//分类 app/h5
    
    private String action;//动作表示
    
    private String params;//参数
    
    private String goto_url;//当category = h5 时跳转的链接

    public Long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(Long msg_id) {
        this.msg_id = msg_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getMsg_time() {
        return msg_time;
    }

    public void setMsg_time(String msg_time) {
        this.msg_time = msg_time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getGoto_url() {
        return goto_url;
    }

    public void setGoto_url(String goto_url) {
        this.goto_url = goto_url;
    }
    
    
    
}
