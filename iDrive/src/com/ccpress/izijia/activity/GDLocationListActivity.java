package com.ccpress.izijia.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.CityEntity;
import com.ccpress.izijia.fragment.HomeFragment;
import com.ccpress.izijia.fragment.HomeTabFragment;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.GDLocationUtil;
import com.ccpress.izijia.util.HanziToPinyin;
import com.ccpress.izijia.util.HanziToPinyinUtil;

import com.ccpress.izijia.util.LocationUtil;
import com.ccpress.izijia.view.CitiyListSideBar;
import com.trs.app.TRSFragmentActivity;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.trs.util.log.Log;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created hexulan
 * Date: 2016/1/7
 * Time: 14:20
 */
public class GDLocationListActivity extends TRSFragmentActivity {
//    public static final String CURRENT_LOCATION = "CurrentLocation";
//    public static final String GPS_LOCATION = "GpsCity";
    public static final String GD_IS_PROVINCE_LIST = "IsProvinceList";
    public static final String GD_PROVINCE_CODE = "ProvinceCode";
    public static final String GD_PROVINCE = "Province";
    public static final String GD_IS_NEED_CHOOSE_CITY = "IsNeedChooseCity";

    private boolean isProvinceList;
    private boolean isNeedChooseCity;
    private String provinceCode = "";
    private String province = "";//当且仅当从省份列表到城市列表才使用此项
    private ListView location_list;
    private String current_location;
    private String gps_location;
    private String gps_location_code;
    private RelativeLayout mLoadingView;
    private EditText search_edit;
    private LinearLayout titleLayout;
    private TextView title;
    private ArrayList<CityEntity> locationList = new ArrayList<CityEntity>();
    private LocationListAdapter locationListAdapter;
    private int lastFirstVisibleItem = -1;
    private SearchAdapter searchAdapter;

    public static final String NEARBY_NEEDED = "NEARBY_NEEDED";
    private TextView txt_nearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

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

    private void initView() {
        txt_nearby = (TextView) findViewById(R.id.txt_nearby);
        if(getIntent().getBooleanExtra(NEARBY_NEEDED, false)){
            txt_nearby.setVisibility(View.VISIBLE);
        }
        txt_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GDLocationListActivity.this, HdMapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        isProvinceList = getIntent().getBooleanExtra(GD_IS_PROVINCE_LIST, true);
        isNeedChooseCity = getIntent().getBooleanExtra(GD_IS_NEED_CHOOSE_CITY, false);
        current_location = isProvinceList ? GDLocationUtil.getGDCurrentSetProvince(getApplicationContext())
                : GDLocationUtil.getGDCurrentSetCity(getApplicationContext());
        gps_location =  GDLocationUtil.getGDGpsCity(getApplicationContext());
        gps_location_code = GDLocationUtil.getGDGpsCityCode(getApplicationContext());
        provinceCode = getIntent().getStringExtra(GD_PROVINCE_CODE);
        province = getIntent().getStringExtra(GD_PROVINCE);
        if (current_location.equals("")) {
            current_location = province;
        }

        mLoadingView = (RelativeLayout) findViewById(R.id.loading_view);

        search_edit = (EditText) findViewById(R.id.search_edit);
        if(isProvinceList){
            search_edit.setHint(R.string.search_province_hint);
        } else {
            search_edit.setHint(R.string.search_city_hint);
        }
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
                    location_list.setAdapter(locationListAdapter);
                    locationListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initListView() {
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) findViewById(R.id.title);

        location_list = (ListView) findViewById(R.id.location_list);
        CityEntity entity1 = new CityEntity();
        entity1.setCode(gps_location_code);
        entity1.setName(gps_location);
        entity1.setProvince("");
        entity1.setIsSelected(false);
        entity1.setSort_key("");
        locationList.add(entity1);

        locationListAdapter = new LocationListAdapter();
        location_list.setAdapter(locationListAdapter);
        location_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                //前一个List Item是固定项，且没有参与排序，这里要过滤掉
                if (firstVisibleItem == 0) {
                    titleLayout.setVisibility(View.GONE);
                    return;
                }
                titleLayout.setVisibility(View.VISIBLE);
                int section = locationListAdapter.getSectionForPosition(firstVisibleItem);
                int nextSecPosition = locationListAdapter.getPositionForSection(section + 1);
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
            //城市list的item点击事件
        location_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CityEntity city = null;
                ListAdapter adapter = location_list.getAdapter();
                if (adapter.getClass().equals(SearchAdapter.class)) {
                    city = searchAdapter.getSearchCityList().get(position);
                }
                if (adapter.getClass().equals(LocationListAdapter.class)) {
                    city = locationList.get(position);
                }
                if (isProvinceList && isNeedChooseCity) {
                    if (position==0){
                    String now="";
                    if(iDriveApplication.app().getLocation() != null){
                        now = iDriveApplication.app().getLocation().getCity();
                    }else {
//                        now="北京市";
                        LocationUtil.clearCurrentLocation(getApplicationContext());
                        now= LocationUtil.getCity(getApplicationContext());
                    }

                        HomeFragment.CITY=now;
                        HomeTabFragment.TAB_CITY=now;

//                        try {
//                            Thread.currentThread().sleep(1000);//阻断1秒
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }//阻断1秒

//                        HomeFragment.CITY=now;
//                        HomeTabFragment.TAB_CITY=now;
//                    Intent intent = new Intent();
//                    intent.setAction(Constant.GD_CITY_CHANGE_ACTION);
//                        sendBroadcast(intent);
//
//
//                    Intent intent1 = new Intent();
//                    intent1.setAction(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION);
//                    intent1.putExtra(Constant.TOP_POPUP_INTERACT_LIST_CLICK_INDEX, 0);
//                    getApplication().sendBroadcast(intent1);


                        finish();
                    }else {
                        HomeTabFragment.TAB_CITY=city.getName();
                        HomeFragment.CITY=city.getName();
                        Intent intent = new Intent(GDLocationListActivity.this, GDLocationListActivity.class);
                        intent.putExtra(GDLocationListActivity.GD_IS_PROVINCE_LIST, false);
                        intent.putExtra(GDLocationListActivity.GD_IS_NEED_CHOOSE_CITY, false);
                        intent.putExtra(GDLocationListActivity.GD_PROVINCE_CODE, city.getCode());
                        intent.putExtra(GDLocationListActivity.GD_PROVINCE, city.getName());

//                        sendBroadcast(intent);
//                        Intent intent1 = new Intent();
//                        intent1.setAction(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION);
//                        intent1.putExtra(Constant.TOP_POPUP_INTERACT_LIST_CLICK_INDEX, 0);
//                        getApplication().sendBroadcast(intent1);
                        startActivity(intent);
                        finish();
                    }


                } else {
                    if (isProvinceList) {

                        GDLocationUtil.setGDCurrentSetProvince(getApplicationContext(), city.getName());
                        GDLocationUtil.setGDCurrentSetProvinceCode(getApplicationContext(), city.getCode());
                        HomeTabFragment.TAB_CITY=city.getName();
                        HomeFragment.CITY=city.getName();
//                        Intent intent = new Intent();
//                        intent.setAction(Constant.GD_CITY_CHANGE_ACTION);
//                        intent.putExtra(Constant.GD_CITY_CHANGE_CITY_STRING, city.getName());
//                        intent.putExtra(Constant.GD_CITY_CHANGE_CODE_STRING, city.getCode());
//                        sendBroadcast(intent);
//
//                        Intent intent1 = new Intent();
//                        intent1.setAction(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION);
//                        intent1.putExtra(Constant.TOP_POPUP_INTERACT_LIST_CLICK_INDEX, 0);
//                        getApplication().sendBroadcast(intent1);

                        finish();
                    } else {
                        GDLocationUtil.setGDCurrentSetCity(getApplicationContext(), city.getName());
                        GDLocationUtil.setGDCurrentSetCityCode(getApplicationContext(), city.getCode());
                        GDLocationUtil.setGDCurrentSetProvince(getApplicationContext(), province);
                        GDLocationUtil.setGDCurrentSetProvinceCode(getApplicationContext(), provinceCode);
                        HomeFragment.CITY=city.getName();
                        HomeTabFragment.TAB_CITY=city.getName();
                        Intent intent = new Intent();
//                        intent.setAction(Constant.GD_CITY_CHANGE_ACTION);
//                        intent.putExtra(Constant.GD_CITY_CHANGE_CITY_STRING, city.getName());
//                        intent.putExtra(Constant.GD_CITY_CHANGE_CODE_STRING, city.getCode());
//                        sendBroadcast(intent);
//
//                        Intent intent1 = new Intent();
//                        intent1.setAction(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION);
//                        intent1.putExtra(Constant.TOP_POPUP_INTERACT_LIST_CLICK_INDEX, 0);
//                        getApplication().sendBroadcast(intent1);

                        finish();
                    }

                }

            }
        });
    }

    /**
     * 创建LocationListAdapter
     */
    private class LocationListAdapter extends BaseAdapter implements SectionIndexer {
        @Override
        public int getCount() {
            return locationList.size();
        }

        @Override
        public CityEntity getItem(int i) {
            return locationList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
      private   TextView txt_name_GPS;
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final CityEntity city = getItem(position);
            LinearLayout layout = null;


            //前一个Item是固定项
            if (position == 0) {
                RelativeLayout rl = (RelativeLayout) LayoutInflater.from(GDLocationListActivity.this)
                        .inflate(R.layout.list_item_city_gps, null);
                txt_name_GPS = (TextView) rl.findViewById(R.id.txt_name_GPS);
                txt_name_GPS.setText(city.getName());
                rl.setTag("0");
                return rl;

            }

            if (convertView == null || convertView.getTag().equals("0")) {
                layout = (LinearLayout) LayoutInflater.from(GDLocationListActivity.this)
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
                String sortStr = locationList.get(i).getSort_key();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public int getSectionForPosition(int i) {
            return locationList.get(i).getSort_key().charAt(0);
        }
    }

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
            if (position == 0) {
                RelativeLayout rl = (RelativeLayout) LayoutInflater.from(GDLocationListActivity.this)
                        .inflate(R.layout.list_item_city_gps, null);
                TextView txt_name_GPS = (TextView) rl.findViewById(R.id.txt_name_GPS);
                txt_name_GPS.setText(city.getName());

                rl.setTag("0");
                return rl;

            }
            if (convertView == null || convertView.getTag().equals("0") ) {
                layout = (LinearLayout) LayoutInflater.from(GDLocationListActivity.this)
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

    /**
     * 右侧栏目布局设置
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
                int position = locationListAdapter.getPositionForSection(sectionPosition + 65);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        location_list.setSelection(position);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        location_list.setSelection(position);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 获取城市list数据
     */
    private void loadCity() {

        if (isProvinceList) {
            String fileName = "province.json";
			
            initCitiList(getFromAssets(fileName));
            locationListAdapter.notifyDataSetChanged();
            mLoadingView.setVisibility(View.GONE);
        } else {
            String fileNames = provinceCode + ".json";
            initCitiList(getFromAssets(fileNames));
            locationListAdapter.notifyDataSetChanged();
            mLoadingView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取Assets的json数据
     * @param fileName
     * @return
     */
    public String getFromAssets(String fileName){
        String result = "";
        try {
            InputStream in = getResources().getAssets().open(fileName);
            int lenght = in.available();
            byte[]  buffer = new byte[lenght];
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 城市list的数据
     * @param result
     */
    private void initCitiList(String result) {
        try {
            CityEntity entity1 = locationList.get(0);
            locationList.clear();
            JSONObject objj = new JSONObject(result);
            JSONArray array = objj.getJSONArray("datas");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String code = object.getString("CODE");
                String city = object.getString("NAME");
                CityEntity entity = new CityEntity();
                entity.setCode(code);
                entity.setName(city);

                /**
                 *Android 自带的汉字转拼音类，效率较低，且不支持多音字
                 */
                entity.setSort_key(HanziToPinyinUtil.getFirstPinYin(city));
                entity.setSort_key_full(HanziToPinyinUtil.getFullPinYin(city));

                if (city.equals(current_location)) {
                    entity.setIsSelected(true);
                } else {
                    entity.setIsSelected(false);
                }
                locationList.add(entity);
            }
            Collections.sort(locationList);
            locationList.add(0, entity1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSearch(String key) {
        key = key.toUpperCase();
        ArrayList<CityEntity> list = new ArrayList<CityEntity>();
        for (int i = 2; i < locationList.size(); i++) {
            CityEntity entity = locationList.get(i);
            String name = entity.getName();
            String sort = entity.getSort_key();
            String sort_full = entity.getSort_key_full();
            if (name.contains(key) || sort.contains(key)
                    || sort_full.contains(key)) {
                list.add(entity);
            }
        }

        if (searchAdapter == null) {
            searchAdapter = new SearchAdapter();
        }
        searchAdapter.setSearchCityList(list);
        location_list.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

}
