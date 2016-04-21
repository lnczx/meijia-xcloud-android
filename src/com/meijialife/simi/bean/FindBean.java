package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：发现页面广告Bean
 * @author： kerryg
 * @date:2015年12月9日 
 */
public class FindBean implements Serializable{
    
    private String title;//标题
    private String img_url;//图片链接
    private String goto_type;//跳转方式h5/app
    private String goto_url;//跳转url h5=纯跳转，app=app内跳转，h5+list跳转h5后点击咨询
    private String service_type_ids;//服务大类集合
    private String action;//操作表示
    public FindBean() {
        super();
    }
    
    public FindBean(String title, String img_url, String goto_type, String goto_url, String service_type_ids) {
        super();
        this.title = title;
        this.img_url = img_url;
        this.goto_type = goto_type;
        this.goto_url = goto_url;
        this.service_type_ids = service_type_ids;
    }


    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getService_type_ids() {
        return service_type_ids;
    }

    public void setService_type_ids(String service_type_ids) {
        this.service_type_ids = service_type_ids;
    }



    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getGoto_type() {
        return goto_type;
    }
    public void setGoto_type(String goto_type) {
        this.goto_type = goto_type;
    }
    public String getGoto_url() {
        return goto_url;
    }
    public void setGoto_url(String goto_url) {
        this.goto_url = goto_url;
    }
    public String getImg_url() {
        return img_url;
    }
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    
}
