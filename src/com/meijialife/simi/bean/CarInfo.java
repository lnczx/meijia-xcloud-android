package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：用户车辆信息实体
 * @author： kerryg
 * @date:2016年3月15日 
 */
public class CarInfo implements Serializable {
    
    private String id;//
    
    private String user_id;
    
    private String car_no;//车牌号

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

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    

}
