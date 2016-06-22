package com.ccpress.izijia.activity.mystyle;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.MsgDetailAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.MsgDetailVo;
import com.ccpress.izijia.vo.MsgDetailVo.User;
import com.ccpress.izijia.vo.ResponseVo;
import com.trs.util.log.Log;
/**
 * 
 * @Des: 我的消息详情
 * @author Rhino
 * @version V1.0
 * @created 2015年5月19日 上午11:50:49
 */
public class MsgDetailActivity extends BaseActivity {

	@ViewInject(R.id.list_view)
	private ListView list_view;

	@ViewInject(R.id.et_comment)
	private EditText et_comment;

	private MsgDetailAdapter adapter;

	private RequestQueue mQueue;

	private String toUid;

	@Override
	public void doBusiness() {

		mQueue = Volley.newRequestQueue(this);

		TitleBar bar = new TitleBar(activity);
		String title = (String) getVo("0");
		toUid = (String) getVo("1");
		bar.setTitle(title);
		bar.showBack();

		adapter = new MsgDetailAdapter(new ArrayList<MsgDetailVo>(), activity,
				R.layout.item_msg_detail);

		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});
		SpUtil sp=new SpUtil(activity);
		String uid=sp.getStringValue(Const.UID);
		
		if (!Utils.isEmpty((String) getVo("2"))) {
			queryRecord(uid,toUid,(String) getVo("2"));
		}else{
			queryRecord(uid,toUid,"");
		}
	}

	/**
	 * 消息发送
	 * @param view
     */
	@OnClick(R.id.tv_commit)
	void send(View view) {
		if (Utils.isEmpty(toUid)) {
			toast("uid为空");
			return;
		}
		if (Utils.isEmpty(et_comment.getText().toString())) {
			toast("请输入内容");
			return;
		}

		PostParams params = new PostParams();
		params.put("to_uid", toUid);
		params.put("content", et_comment.getText().toString());
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.SEND_MSG),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo data = GsonTools.getVo(obj.toString(),
								ResponseVo.class);

						if (data.isSucess()) {
							SpUtil sp = new SpUtil(activity);
							MsgDetailVo vo = new MsgDetailVo();
							vo.setContent(et_comment.getText().toString());
							vo.setCreate_time(System.currentTimeMillis());
							User user = new User();
							user.setNickname(sp.getStringValue(Const.USERNAME));
							vo.setSend(true);
							user.setAvatar(sp.getStringValue(Const.AVATAR));
							vo.setUid(sp.getStringValue(Const.UID));
							vo.setUser(user);
							adapter.addItem(vo, adapter.getCount());
							et_comment.setText("");
							Utils.hideKeyboard(activity);
							list_view.setSelection(adapter.getCount());
						} else {
							toast("发送失败");
						}
					}

					@Override
					public void doFailed() {
						dismissDialog();
						toast("发送失败");
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 请求数据
	 * @param uid
	 * @param touid
	 * @param talkId
     */
	private void queryRecord(String uid,String touid,String talkId) {
		showDialog();
		
		PostParams params = new PostParams();
		params.put("uid", uid);
		params.put("talk_id", talkId);
		params.put("to_uid", touid);
		PostRequest req = new PostRequest(
				activity,
				params,
				PersonalCenterUtils.buildUrl(activity, Const.TALK_MESSAGE_LIST),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							try {
								List<MsgDetailVo> data = GsonTools.getList(
										obj.getJSONArray("data"),
										MsgDetailVo.class);
								Log.e("ResponseVo ", obj.getJSONArray("data").toString());
								if (!Utils.isEmpty(data)) {
									adapter.addItems(data);
									list_view.setSelection(adapter.getCount());
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_msg_detail;
	}

}
