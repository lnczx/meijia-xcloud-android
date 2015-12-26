package com.meijialife.simi.bean;

import com.meijialife.simi.Constants;


public class Response<T>{
	private Integer status =Constants.STATUS_SUCCESS;
	private String msg = "ok";
	private T data;

	public Response(){}

	public Response(ErrorEnum errorEnum) {
		this.status = errorEnum.getErrorNumber().intValue();
		this.msg = errorEnum.getErrorMsg();
		this.data = null;
	}

	public Response(Integer status, String msg, T data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
