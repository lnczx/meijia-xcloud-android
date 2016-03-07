package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：应用中心--送水实体
 * @author： kerryg
 * @date:2016年3月3日 
 */
public class WaterData implements Serializable {

    private String order_id;//订单Id
    
    private String order_no;//订单号
    
    private Long user_id;//用户Id
    
    private String name;//用户名称
    
    private String service_type_name;//服务类别名称
    
    private String addr_name;//服务地址
    
    private String service_price_name;//送水商品名称
    
    private String img_url;//送水商品图片
    
    private String dis_price;//送水商品价格单价
    
    private String service_num;//总数量
    
    private String order_status_name;//订单状态
    
    private String add_time_str;//下单时间
    
    private int order_ext_status;//是否已签收 0 = 运营处理中 1 = 已派发服务人员 2= 已签收

    private String order_money;//商品总价
    
    private String order_pay;//支付金额
    
    private int  order_status;//订单状态值  0 =  已关闭  1= 待支付  2= 已支付  3= 处理中


    
    

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public String getOrder_money() {
        return order_money;
    }

    public void setOrder_money(String order_money) {
        this.order_money = order_money;
    }

    public String getOrder_pay() {
        return order_pay;
    }

    public void setOrder_pay(String order_pay) {
        this.order_pay = order_pay;
    }

    public int getOrder_ext_status() {
        return order_ext_status;
    }

    public void setOrder_ext_status(int order_ext_status) {
        this.order_ext_status = order_ext_status;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService_type_name() {
        return service_type_name;
    }

    public void setService_type_name(String service_type_name) {
        this.service_type_name = service_type_name;
    }

    public String getAddr_name() {
        return addr_name;
    }

    public void setAddr_name(String addr_name) {
        this.addr_name = addr_name;
    }

    public String getService_price_name() {
        return service_price_name;
    }

    public void setService_price_name(String service_price_name) {
        this.service_price_name = service_price_name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDis_price() {
        return dis_price;
    }

    public void setDis_price(String dis_price) {
        this.dis_price = dis_price;
    }

    public String getService_num() {
        return service_num;
    }

    public void setService_num(String service_num) {
        this.service_num = service_num;
    }

    public String getOrder_status_name() {
        return order_status_name;
    }

    public void setOrder_status_name(String order_status_name) {
        this.order_status_name = order_status_name;
    }

    
}
