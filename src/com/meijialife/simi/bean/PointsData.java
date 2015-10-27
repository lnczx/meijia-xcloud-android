package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * 积分明细数据
 * @author RUI
 *
 */
public class PointsData implements Serializable{

	public String id;
	
	/** 手机号 **/
	private String mobile;
	
	/** 积分ID **/
	private String score_id;
	
	/** 1 = 订单获得积分 2 = 订单使用积分 3 = 分享获得积分 **/
	private int action_id;
	
	/** 0 = 获得 + 1 = 使用 - **/
	private int is_consume;
	
	/** 会员积分 **/
	private String score;
	
	/** 时间戳 **/
	private String add_time;
	
	public PointsData(){}

	public PointsData(String mobile, String score_id, int action_id,
			int is_consume, String score, String add_time) {
		super();
		this.mobile = mobile;
		this.score_id = score_id;
		this.action_id = action_id;
		this.is_consume = is_consume;
		this.score = score;
		this.add_time = add_time;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getScore_id() {
		return score_id;
	}

	public void setScore_id(String score_id) {
		this.score_id = score_id;
	}

	public int getAction_id() {
		return action_id;
	}

	public void setAction_id(int action_id) {
		this.action_id = action_id;
	}

	public int getIs_consume() {
		return is_consume;
	}

	public void setIs_consume(int is_consume) {
		this.is_consume = is_consume;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	
	
}
