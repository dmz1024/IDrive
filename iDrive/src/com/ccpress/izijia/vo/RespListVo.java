package com.ccpress.izijia.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;

/**
 * 服务器返回的List对象
 * 
 * @author wangyi
 * @param <E>
 * @param <E>
 * 
 */
public class RespListVo{

	private String result;

	private String msg;

	private JSONObject data;

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

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	class Data{
		private int total_page;
		private List<CollectVo> datas;
		public int getTotal_page() {
			return total_page;
		}
		public void setTotal_page(int total_page) {
			this.total_page = total_page;
		}
		public List<CollectVo> getDatas() {
			return datas;
		}
		public void setDatas(List<CollectVo> datas) {
			this.datas = datas;
		}
	}
	
	public boolean isSucess() {
		if (Utils.isEmpty(result)) {
			return false;
		}
		return result.equals("true");
	}


}
