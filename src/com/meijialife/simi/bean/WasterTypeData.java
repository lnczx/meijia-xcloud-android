package com.meijialife.simi.bean;

import java.io.Serializable;

public class WasterTypeData implements Serializable {

    private String recycle_type_id;//类型Id
    
    private String recycle_type_name;//类型名称

    public String getRecycle_type_id() {
        return recycle_type_id;
    }

    public void setRecycle_type_id(String recycle_type_id) {
        this.recycle_type_id = recycle_type_id;
    }

    public String getRecycle_type_name() {
        return recycle_type_name;
    }

    public void setRecycle_type_name(String recycle_type_name) {
        this.recycle_type_name = recycle_type_name;
    }

    public WasterTypeData(String recycle_type_id, String recycle_type_name) {
        super();
        this.recycle_type_id = recycle_type_id;
        this.recycle_type_name = recycle_type_name;
    }

    public WasterTypeData() {
        super();
    }
    
    
    
}
