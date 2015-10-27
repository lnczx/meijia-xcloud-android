package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 城市数据
 *
 */
@Table(name="city")
public class CityData implements Serializable {

	@Id(column="city_id")
	
	/** 城市ID **/
	public String city_id;
	/** 行政编码 **/
	public String zip_code;
	/** 名称 **/
	public String name;
	/** 省份id **/
	public String province_id;
	/** 是否可用（1=可用，0=不可用） **/
	public int is_enable;
	/** 时间戳，代表最后更新时间 **/
	public long add_time;
	
	public CityData(){}

	public CityData(String city_id, String zip_code, String name,
			String province_id, int is_enable, long add_time) {
		super();
		this.city_id = city_id;
		this.zip_code = zip_code;
		this.name = name;
		this.province_id = province_id;
		this.is_enable = is_enable;
		this.add_time = add_time;
	}

	public String getCity_id() {
		return city_id;
	}

	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}

	public String getZip_code() {
		return zip_code;
	}

	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvince_id() {
		return province_id;
	}

	public void setProvince_id(String province_id) {
		this.province_id = province_id;
	}

	public int getIs_enable() {
		return is_enable;
	}

	public void setIs_enable(int is_enable) {
		this.is_enable = is_enable;
	}

	public long getAdd_time() {
		return add_time;
	}

	public void setAdd_time(long add_time) {
		this.add_time = add_time;
	}

	
	
	
}
