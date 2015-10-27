package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 优惠卡券数据
 * 
 */
@Table(name = "DiscountCard")
public class DiscountCardData implements Serializable {

	public String id;

	/** 卡券类型 **/
	public String type;

	/** 有效期 **/
	public String date;

	/** 金额 **/
	public String amount;

	/**  **/
	public String text;

	public DiscountCardData() {
	}

	public DiscountCardData(String type, String date, String amount, String text) {
		super();
		this.type = type;
		this.date = date;
		this.amount = amount;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
