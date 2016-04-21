package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：默认商品列表实体
 * @author： kerryg
 * @date:2016年4月19日 
 */
public class DefaultServiceData implements Serializable {

    private String img_url;//图片url
    
    private String name;//商品名称

    private String service_title;//副标题
    
    private String service_price_id;//商品价格Id
    
    private String price;//原价
    
    private Double dis_price;//折扣价

    private String detail_url;//说明链接
    
    private int is_addr;//是否需要地址0=不需要，1=需要
    
    private String service_type_id;//服务大类
    
    private String partner_user_id;//服务人员Id
    
    
    

    
    public String getPartner_user_id() {
        return partner_user_id;
    }

    public void setPartner_user_id(String partner_user_id) {
        this.partner_user_id = partner_user_id;
    }

    public Double getDis_price() {
        return dis_price;
    }

    public void setDis_price(Double dis_price) {
        this.dis_price = dis_price;
    }

    public String getService_title() {
        return service_title;
    }

    public void setService_title(String service_title) {
        this.service_title = service_title;
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public int getIs_addr() {
        return is_addr;
    }

    public void setIs_addr(int is_addr) {
        this.is_addr = is_addr;
    }

    public String getService_type_id() {
        return service_type_id;
    }

    public void setService_type_id(String service_type_id) {
        this.service_type_id = service_type_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService_price_id() {
        return service_price_id;
    }

    public void setService_price_id(String service_price_id) {
        this.service_price_id = service_price_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

  

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    
    
    
    
    
}
