package com.trs.types;

import com.trs.util.Util;
import net.endlessstudio.dbhelper.DBColumn;
import net.endlessstudio.dbhelper.types.AbsDBItem;

/**
 * Created by john on 14-6-12.
 */
public class SubscribeItem extends AbsDBItem {

	@DBColumn private String tag;

	@DBColumn private String name;
	@DBColumn private String url;
	@DBColumn private String type;

	@DBColumn private boolean enabled;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof SubscribeItem)){
			return false;
		}

		SubscribeItem otherItme = (SubscribeItem)o;

		if(!Util.equals(tag, otherItme.tag)){
			return false;
		}

		if(!Util.equals(name, otherItme.name)){
			return false;
		}

		if(!Util.equals(url, otherItme.url)){
			return false;
		}

		if(!Util.equals(type, otherItme.type)){
			return false;
		}

		if(enabled ^ otherItme.enabled){
			return false;
		}

		return true;
	}
}
