package com.ccpress.izijia.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.LinesDetailUploadEntity;
import com.ccpress.izijia.iDriveApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yl on 2015/5/25.
 * 全国范围内搜索
 */
public class MapFullSearchActivity extends TRSFragmentActivity implements  PoiSearch.OnPoiSearchListener, View.OnClickListener {
    public static final int MAX_HISTORY_KEYWORD_COUNT = 10;
    public static final String KEY_HISTORY_SEARCH_KEYWROD = "history_search_keyword";

    private String texts[] = null;
    private int images[] = null;
    private AutoCompleteTextView searchText;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private PoiResult poiResult; // poi返回的结果
    private String keyWord = "";// 要输入的poi搜索关键字
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private MyAdapter myAdapter;
    ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

    private ListView mHistorySearchKeyword;
    private View Layout_historyList;
    private ArrayList<String> mSearchKeywordList = new ArrayList<String>();

    private ListView mResultList;
    private ViewAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsearch);
       // initGPS();
        initView();
    }
    private void initGPS() {
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        //判断GPS模块是否开启，如果没有则开启
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getBaseContext(), "GPS没有打开,请打开GPS!", Toast.LENGTH_SHORT).show();
            //转到手机设置界面，用户设置GPS
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0); //设置完成后返回到原来的界面
        }
    }
    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        searchText = (AutoCompleteTextView) this.findViewById(R.id.searchText);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String keyword = searchText.getText().toString();
                if(keyword.trim().length() == 0){
                    Toast.makeText(MapFullSearchActivity.this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
                    return true;
                }

                doSearchQuery();
                return true;
            }
        });
        searchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        keyWord = searchText.getText().toString().trim();
        Layout_historyList = findViewById(R.id.Layout_historyList);
        mHistorySearchKeyword = (ListView) findViewById(R.id.searchwordhistory_list);
        mHistorySearchKeyword.setAdapter(mHistorySearchKeywordAdapter);
        mHistorySearchKeyword.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String keyword = mSearchKeywordList.get(position);
                searchText.setText(keyword);
                doSearchQuery();
            }
        });

        mResultList = (ListView) findViewById(R.id.mListSearchResult);
        animator = (ViewAnimator) findViewById(R.id.viewAnimator);
        mResultList.setAdapter(mResultAdapter);

        images = new int[]{R.drawable.gas_station, R.drawable.bus_station,
                R.drawable.repair_factory, R.drawable.service_area,
                R.drawable.restaurant, R.drawable.atm,
                R.drawable.hotel, R.drawable.toilet};
        texts = new String[]{"加油", "停车场",
                "修理厂", "服务区",
                "餐厅", "ATM",
                "酒店", "厕所"};

        GridView gridview = (GridView) findViewById(R.id.gv_search);

        for (int i = 0; i < 8; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", images[i]);
            map.put("itemText", texts[i]);
            lstImageItem.add(map);
        }

        myAdapter = new MyAdapter();
        gridview.setAdapter(myAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchText.setText(texts[position]);
                String type ="";
                switch (position) {
                    case 0:
                        type = "汽车服务|汽车维修|摩托车服务|交通设施服务";
                        break;
                    case 1:
                        type = "汽车服务|道路附属设施|公共设施|生活服务";
                        break;
                    case 2:
                        type = "汽车维修|摩托车服务|生活服务|金融保险服务";
                        break;
                    case 3:
                        type = "购物服务|生活服务|餐饮服务|公共设施|医疗保健服务";
                        break;
                    case 4:
                        type = "餐饮服务|购物服务|生活服务";
                        break;
                    case 5:
                        type = "金融保险服务|生活服务|公共设施";
                        break;
                    case 6:
                        type = "住宿服务|生活服务|体育休闲服务";
                        break;
                    case 7:
                        type = "公共设施|生活服务";
                        break;
                    default:
                        type = "";
                        break;
                }
                doIconSearchQuery(type);
            }
        });
        animator.setDisplayedChild(0);
    }

    class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return lstImageItem.size();
        }

        @Override
        public Object getItem(int position) {
            return lstImageItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MapFullSearchActivity.this, R.layout.grid_item_search, null);
            }
            TextView itemText = (TextView) convertView.findViewById(R.id.itemText);
            ImageView itemImage = (ImageView) convertView.findViewById(R.id.itemImage);
            itemText.setText(texts[position]);
            itemImage.setImageResource(images[position]);
            return convertView;
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 开始进行 Icon_poi搜索
     */
    protected void doIconSearchQuery(String type) {
        keyWord = searchText.getText().toString().trim();
        if (mSearchKeywordList.contains(keyWord)) {
            mSearchKeywordList.remove(keyWord);
        }
        mSearchKeywordList.add(keyWord);
        int historyKeywordCount = mSearchKeywordList.size();
        for (int i = MAX_HISTORY_KEYWORD_COUNT; i < historyKeywordCount; i++) {
            mSearchKeywordList.remove(MAX_HISTORY_KEYWORD_COUNT);
        }
//        mHistorySearchKeywordAdapter.notifyDataSetChanged();
//        Layout_historyList.setVisibility(View.VISIBLE);

        showProgressDialog();// 显示进度框

        String city = iDriveApplication.app().getLocation() == null ? "北京市" : iDriveApplication.app().getLocation().getCity();
        Log.e("YL", "doSearchQuery keyword:" + keyWord + " city:" + city);
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, type ,city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(query.getPageNum());// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        LatLonPoint lp = new LatLonPoint(iDriveApplication.app().getLocation().getLatitude(), iDriveApplication.app().getLocation().getLongitude());
        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            //poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
			/*
			 * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
			 * list.add(lp);
			 * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
			 * poiSearch.setBound(new SearchBound(list));// 设置多边形poi搜索范围
			 */
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        keyWord = searchText.getText().toString().trim();
        if (mSearchKeywordList.contains(keyWord)) {
            mSearchKeywordList.remove(keyWord);
        }
        mSearchKeywordList.add(keyWord);
        int historyKeywordCount = mSearchKeywordList.size();
        for (int i = MAX_HISTORY_KEYWORD_COUNT; i < historyKeywordCount; i++) {
            mSearchKeywordList.remove(MAX_HISTORY_KEYWORD_COUNT);
        }
//        mHistorySearchKeywordAdapter.notifyDataSetChanged();
//        Layout_historyList.setVisibility(View.VISIBLE);

        showProgressDialog();// 显示进度框

        String city = iDriveApplication.app().getLocation() == null ? "北京市" : iDriveApplication.app().getLocation().getCity();
        Log.e("YL", "doSearchQuery keyword:" + keyWord + " city:" + city);
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "",city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(query.getPageNum());// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        LatLonPoint lp = new LatLonPoint(iDriveApplication.app().getLocation().getLatitude(), iDriveApplication.app().getLocation().getLongitude());
        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            //poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
			/*
			 * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
			 * list.add(lp);
			 * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
			 * poiSearch.setBound(new SearchBound(list));// 设置多边形poi搜索范围
			 */
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        save();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        Log.e("WLH", "onPoiSearched poiItems.size:" + poiItems.size());
//                        showSearchResult(poiItems);
                        animator.setDisplayedChild(1);
                        mResultAdapter.notifyDataSetChanged();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(MapFullSearchActivity.this, "没有搜索结果", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(MapFullSearchActivity.this, "没有搜索结果", Toast.LENGTH_SHORT).show();
            }
        } else if (rCode == 27) {
            Toast.makeText(MapFullSearchActivity.this, "没有搜索结果", Toast.LENGTH_SHORT).show();
        } else if (rCode == 32) {
            Toast.makeText(MapFullSearchActivity.this, "没有搜索结果", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MapFullSearchActivity.this, "没有搜索结果", Toast.LENGTH_SHORT).show();
        }


    }

    private void showSearchResult(List<PoiItem> poiItems) {
        List<String> listString = new ArrayList<String>();
        for (int i = 0; i < poiItems.size(); i++) {
            listString.add(poiItems.get(i).getTitle());
        }
        ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                MapFullSearchActivity.this,
                R.layout.route_inputs, listString);
        searchText.setAdapter(aAdapter);
        aAdapter.notifyDataSetChanged();
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        Toast.makeText(MapFullSearchActivity.this, "没有搜索到数据", Toast.LENGTH_SHORT).show();

    }

    private BaseAdapter mHistorySearchKeywordAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mSearchKeywordList == null ? 0 :mSearchKeywordList.size();
        }

        @Override
        public String getItem(int position) {
            return mSearchKeywordList == null ? "" : mSearchKeywordList.get(position);
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
            }else{
                view = getLayoutInflater().inflate(R.layout.item_history_list, parent, false);
            }

            TextView text = (TextView) view.findViewById(R.id.title);
            text.setText(getItem(position));

            return view;
        }
    };

    private void load() {
        SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mSearchKeywordList = new Gson().fromJson(sp.getString(KEY_HISTORY_SEARCH_KEYWROD, "[]"), new TypeToken<ArrayList<String>>() {
        }.getType());
        mHistorySearchKeywordAdapter.notifyDataSetChanged();
        if(mSearchKeywordList != null && mSearchKeywordList.size() > 0){
            Layout_historyList.setVisibility(View.VISIBLE);
        }else {
            Layout_historyList.setVisibility(View.GONE);
        }
    }

    /**
     * 保存key
     */
    private void save() {
        SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_HISTORY_SEARCH_KEYWROD, new Gson().toJson(mSearchKeywordList));
        editor.commit();
    }

    /**
     * 清除key
     * @param v
     */
    public void onClearHistory(View v){
        SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sp.edit().clear().commit();
        Layout_historyList.setVisibility(View.GONE);
        mSearchKeywordList.clear();
    }


    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

    }

    private BaseAdapter mResultAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if(poiResult != null && poiResult.getPois() != null){
                return poiResult.getPois().size();
            }
            return 0;
        }

        @Override
        public PoiItem getItem(int i) {
            if(poiResult != null && poiResult.getPois() != null){
                return poiResult.getPois().get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view;
            if(convertView != null){
                view = convertView;
            }else{
                view = getLayoutInflater().inflate(R.layout.item_search_list, parent, false);
            }
            final TextView title = (TextView) view.findViewById(R.id.title);
            TextView adress= (TextView) view.findViewById(R.id.address);
            title.setText(getItem(position).getTitle());
            adress.setText(getItem(position).getAdName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent=new Intent(MapSearchActivity.this,MapSearchResultActivity.class);
//                    intent.putExtra("title",getItem(position).getTitle());
//                    intent.putExtra("address",getItem(position).getAdName());
//                    intent.putExtra("geo",getItem(position).getLatLonPoint().toString());
//                    MapSearchActivity.this.startActivity(intent);
                    LinesDetailMapActivity.POSITION=position;
                    LinesDetailMapActivity.FLAG=true;
                    ArrayList<PoiItem> arrayList = poiResult.getPois();
                    ArrayList<LinesDetailUploadEntity.ViewSpot> array = new ArrayList<LinesDetailUploadEntity.ViewSpot>();
                    for(int i=0; i<arrayList.size(); i++) {
                        LinesDetailUploadEntity.ViewSpot spot = new LinesDetailUploadEntity.ViewSpot();
                        PoiItem item = arrayList.get(i);
                        spot.setName(item.getTitle());
                        spot.setDesc(item.getSnippet());
                        String geo = item.getLatLonPoint().toString();
                        String str[] = geo.split(",");
                        if(str != null && str.length == 2){
                            String lati = str[0];
                            String longti = str[1];
                            spot.setGeo(longti + "," +lati);
                        }
                        array.add(spot);
                    }
                    Intent mapIntent = new Intent();
                    mapIntent.putExtra("zoomLevel", 15f);
                    mapIntent.putExtra(LinesDetailMapActivity.EXTRA_VIEWPOTS, array);
                    mapIntent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, LinesDetailUserUploadActivity.Destination);
                    mapIntent.putExtra(LinesDetailMapActivity.NAME_TEXT,keyWord);
//                    mapIntent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, lid);
                    mapIntent.setClass(MapFullSearchActivity.this, LinesDetailMapActivity.class);
                    startActivity(mapIntent);
                }
            });
            Log.e("POIaDress",getItem(position).getAdName());
            Log.e("POIaDress",getItem(position).getLatLonPoint().toString());
            return view;
        }
    };

}
