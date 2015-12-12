package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：天气数据Bean
 * @author： kerryg
 * @date:2015年12月12日 
 */
public class WeatherDatas implements Serializable {

    private String date;//日期
    private String dayPictureUrl;//白天图
    private String nightPictureUrl;//晚上图
    private String weather;//天气
    private String wind;//风
    private String temperature;//温度
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDayPictureUrl() {
        return dayPictureUrl;
    }
    public void setDayPictureUrl(String dayPictureUrl) {
        this.dayPictureUrl = dayPictureUrl;
    }
    public String getNightPictureUrl() {
        return nightPictureUrl;
    }
    public void setNightPictureUrl(String nightPictureUrl) {
        this.nightPictureUrl = nightPictureUrl;
    }
    public String getWeather() {
        return weather;
    }
    public void setWeather(String weather) {
        this.weather = weather;
    }
    public String getWind() {
        return wind;
    }
    public void setWind(String wind) {
        this.wind = wind;
    }
    public String getTemperature() {
        return temperature;
    }
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
   
    
}
