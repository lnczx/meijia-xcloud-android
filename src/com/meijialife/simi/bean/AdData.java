package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：首页广告位实体
 * @author： kerryg
 * @date:2016年4月5日 
 */
public class AdData implements Serializable {

    private Long id;
    
    private Long no;
    
    private String img_url;
    
    private String goto_url;
    
    private int ad_type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getGoto_url() {
        return goto_url;
    }

    public void setGoto_url(String goto_url) {
        this.goto_url = goto_url;
    }

    public int getAd_type() {
        return ad_type;
    }

    public void setAd_type(int ad_type) {
        this.ad_type = ad_type;
    }
    
    
    
}
