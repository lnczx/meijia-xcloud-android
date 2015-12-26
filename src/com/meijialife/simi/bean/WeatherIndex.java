package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：天气描述信息Bean
 * @author： kerryg
 * @date:2015年12月12日 
 */
public class WeatherIndex implements Serializable {

    private String title;//标题
    private String zs;
    private String tipt;//贴士
    private String des;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getZs() {
        return zs;
    }
    public void setZs(String zs) {
        this.zs = zs;
    }
    public String getTipt() {
        return tipt;
    }
    public void setTipt(String tipt) {
        this.tipt = tipt;
    }
    public String getDes() {
        return des;
    }
    public void setDes(String des) {
        this.des = des;
    }
    public WeatherIndex(String title, String zs, String tipt, String des) {
        super();
        this.title = title;
        this.zs = zs;
        this.tipt = tipt;
        this.des = des;
    }
    public WeatherIndex() {
        super();
    }
    
    
    
}
