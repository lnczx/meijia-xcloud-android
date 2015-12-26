package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：好友动态图片实体
 * @author： kerryg
 * @date:2015年12月23日 
 */
public class DynamicImaData implements Serializable {

    private String img_url;//大图url
    private String img_small;//小图url
    private String fid;
    private String user_id;//
    private String img_middle;
    
    
    
    public String getFid() {
        return fid;
    }
    public void setFid(String fid) {
        this.fid = fid;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getImg_middle() {
        return img_middle;
    }
    public void setImg_middle(String img_middle) {
        this.img_middle = img_middle;
    }
    public String getImg_url() {
        return img_url;
    }
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    public String getImg_small() {
        return img_small;
    }
    public void setImg_small(String img_small) {
        this.img_small = img_small;
    }
    
    
}
