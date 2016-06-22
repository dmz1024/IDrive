package com.trs.bus;

import android.content.Context;
import com.trs.constants.Constants;
import com.trs.util.AsyncTask;
import com.trs.util.log.Log;
import net.endlessstudio.util.Util;

import java.net.URLEncoder;

/**
 * Created by john on 14-3-25.
 * require: city and station name
 */
public class GetBusStationInfoTask extends AsyncTask<String, Object, BusStationInfo>{
	private Context context;

	public GetBusStationInfoTask(Context context) {
		this.context = context;
	}

	@Override
	protected BusStationInfo doInBackground(String... params) {
		String city = URLEncoder.encode(params.length > 0 ? params[0] : "");
		String station = URLEncoder.encode(params.length > 1? params[1]: "");

		try {
			String xml = Util.getString(context, String.format(Constants.GET_BUS_STATION_INFO_URL, city, station), "utf-8");
			return BusStationInfo.create(xml);
		} catch (Exception e) {
			Log.w("GetBusLineInfoTask", "get bus line info failed: " + e);
		}

		return null;
	}
}
