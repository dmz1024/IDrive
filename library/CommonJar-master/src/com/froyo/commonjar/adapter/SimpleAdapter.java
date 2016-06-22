package com.froyo.commonjar.adapter;

import java.lang.reflect.Field;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.BitmapCache;
import com.froyo.commonjar.utils.RUtils;
import com.froyo.commonjar.utils.Utils;

/**
 * 
 * @Des: Adapter基类，供普通Adapter继承
 * @author Rhino
 * @version V1.0
 * @created 2014年6月10日 下午4:33:12
 * @param <E>
 */
public abstract class SimpleAdapter<E> extends BaseAdapter {

	private List<E> data;

	protected BaseActivity activity;

	private int layoutId;

	private Class<?> holderClass;

	protected Object holder;
	
	private Class<?> cls;

	public RequestQueue mQueue;

	public ImageLoader imageLoader;
	
	public SimpleAdapter(List<E> data, BaseActivity activity, int layoutId,
			Class<?> holderClass,Class<?> cls) {
		this.data = data;
		this.activity = activity;
		this.layoutId = layoutId;
		this.holderClass = holderClass;
		this.cls=cls;
		mQueue = Volley.newRequestQueue(activity);
		imageLoader = new ImageLoader(mQueue, BitmapCache.getInstance());
	}

	@Override
	public int getCount() {
		if (!Utils.isEmpty(data)) {
			return data.size();
		}
		return 0;
	}

	@Override
	public E getItem(int arg0) {
		if (!Utils.isEmpty(data)) {
			return data.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = activity.makeView(layoutId);
			holder = buildHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = convertView.getTag();
		}
		// 避免Item在滚动中出现黑色背景
		convertView.setDrawingCacheEnabled(false);
		E item = getItem(position);
		doExtra(convertView, item,position);
		return convertView;
	}

	public void addItems(List<E> extras) {
		if(Utils.isEmpty(extras)){
			return;
		}
		data.addAll(getCount(), extras);
		notifyDataSetChanged();
	}

	public void addItems(List<E> extras, int local) {
		if(Utils.isEmpty(extras)){
			return;
		}
		data.addAll(local, extras);
		notifyDataSetChanged();
	}

	public void addItem(E extras, int local) {
		data.add(local, extras);
		notifyDataSetChanged();
	}

	public void reload(List<E> newData) {
		data.clear();
		addItems(newData);
	}

	public void refresh() {
		if (!Utils.isEmpty(data)) {
			notifyDataSetChanged();
		}
	}

	public void removeByPos(int position) {
		E e = getItem(position);
		remove(e);
	}

	public void remove(E item) {
		data.remove(item);
		notifyDataSetChanged();
	}

	public void removeAll() {
		data.clear();
		notifyDataSetChanged();
	}

	public List<E> getDataSource() {
		return data;
	}

	public void replace(E e,int position){
		if (!Utils.isEmpty(data)) {
			data.remove(position);
			data.add(position, e);
			notifyDataSetChanged();
		}
	}
	public int getPosition(E e) {
		if (!Utils.isEmpty(data)) {
			return data.indexOf(e);
		}
		return 0;
	}

	public List<E> getNearbyList(int end) {
		if (!Utils.isEmpty(data)) {
			if (end > data.size()) {
				return data.subList(0, data.size() - 1);
			}
			return data.subList(0, end);
		}
		return null;
	}

	private Object buildHolder(View convertView) {
		try {
			holder = holderClass.newInstance();
			for (Field f : holderClass.getDeclaredFields()) {
				String name = f.getName();
				f.setAccessible(true);
				// ViewHolder的属性，不论类型都初始化赋值
				f.set(holder, convertView.findViewById(RUtils.getId(name,cls)));
			}
		} catch (Exception e) {
			throw new RuntimeException("holder初始化出错    " + e);
		}
		return holder;
	}

	public abstract void doExtra(View convertView, E item,int position);
	
}
