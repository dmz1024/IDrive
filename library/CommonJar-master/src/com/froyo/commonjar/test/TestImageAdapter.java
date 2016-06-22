//package com.froyo.commonjar.test;
//
//import java.util.List;
//
//import android.view.View;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.ImageLoader;
//import com.android.volley.toolbox.NetworkImageView;
//import com.android.volley.toolbox.Volley;
//import com.froyo.commonjar.activity.BaseActivity;
//import com.froyo.commonjar.adapter.SimpleAdapter;
//import com.froyo.commonjar.network.BitmapCache;
///**
// * 
// * @Des: 加载图片
// * @author Rhino 
// * @version V1.0 
// * @created  2015年3月5日 上午10:13:16
// */
//public class TestImageAdapter extends SimpleAdapter<String> {
//
//	private RequestQueue mQueue;
//
//	private ImageLoader imageLoader;
//
//	public TestImageAdapter(List<String> data, BaseActivity activity,
//			int layoutId) {
//		super(data, activity, layoutId, ViewHolder.class);
//		mQueue = Volley.newRequestQueue(activity);
//		imageLoader = new ImageLoader(mQueue, new BitmapCache());
//	}
//
//	@Override
//	public void doExtra(View convertView, final String item, int position) {
//		ViewHolder h = (ViewHolder) holder;
//		h.iv_call.setImageUrl(item, imageLoader);
//	}
//
//	public static class ViewHolder {
//		//显示图片使用的控件;NetworkImageView
//		NetworkImageView iv_call; 
//	}
//}
