package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 充值列表
 */
@Table(name = "SecretarySenior")
public class RechangeList implements Serializable {

    public String id;
    private String name;
    private String card_value;//充值卡价值，实际充到用户账户的钱数
    private String card_pay;//实际支付金额
    private String description;//折扣
    private String add_time;//添加时间
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCard_value() {
        return card_value;
    }
    public void setCard_value(String card_value) {
        this.card_value = card_value;
    }
    public String getCard_pay() {
        return card_pay;
    }
    public void setCard_pay(String card_pay) {
        this.card_pay = card_pay;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAdd_time() {
        return add_time;
    }
    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }
    
    
    
}
