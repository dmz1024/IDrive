package com.ccpress.izijia.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.entity.FriendslistEntity;
import com.ccpress.izijia.util.HttpUtils;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.ImageDownloader;
import com.trs.util.log.Log;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by YL on 2015/5/13.
 * 添加分组页面
 */
public class AddFriendsActivity extends TRSFragmentActivity {
    public static final String IMGURL = "IMGURL";
    public static final String FRIENDSNAME = "FRIENDSNAME";
    public static final String MID = "MID";
    private EditText mSearchword;
    private ListView mFriendsListView;
    private ImageView mFriendsSearch;
    public static final int RESULT_CODE =101;
    private FriendsListAdapter mFriendsListAdapter;
    private RelativeLayout loading_view;
    private ArrayList<FriendslistEntity> mFriendsList= new ArrayList<FriendslistEntity>();
    private String URL = "raw://searchlist";
    private String imgurl,name;
    private int mid;
    private String searchkeyword;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Intent intent=new Intent(AddFriendsActivity.this,GroupManagerActivity.class);
                intent.putExtra(IMGURL,imgurl);
                intent.putExtra(FRIENDSNAME,name);
                intent.putExtra(MID,mid);
                setResult(RESULT_CODE, intent);
                finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);

        initView();
        initList();
    }

    private void initView() {
        loading_view = (RelativeLayout) this.findViewById(R.id.loading_view);
        mSearchword= (EditText) this.findViewById(R.id.search_keyword);
        mFriendsSearch= (ImageView) this.findViewById(R.id.friends_search);

    }
    private void initList() {
        mFriendsListView= (ListView) this.findViewById(R.id.friends_list);
        mFriendsListAdapter=new FriendsListAdapter();
        mFriendsListView.setAdapter(mFriendsListAdapter);
        mFriendsSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 searchkeyword=mSearchword.getText().toString();
                if(searchkeyword.length()==0){
                    Toast.makeText(AddFriendsActivity.this,"搜索内容不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    loadData(searchkeyword);
                }
                hideSystemKeyBoard(AddFriendsActivity.this,mFriendsListView);
                mFriendsListAdapter.notifyDataSetChanged();
            }
        });
    }



    public static void hideSystemKeyBoard(Context mcontext,View v) {
        InputMethodManager imm = (InputMethodManager) ((AddFriendsActivity) mcontext)
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * 通过关键字，向服务器请求数据
     * @param searchkeyword
     */
    private void loadData(String searchkeyword) {
        String url= PersonalCenterUtils.buildUrlMy(this,Const.SEARCH_USER+"&keyword="
                +Uri.encode(searchkeyword));
        LoadWCMJsonTask task = new LoadWCMJsonTask(this) {
            @Override
            public void onDataReceived(String result, boolean isCache) throws Exception {
                loading_view.setVisibility(View.GONE);
                mFriendsList.clear();
                JSONObject jsonObject=new JSONObject(result);
                JSONArray array = jsonObject.getJSONArray("datas");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    FriendslistEntity entity=new FriendslistEntity();
                    entity.setName(object.getString("mname"));
                    entity.setMid(object.getInt("mid"));
                    entity.setImgurl(object.getString("mimage"));
                    mFriendsList.add(entity);
                    mFriendsListAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onError(Throwable t) {
                loading_view.setVisibility(View.GONE);
            }
        };
        task.start(url);
    }

    /**
     * 好友list的Adapter
     */
    private class FriendsListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mFriendsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFriendsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView =View.inflate(AddFriendsActivity.this,R.layout.list_item_addfriends,null);
            }
            TextView mFriendsName= (TextView) convertView.findViewById(R.id.friends_name);
            imgurl=mFriendsList.get(position).getImgurl();
            name=mFriendsList.get(position).getName();
            mid=mFriendsList.get(position).getMid();
            mFriendsName.setText(mFriendsList.get(position).getName());
            Log.e("mid",mFriendsList.get(position).getMid()+"");
            Button btn_addFriends= (Button) convertView.findViewById(R.id.btn_addfriends);
            final Bundle bundle=getIntent().getExtras();
            btn_addFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doAddUserPost(bundle.getInt(GroupManagerActivity.GID),mFriendsList.get(position).getMid(),position);
                }
            });
            ImageView mFriendsImg= (ImageView) convertView.findViewById(R.id.img_friends);
            new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).build(mFriendsList.get(position).getImgurl(), mFriendsImg).start();
            return convertView;
        }
    }


    //添加成员
    private void doAddUserPost(final int gid,final int mid, final int position) {
        final String url = Const.ADD_USER;
        SpUtil sp = new SpUtil(this);
        final String token = sp.getStringValue(Const.AUTH);
        final int uid = Integer.parseInt(sp.getStringValue(Const.UID));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String state = HttpUtils.addUser(url, gid, mid, Uri.encode(token), uid);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        try {
                            JSONObject json = new JSONObject(state);
                            Log.e("addUser", state);
                            int code = json.getInt("code");
                            String message = json.getString("message");
                            loading_view.setVisibility(View.GONE);
                            if (code == 0) {
                                Toast.makeText(AddFriendsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(1);
                    }
                });
            }
        }).start();
    }
}
