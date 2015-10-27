package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 我的钱包数据
 *
 */
@Table(name="myWallet")
public class MyWalletData implements Serializable {

	public String id;
	
	/** 收支类型 **/
	public String type;
	
	/** 时间 **/
	public String date;
	
	/**  **/
	public String count;
	
	/**  **/
	public String account;
	
	public MyWalletData(){}

	public MyWalletData(String type, String date, String count, String account) {
		super();
		this.type = type;
		this.date = date;
		this.count = count;
		this.account = account;
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

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	};
	
	
}
