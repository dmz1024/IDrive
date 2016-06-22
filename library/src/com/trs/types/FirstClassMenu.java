package com.trs.types;

import com.trs.app.TRSApplication;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by john on 14-3-6.
 */
public class FirstClassMenu implements Serializable {
	private String type;
	private final ArrayList<Channel> channelList = new ArrayList<Channel>();
	private final ArrayList<Channel> invisibleChannelList = new ArrayList<Channel>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<Channel> getChannelList() {
		ArrayList<Channel> list = new ArrayList<Channel>();
		list.addAll(channelList);
		return list;
	}

	public void setChannelList(ArrayList<Channel> menuList) {
		this.channelList.clear();
		this.channelList.addAll(menuList);
	}

	public ArrayList<Channel> getInvisibleChannelList() {
		ArrayList<Channel> list = new ArrayList<Channel>();
		list.addAll(invisibleChannelList);
		return list;
	}

	public void setInvisibleChannelList(ArrayList<Channel> menuList) {
		this.invisibleChannelList.clear();
		this.invisibleChannelList.addAll(menuList);
	}

	public static FirstClassMenu create(String data) throws JSONException {
		switch(TRSApplication.app().getSourceType()){
		case JSON:
			return create(new JSONObject(data));
		case XML:
			return createFromXml(data);
		default:
			return null;
		}
	}

	public static FirstClassMenu create(JSONObject obj) throws JSONException{
		FirstClassMenu menu = new FirstClassMenu();
		JSONObjectHelper helper = new JSONObjectHelper(obj);
		String type = helper.getString("type", "0");
		menu.setType(type);
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		JSONArray array = helper.getJSONArray("channels", new JSONArray());
		for(int i = 0; i < array.length(); i ++){
			JSONObject channelObj = array.getJSONObject(i);

			channelList.add(new Channel(channelObj));
		}

		menu.setChannelList(channelList);

		return menu;
	}

	public static FirstClassMenu createFromXml(String xml){
		try {
			FirstClassMenu menu = new FirstClassMenu();
			menu.setType("0");

			JSONObject obj = XML.toJSONObject(xml);
			obj = obj.getJSONObject("cs");
			JSONArray array = obj.getJSONArray("c");

			ArrayList<Channel> channelList = new ArrayList<Channel>();
			ArrayList<Channel> invisibleChannelList = new ArrayList<Channel>();
			for(int i = 0; i < array.length(); i ++){
				Channel channel = new Channel(array.getJSONObject(i));

				if(channel.getType().equals("1")){
					invisibleChannelList.add(channel);
				}
				else{
					channelList.add(channel);
				}
			}

			menu.setChannelList(channelList);
			menu.setInvisibleChannelList(invisibleChannelList);

			return menu;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
