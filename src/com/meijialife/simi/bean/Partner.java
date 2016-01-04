package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.List;
/**
 * 
 * @description：服务提供商接口返回的实例对象
 * @author： kerryg
 * @date:2015年11月12日
 */
public class Partner implements Serializable{
    
    private int id;//主键
    private int partner_id;//服务商Id
    private int user_id;//用户Id
    private int service_type_id;//服务大类Id
    private Long response_time;
    private Long province_id;
    private Long city_id;
    private Long region_id;
    private Long add_time;
    private String company_name;//公司名称
    private String service_type_name;//服务大类名称
    private String name;//用户名称
    private String mobile;
    private String head_img;//头像
    private String city_and_region;//所在区域
    private String response_time_name;//下单响应时间
    private String introduction;//个人介绍
    private List<UserTag> user_tags;//标签tag_id(标签Id)tag_name(标签名称) 
    private int weight_type;//权重类型 ： 0 = 默认 1 = 推荐 2 = 优惠 3 = 新人
    private String weight_type_name;//0 = 默认 1 = 推荐 2 = 优惠 3 = 新人
   
    
    
    public int getWeight_type() {
        return weight_type;
    }
    public void setWeight_type(int weight_type) {
        this.weight_type = weight_type;
    }
    public String getWeight_type_name() {
        return weight_type_name;
    }
    public void setWeight_type_name(String weight_type_name) {
        this.weight_type_name = weight_type_name;
    }
    public Long getResponse_time() {
        return response_time;
    }
    public void setResponse_time(Long response_time) {
        this.response_time = response_time;
    }
    public Long getProvince_id() {
        return province_id;
    }
    public void setProvince_id(Long province_id) {
        this.province_id = province_id;
    }
    public Long getCity_id() {
        return city_id;
    }
    public void setCity_id(Long city_id) {
        this.city_id = city_id;
    }
    public Long getRegion_id() {
        return region_id;
    }
    public void setRegion_id(Long region_id) {
        this.region_id = region_id;
    }
    public Long getAdd_time() {
        return add_time;
    }
    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public List<UserTag> getUser_tags() {
        return user_tags;
    }
    public void setUser_tags(List<UserTag> user_tags) {
        this.user_tags = user_tags;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getHead_img() {
        return head_img;
    }
    public void setHead_img(String head_img) {
        this.head_img = head_img;
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
    public String getCity_and_region() {
        return city_and_region;
    }
    public void setCity_and_region(String city_and_region) {
        this.city_and_region = city_and_region;
    }
    public String getCompany_name() {
        return company_name;
    }
    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }
    public String getResponse_time_name() {
        return response_time_name;
    }
    public void setResponse_time_name(String response_time_name) {
        this.response_time_name = response_time_name;
    }
    public int getPartner_id() {
        return partner_id;
    }
    public void setPartner_id(int partner_id) {
        this.partner_id = partner_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public int getService_type_id() {
        return service_type_id;
    }
    public void setService_type_id(int service_type_id) {
        this.service_type_id = service_type_id;
    }
    
    
    
}
