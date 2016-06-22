package com.trs.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.astuetz.PagerSlidingTabStrip;
import com.trs.app.FragmentCache;
import com.trs.app.FragmentFactory;
import com.trs.app.TRSApplication;
import com.trs.mobile.R;
import com.trs.types.Channel;
import com.trs.util.log.Log;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14-3-11.
 */
public class TabFragment extends AbsUrlFragment implements PagerSlidingTabStrip.OnScrollChangedListener {
	public static final String EXTRA_CATEGORY = "category";
	private PagerSlidingTabStrip mTab;
	private ViewPager mPager;
	private ArrayList<Channel> mChannelList = new ArrayList<Channel>();
//    private View mLoadingView;
	private View mLeftArrow;
	private View mRightArrow;
	private String mCategory;

	private FragmentStatePagerAdapter mAdapter;

    private View mTabContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();
		if(arguments != null){
			mCategory = arguments.getString(EXTRA_CATEGORY);
		}
	}

    public View getTabView(){
        return mTabContent;
    }

	public ViewPager getPager(){
		return mPager;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View view = View.inflate(getActivity(), getViewID(), null);

        mTabContent = view.findViewById(R.id.tab_container);
		mTab = (PagerSlidingTabStrip) view.findViewById(R.id.tab);
		mPager = (ViewPager) view.findViewById(R.id.pager);
//		mLoadingView = view.findViewById(R.id.loading_view);
		mLeftArrow = view.findViewById(R.id.left_arrow);
		mRightArrow = view.findViewById(R.id.right_arrow);

		mTab.setTextColor(Color.BLACK, Color.rgb(232,58,59));
		mPager.post(new Runnable() {
			@Override
			public void run() {
				mTab.setScrollOffset(mPager.getWidth() * 2 / 5);
			}
		});

		mAdapter  = new FragmentStatePagerAdapter(getChildFragmentManager()) {
			private Fragment mCurrentPrimaryItem = null;

			@Override
			public int getItemPosition(Object object) {
				return POSITION_NONE;
			}

			@Override
			public Fragment getItem(int position) {
				Channel channel = getChannelList().get(position);
				Fragment fragment = FragmentCache.getInstance().get(channel);
				if(fragment == null){
					fragment = FragmentFactory.createFragment(getActivity(), channel);
					Bundle arguments = fragment.getArguments();
//					if(arguments != null){
//						fragment.getArguments().remove(AbsTRSFragment.EXTRA_TITLE);
//					}
//					else{
//						arguments = new Bundle();
//					}
//
//					if(fragment instanceof DocumentListFragment){
//						arguments.putString(DocumentListFragment.EXTRA_CATEGORY, getCategory());
//					}
				}

                return fragment;
			}

			@Override
			public void setPrimaryItem(ViewGroup container, int position, Object object) {
				super.setPrimaryItem(container, position, object);
				if(mCurrentPrimaryItem != object){
                    Fragment primaryItem = (Fragment) object;
					System.out.println("fragment " + position + " been set as primary");
					if(primaryItem instanceof AbsTRSFragment){
                        ((AbsTRSFragment) primaryItem).notifyDisplay();
                    }

                    if(mCurrentPrimaryItem != null && mCurrentPrimaryItem instanceof AbsTRSFragment){
                        ((AbsTRSFragment)mCurrentPrimaryItem).onHide();
                    }

                    mCurrentPrimaryItem = primaryItem;
				}
			}

			@Override
			public int getCount() {
				return getChannelList().size();
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return getChannelList().get(position).getTitle();
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return super.isViewFromObject(view, object);
			}
		};

		mPager.setAdapter(mAdapter);
		mTab.setViewPager(mPager);

		mTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				System.out.print("Set fragment " + mPager.getCurrentItem() + " selected on on page selected");
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		mTab.setOnScrollChangedListener(this);

		loadData();
		return view;
	}

	protected void loadData(){
		LoadWCMJsonTask task = new LoadWCMJsonTask(getActivity()) {
			@Override
			public void onDataReceived(String result, boolean isCache) throws Exception {
				if(getActivity() == null){
					return;
				}

				JSONObject obj = new JSONObject(result);
				if(obj.has("code") && obj.getString("code").equals("0")){
					TabFragment.this.onDataReceived(createChannelList(obj.getString("datas")));
				} else {
					if(obj.has("message")){
						Log.v("Library_TabFragment", obj.getString("message"));
					}
					Toast.makeText(getActivity(),
							"标签下载失败",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onError(Throwable t) {
				if(getActivity() == null){
					return;
				}

//				mLoadingView.setVisibility(View.GONE);
				Toast.makeText(getActivity(), R.string.internet_unavailable, Toast.LENGTH_LONG).show();
			}
		};
//		mLoadingView.setVisibility(View.VISIBLE);
		task.start(getUrl());
	}

	@Override
	public String getUrl() {
		switch(TRSApplication.app().getSourceType()){
			case JSON:
				return getRequestJsonUrl();
			case XML:
				return getRequestXmlUrl();
            case SOAP:
                return getRequestSoapUrl();
			default:
				return null;
		}
	}

    private String getRequestSoapUrl(){
        return super.getUrl();
    }

	private String getRequestXmlUrl(){
		String url = super.getUrl();
		if(!url.endsWith("xml")){
			if(!url.endsWith("/")){
				url += "/";
			}

			url += "channels.xml";
		}

		return url;
	}

	private String getRequestJsonUrl(){
		String url = super.getUrl();
		//爱自驾需要去掉下面这段
//		if(!url.endsWith("json")){
//			if(!url.endsWith("/")){
//				url += "/";
//			}
//
//			url += "index.json";
//		}

		return url;
	}

	public void onDataReceived(List<Channel> channel){
//		mLoadingView.setVisibility(View.GONE);

		mChannelList.clear();
		mChannelList.addAll(channel);

		mAdapter.notifyDataSetChanged();
		mTab.notifyDataSetChanged();

		mTab.postDelayed(new Runnable() {
			@Override
			public void run() {
				onScrollChanged(mTab.getScrollX(), mTab.getScrollY(), mTab.getScrollX(), mTab.getScrollY());
			}
		}, 500);

		mPager.setCurrentItem(getInitPage());

	}

	@Override
	public void onDisplay() {

	}

	@Override
	public void onHide() {

	}

	protected List<Channel> createChannelList(String data) throws JSONException {
		switch(TRSApplication.app().getSourceType()){
		case JSON:
			return createFromJson(data);
		case XML:
			return createFromXML(data);
		default:
			return null;
		}
	}

	private List<Channel> createFromJson(String json) throws JSONException {
		JSONArray array = new JSONArray(json);
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		for(int i = 0; i < array.length(); i ++){
			Channel c = new Channel(array.getJSONObject(i));
			c.setType("0");
			channelList.add(c);
		}

		return channelList;

	}

	private List<Channel> createFromXML(String xml) throws JSONException {
		JSONObject obj = XML.toJSONObject(xml);
		obj = obj.getJSONObject("cs");
		JSONArray array = obj.getJSONArray("c");
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		for(int i = 0; i < array.length(); i ++){
			Channel c = new Channel(array.getJSONObject(i));
			channelList.add(c);
		}

		return channelList;

	}

	protected int getInitPage(){
		return 0;
	}

	protected List<Channel> getChannelList(){
		return mChannelList;
	}

	public PagerSlidingTabStrip getTab(){
		return mTab;
	}

	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		int contentWidth = mTab.getContainer().getWidth();
		int tabWidth = mTab.getWidth() - mTab.getPaddingLeft() - mTab.getPaddingRight();

		mLeftArrow.setEnabled(l > 100);
		mRightArrow.setEnabled(contentWidth - (l + tabWidth) > 100);
	}

	/**
	 * Get view's layout id.<br/>
	 * Required view:<br/>
	 * View	ID<br/>
	 * * PagerSlidingTabStrip with id: tab<br/>
	 * * ViewPager with id: pager<br/>
	 * Optional view:<br/>
	 * * View(enable/disable) with id: left_arrow.<br/>
	 * * View(enable/disable) with id: right_arrow<br/>
	 * @return layout id
	 */
	public int getViewID(){
		return R.layout.tab_fragment;
	}

	public PagerAdapter getAdapter(){
		return mAdapter;
	}

	public void notifyChannelDataChanged(){
		mAdapter.notifyDataSetChanged();
		mTab.notifyDataSetChanged();
	}

	public String getCategory() {
		return mCategory;
	}

//    public View getmLoadingView() {
//        return mLoadingView;
//    }
}
