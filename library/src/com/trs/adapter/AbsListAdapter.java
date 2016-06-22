package com.trs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.trs.mobile.R;
import com.trs.types.ListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 13-11-18.
 */
abstract public class AbsListAdapter extends BaseAdapter {
	private final Context mContext;
	private final ArrayList<ListItem> mListItems = new ArrayList<ListItem>();

	public AbsListAdapter(Context context){
		if(context == null){
			throw new NullPointerException();
		}
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public ListItem getItem(int position) {
		return mListItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void add(ListItem item){
		mListItems.add(item);
	}

	public void addAll(List<ListItem> dataList){
		mListItems.addAll(dataList);
	}

	public void clear(){
		mListItems.clear();
	}

	public Context getContext(){
		return mContext;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if(convertView != null){
			view = convertView;
		}
		else{
			view = createView(position, convertView, parent);
		}

		updateView(view, position, convertView, parent);

		return view;
	}

	public View createView(int position, View convertView, ViewGroup parent){
//		return LayoutInflater.from(mContext).inflate(getViewID(), null);
		return LayoutInflater.from(mContext).inflate(getViewID(), parent, false);
	}

	public void updateView(View view, int position, View convertView, ViewGroup parent){

	}

	abstract public int getViewID();
}
