package com.trs.bus;

import android.content.Context;
import com.trs.app.TRSApplication;
import com.trs.constants.Constants;
import com.trs.util.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by john on 14-3-25.
 * require: city & line name
 */
public class GetBusLineInfoTask extends AsyncTask<String, Object, BusLineInfo> {
	private Context context;
    public BusLineInfo mBusLineInfo;

	public GetBusLineInfoTask(Context context) {
		this.context = context;
	}

	@Override
	protected BusLineInfo doInBackground(String... params) {
//		String city = "成都";//URLEncoder.encode(params.length > 0? params[0]: "");
//		String line = "45";//URLEncoder.encode(params.length > 1? params[1]: "");
//
//		try {
//			String reqUrl = String.format(Constants.GET_BUS_LINE_INFO_URL, city, line);
//			String xml = Util.getString(context, reqUrl, "utf-8");
//			return BusLineInfo.create(xml);
//		} catch (Exception e) {
//			Log.w("GetBusLineInfoTask", "get bus line info failed: " + e);
//		}
        String xml = null;
        URL url = null;
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            String city = "成都";//URLEncoder.encode(params.length > 0? params[0]: "");
		    String line = "45";//URLEncoder.encode(params.length > 1? params[1]: "");
			String reqUrl = String.format(Constants.GET_BUS_LINE_INFO_URL, city, line);
            url = new URL(reqUrl);
            connection = (HttpURLConnection) url.openConnection();
            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String size = null;
            while ((size = bufferedReader.readLine()) != null)
            {
                strBuffer.append(size);
            }
            xml = strBuffer.toString();
            return BusLineInfo.create(xml);
        }
        catch (Exception e){
            e.printStackTrace();
        }
		return null;
	}

    @Override
    protected void onPostExecute(BusLineInfo busLineInfo) {
        if(busLineInfo != null){
            TRSApplication.app().setBusLineInfo(busLineInfo);
        }
    }
}
