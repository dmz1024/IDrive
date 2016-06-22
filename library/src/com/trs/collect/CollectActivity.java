package com.trs.collect;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.astuetz.PagerSlidingTabStrip;
import com.trs.app.TRSFragmentActivity;
import com.trs.mobile.R;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by john on 14-2-27.
 */
public class CollectActivity extends TRSFragmentActivity {
	public static final String ACTION_COLLECT_ITEM_CLICKED = "com.trs.mobile.extra.collect_item_clicked";
	public static final String EXTRA_COLLECT_ITEM = "collect_item";

	private String[] mTypes = {"全部", "今日推荐", "政府资讯","政务公开","今日金华","生活资讯"};
	private View[] mListViews = new ListView[mTypes.length];
	private ArrayList<CollectItem>[] mCollectList = new ArrayList[mTypes.length];
	private PagerSlidingTabStrip mTab;
	private ViewPager mPager;

	private class CollectAdapter extends BaseAdapter{
		private int mTypeIndex;
		private ArrayList<CollectItem> mList;

		private CollectAdapter(int typeIndex) {
			this.mTypeIndex = typeIndex;
			this.mList = mCollectList[typeIndex];
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public CollectItem getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if(convertView != null){
				view = convertView;
			}
			else{
				view = LayoutInflater.from(CollectActivity.this).inflate(R.layout.collect_list_item, parent, false);
			}

			CollectItem item = getItem(position);

			ImageView img = (ImageView) view.findViewById(R.id.img);
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView date = (TextView) view.findViewById(R.id.date);
			TextView summary = (TextView) view.findViewById(R.id.summary);
			TextView type = (TextView) view.findViewById(R.id.type);

			boolean hasImg = !StringUtil.isEmpty(item.getPic());
			boolean hasTitle = !StringUtil.isEmpty(item.getTitle());
			boolean hasDate = !StringUtil.isEmpty(item.getTime());
			boolean hasSummary = false;
			boolean hasType = mTypeIndex == 0 && !StringUtil.isEmpty(item.getType());

			if(img != null){
//				img.setVisibility(hasImg ? View.VISIBLE : View.GONE);
				if(hasImg){
					new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).build(item.getPic(), img).start();
				}
			}

			if(title != null){
				title.setVisibility(hasTitle ? View.VISIBLE : View.GONE);
				if(hasTitle){
					title.setText(item.getTitle());
					title.setMaxLines(hasSummary ? 1 : 2);
				}
			}

			if(date != null){
				date.setVisibility(hasDate ? View.VISIBLE : View.GONE);
				if(hasDate){
					date.setText(item.getTime());
				}
			}

			if(summary != null){
				summary.setVisibility(hasSummary? View.VISIBLE : View.GONE);
			}

			if(type != null){
				type.setVisibility(hasType? View.VISIBLE: View.GONE);
				if(hasType){
					type.setText(item.getType());
				}
			}

			return view;

		}
	}

	private PagerAdapter mPagerAdapter = new PagerAdapter() {
		@Override
		public int getCount() {
			return mTypes.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTypes[position];
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if(mListViews[position] == null){
				mListViews[position] = getLayoutInflater().inflate(R.layout.collect_list, null);
				mCollectList[position] = new ArrayList<CollectItem>();
				mCollectList[position].addAll(CollectItem.get(CollectActivity.this, position == 0? null: mTypes[position]));

				final ListView list = (ListView) mListViews[position].findViewById(R.id.list);
				list.setAdapter(new CollectAdapter(position));

				list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						CollectAdapter adapter = (CollectAdapter) list.getAdapter();
						CollectItem item = adapter.getItem((int)id);

						CollectActivity.this.onItemClick(item);
					}
				});
			}

			container.addView(mListViews[position]);
			return mListViews[position];
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.collect);

		mTab = (PagerSlidingTabStrip) findViewById(R.id.tab);
		mPager = (ViewPager) findViewById(R.id.pager);

		mPager.setAdapter(mPagerAdapter);
		mTab.setViewPager(mPager);

		mTab.setTextColor(Color.WHITE, Color.BLACK);
		mPager.post(new Runnable() {
			@Override
			public void run() {
				mTab.setScrollOffset(mPager.getWidth() * 2 / 3);
			}
		});
	}

	public void onBtnBackClick(View view){
		finish();
	}

	public void onItemClick(CollectItem item){
		Intent intent = new Intent(ACTION_COLLECT_ITEM_CLICKED);
		intent.putExtra(EXTRA_COLLECT_ITEM, item);
		sendBroadcast(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mCollectList[mPager.getCurrentItem()] != null){
			mCollectList[mPager.getCurrentItem()].clear();
			mCollectList[mPager.getCurrentItem()].addAll(CollectItem.get(CollectActivity.this, mPager.getCurrentItem() == 0? null: mTypes[mPager.getCurrentItem()]));
			((ListView)mListViews[mPager.getCurrentItem()].findViewById(R.id.list)).setAdapter(new CollectAdapter(mPager.getCurrentItem()));
		}
	}
}
