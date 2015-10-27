package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.ArrayList;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 手机联系人
 */
@Table(name = "Contact")
public class Contact implements Serializable {

    public String id;

    /**  **/
    private String contactId;
    /** 姓名 **/
    private String name;
    /** 手机号 **/
    private ArrayList<String> phoneNumList;
    
    public Contact(String contactId, String name, ArrayList<String> phoneNumList) {
        super();
        this.contactId = contactId;
        this.name = name;
        this.phoneNumList = phoneNumList;
    }
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
    public ArrayList<String> getPhoneNumList() {
        return phoneNumList;
    }
    public void setPhoneNumList(ArrayList<String> phoneNumList) {
        this.phoneNumList = phoneNumList;
    }

    
    
}
