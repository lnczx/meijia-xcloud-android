package com.meijialife.simi.bean;

import java.io.Serializable;


/**
 * @description：服务报价
 * @author： kerryg
 * @date:2015年11月13日 
 */
public class ServicePrices implements Serializable{
    
    private Long id;//价格
    private String detail_url;//说明链接
    private Double price;//原价
    private Double dis_price;//折扣价
    private String name;//价格名称
    private Long service_price_id;//服务类别
    private int is_addr;
    
    
   
    public int getIs_addr() {
        return is_addr;
    }
    public void setIs_addr(int is_addr) {
        this.is_addr = is_addr;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDetail_url() {
        return detail_url;
    }
    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Double getDis_price() {
        return dis_price;
    }
    public void setDis_price(Double dis_price) {
        this.dis_price = dis_price;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getService_price_id() {
        return service_price_id;
    }
    public void setService_price_id(Long service_price_id) {
        this.service_price_id = service_price_id;
    }
   
    
    
    

}
