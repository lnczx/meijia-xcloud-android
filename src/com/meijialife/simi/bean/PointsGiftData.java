package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * 积分兑换数据
 * @author RUI
 *
 */
public class PointsGiftData implements Serializable{

	public String id;
	
	/** 名称 **/
	private String name;
	
	/** 所需积分 **/
	private String score;
	
	public PointsGiftData(){}

	public PointsGiftData(String name, String score) {
		super();
		this.name = name;
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	
}
