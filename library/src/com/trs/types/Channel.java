package com.trs.types;

import com.trs.constants.Constants;
import com.trs.util.Util;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 13-11-18.
 */
public class Channel implements Serializable {
	private String type;
	private String url;
	private String pic;
    private String title;
	private boolean subscribeable;
	private String extra;
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {

        return title;
    }

    public Channel() {

	}

	public Channel(JSONObject obj) {
		JSONObjectHelper helper = new JSONObjectHelper(obj);
		setType(helper.getString(Constants.TYPE_NAMES, null));
		setPic(helper.getString(Constants.IMAGE_URL_NAMES, null));
        setUrl(helper.getString(Constants.URL_NAMES, null));
        setTitle(helper.getString(Constants.TITLE_NAMES, null));
        setSubscribeable(!"0".equals(helper.getString("issubscrible", "0")));
        setExtra(helper.getString("extra", null));
		setId(helper.getInt("tid", -1));
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public boolean isSubscribeable() {
		return subscribeable;
	}

	public void setSubscribeable(boolean subscribeable) {
		this.subscribeable = subscribeable;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this){
			return true;
		}

		if(o == null || o.getClass() != ((Object)this).getClass()){
			return false;
		}

		Channel c = (Channel)o;
		if(!Util.equals(c.getPic(), getPic())){
			return false;
		}

		if(!Util.equals(c.getUrl(), getUrl())){
			return false;
		}

		if(!Util.equals(c.getType(), getType())){
			return false;
		}

		if(!Util.equals(c.getTitle(), getTitle())){
			return false;
		}

		return true;
	}

	public static List<Channel> createList(JSONArray array) throws JSONException {
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		for(int i = 0; i < array.length(); i ++){
			JSONObject channelObj = array.getJSONObject(i);

			channelList.add(new Channel(channelObj));
		}
		return channelList;
	}

	@Override
	public String toString() {
		return String.format("%s-%s-%s-%s-%s", getTitle(), getUrl(), getPic(), getType(), getExtra());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
