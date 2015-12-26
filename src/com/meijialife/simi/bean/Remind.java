package com.meijialife.simi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 提醒闹钟数据
 *
 */
@Table(name="Remind")
public class Remind implements Serializable {

	@Id(column="card_id")
	
	/** 卡片ID **/
	public String card_id;
	
	/** 卡片类型 0 = 通用(保留) 1 = 会议安排 2 = 秘书叫早 3 = 事务提醒 4 = 邀约通知 5 = 行程规划 **/
	public String card_type;
	
	/** 卡片名称 通用 会议安排 秘书叫早 事务提醒 邀约通知 差旅规划 **/
	public String card_type_name;
	
	/** 卡片发生时间， 时间戳格式，精确到秒. **/
	public String service_time;
	
	/** 闹钟提醒时间，时间戳格式，精确到秒. **/
	public String remind_time;
	
	/** 闹钟提醒内容 **/
	public String remind_content;

    public Remind(String card_id, String card_type, String card_type_name, String service_time, String remind_time, String remind_content) {
        super();
        this.card_id = card_id;
        this.card_type = card_type;
        this.card_type_name = card_type_name;
        this.service_time = service_time;
        this.remind_time = remind_time;
        this.remind_content = remind_content;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_type_name() {
        return card_type_name;
    }

    public void setCard_type_name(String card_type_name) {
        this.card_type_name = card_type_name;
    }

    public String getService_time() {
        return service_time;
    }

    public void setService_time(String service_time) {
        this.service_time = service_time;
    }

    public String getRemind_time() {
        return remind_time;
    }

    public void setRemind_time(String remind_time) {
        this.remind_time = remind_time;
    }

    public String getRemind_content() {
        return remind_content;
    }

    public void setRemind_content(String remind_content) {
        this.remind_content = remind_content;
    }
	
	
}
