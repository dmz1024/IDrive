package com.trs.fragment.wcm;

import com.trs.fragment.TabFragment;
import com.trs.types.Channel;
import com.trs.util.StringUtil;
import org.json.JSONException;

import java.util.List;

/**
 * Created by john on 14-5-5.
 */
public class WcmInfoTabFragment extends TabFragment {
	@Override
	protected List<Channel> createChannelList(String json) throws JSONException {
		List<Channel> channelList = super.createChannelList(json);

		for(Channel c: channelList){
			if(c.getType() == null || StringUtil.isEmpty(c.getType())){
				c.setType("1");
			}
		}

		return channelList;
	}
}
