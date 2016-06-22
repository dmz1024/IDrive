package com.ccpress.izijia.activity.order;

import com.froyo.commonjar.activity.BaseActivity;
import com.ccpress.izijia.R;
import com.ccpress.izijia.componet.TitleBar;
/**
 * 
 * @Des: 我的订单
 * @author Rhino 
 * @version V1.0 
 * @created  2015年5月6日 下午3:25:22
 */
public class OrderActivity extends BaseActivity {

	@Override
	public void doBusiness() {
		TitleBar bar =new TitleBar(activity);
		bar.showBack();
		bar.setTitle("我的订单");
	}
	
	@Override
	protected int setLayoutResID() {
		return R.layout.activity_order;
	}

}
