package com.ccpress.izijia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.CityEntity;
import com.ccpress.izijia.util.HanziToPinyinUtil;
import com.ccpress.izijia.view.CitiyListSideBar;
import com.trs.app.TRSFragmentActivity;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/8
 * Time: 17:43
 */
public class CityListActivity extends TRSFragmentActivity {
    private ListView city_list;
    private ArrayList<CityEntity> cityList = new ArrayList<CityEntity>();
    private CityListAdapter cityListAdapter;
    private SearchAdapter searchAdapter;
    private int lastFirstVisibleItem = -1;
    private LinearLayout titleLayout;
    private TextView title;
    private String current_city;
    private String gps_city;
    public static final String CURRENT_CITY = "CurrentCity";
    public static final String GPS_CITY = "GpsCity";
    public static final String NEARBY_NEEDED = "NEARBY_NEEDED";
    private TextView txt_nearby;
    private EditText search_edit;
    private RelativeLayout mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        initView();
        initListView();
        initSideBar();
        mLoadingView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCity();
            }
        }, 1000);

    }

    /**
     * 布局设置
     */
    private void initView() {
        current_city = getIntent().getStringExtra(CURRENT_CITY);
        gps_city = getIntent().getStringExtra(GPS_CITY);

        mLoadingView = (RelativeLayout) findViewById(R.id.loading_view);

        txt_nearby = (TextView) findViewById(R.id.txt_nearby);
        if(getIntent().getBooleanExtra(NEARBY_NEEDED, false)){
            txt_nearby.setVisibility(View.VISIBLE);
        }
        //附近点击
        txt_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CityListActivity.this, HdMapActivity.class);
                startActivity(intent);
            }
        });

        search_edit = (EditText) findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String key = editable.toString();
                key = key.replaceAll(" ", "");
                if (!key.equals("")) {
                    doSearch(key);
                } else {
                    city_list.setAdapter(cityListAdapter);
                    cityListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 城市list 的控件布局
     */
    private void initListView() {
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) findViewById(R.id.title);

        city_list = (ListView) findViewById(R.id.city_list);
        CityEntity entity1 = new CityEntity();
        entity1.setCode("");
        entity1.setName(gps_city);
        entity1.setProvince("");
        entity1.setIsSelected(false);
        entity1.setSort_key("");
        cityList.add(entity1);

        cityListAdapter = new CityListAdapter();
        city_list.setAdapter(cityListAdapter);
        //list的ScrollListener
        city_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                //前一个List Item是固定项，且没有参与排序，这里要过滤掉
                if (firstVisibleItem == 0) {
                    titleLayout.setVisibility(View.INVISIBLE);
                    return;
                }

                titleLayout.setVisibility(View.VISIBLE);
                int section = cityListAdapter.getSectionForPosition(firstVisibleItem);
                int nextSecPosition = cityListAdapter.getPositionForSection(section + 1);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params =
                            (ViewGroup.MarginLayoutParams) titleLayout.getLayoutParams();
                    params.topMargin = 0;
                    titleLayout.setLayoutParams(params);
                    title.setText(String.valueOf((char) section));
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = titleLayout.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            titleLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                titleLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });
    //list的item点击事件
        city_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CityEntity city = null;
                ListAdapter adapter = city_list.getAdapter();
                if(adapter.getClass().equals(SearchAdapter.class)){
                    city = searchAdapter.getSearchCityList().get(position);
                }
                if(adapter.getClass().equals(CityListAdapter.class)){
                    city = cityList.get(position);
                }

                Intent intent = new Intent();
                intent.setAction(Constant.CITY_CHANGE_ACTION);
                intent.putExtra(Constant.CITY_CHANGE_CITY_STRING, city.getName());
                intent.putExtra(Constant.CITY_CHANGE_CODE_STRING, city.getCode());
                sendBroadcast(intent);
                finish();
            }
        });
    }

    /**
     * 获取城市数据
     */
    private void loadCity() {
        LoadWCMJsonTask task = new LoadWCMJsonTask(this) {
            @Override
            public void onDataReceived(String result, boolean isCache) throws Exception {
                initCitiList(result);
                cityListAdapter.notifyDataSetChanged();
                mLoadingView.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable t) {
                mLoadingView.setVisibility(View.GONE);
                Toast.makeText(CityListActivity.this,
                        getResources().getText(R.string.city_fail),
                        Toast.LENGTH_SHORT).show();
            }
        };
        task.start(Constant.CITY_LIST_URL);
    }

    /**
     * 设置城市list布局
     * @param result
     */
    private void initCitiList(String result) {
        try {
            CityEntity entity1 = cityList.get(0);
//            CityEntity entity2 = cityList.get(1);
            cityList.clear();
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Iterator it = object.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = object.getString(key);

                    JSONArray city_array = new JSONArray(value);
                    for (int j = 0; j < city_array.length(); j++) {
                        JSONObject obj = city_array.getJSONObject(j);
                        String code = obj.getString("code");
                        String city = obj.getString("city");

                        CityEntity entity = new CityEntity();
                        entity.setCode(code);
                        entity.setName(city);
                        entity.setProvince(key);
                        entity.setSort_key(HanziToPinyinUtil.getFirstPinYinByPinyin4j(city));
                        entity.setSort_key_full(HanziToPinyinUtil.getFullPinYinByPinyin4j(city));
                        if(city.equals(current_city)){
                            entity.setIsSelected(true);
                        } else {
                            entity.setIsSelected(false);
                        }

                        /**
                        *Android 自带的汉字转拼音类，效率较低，且不支持多音字
                         */
//                        entity.setSort_key(HanziToPinyinUtil.getFirstPinYin(city));
//                        entity.setSort_key_full(HanziToPinyinUtil.getFullPinYin(city));

                        cityList.add(entity);
                    }
                }
            }
            Collections.sort(cityList);
            cityList.add(0, entity1);
//            cityList.add(1, entity2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CityListAdapter extends BaseAdapter implements SectionIndexer {
        @Override
        public int getCount() {
            return cityList.size();
        }

        @Override
        public CityEntity getItem(int i) {
            return cityList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CityEntity city = getItem(position);
            LinearLayout layout = null;

            //前一个Item是固定项
            if(position == 0){
                RelativeLayout rl = (RelativeLayout) LayoutInflater.from(CityListActivity.this)
                        .inflate(R.layout.list_item_city_gps, null);
                TextView txt_name_GPS = (TextView) rl.findViewById(R.id.txt_name_GPS);
                txt_name_GPS.setText(city.getName());
                rl.setTag("0");
                return rl;
            }
//            if(position == 1){
//                RelativeLayout rl = (RelativeLayout) LayoutInflater.from(CityListActivity.this)
//                        .inflate(R.layout.list_item_city_abroad, null);
//                TextView txt_name_abroad = (TextView) rl.findViewById(R.id.txt_name_abroad);
//                txt_name_abroad.setText(city.getName());
//                rl.setTag("1");
//                return rl;
//            }

            if (convertView == null || convertView.getTag().equals("0")) {
                layout = (LinearLayout) LayoutInflater.from(CityListActivity.this)
                        .inflate(R.layout.list_item_city, null);
                layout.setTag(position);
            } else {
                layout = (LinearLayout) convertView;
            }
            TextView name = (TextView) layout.findViewById(R.id.name);
            LinearLayout sortKeyLayout = (LinearLayout) layout.findViewById(R.id.sort_key_layout);
            TextView sortKey = (TextView) layout.findViewById(R.id.sort_key);
            name.setText(city.getName());
            ImageView icon_city_checked = (ImageView) layout.findViewById(R.id.icon_city_checked);

            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                sortKey.setText(city.getSort_key().substring(0, 1));
                sortKeyLayout.setVisibility(View.VISIBLE);
            } else {
                sortKeyLayout.setVisibility(View.GONE);
            }

            if (city.isSelected()) {
                icon_city_checked.setVisibility(View.VISIBLE);
            } else {
                icon_city_checked.setVisibility(View.GONE);
            }
            return layout;
        }

        @Override
        public Object[] getSections() {
            return null;
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = 1; i < getCount(); i++) { //前一个List Item是固定项，且没有参与排序，这里要过滤掉
                String sortStr = cityList.get(i).getSort_key();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public int getSectionForPosition(int i) {
            return cityList.get(i).getSort_key().charAt(0);
        }
    }

    /**
     *右侧栏目
     */
    private void initSideBar() {
        final CitiyListSideBar sideBar = (CitiyListSideBar) findViewById(R.id.sidebar);
        sideBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float sideBarHeight = sideBar.getHeight();
                float y = event.getY();
                int sectionPosition = (int) ((y / sideBarHeight) / (1f / 26f));
                if (sectionPosition < 0) {
                    sectionPosition = 0;
                } else if (sectionPosition > 25) {
                    sectionPosition = 25;
                }
                int position = cityListAdapter.getPositionForSection(sectionPosition + 65);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        city_list.setSelection(position);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        city_list.setSelection(position);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 创建SearchAdapter
     */
    private class SearchAdapter extends BaseAdapter {
        private ArrayList<CityEntity> searchCityList;

        public void setSearchCityList(ArrayList<CityEntity> cityList) {
            this.searchCityList = cityList;
        }

        public ArrayList<CityEntity> getSearchCityList() {
            return searchCityList;
        }

        @Override
        public int getCount() {
            return searchCityList.size();
        }

        @Override
        public CityEntity getItem(int i) {
            return searchCityList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            CityEntity city = getItem(position);
            LinearLayout layout = null;

            if (convertView == null) {
                layout = (LinearLayout) LayoutInflater.from(CityListActivity.this)
                        .inflate(R.layout.list_item_city, null);
            } else {
                layout = (LinearLayout) convertView;
            }
            layout.setTag(position);

            TextView name = (TextView) layout.findViewById(R.id.name);
            name.setText(city.getName());
            ImageView icon_city_checked = (ImageView) layout.findViewById(R.id.icon_city_checked);

            if (city.isSelected()) {
                icon_city_checked.setVisibility(View.VISIBLE);
            } else {
                icon_city_checked.setVisibility(View.GONE);
            }
            return layout;
        }
    }

    private void doSearch(String key) {
        key = key.toUpperCase();
        ArrayList<CityEntity> list = new ArrayList<CityEntity>();
        for(int i=2; i<cityList.size();i++) {
            CityEntity entity = cityList.get(i);
            String name = entity.getName();
            String sort = entity.getSort_key();
            String sort_full = entity.getSort_key_full();
            if(name.contains(key) || sort.contains(key)
                    || sort_full.contains(key)){
                list.add(entity);
            }
        }

        if(searchAdapter == null){
            searchAdapter = new SearchAdapter();
        }
        searchAdapter.setSearchCityList(list);
        city_list.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }
}
