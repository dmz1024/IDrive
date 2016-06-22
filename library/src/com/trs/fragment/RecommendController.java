package com.trs.fragment;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.trs.mobile.R;
import com.trs.types.Topic;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;
import com.viewpagerindicator.PageIndicator;
import net.endlessstudio.util.EDog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14-3-14.
 */
public class RecommendController {
	public static final int SWITCH_PAGE_DURATION = 5 * 1000;
	public static interface OnItemClickListener{
		public void onItemClick(Topic topic);
	}

	private ArrayList<Topic> mTopicList = new ArrayList<Topic>();
	private View mView;
	private ViewPager mViewPager;
	private TextView mTitle;
	private TextView mType;
	private PageIndicator mIndicator;
	private Context mContext;
	private OnItemClickListener mOnItemClickListener;
	private EDog mSwitchPageEDog = new EDog();
	private boolean mAutoSwitchPage;
	private int mLayoutID;
	private ViewGroup mParent;
	private Runnable mSwitchPageTask = new Runnable() {
		@Override
		public void run() {
			if(mViewPager.getAdapter().getCount() > 0){
				int switchToItem = (mViewPager.getCurrentItem() + 1) % mViewPager.getAdapter().getCount();
				mViewPager.setCurrentItem(switchToItem);
				startSwitchPage();
			}
		}
	};

    public RecommendController(Context context, ViewGroup parent) {
		this(context, parent, true);
	}

	public RecommendController(Context context, ViewGroup parent, boolean autoSwitchPage) {
		this(context, R.layout.recommend_view, parent, autoSwitchPage);
	}

	/**
	 * Requirement of layout id
	 * Required view:
	 * * ViewPager with id: pager
	 * * PageIndicator with id: pager_indicator
	 * Optional view:
	 * TextView with id: title
	 *
	 * @param context context
	 * @param layoutId layoutID
	 * @param autoSwitchPage is auto switch page
	 */
	public RecommendController(Context context, int layoutId, ViewGroup parent, boolean autoSwitchPage) {
		this.mContext = context;
		this.mLayoutID = layoutId;
		this.mParent = parent;
		this.mAutoSwitchPage = autoSwitchPage;
		createView();
		startSwitchPage();
	}

	private PagerAdapter mAdapter = new PagerAdapter() {
		@Override
		public int getCount() {
			return mTopicList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			final Topic topic = mTopicList.get(position);
			ImageView imgView = new ImageView(mContext);
			imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imgView.setBackgroundResource(R.drawable.cqsw_default_pic);
			new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                    build(topic.getImgUrl(), imgView).start();

			container.addView(imgView);
			imgView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mOnItemClickListener != null){
						mOnItemClickListener.onItemClick(topic);
					}
				}
			});
			return imgView;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTopicList.get(position).getTitle();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
	};

	public View getView(){
		return mView;
	}

	private void createView(){
		LayoutInflater factory = LayoutInflater.from(mContext);
		mView = factory.inflate(mLayoutID, mParent, false);

		mViewPager = (ViewPager) mView.findViewById(R.id.pager);
		mTitle = (TextView) mView.findViewById(R.id.title);
		mType = (TextView) mView.findViewById(R.id.type);
		mIndicator = (PageIndicator) mView.findViewById(R.id.pager_indicator);

		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
						cancelSwitchPage();
						System.out.println("Touches ViewPager, cancel EDog task");
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						startSwitchPage();
						System.out.println("Un-touches ViewPager, start EDog task");
						break;
				}
				return false;
			}
		});
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				RecommendController.this.onPageSelected(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		if(mTopicList.size() == 0){
			mView.setVisibility(View.GONE);
		}
	}

	private void onPageSelected(int position){
		if(position >= 0){
			Topic topic = mTopicList.get(position);
			if(!StringUtil.isEmpty(topic.getChannelname())){
				mType.setVisibility(View.VISIBLE);
				mType.setText(topic.getChannelname());
			}
			else{
				mType.setVisibility(View.INVISIBLE);
			}
            String titleText = mAdapter.getPageTitle(position).toString();
			mTitle.setText((titleText.length() > 15)?titleText.substring(0,15)+"...":titleText);
		}
	}

	public void setTopicList(List<Topic> topicList){
		mTopicList.clear();
		if(topicList != null){
			mTopicList.addAll(topicList);
		}
		mAdapter.notifyDataSetChanged();
		if(mAdapter.getCount() > 0){
			onPageSelected(mViewPager.getCurrentItem());
		}
		mView.setVisibility(mAdapter.getCount() > 0? View.VISIBLE: View.GONE);
		startSwitchPage();
	}

	public void setOnClickListener(OnItemClickListener listener){
		this.mOnItemClickListener = listener;
	}

	public void cancelSwitchPage(){
		mSwitchPageEDog.cancel();
	}

	public void startSwitchPage(){
		if(mAutoSwitchPage){
			mSwitchPageEDog.feed(mSwitchPageTask, SWITCH_PAGE_DURATION);
		}
	}

	public void onPause(){
		cancelSwitchPage();
	}

	public void onResume(){
		startSwitchPage();
	}

}
