package com.ccpress.izijia.vo;

import com.froyo.commonjar.utils.Utils;

/**
 * 服务器返回的JSON对象
 * @author wangyi
 *
 */
public class ResponseVo {
	
	private String result;
	
	private String msg;
	
	private Object data;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public boolean isSucess(){
		if(Utils.isEmpty(result)){
			return false;
		}
		return result.equals("true");
	}
	
	
}
