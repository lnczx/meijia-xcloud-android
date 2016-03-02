package com.meijialife.simi.bean;

import java.io.Serializable;


/**
 * @description：员工考勤列表实体
 * @author： kerryg
 * @date:2016年3月1日 
 */
public class CheckListData implements Serializable {

    private String checkinTime;//考勤时间，格式HH:mm    
    
    private String poiName;//考勤地理位置名称

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

  
    
    
}
