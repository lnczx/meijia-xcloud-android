package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 我的订单数据
 *
 */
@Table(name="MyOrder")
public class MyOrderData implements Serializable {

	public String id;
	
	/**  **/
	public String name;
	
	public MyOrderData(){}

	public MyOrderData(String name) {
		super();
		this.name = name;
	}

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

	
}
