package com.ccpress.izijia.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.entity.EditGroupListEntity;
import com.ccpress.izijia.entity.FriendslistEntity;
import com.ccpress.izijia.util.HttpUtils;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.view.MyGridView;
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
 * Created by YL on 2015/5/12.
 * 分组管理页面
 */
public class GroupManagerActivity extends TRSFragmentActivity {
    private TextView groupname, groupnum, name_friends;
    private ImageView img_friends, delete_friendsimg;
    private RelativeLayout rl_friendgroup;

    private RelativeLayout loading_view;
    private MyAdapter mGridAdapter;
    public static final String GID = "GID";
    private MyGridView gv_friends;
    public static  boolean checkboxFlag=false;
    public static final int REQUSET = 1;
    private int gid;
    private int gIsNewAlert;

    private ArrayList<FriendslistEntity> friedslist = new ArrayList<FriendslistEntity>();
    private String URL = "raw://user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmanager);
        //初始化Loading View
        loading_view = (RelativeLayout) this.findViewById(R.id.loading_view);
        Bundle bundle = getIntent().getExtras();
        gid = bundle.getInt(EditGroupActivity.GID);
        gIsNewAlert =bundle.getInt(EditGroupActivity.GFLAG);
//        loadData(gid);
        initView();
        initgvlist();
    }


    /**
     * 获取成员列表
     */
    private void loadData(final int gid) {

        final String url = Const.GET_USER;
        SpUtil sp = new SpUtil(this);
        final String token = sp.getStringValue(Const.AUTH);
        final String uid = sp.getStringValue(Const.UID);

        friedslist.clear();
        LoadWCMJsonTask task = new LoadWCMJsonTask(this) {
            @Override
            public void onDataReceived(String result, boolean isCache) throws Exception {
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt("code");
                    String message = json.getString("message");
                    JSONArray array = json.getJSONArray("datas");

                    if (code == 0) {
                        friedslist.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            FriendslistEntity entity = new FriendslistEntity();
                            entity.setName(object.getString("mname"));
                            entity.setMid(object.getInt("mid"));
                            entity.setImgurl(object.getString("mimage"));
                            friedslist.add(entity);
                        }
                        mGridAdapter.notifyDataSetChanged();
                        groupnum.setText(friedslist.size() + "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!(t instanceof NullPointerException)){
                    Toast.makeText(GroupManagerActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
        String u =PersonalCenterUtils.buildUrlMy(this,(url + "&gid=" + gid) );
        task.start(u);
    }


    private void initgvlist() {
        gv_friends = (MyGridView) this.findViewById(R.id.gv_friends);
        mGridAdapter = new MyAdapter();
        gv_friends.setAdapter(mGridAdapter);
        gv_friends.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {
            @Override
            public boolean onTouchInvalidPosition(int motionEvent) {
                mGridAdapter.setIsShowDelete(false);
                return true;
            }
        });
    }

    public class MyAdapter extends BaseAdapter {

        private boolean isShowDelete;//根据这个变量来判断是否显示删除图标，true是显示，false是不显示

        @Override
        public int getCount() {
            return friedslist.size() + 2;
        }

        @Override
        public FriendslistEntity getItem(int position) {
            return friedslist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setIsShowDelete(boolean isShowDelete) {
            this.isShowDelete = isShowDelete;
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(GroupManagerActivity.this, R.layout.item_gv, null);
            }
            name_friends = (TextView) convertView.findViewById(R.id.name_friends);
            img_friends = (ImageView) convertView.findViewById(R.id.img_friends);
            final Bundle bundle = getIntent().getExtras();
            delete_friendsimg = (ImageView) convertView.findViewById(R.id.delete_friendsimg);
            if (position > 1) {
                name_friends.setText(friedslist.get(position - 2).getName());
                delete_friendsimg.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);//设置删除按钮是否显示
                new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).build(friedslist.get(position - 2).getImgurl(), img_friends).start();
                img_friends.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (isShowDelete) {
                            isShowDelete = false;
                        } else {
                            isShowDelete = true;
                        }
                        mGridAdapter.setIsShowDelete(isShowDelete);
                        return false;
                    }
                });
                delete_friendsimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doDelUserPost(bundle.getInt(EditGroupActivity.GID), friedslist.get(position - 2).getMid(), position - 2);
                        groupnum.setText(friedslist.size() + "");
                    }
                });
            }
            if (position == 0) {
                name_friends.setText("添加成员");
                img_friends.setImageResource(R.drawable.add_friends);
                RelativeLayout.LayoutParams addfriendsimg = new RelativeLayout.LayoutParams((int) getResources().getDimension(com.trs.mobile.R.dimen.addfriendsimg),
                        (int) getResources().getDimension(com.trs.mobile.R.dimen.addfriendsimg));
                addfriendsimg.setMargins(11, 11, 11, 11);
                img_friends.setLayoutParams(addfriendsimg);
                delete_friendsimg.setVisibility(View.GONE);
                img_friends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupManagerActivity.this, AddFriendsActivity.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt(GID, bundle.getInt(EditGroupActivity.GID));
                        intent.putExtras(bundle1);
                        GroupManagerActivity.this.startActivityForResult(intent, REQUSET);
                    }
                });
            }
            if (position == 1) {
                name_friends.setText("移除成员");
                img_friends.setImageResource(R.drawable.delete_friends);
                RelativeLayout.LayoutParams addfriendsimg = new RelativeLayout.LayoutParams((int) getResources().getDimension(com.trs.mobile.R.dimen.addfriendsimg),
                        (int) getResources().getDimension(com.trs.mobile.R.dimen.addfriendsimg));
                addfriendsimg.setMargins(11, 11, 11, 11);
                img_friends.setLayoutParams(addfriendsimg);
                delete_friendsimg.setVisibility(View.GONE);
                img_friends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isShowDelete = true;
                        mGridAdapter.setIsShowDelete(isShowDelete);
                    }
                });

            }

            return convertView;
        }
    }


    private void initView() {
        groupname = (TextView) this.findViewById(R.id.group_name);
        groupnum = (TextView) this.findViewById(R.id.group_num);
        final Bundle bundle = getIntent().getExtras();
        groupname.setText(bundle.getString(EditGroupActivity.GROUPNAME));
        groupnum.setText(bundle.getInt(EditGroupActivity.GROUPNUM) + "");
        rl_friendgroup = (RelativeLayout) this.findViewById(R.id.rl_friendgroup);
        rl_friendgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(GroupManagerActivity.this);
                RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.dialog_newgroup, null);

                final Dialog dialog = new Dialog(GroupManagerActivity.this);
                dialog.setCancelable(false);

                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setContentView(layout);

                TextView dialog_msg = (TextView) layout.findViewById(R.id.dialog_msg);
                dialog_msg.setText("编辑分组名称");
                TextView dialog_ok = (TextView) layout.findViewById(R.id.dialog_ok);
                final EditText edit_group = (EditText) layout.findViewById(R.id.add_new_group);
                ImageView delete_edit = (ImageView) layout.findViewById(R.id.delete_edit);
                delete_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit_group.setText("");
                    }
                });
                dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String editGroup = edit_group.getText().toString().trim();
                        if (editGroup.length() != 0) {
                            doEditGroupPost(editGroup, bundle.getInt(EditGroupActivity.GID));
                            dialog.dismiss();
                        } else {
                            Toast.makeText(GroupManagerActivity.this, "编辑分组名称不能为空", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                TextView dialog_cancel = (TextView) layout.findViewById(R.id.dialog_cancel);
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


            }
        });
    }

    //新消息提醒状态更新
    private void doEditSwtichGroupMessage(final int gIsNewAlert, final int gid){
        final String url = Const.SWITCH_GROUP_MESSAGE;
        SpUtil sp = new SpUtil(this);
        final String token = sp.getStringValue(Const.AUTH);
        final int uid = Integer.parseInt(sp.getStringValue(Const.UID));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String state = HttpUtils.editSwtichGroupMessage(url, gid, Uri.encode(token), uid);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        try {
                            JSONObject json = new JSONObject(state);
                            Log.e("GroupUrl", state);
                            int code = json.getInt("code");
                            String message = json.getString("message");
                            EditGroupListEntity entity=new EditGroupListEntity();
                            loading_view.setVisibility(View.GONE);
                            if (code == 0) {
                                entity.setGflag(gIsNewAlert);
                            }
                            Toast.makeText(GroupManagerActivity.this, message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
    //编辑分组
    private void doEditGroupPost(final String gname, final int gid) {
        final String url = Const.EDIT_GROUP;
        SpUtil sp = new SpUtil(this);
        final String token = sp.getStringValue(Const.AUTH);
        final int uid = Integer.parseInt(sp.getStringValue(Const.UID));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String state = HttpUtils.editGroup(url, Uri.encode(gname), gid, Uri.encode(token), uid);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        try {
                            JSONObject json = new JSONObject(state);
                            Log.e("delGroupUrl", state);
                            int code = json.getInt("code");
                            String message = json.getString("message");
                            loading_view.setVisibility(View.GONE);
                            if (code == 0) {
                                groupname.setText(gname);
                            }
                            Toast.makeText(GroupManagerActivity.this, message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        handler.sendEmptyMessage(1);
                    }
                });
            }
        }).start();
    }

    //删除成员
    private void doDelUserPost(final int gid, final int mid, final int position) {
        final String url = Const.DEL_USER;
        SpUtil sp = new SpUtil(this);
        final String token = sp.getStringValue(Const.AUTH);
        final int uid = Integer.parseInt(sp.getStringValue(Const.UID));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String state = HttpUtils.delUser(url, gid, mid, Uri.encode(token), uid);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        try {
                            JSONObject json = new JSONObject(state);
                            Log.e("doDelUserPosturl", state);
                            int code = json.getInt("code");
                            String message = json.getString("message");
                            if (code == 0) {
                                friedslist.remove(position);
                                groupnum.setText(friedslist.size() + "");
                                mGridAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(GroupManagerActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        handler.sendEmptyMessage(1);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("YLYLYL", resultCode + "");
        if (resultCode == AddFriendsActivity.RESULT_CODE) {
//            FriendslistEntity entity = new FriendslistEntity();
//            entity.setName(data.getStringExtra(AddFriendsActivity.FRIENDSNAME));
//            entity.setImgurl(data.getStringExtra(AddFriendsActivity.IMGURL));
////                entity.setMid(Integer.parseInt(data.getStringExtra(AddFriendsActivity.MID)));
//            friedslist.add(entity);
//            groupnum.setText(friedslist.size() + "");
//            mGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(gid);
    }
}
