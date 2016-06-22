package com.ccpress.izijia.utils;

import android.content.Context;
import android.net.Uri;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.ccpress.izijia.activity.mystyle.InfoActivity;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.vo.UserVo;

/**
 * 
 * @Des:所有的方法，参数，在调用方法之前，请自行判断非空
 * @author Rhino
 * @version V1.0
 * @created 2015年5月19日 上午11:29:00
 */
public class PersonalCenterUtils {
	//private PersonalCenterUtils(){};


	/**
	 * 所有接口访问，除登录之外，都需要在url后面拼接uid和auth
	 * 
	 * @param activity
	 * @param url
	 * @return
	 */
	public static String buildUrl(Context activity, String url) {
		SpUtil sp = new SpUtil(activity);
		if (!isLogin(activity)) {
			return url;
		}
		return url + "&auth=" + Uri.encode(sp.getStringValue(Const.AUTH))
				+ "&uid=" + sp.getStringValue(Const.UID);
	}
	public static String buildUrlMy(Context activity, String url) {
		SpUtil sp = new SpUtil(activity);
		if (!isLogin(activity)) {
			return url;
		}
		return url + "&token=" + Uri.encode(sp.getStringValue(Const.AUTH))
				+ "&uid=" + sp.getStringValue(Const.UID);
	}

	public static String buildCartID(Context activity) {
		SpUtil sp = new SpUtil(activity);
		if (!isLogin(activity)) {
			return null;
		}
		return sp.getStringValue(Const.CAR_TEAM_ID);
	}

	/**
	 * 判断是否登录
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isLogin(Context activity) {
		SpUtil sp = new SpUtil(activity);
		if (Utils.isEmpty(sp.getStringValue(Const.AUTH))) {
			return false;
		}
		return true;
	}

	/**
	 * 获取用户登录成功之后返回的信息
	 * 
	 * @param activity
	 * @return
	 */
	public static UserVo getUserInfo(BaseActivity activity) {
		SpUtil sp = new SpUtil(activity);
		UserVo vo = new UserVo();
		vo.setAuth(sp.getStringValue(Const.AUTH));
		vo.setUid(Const.UID);
		vo.setUserName(Const.USERNAME);
		return vo;
	}

	/**
	 * 跳转到登录界面
	 * 
	 * @param activity
	 */
	public static void skipLoginActivity(BaseActivity activity) {
		activity.skip(LoginActivity.class);
	}

	/**
	 * 
	 * @param activity
	 * @param uid
	 *            :指定的好友uid
	 */
	public static void skipFriendsActivity(BaseActivity activity, String uid) {
		activity.skip(InfoActivity.class, uid);
	}

	/**
	 * 判断指定的uid用户，是否为当前的登录者
	 * 
	 * @param activity
	 * @param uid
	 * @return
	 */
	public static boolean isMyself(BaseActivity activity, String uid) {
		SpUtil sp = new SpUtil(activity);
		return sp.getStringValue(Const.UID).equals(uid);
	}

	/**
	 * 取消我的订单（允许多id取消），ids 订单的id号，以逗号分隔
	 * @param activity
	 * @param ids
	 * @param callBackListener
	 */
	public static void cancelOrder(BaseActivity activity, String ids,
			RespListener callBackListener) {
		RequestQueue mQueue = Volley.newRequestQueue(activity);
		PostParams params = new PostParams();
		params.put("ids", ids);
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.CANCEL_ORDER),
				callBackListener);
		mQueue.add(req);
		mQueue.start();
	}
	
	/**
	 * 取消收藏
	 * @param activity
	 * @param id：收藏项的id
	 * @param callBackListener
	 */
	public static void cancelCollect(BaseActivity activity, String id,
			RespListener callBackListener) {
		RequestQueue mQueue = Volley.newRequestQueue(activity);
		PostParams params = new PostParams();
		params.put("id", id);
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.CANCEL_COLLECT),
				callBackListener);
		mQueue.add(req);
		mQueue.start();
	}
}
