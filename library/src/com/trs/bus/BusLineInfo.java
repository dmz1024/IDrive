package com.trs.bus;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14-3-25.
 */
public class BusLineInfo {
	public static class Station{
		public String Name;
		public String Info;
		public String Stats;
	}

	public ArrayList<Station> LinesInfo;

	public static BusLineInfo create(String xml) throws JSONException {
		JSONObject obj = XML.toJSONObject(xml);

		Gson gson = new Gson();
		BusLineInfo info = gson.fromJson(obj.getJSONObject("ArrayOfLinesInfo").toString(), BusLineInfo.class);

		return info;
	}
}
