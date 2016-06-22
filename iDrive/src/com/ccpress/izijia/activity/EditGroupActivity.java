package com.ccpress.izijia.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.entity.EditGroupListEntity;
import com.ccpress.izijia.entity.TopPopupListEntity;
import com.ccpress.izijia.fragment.HomeFragment;
import com.ccpress.izijia.util.HttpUtils;
import com.daimajia.swipe.SwipeLayout;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.constants.Constants;
import com.trs.types.ListItem;
import com.trs.util.log.Log;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by YL on 2015/5/11.
 * 编辑分组页面
 */
public class EditGroupActivity extends TRSFragmentActivity {
    public static final String GROUPNAME = "GROUPNAME";
    public static final String GROUPNUM = "GROUPNUM";
    public static final String GID = "GID";
    public static final String GFLAG="GFLAG";

    private ListView editGroupListView;
    private SwipeLayout mSwipeLayout;
    private EditgrouplistAdapter editGroupListAdapter;
    private RelativeLayout loading_view;
    private TextView new_group;
    private String URL = "raw://edit_group";
    private ArrayList<EditGroupListEntity> editGroupList = new ArrayList<EditGroupListEntity>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        SpUtil sp = new SpUtil(this);
        //初始化Loading View
        loading_view = (RelativeLayout) this.findViewById(R.id.loading_view);
        initList();
        initView();
    }

    private void initView() {
        new_group= (TextView) this.findViewById(R.id.new_group);
        //新建分组
        new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater=LayoutInflater.from(EditGroupActivity.this);
                RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.dialog_newgroup, null);
                final Dialog dialog = new Dialog(EditGroupActivity.this);
                dialog.setCancelable(false);
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setContentView(layout);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                final EditText add_new_group= (EditText) layout.findViewById(R.id.add_new_group);
                ImageView delete_edit= (ImageView) layout.findViewById(R.id.delete_edit);
                delete_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add_new_group.setText("");
                    }
                });
                TextView dialog_ok= (TextView) layout.findViewById(R.id.dialog_ok);
                dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String addNewgroup=add_new_group.getText().toString().trim();
                        if(addNewgroup.length()!=0){

                            doNewGroupPost(addNewgroup);
                            dialog.dismiss();
                        }else {

                            Toast.makeText(EditGroupActivity.this,"分组名称不能为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                TextView dialog_cancel= (TextView) layout.findViewById(R.id.dialog_cancel);
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });
    }
    //删除分组
    private void doDelGroupPost(final int gid,final int position){
        final String url=Const.DEL_GROUP;
        SpUtil sp = new SpUtil(this);
        final String token=sp.getStringValue(Const.AUTH);
        final int uid= Integer.parseInt(sp.getStringValue(Const.UID));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String state= HttpUtils.delGroup(url, gid,Uri.encode(token),uid);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        try {
                            JSONObject json = new JSONObject(state);
                            Log.e("delGroupUrl",state);
                            int code=json.getInt("code");
                            String message=json.getString("message");
                            loading_view.setVisibility(View.GONE);
                            if(code==0){
                                editGroupList.remove(position);
                                editGroupListAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(EditGroupActivity.this, message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }).start();
    }

    /**
     * 新建分组数据的提交
     * @param gname
     */
    private void doNewGroupPost(final String gname){
        final String url=Const.NEW_GROUP;
        SpUtil sp = new SpUtil(this);
        final String token=sp.getStringValue(Const.AUTH);
        final int uid= Integer.parseInt(sp.getStringValue(Const.UID));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String state= HttpUtils.newGroup(url, Uri.encode(gname),Uri.encode(token),uid);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        try {
                            JSONObject json = new JSONObject(state);
                            Log.e("addNewGroupUrl",state);
                            int code=json.getInt("code");
                            String message=json.getString("message");
                            EditGroupListEntity entity=new EditGroupListEntity();
                            loading_view.setVisibility(View.GONE);
                            if(code==0){
                                entity.setName(gname);
                                entity.setNum(0);
                                editGroupList.add(entity);
                                editGroupListAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(EditGroupActivity.this, message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }


    /**获取分组列表*/
    private void lodaData() {
        loading_view.setVisibility(View.VISIBLE);
        SpUtil sp = new SpUtil(this);
        String url= Const.MY_GROUP+"&token="
                + Uri.encode(sp.getStringValue(Const.AUTH))+"&uid="+
                        sp.getStringValue(Const.UID);
        Log.e("YLYLlodaData+url",url);
        LoadWCMJsonTask task = new LoadWCMJsonTask(this) {
            @Override
            public void onDataReceived(String result, boolean isCache) throws Exception {
                Log.e("YLYL",result);
                JSONObject jsonObject=new JSONObject(result);
                editGroupList.clear();
                JSONArray array = jsonObject.getJSONArray("datas");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    EditGroupListEntity entity = new EditGroupListEntity();
                    entity.setName(object.getString("gname"));
                    entity.setGid(object.getInt("gid"));
                    entity.setGflag(object.getInt("gIsNewAlert"));
                    entity.setNum(object.getInt("friends_count"));
                    editGroupList.add(entity);
                }
                loading_view.setVisibility(View.GONE);
                editGroupListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(Throwable t) {
                loading_view.setVisibility(View.GONE);

            }

        };
        task.start(url);
    }

    private void initList() {
        editGroupListView= (ListView) this.findViewById(R.id.edit_group_list);
        editGroupListAdapter = new EditgrouplistAdapter();
        editGroupListView.setAdapter(editGroupListAdapter);
        editGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(EditGroupActivity.this,GroupManagerActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString(GROUPNAME,editGroupList.get(position).getName());
                bundle.putInt(GROUPNUM,editGroupList.get(position).getNum());
                bundle.putInt(GID,editGroupList.get(position).getGid());
                bundle.putInt(GFLAG,editGroupList.get(position).getGflag());
                 Log.e("groupname",editGroupList.get(position).getName()+editGroupList.get(position).getNum()+
                         editGroupList.get(position).getGflag());
                intent.putExtras(bundle);
                EditGroupActivity.this.startActivity(intent);
            }
        });
    }

    private class EditgrouplistAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return editGroupList.size();
        }

        @Override
        public EditGroupListEntity getItem(int i) {
            return editGroupList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView =View.inflate(EditGroupActivity.this,R.layout.list_item_edit_group,null);
            }
            //左滑删除
            SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout swipeLayout) {
                    if(mSwipeLayout != null) {
                        mSwipeLayout.close();
                    }
                    mSwipeLayout = swipeLayout;
                }
                @Override
                public void onOpen(SwipeLayout swipeLayout) {

                }
                @Override
                public void onStartClose(SwipeLayout swipeLayout) {

                }
                @Override
                public void onClose(SwipeLayout swipeLayout) {

                }
                @Override
                public void onUpdate(SwipeLayout swipeLayout, int i, int i2) {

                }

                @Override
                public void onHandRelease(SwipeLayout swipeLayout, float v, float v2) {

                }
            });

            TextView tv_groupname= (TextView) convertView.findViewById(R.id.edit_group_name);
            tv_groupname.setText(editGroupList.get(position).getName());

            TextView tv_groupnum= (TextView) convertView.findViewById(R.id.edit_group_num);
            tv_groupnum.setText(editGroupList.get(position).getNum()+"");

            RelativeLayout deleteitem = (RelativeLayout) convertView.findViewById(R.id.bottom_wrapper);
            //弹出dialog
            deleteitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater=LayoutInflater.from(EditGroupActivity.this);
                    RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.dialog_delete, null);
                    final Dialog dialog = new AlertDialog.Builder(EditGroupActivity.this).create();
                    dialog.setCancelable(false);
                    dialog.show();
                    dialog.getWindow().setContentView(layout);

                    TextView dialog_ok= (TextView) layout.findViewById(R.id.dialog_ok);
                    dialog_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doDelGroupPost(editGroupList.get(position).getGid(),position);
                            dialog.dismiss();
                        }
                    });
                    TextView dialog_cancel= (TextView) layout.findViewById(R.id.dialog_cancel);
                    dialog_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            View separate_line = convertView.findViewById(R.id.separate_line);
            if (position == getCount() - 1) {
                separate_line.setVisibility(View.INVISIBLE);
            } else {
                separate_line.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    /**
     * 返回上个Activity
     * @param view
     */
    @Override
    public void onBtnBackClick(View view) {
//        Intent intent=new Intent();
//        intent.setAction(Constant.EDIT_CHANG_ACTION);
//        sendBroadcast(intent);
        editGroupList.clear();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lodaData();
    }

}
