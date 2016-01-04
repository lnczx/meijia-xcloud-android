package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：企业详情实体
 * @author： kerryg
 * @date:2016年1月4日 
 */
public class CompanyDetail implements Serializable {
    private String companyId;//
    private String companyName;//公司名称
    private String companyType;//0=公司，1=群组
    private String short_name;//公司简称
    private String invitationCode;//企业邀请码
    private String qrCode;//企业二维码
    private String addTimeStr;//创建时间
    public String getCompanyId() {
        return companyId;
    }
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getCompanyType() {
        return companyType;
    }
    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }
    public String getShort_name() {
        return short_name;
    }
    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }
    public String getInvitationCode() {
        return invitationCode;
    }
    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
    public String getQrCode() {
        return qrCode;
    }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    public String getAddTimeStr() {
        return addTimeStr;
    }
    public void setAddTimeStr(String addTimeStr) {
        this.addTimeStr = addTimeStr;
    }
    

    
}
