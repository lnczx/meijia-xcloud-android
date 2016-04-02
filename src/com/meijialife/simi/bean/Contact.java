package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 手机联系人
 */
@Table(name = "Contact")
public class Contact implements Serializable {

    @Id(column = "contactId")
    
    /**  **/
    public String contactId;
    /** 姓名 **/
    private String name;
    /** 手机号 **/
    private String phoneNum;
    
    private boolean checked;
//    /** 手机号（多个） **/
//    private ArrayList<String> phoneNumList;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Contact(String contactId, String name, String phoneNum) {
        super();
        this.contactId = contactId;
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public Contact() {
        super();
    }
    
    
    
   
}
