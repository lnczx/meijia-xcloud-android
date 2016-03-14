package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：快递图片实体
 * @author： kerryg
 * @date:2016年3月11日 
 */
public class ImgsData implements Serializable{

    private String img_id;//
    
    private String img_url;

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    
    
    
}
