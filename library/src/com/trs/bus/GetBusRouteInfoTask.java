package com.trs.bus;

import android.content.Context;
import com.trs.constants.Constants;
import com.trs.util.AsyncTask;
import com.trs.util.log.Log;
import net.endlessstudio.util.Util;

import java.net.URLEncoder;

/**
 * Created by john on 14-3-25.
 * require: city, start address & end address
 */
public class GetBusRouteInfoTask extends AsyncTask<String, Object, BusRouteInfo> {
	private Context context;

	public GetBusRouteInfoTask(Context context) {
		this.context = context;
	}

	@Override
	protected BusRouteInfo doInBackground(String... params) {
		String city = URLEncoder.encode(params.length > 0 ? params[0] : "");
		String startAddr = URLEncoder.encode(params.length > 1? params[1]: "");
		String endAddr = URLEncoder.encode(params.length > 2? params[2]: "");

		try {
			String reqUrl = String.format(Constants.GET_BUS_ROUTE_INFO_URL, city, startAddr, endAddr);
			String xml = Util.getString(context, reqUrl, "utf-8");
			return BusRouteInfo.create(xml);
		} catch (Exception e) {
			Log.w("GetBusLineInfoTask", "get bus line info failed: " + e);
		}

		return null;
	}

}
