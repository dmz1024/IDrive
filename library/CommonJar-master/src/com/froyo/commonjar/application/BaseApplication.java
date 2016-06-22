package com.froyo.commonjar.application;

import java.util.Stack;

import android.app.Activity;
import android.app.Application;

import com.froyo.commonjar.utils.Utils;

/*
 * 
 * @Des: TODO
 * @author Rhino 
 * @version V1.0 
 * @created  2015年7月1日 下午3:21:00
 */
public class BaseApplication extends Application {
	private Stack<Activity> activityStack;

	public static BaseApplication APP;

	@Override
	public void onCreate() {
		super.onCreate();
		activityStack = new Stack<Activity>();
	}

	public void interceptAnr() {
		ForceCloseHandler h = ForceCloseHandler.newInstance();
		h.register(this);
	}

	public static void set(BaseApplication app) {
		APP = app;
	}

	public synchronized void addActivity(Activity act) {
		if (activityStack == null) {
			return;
		}
		if (!hasAct(act.getClass().getSimpleName())) {
			activityStack.add(act);
		}
	}

	public void finishActExcept(String actName) {
		if (activityStack == null) {
			return;
		}
		for (Activity act : activityStack) {
			if (act != null && !act.getClass().getSimpleName().equals(actName)) {
				act.finish();
			}
		}
		activityStack.clear();
	}

	public Activity currActivity() {
		if (activityStack == null) {
			return null;
		}
		if (!Utils.isEmpty(activityStack)) {
			Activity a = activityStack.get(activityStack.size() - 1);
			return a;
		}
		return null;
	}

	public void finishAct(String actName) {
		if (activityStack == null) {
			return;
		}
		for (Activity act : activityStack) {
			if (act != null) {
				if (act.getClass().getSimpleName().equals(actName)) {
					act.finish();
				}
			}
		}
	}

	public void finishAll() {
		if (activityStack == null) {
			return;
		}
		for (Activity act : activityStack) {
			if (act != null) {
				act.finish();
			}
		}
		activityStack.clear();
	}

	public boolean hasAct(String actName) {
		if (activityStack == null) {
			return false;
		}
		for (Activity act : activityStack) {
			if (act != null) {
				if (act.getClass().getSimpleName().equals(actName)) {
					return true;
				}
			}
		}
		return false;
	}
}
