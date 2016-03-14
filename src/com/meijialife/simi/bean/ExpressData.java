package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @description：快递列表实体类
 * @author： kerryg
 * @date:2016年3月11日 
 */
public class ExpressData implements Serializable {
    
    private String id;//登记Id
    
    private String user_id;//当前用户Id
    
    private String express_name;//快递公司
    
    private String express_no;//快递单号
    
    private short express_type;//0 = 收件 1 = 寄件

    private short pay_type;//0 = 公费 1 = 自费

    private String from_addr;//寄件人地址
    
    private String from_name;//寄件人姓名
    
    private String from_tel;//寄件人联系方式
    
    private String to_addr;//收件人地址
    
    private String to_name;//收件人姓名
    
    private String to_tel;//收件人联系方式
    
    private String add_time_str;//登记时间
    
    private short is_done;//0 = 在路上 1 = 已送达
    
    private short is_close;//0 = 未结算 1 = 已结算
    
    private ArrayList<ImgsData> imgs;//

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getExpress_name() {
        return express_name;
    }

    public void setExpress_name(String express_name) {
        this.express_name = express_name;
    }

    public String getExpress_no() {
        return express_no;
    }

    public void setExpress_no(String express_no) {
        this.express_no = express_no;
    }

    public short getExpress_type() {
        return express_type;
    }

    public void setExpress_type(short express_type) {
        this.express_type = express_type;
    }

    public short getPay_type() {
        return pay_type;
    }

    public void setPay_type(short pay_type) {
        this.pay_type = pay_type;
    }

    public String getFrom_addr() {
        return from_addr;
    }

    public void setFrom_addr(String from_addr) {
        this.from_addr = from_addr;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getFrom_tel() {
        return from_tel;
    }

    public void setFrom_tel(String from_tel) {
        this.from_tel = from_tel;
    }

    public String getTo_addr() {
        return to_addr;
    }

    public void setTo_addr(String to_addr) {
        this.to_addr = to_addr;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getTo_tel() {
        return to_tel;
    }

    public void setTo_tel(String to_tel) {
        this.to_tel = to_tel;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }

    public short getIs_done() {
        return is_done;
    }

    public void setIs_done(short is_done) {
        this.is_done = is_done;
    }

    public short getIs_close() {
        return is_close;
    }

    public void setIs_close(short is_close) {
        this.is_close = is_close;
    }

    public ArrayList<ImgsData> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<ImgsData> imgs) {
        this.imgs = imgs;
    }
    
    

}
