package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：好友动态--赞实体
 * @author： kerryg
 * @date:2015年12月24日 
 */
public class DynamicZan implements Serializable {
    
    private String head_img;
    private String user_id;//
    public String getHead_img() {
        return head_img;
    }
    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    
    

}
