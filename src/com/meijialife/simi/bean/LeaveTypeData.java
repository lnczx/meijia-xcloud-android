package com.meijialife.simi.bean;

import java.io.Serializable;


/**
 * @description：请假类型实体
 * @author： kerryg
 * @date:2016年3月9日 
 */
public class LeaveTypeData implements Serializable {

    private String leave_type_id;//类型Id
    
    private String leave_type_name;//类型名称


    public LeaveTypeData() {
        super();
    }

    public LeaveTypeData(String leave_type_id, String leave_type_name) {
        super();
        this.leave_type_id = leave_type_id;
        this.leave_type_name = leave_type_name;
    }

    public String getLeave_type_id() {
        return leave_type_id;
    }

    public void setLeave_type_id(String leave_type_id) {
        this.leave_type_id = leave_type_id;
    }

    public String getLeave_type_name() {
        return leave_type_name;
    }

    public void setLeave_type_name(String leave_type_name) {
        this.leave_type_name = leave_type_name;
    }
}