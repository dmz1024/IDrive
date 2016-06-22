package com.froyo.commonjar.application;

import java.lang.Thread.UncaughtExceptionHandler;

public class ForceCloseHandler implements UncaughtExceptionHandler {

	private static ForceCloseHandler INSTANCE;

	private BaseApplication app;

	private ForceCloseHandler() {
	}

	public static ForceCloseHandler newInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ForceCloseHandler();
		}
		return INSTANCE;
	}

	public void register(BaseApplication app) {
		this.app = app;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		System.out.println(ex);
		// handleException(ex);
		// try {
		// Thread.sleep(1500);
		// } catch (InterruptedException e) {
		//
		// }
		app.finishAll();
		// 强行关闭程序
		// android.os.Process.killProcess(android.os.Process.myPid());
	}
}
