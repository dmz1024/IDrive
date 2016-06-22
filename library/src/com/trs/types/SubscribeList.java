package com.trs.types;

import android.content.Context;
import com.trs.db.SubscribeDB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14-6-16.
 */
public class SubscribeList extends ArrayList<SubscribeItem> {
	private String mTag;
	private boolean mDefaultSubscribed = false;

	public SubscribeList(List<SubscribeItem> itemList, String tag) {
		addAll(itemList);
		this.mTag = tag;
	}

	public boolean hasSubscribedItem(List<Channel> channelList){
		for(Channel c: channelList){
			if(isSubscribed(c)){
				return true;
			}
		}

		return false;
	}

	public boolean isSubscribed(Channel channel) {
		if(!channel.isSubscribeable()){
			return true;
		}

		SubscribeItem item = getSubscribeItem(channel);
		return item.isEnabled();
	}

	public void setSubscribed(Context context, Channel channel, boolean subscribed){
		SubscribeItem item = getSubscribeItem(channel);
		item.setEnabled(subscribed);
		SubscribeDB.getInstance(context).updateOrInsert(item);
	}

	public SubscribeItem getSubscribeItem(Channel channel){
		SubscribeItem item = null;

		for(SubscribeItem subscribeItem: this){
			if(subscribeItem.getName().equals(channel.getTitle())){
				item = subscribeItem;
				break;
			}
		}

		if(item == null){
			item = new SubscribeItem();
			item.setEnabled(mDefaultSubscribed);
			item.setTag(mTag);
			item.setName(channel.getTitle());
			item.setType(channel.getType());
			item.setUrl(channel.getUrl());

			add(item);
		}

		return item;

	}

	private List<SubscribeItem> getDisabledItem(){
		ArrayList<SubscribeItem> disabledItem = new ArrayList<SubscribeItem>();
		for(SubscribeItem item: this){
			if(mDefaultSubscribed ^ item.isEnabled()){
				disabledItem.add(item);
			}
		}

		return disabledItem;
	}

	public String getTag() {
		return mTag;
	}

	public boolean isChanged(SubscribeList otherList){
		List<SubscribeItem> myDisabledItem = getDisabledItem();
		List<SubscribeItem> otherDisabledItem = otherList.getDisabledItem();

		if(myDisabledItem.size() != otherDisabledItem.size()){
			return true;
		}

		for(SubscribeItem item: myDisabledItem){
			boolean contains = false;
			for(SubscribeItem otherItem: otherDisabledItem){
				if(otherItem.equals(item)){
					contains = true;
					break;
				}
			}

			if(!contains){
				return true;
			}
		}

		return false;
	}

	public void setDefaultSubscribed(boolean defaultSubscribed) {
		this.mDefaultSubscribed = defaultSubscribed;
	}
}
