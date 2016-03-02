package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.List;


/**
 * @description：员工当天考勤实体
 * @author： kerryg
 * @date:2016年3月1日 
 */
public class CheckData implements Serializable {

    private Long companyId;//公司Id
    
    private String companyName;//公司名称
    
    private String benz;//班次名称
    
    private List<CheckListData> list;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBenz() {
        return benz;
    }

    public void setBenz(String benz) {
        this.benz = benz;
    }

    public List<CheckListData> getList() {
        return list;
    }

    public void setList(List<CheckListData> list) {
        this.list = list;
    }


}
