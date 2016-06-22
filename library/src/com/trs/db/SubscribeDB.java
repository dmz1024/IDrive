package com.trs.db;

import android.content.Context;
import com.trs.types.SubscribeItem;
import com.trs.types.SubscribeList;
import net.endlessstudio.dbhelper.DataDB;
import net.endlessstudio.dbhelper.query.QueryClause;
import net.endlessstudio.dbhelper.query.QueryClauseBuilder;
import net.endlessstudio.dbhelper.selection.Equal;

import java.util.List;

/**
 * Created by john on 14-6-12.
 */
public class SubscribeDB extends DataDB<SubscribeItem> {
	private static SubscribeDB sInstance;

	public static SubscribeDB getInstance(Context context){
		if(sInstance == null){
			sInstance = new SubscribeDB(context);
		}

		return sInstance;
	}

	public SubscribeDB(Context context) {
		super(context);
	}

	@Override
	public String getTableName() {
		return "subscribe";
	}

	@Override
	protected Class<SubscribeItem> getItemClass() {
		return SubscribeItem.class;
	}

	public SubscribeList getList(String tag){
		QueryClause clause = new QueryClauseBuilder().selection(new Equal("tag", tag)).create();
		List<SubscribeItem> itemList = get(clause);

		return new SubscribeList(itemList, tag);
	}
}
