package com.trs.weather;

import android.content.Context;
import com.trs.app.TRSApplication;
import com.trs.constants.Constants;
import com.trs.util.AsyncTask;
import com.trs.util.log.Log;
import net.endlessstudio.util.Util;

/**
 * Created by john on 14-3-25.
 */
public class GetWeatherTask extends AsyncTask<String, Object, WeatherInfo> {
	private Context context;

	public GetWeatherTask(Context context) {
		this.context = context;
	}

	@Override
	protected WeatherInfo doInBackground(String[] params) {
		String ip = params != null && params.length > 0? params[0]: "";
		try {
			String xml = Util.getString(context, String.format(Constants.GET_WEATHER_INFO_URL, ip), "utf-8");
			WeatherInfo info = WeatherInfo.create(xml);
			return info;

		} catch (Exception e) {
			Log.w("GetWeatherTask", "Get weather info failed " + e);
		}

		return null;
	}

	@Override
	protected void onPostExecute(WeatherInfo info) {
		if(info != null){
			TRSApplication.app().setWeatherInfo(info);
		}
	}

	public static void updateWeatherInfo(Context context){
		GetWeatherTask task = new GetWeatherTask(context);
		task.execute();
	}
}
