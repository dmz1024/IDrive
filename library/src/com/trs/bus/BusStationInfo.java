package com.trs.bus;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;

/**
 * Created by john on 14-3-25.
 */
public class BusStationInfo {
	public static class Station{
		public String Name;
		public String Xy;
		public String Line_names;
	}

	public ArrayList<Station> StationInfo;

	public static BusStationInfo create(String xml) throws JSONException {
		JSONObject obj = XML.toJSONObject(xml);

		Gson gson = new Gson();
		BusStationInfo info = gson.fromJson(obj.getJSONObject("ArrayOfStationInfo").toString(), BusStationInfo.class);

		return info;
	}
}
