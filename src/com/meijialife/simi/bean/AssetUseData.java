package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @description：公司资产领用列表实体
 * @author： kerryg
 * @date:2016年4月1日 
 */
public class AssetUseData implements Serializable {

    
    private String id;
    
    private String company_id;//公司Id
    
    private String user_id;//经办人Id
    
    private String from_head_img;//经办人头像
    
    private String from_name;//经办人姓名
    
    private String from_mobile;//经办人手机号
    
    private String to_user_id;//领用Id
    
    private String to_head_img;//领用头像
    
    private String name;//领用人姓名
    
    private String mobile;//领用人手机号
    
    private ArrayList<AssetJson> asset_json;
    
    private String purpose;//用户用途
    
    private String imgs;//

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFrom_head_img() {
        return from_head_img;
    }

    public void setFrom_head_img(String from_head_img) {
        this.from_head_img = from_head_img;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getFrom_mobile() {
        return from_mobile;
    }

    public void setFrom_mobile(String from_mobile) {
        this.from_mobile = from_mobile;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getTo_head_img() {
        return to_head_img;
    }

    public void setTo_head_img(String to_head_img) {
        this.to_head_img = to_head_img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public ArrayList<AssetJson> getAsset_json() {
        return asset_json;
    }

    public void setAsset_json(ArrayList<AssetJson> asset_json) {
        this.asset_json = asset_json;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }
    
    
    
}
