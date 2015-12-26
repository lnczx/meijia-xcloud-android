package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @description：卡片额外信息
 * @author： kerryg
 * @date:2015年12月12日 
 */
public class CardExtra implements Serializable {

    private ArrayList<WeatherDatas> weatherDatas;
    private WeatherIndex weatherIndex;
    private String cityName;
    private String real_temp;
    
 
    
    public String getReal_temp() {
        return real_temp;
    }
    public void setReal_temp(String real_temp) {
        this.real_temp = real_temp;
    }
    public WeatherIndex getWeatherIndex() {
        return weatherIndex;
    }
    public void setWeatherIndex(WeatherIndex weatherIndex) {
        this.weatherIndex = weatherIndex;
    }
    private String ticket_type;//票务类型
    private String tikcet_from_city_name;//出发城市
    private String ticket_from_city_id;//出发城市Id
    private String ticket_to_city_name;//到达城市
    private String ticket_to_city_id;//
    
    private String poi_lng;//
    private String poi_lat;//
    private String poi_name;
    
    
  
 
    public ArrayList<WeatherDatas> getWeatherDatas() {
        return weatherDatas;
    }
    public void setWeatherDatas(ArrayList<WeatherDatas> weatherDatas) {
        this.weatherDatas = weatherDatas;
    }
    public String getTicket_type() {
        return ticket_type;
    }
    public void setTicket_type(String ticket_type) {
        this.ticket_type = ticket_type;
    }
    public String getTikcet_from_city_name() {
        return tikcet_from_city_name;
    }
    public void setTikcet_from_city_name(String tikcet_from_city_name) {
        this.tikcet_from_city_name = tikcet_from_city_name;
    }
    public String getTicket_from_city_id() {
        return ticket_from_city_id;
    }
    public void setTicket_from_city_id(String ticket_from_city_id) {
        this.ticket_from_city_id = ticket_from_city_id;
    }
    public String getTicket_to_city_name() {
        return ticket_to_city_name;
    }
    public void setTicket_to_city_name(String ticket_to_city_name) {
        this.ticket_to_city_name = ticket_to_city_name;
    }
    public String getTicket_to_city_id() {
        return ticket_to_city_id;
    }
    public void setTicket_to_city_id(String ticket_to_city_id) {
        this.ticket_to_city_id = ticket_to_city_id;
    }
    public String getPoi_lng() {
        return poi_lng;
    }
    public void setPoi_lng(String poi_lng) {
        this.poi_lng = poi_lng;
    }
    public String getPoi_lat() {
        return poi_lat;
    }
    public void setPoi_lat(String poi_lat) {
        this.poi_lat = poi_lat;
    }
    public String getPoi_name() {
        return poi_name;
    }
    public void setPoi_name(String poi_name) {
        this.poi_name = poi_name;
    }
    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
 /*   public ArrayList<WeatherDatas> getWeatherDatas() {
        return weatherDatas;
    }
    public void setWeatherDatas(ArrayList<WeatherDatas> weatherDatas) {
        this.weatherDatas = weatherDatas;
    }*/
  /*  public WeatherIndex getWeatherIndex() {
        return weatherIndex;
    }
    public void setWeatherIndex(WeatherIndex weatherIndex) {
        this.weatherIndex = weatherIndex;
    }
*/
 
  
    
  
    
    
    
}
