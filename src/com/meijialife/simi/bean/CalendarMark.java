package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 日历当天是否有卡片
 *
 */
@Table(name="CalendarMark")
public class CalendarMark implements Serializable {

    @Id(column="service_date")
    
    /** 日期，格式为 YYY-MM-DD **/
    public String service_date;
    
	/** 个数 **/
	public String total;
	
    public String getTotal() {
        return total;
    }
    public void setTotal(String total) {
        this.total = total;
    }
    public String getService_date() {
        return service_date;
    }
    public void setService_date(String service_date) {
        this.service_date = service_date;
    }
	
	
}
