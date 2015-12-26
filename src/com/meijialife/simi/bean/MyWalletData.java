package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 我的钱包数据
 *
 */
@Table(name="myWallet")
public class MyWalletData implements Serializable {

	private String id;
	private String user_id;
	private String mobile;
	private String pay_account;//买家账号
	private short order_type;//订单类型
	private String order_id;//订单Id
	private String order_no;//订单号
	private Double order_money;//订单总金额
	private Double rest_money;//用户余额
	private Double order_pay;//订单支付金额
	private String trade_no;//交易号
	private String trade_status;//交易状态
	private short pay_type;//支付类型
	private Long add_time;
	private String add_time_str;//添加时间str
	private String order_type_name;//订单类型名
	
	
    public String getAdd_time_str() {
        return add_time_str;
    }
    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    public String getOrder_type_name() {
        return order_type_name;
    }
    public void setOrder_type_name(String order_type_name) {
        this.order_type_name = order_type_name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getPay_account() {
        return pay_account;
    }
    public void setPay_account(String pay_account) {
        this.pay_account = pay_account;
    }
    public short getOrder_type() {
        return order_type;
    }
    public void setOrder_type(short order_type) {
        this.order_type = order_type;
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
    public Double getOrder_money() {
        return order_money;
    }
    public void setOrder_money(Double order_money) {
        this.order_money = order_money;
    }
    public Double getRest_money() {
        return rest_money;
    }
    public void setRest_money(Double rest_money) {
        this.rest_money = rest_money;
    }
    public Double getOrder_pay() {
        return order_pay;
    }
    public void setOrder_pay(Double order_pay) {
        this.order_pay = order_pay;
    }
    public String getTrade_no() {
        return trade_no;
    }
    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }
    public String getTrade_status() {
        return trade_status;
    }
    public void setTrade_status(String trade_status) {
        this.trade_status = trade_status;
    }
    public short getPay_type() {
        return pay_type;
    }
    public void setPay_type(short pay_type) {
        this.pay_type = pay_type;
    }
    public Long getAdd_time() {
        return add_time;
    }
    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }
	
	
	
	
	
	
	
	
	
	
}
