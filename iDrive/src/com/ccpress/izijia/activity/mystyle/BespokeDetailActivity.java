//package com.ccpress.izijia.activity.mystyle;
//
//import android.os.Handler;
//import android.widget.TextView;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.ImageLoader;
//import com.android.volley.toolbox.NetworkImageView;
//import com.android.volley.toolbox.Volley;
//import com.froyo.commonjar.activity.BaseActivity;
//import com.froyo.commonjar.network.BitmapCache;
//import com.froyo.commonjar.xutils.view.annotation.ViewInject;
//import com.ccpress.izijia.R;
//import com.ccpress.izijia.componet.TitleBar;
//import com.ccpress.izijia.vo.BespokeVo;
//
///**
// * 
// * @Des: 看点详情
// * @author Rhino
// * @version V1.0
// * @created 2015年6月16日 下午2:41:33
// */
//public class BespokeDetailActivity extends BaseActivity {
//
//	@ViewInject(R.id.niv_image)
//	private NetworkImageView niv_image;
//
//	@ViewInject(R.id.tv_desc)
//	private TextView tv_desc;
//
//	@ViewInject(R.id.tv_address)
//	private TextView tv_address;
//
//	private RequestQueue mQueue;
//
//	@Override
//	public void doBusiness() {
//		mQueue = Volley.newRequestQueue(activity);
//		TitleBar bar = new TitleBar(activity);
//		bar.showBack();
//		bar.setTitle("颐和园");
//
//		BespokeVo vo = (BespokeVo) getVo("0");
//
//		tv_desc.setText(vo.getDesc());
//		tv_address.setText(vo.getMore().getAddr());
//		niv_image.setImageUrl(vo.getMore().getImage(), new ImageLoader(mQueue,
//				BitmapCache.getInstance()));
//
//		showDialog();
//		new Handler().postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//
//				dismissDialog();
//			}
//		}, 2000);
//	}
//
//	@Override
//	protected int setLayoutResID() {
//		return R.layout.activity_bespoke_detail;
//	}
//
//}
