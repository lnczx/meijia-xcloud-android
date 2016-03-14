package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：保洁类型实体
 * @author： kerryg
 * @date:2016年3月8日 
 */
public class CleanTypeData implements Serializable {

    private String clean_type_id;//类型Id
    
    private String clean_type_name;//类型名称

    public String getClean_type_id() {
        return clean_type_id;
    }

    public void setClean_type_id(String clean_type_id) {
        this.clean_type_id = clean_type_id;
    }

    public String getClean_type_name() {
        return clean_type_name;
    }

    public void setClean_type_name(String clean_type_name) {
        this.clean_type_name = clean_type_name;
    }

    public CleanTypeData(String clean_type_id, String clean_type_name) {
        super();
        this.clean_type_id = clean_type_id;
        this.clean_type_name = clean_type_name;
    }

    public CleanTypeData() {
        super();
    }

   
    
    
}
