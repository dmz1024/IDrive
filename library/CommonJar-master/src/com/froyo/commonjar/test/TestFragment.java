//package com.froyo.commonjar.test;
//
//import android.view.View;
//import android.widget.TextView;
//
//import com.froyo.commonjar.R;
//import com.froyo.commonjar.fragment.BaseFragment;
//import com.froyo.commonjar.xutils.view.annotation.ViewInject;
//import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
//
//public class TestFragment extends BaseFragment {
//
//	@ViewInject(R.id.tv_test)
//	private TextView tv_test;
//
//	@Override
//	protected void setListener() {
//		tv_test.setText("这里设置逻辑代码实现业务");
//	}
//
//	@OnClick(R.id.btn_test)
//	void testClickBtn(View view) {
//		activity.toast("点击了按钮");
//	}
//
//	@Override
//	protected int setLayoutResID() {
//		return R.layout.fragment_test;
//	}
//}
