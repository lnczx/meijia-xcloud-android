package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * 地址数据
 *
 */
public class AddressData implements Serializable {
    
    public String id;
    
	private String user_id;
	private String mobile;
	private String cell_id;
	private String latitude;
	private String longitude;
	private String poi_type;
	private String name;
	private String address;
	private String addr;
	private String city;
	private String uid;
	private String phone;
	private String post_code;
	private int is_default;
	private String add_time;
	private String update_time;
	
    public AddressData(String id, String user_id, String mobile, String cell_id, String latitude, String longitude, String poi_type, String name,
            String address, String addr, String city, String uid, String phone, String post_code, int is_default, String add_time,
            String update_time) {
        super();
        this.id = id;
        this.user_id = user_id;
        this.mobile = mobile;
        this.cell_id = cell_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.poi_type = poi_type;
        this.name = name;
        this.address = address;
        this.addr = addr;
        this.city = city;
        this.uid = uid;
        this.phone = phone;
        this.post_code = post_code;
        this.is_default = is_default;
        this.add_time = add_time;
        this.update_time = update_time;
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
    public String getCell_id() {
        return cell_id;
    }
    public void setCell_id(String cell_id) {
        this.cell_id = cell_id;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getPoi_type() {
        return poi_type;
    }
    public void setPoi_type(String poi_type) {
        this.poi_type = poi_type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddr() {
        return addr;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPost_code() {
        return post_code;
    }
    public void setPost_code(String post_code) {
        this.post_code = post_code;
    }
    public int getIs_default() {
        return is_default;
    }
    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }
    public String getAdd_time() {
        return add_time;
    }
    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }
    public String getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
	
	

}
