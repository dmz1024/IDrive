package com.trs.bus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;

/**
 * Created by john on 14-3-25.
 */
public class BusRouteInfo {
	public static class Route{
		public String Start_stat;
		public String End_stat;
		public String Line_name;
		public String Stats;
		public String Line_dist;
		public String Foot_dist;
	}

	public int LinesCount;
	public ArrayList<ArrayList<Route>> TransferSchemes;

	public static BusRouteInfo create(String xml) throws JSONException {
		JSONObject obj = XML.toJSONObject(xml);

		//remove nesting
		obj = obj.getJSONObject("TransferLines");
		if(obj.has("TransferSchemes")){
			JSONObject transferSchemes = obj.getJSONObject("TransferSchemes");
			if(transferSchemes.has("ArrayOfTransferInfo")){
				JSONArray transferArray = transferSchemes.getJSONArray("ArrayOfTransferInfo");

				JSONArray newTransferArray = new JSONArray();
				for(int i = 0; i < transferArray.length(); i ++){
					JSONObject object = transferArray.getJSONObject(i);
					Object transferInfo = object.get("TransferInfo");
					if(transferInfo instanceof JSONArray){
						newTransferArray.put(transferInfo);
					}
					else if(transferInfo instanceof JSONObject){
						JSONArray array = new JSONArray();
						array.put(transferInfo);
						newTransferArray.put(array);
					}
				}

				obj.put("TransferSchemes", newTransferArray);
			}
		}

		Gson gson = new Gson();
		BusRouteInfo info = gson.fromJson(obj.toString(), BusRouteInfo.class);

		return info;
	}

}
