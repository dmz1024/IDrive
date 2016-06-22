package com.ccpress.izijia.activity.portal;

import com.froyo.commonjar.activity.BaseActivity;
import com.ccpress.izijia.R;
import com.ccpress.izijia.componet.TitleBar;

/**
 * 注册协议
 * @author wangyi
 *
 */
public class ProtocolActivity extends BaseActivity {
	

	@Override
	public void doBusiness() {
		TitleBar bar=new TitleBar(activity);
		bar.showBack();
		bar.setTitle("爱自驾用户注册协议");
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_register_protocol;
	}

}
