package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：获取用户图片接口实体（秘书详情中的生活照）
 * @author： kerryg
 * @date:2015年11月9日 
 */
public class SecretaryImages implements Serializable {
    
    private String user_id;//用户Id
    private int img_id;//图片Id
    private String img_trumb;//大小为weith=400 heigth=400的图片链接,用于列表展现
    private String img_url;//原图链接
    private Long add_time;//添加时间戳
    private int default_img;
    
    
    
    public int getDefault_img() {
        return default_img;
    }
    public void setDefault_img(int default_img) {
        this.default_img = default_img;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public int getImg_id() {
        return img_id;
    }
    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }
    public String getImg_trumb() {
        return img_trumb;
    }
    public void setImg_trumb(String img_trumb) {
        this.img_trumb = img_trumb;
    }
    public String getImg_url() {
        return img_url;
    }
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    public Long getAdd_time() {
        return add_time;
    }
    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }
  
    public SecretaryImages(String string, int default_img) {
        super();
        this.user_id = string;
        this.default_img = default_img;
    }
    public SecretaryImages() {
        super();
    }
    
    
    
    

}
