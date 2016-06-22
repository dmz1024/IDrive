package com.trs.main.slidingmenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * Created by john on 14-5-8.
 */
public class AppSetting {
	public static final String SAVE_NAME = "app_setting";
	public static final String KEY_FONT_SIZE = "font_size";
	public static final String KEY_NIGHT_MODE = "night_mode";

	public static enum FontSize{
		Small, Medium, Large
	}

	public static AppSetting sSetting;

	private Context context;
	private FontSize fontSize = FontSize.Medium;
	private boolean nightMode = false;

	public static AppSetting getInstance(Context context) {
		if(sSetting == null){
			sSetting = new AppSetting(context);
		}

		return sSetting;
	}

	private AppSetting(Context context){
		this.context = context;
		load();
	}

	public FontSize getFontSize() {
		return fontSize;
	}

	public void setFontSize(FontSize fontSize) {
		this.fontSize = fontSize;
		save();
	}

	public boolean isNightMode() {
		return nightMode;
	}

	public void setNightMode(boolean nightMode) {
		this.nightMode = nightMode;
		save();
	}

	public void save(){
		SharedPreferences.Editor editor = getEditor();
		editor.putInt(KEY_FONT_SIZE, fontSize.ordinal());
		editor.putBoolean(KEY_NIGHT_MODE, nightMode);
		editor.commit();
	}

	public void load(){
		SharedPreferences sp = getSp();
		setFontSize(FontSize.values()[sp.getInt(KEY_FONT_SIZE, FontSize.Medium.ordinal())]);
		setNightMode(sp.getBoolean(KEY_NIGHT_MODE, false));
	}

	private SharedPreferences getSp(){
		return context.getSharedPreferences(SAVE_NAME, Context.MODE_PRIVATE);
	}

	private SharedPreferences.Editor getEditor(){
		return getSp().edit();
	}
}
