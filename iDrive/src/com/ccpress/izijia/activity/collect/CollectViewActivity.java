package com.ccpress.izijia.activity.collect;


import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.view.CustomExpandableListView;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.CollectViewAdapter;
import com.ccpress.izijia.vo.CollectViewChildVo;
import com.ccpress.izijia.vo.CollectViewPvo;
/**
 * 收藏---看点---详情
 * @author wangyi
 *
 */
public class CollectViewActivity extends BaseActivity {

	@ViewInject(R.id.ex_list)
	private CustomExpandableListView ex_list;
	
	private  CollectViewAdapter adapter;
	
	/** 控制列表的展开，用于每次都定位于展开的项 */
	private int sign = -1;
	
	@Override
	public void doBusiness() {
		adapter=new CollectViewAdapter(activity, new ArrayList<CollectViewPvo>(), R.layout.expand_list_item, R.layout.expand_list_child_item);
		ex_list.setAdapter(adapter);
		for (int i = 0; i < 10; i++) {
			CollectViewPvo vo=new CollectViewPvo();
			vo.setTitle("看点"+i);
			List<CollectViewChildVo> childs=new ArrayList<CollectViewChildVo>();
			for (int j = 0; j < 5; j++) {
				CollectViewChildVo temp=new CollectViewChildVo();
				temp.setContent("小看点"+i+" "+j);
				childs.add(temp);
			}
			vo.setChild(childs);
			adapter.addParent(vo);
		}
		ex_list.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				CollectViewChildVo item = (CollectViewChildVo) adapter.getChild(groupPosition,
						childPosition);
				toast(item.getContent());
				return true;
			}
		});
		// 控制list只展开一项
		ex_list.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				for (int i = 0; i < adapter.getGroupCount(); i++) {
					if (groupPosition != i) {
						ex_list.collapseGroup(i);
					}else{
						
					}
				}
			}
		});
		// 控制list展开的一项为最前面
		ex_list.expandGroup(0);
		ex_list.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (adapter.hasChild(groupPosition)) {
					if (sign == -1) {
						ex_list.expandGroup(groupPosition);
						ex_list.setSelectedGroup(groupPosition);
						sign = groupPosition;
					} else if (sign == groupPosition) {
						ex_list.collapseGroup(groupPosition);
						sign = -1;
					} else {
						ex_list.collapseGroup(sign);
						ex_list.expandGroup(groupPosition);
						ex_list.setSelectedGroup(groupPosition);
						sign = groupPosition;
					}
					return true;
				} else {
					return false;
				}
			}
		});
		
	}
	
	
	@Override
	protected int setLayoutResID() {
		return R.layout.activity_collect_view;
	}

}
