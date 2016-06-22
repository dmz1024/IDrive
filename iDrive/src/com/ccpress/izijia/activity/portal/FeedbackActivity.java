package com.ccpress.izijia.activity.portal;

import org.json.JSONObject;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.ResponseVo;

/**
 * 意见反馈
 * 
 * @author wangyi
 * 
 */
public class FeedbackActivity extends BaseActivity {

	@ViewInject(R.id.et_content)
	private EditText et_content;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("意见反馈");

		bar.showRightText(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(!Utils.isEmpty(et_content.getText().toString().trim())){
					sendMsg(et_content.getText().toString());
				}else{
					toast("请填写你的宝贵意见");
				}
			}
		}, "发送");
	}

	private void sendMsg(String content){
		showDialog();
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("content", content);
		PostRequest req=new PostRequest(activity, params, PersonalCenterUtils.buildUrl(activity, Const.FEED_BACK), new RespListener(activity) {
			
			@Override
			public void getResp(JSONObject obj) {
			    dismissDialog();
				ResponseVo resp=GsonTools.getVo(obj.toString(), ResponseVo.class);
				if(resp.isSucess()){
					toast("感谢你的意见");
					et_content.setText("");
				}else{
					toast(resp.getMsg());
				}
			}
		});
		mQueue.add(req);
		mQueue.start();
	}
	@Override
	protected int setLayoutResID() {
		return R.layout.activity_edit_sign;
	}

}
