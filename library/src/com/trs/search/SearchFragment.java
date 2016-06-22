package com.trs.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trs.fragment.AbsWCMListFragment;
import com.trs.mobile.R;
import com.trs.types.Page;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by john on 14-5-7.
 */
public class SearchFragment extends AbsWCMListFragment {
    public static final int MAX_HISTORY_KEYWORD_COUNT = 10;
    public static final String TAG_VIEW_TYPE = "key_word";
    public static final String KEY_HISTORY_SEARCH_KEYWROD = "history_search_keyword";

    private EditText mSearchKeyword;
    private ListView mHistorySearchKeyword;
    private ArrayList<String> mSearchKeywordList = new ArrayList<String>();
    private String mKeyword = "";
    private BaseAdapter mHistorySearchKeywordAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mSearchKeyword = (EditText) view.findViewById(R.id.search_keyword);
        mSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String keyword = mSearchKeyword.getText().toString();
                if(keyword.trim().length() == 0){
                    Toast.makeText(getActivity(), "请输入搜索关键字", Toast.LENGTH_SHORT).show();
                    return true;
                }

                doSearch(keyword);
                return true;
            }
        });

        mHistorySearchKeyword = (ListView) view.findViewById(R.id.history_search_keyword);
        mHistorySearchKeyword.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String keyword = mSearchKeywordList.get(position);
                mSearchKeyword.setText(keyword);
                doSearch(keyword);
            }
        });

        mHistorySearchKeywordAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mSearchKeywordList.size();
            }

            @Override
            public String getItem(int position) {
                return mSearchKeywordList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                if(convertView != null && TAG_VIEW_TYPE.equals(convertView.getTag(R.id.view_type))){
                    view = convertView;
                }
                else{
                    view = getActivity().getLayoutInflater().inflate(R.layout.search_keyword_item, parent, false);
                    view.setTag(R.id.view_type, TAG_VIEW_TYPE);
                }

                TextView text = (TextView) view.findViewById(R.id.title);
                text.setText(getItem(position));

                return view;
            }
        };
        mHistorySearchKeyword.setAdapter(mHistorySearchKeywordAdapter);

        return view;
    }

    @Override
    protected String getRequestUrl(int requestIndex) {
        String keyword = this.mKeyword;

        try {
            keyword = URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getUrl()).append('?').append('&').append("Searchword=")
                .append(keyword);
        if (requestIndex > 0) {
            sb.append("&PageIndex=").append(requestIndex);
        }

        return sb.toString();
    }

    @Override
    protected Page createPage(String data) throws JSONException {
        Page page = new Page(new JSONObject(data));
        return page;
    }

    @Override
    protected int getViewID() {
        return R.layout.search_fragment;
    }

    @Override
    protected View getTopView(RelativeLayout parent) {
        return null;
    }

    @Override
    protected void onDataReceived(Page page) {
        super.onDataReceived(page);

        if(getAdapter().getCount() == 0){
            mHistorySearchKeyword.setVisibility(View.VISIBLE);
            getListView().setVisibility(View.GONE);
        }
        else{
            mHistorySearchKeyword.setVisibility(View.GONE);
            getListView().setVisibility(View.VISIBLE);
        }
    }

    private void doSearch(String keyword){
        if(mSearchKeywordList.contains(keyword)){
            mSearchKeywordList.remove(keyword);
        }

        mSearchKeywordList.add(keyword);
        int historyKeywordCount = mSearchKeywordList.size();
        for(int i = MAX_HISTORY_KEYWORD_COUNT; i < historyKeywordCount; i ++){
            mSearchKeywordList.remove(MAX_HISTORY_KEYWORD_COUNT);
        }

        mHistorySearchKeywordAdapter.notifyDataSetChanged();
        this.mKeyword = keyword;
//		loadData(true);
        mHistorySearchKeyword.setVisibility(View.GONE);
        getListView().setVisibility(View.VISIBLE);
        getListView().setRefreshing(true);
    }

    @Override
    public String getUrl() {
        return getResources().getString(R.string.search_url);
    }


    @Override
    public void onStop() {
        super.onStop();
        save();
    }

    private void load() {
        SharedPreferences sp = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        mSearchKeywordList = new Gson().fromJson(sp.getString(KEY_HISTORY_SEARCH_KEYWROD, "[]"), new TypeToken<ArrayList<String>>(){}.getType());
    }

    private void save(){
        SharedPreferences sp = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_HISTORY_SEARCH_KEYWROD, new Gson().toJson(mSearchKeywordList));
        editor.commit();
    }
}
