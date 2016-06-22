package com.froyo.commonjar.adapter;

import java.lang.reflect.Field;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.utils.RUtils;
import com.froyo.commonjar.utils.Utils;

/**
 * 
 * @desc
 * @author WY 创建时间 2014年3月11日 下午2:11:43
 * @param <E>
 */
public abstract class SimpleExpandAdapter<ParentVo, E> extends
		BaseExpandableListAdapter {

	private List<ParentVo> parentData;

	private int parentResId;

	private int childResId;

	private BaseActivity activity;

	private Class<?> parentHolderClass;

	private Class<?> childHolderClass;

	protected Object parentHolder;

	protected Object childHolder;

	private Class<?> idcls;

	public SimpleExpandAdapter(BaseActivity activity,
			Class<?> parentHolderClass, Class<?> childHolderClass,
			Class<?> idcls, List<ParentVo> parentData, int parentResId,
			int childResId) {
		this.parentData = parentData;
		this.activity = activity;
		this.parentResId = parentResId;
		this.childResId = childResId;
		this.parentHolderClass = parentHolderClass;
		this.childHolderClass = childHolderClass;
		this.idcls = idcls;
	}

	@Override
	public E getChild(int groupPosition, int childPosition) {
		ParentVo vo = parentData.get(groupPosition);
		return (E) ((com.froyo.commonjar.adapter.ParentVo) vo).getChild().get(
				childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = activity.makeView(childResId);
			childHolder = buildChildHolder(convertView);
			convertView.setTag(childHolder);
		} else {
			childHolder = convertView.getTag();
		}
		// 避免Item在滚动中出现黑色背景
		convertView.setDrawingCacheEnabled(false);
		E item = getChild(groupPosition, childPosition);
		doChildExtra(convertView, item, groupPosition, childPosition,
				isLastChild);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (Utils.isEmpty(parentData)) {
			return 0;
		}
		return ((com.froyo.commonjar.adapter.ParentVo) parentData
				.get(groupPosition)).getChild().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return parentData.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return parentData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = activity.makeView(parentResId);
			parentHolder = buildParentHolder(convertView);
			convertView.setTag(parentHolder);
		} else {
			parentHolder = convertView.getTag();
		}
		// 避免Item在滚动中出现黑色背景
		convertView.setDrawingCacheEnabled(false);
		ParentVo item = (ParentVo) getGroup(groupPosition);
		doParentExtra(convertView, item, groupPosition, isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void addParents(List<ParentVo> datas) {
		parentData.addAll(datas);
		notifyDataSetChanged();
	}

	public void addParent(ParentVo data) {
		parentData.add(data);
		notifyDataSetChanged();
	}

	public void addChilds(int groupPosition, List<E> childs) {
		((com.froyo.commonjar.adapter.ParentVo) parentData.get(groupPosition))
				.addChild(childs);
		notifyDataSetChanged();
	}
	public void addChild(int groupPosition, E child) {
		((com.froyo.commonjar.adapter.ParentVo) parentData.get(groupPosition))
		.addChild(child);
		notifyDataSetChanged();
	}

	public boolean hasChild(int groupPosition) {
		return getChildrenCount(groupPosition) > 0 ? true : false;
	}

	private Object buildParentHolder(View convertView) {
		try {
			parentHolder = parentHolderClass.newInstance();
			for (Field f : parentHolderClass.getDeclaredFields()) {
				String name = f.getName();
				f.setAccessible(true);
				// ViewHolder的属性，不论类型都初始化赋值
				f.set(parentHolder,
						convertView.findViewById(RUtils.getId(name, idcls)));
			}
		} catch (Exception e) {
			throw new RuntimeException("parentHolder初始化出错    " + e);
		}
		return parentHolder;
	}

	private Object buildChildHolder(View convertView) {
		try {
			childHolder = childHolderClass.newInstance();
			for (Field f : childHolderClass.getDeclaredFields()) {
				String name = f.getName();
				f.setAccessible(true);
				// ViewHolder的属性，不论类型都初始化赋值
				f.set(childHolder,
						convertView.findViewById(RUtils.getId(name, idcls)));
			}
		} catch (Exception e) {
			throw new RuntimeException("childHolder初始化出错    " + e);
		}
		return childHolder;
	}

	public abstract void doParentExtra(View convertView, ParentVo item,
			int groupPosition, boolean isExpanded);

	public abstract void doChildExtra(View convertView, E item,
			int groupPosition, int childPostion, boolean isLastChild);
}
