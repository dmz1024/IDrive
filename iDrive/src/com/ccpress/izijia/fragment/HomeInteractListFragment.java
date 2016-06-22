package com.ccpress.izijia.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.*;
import com.ccpress.izijia.activity.mystyle.InfoActivity;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.dfy.activity.DetailsActivity;
import com.ccpress.izijia.util.HttpUtils;
import com.ccpress.izijia.util.ScreenUtil;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.trs.adapter.AbsListAdapter;
import com.trs.fragment.DocumentListFragment;
import com.trs.types.ListItem;
import com.trs.types.UserInfoEntity;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.widget.WebView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/6
 * Time: 11:51
 */
public class HomeInteractListFragment extends DocumentListFragment {
    private   SpUtil sp;
    private  View v;
    private HomeInteractListBroadcastReceiver mReceiver  = new HomeInteractListBroadcastReceiver();;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         v = super.onCreateView(inflater, container, savedInstanceState);
        initBroadcastReceiver();
      sp = new SpUtil(getActivity());
        return v;
    }

    /**
     * 发送更新数据的广播
     */
    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.INTERACT_LIST_UPDATE);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected String getRequestUrl(int requestIndex) {
        v.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
        return getUrl() + String.valueOf(requestIndex);
    }

    /**
     * 分享点击事件
     * @param item
     *Created by Wu Hexulan
     */
    @Override
    protected void onItemClick(ListItem item) {

        if(item != null && !StringUtil.isEmpty(item.getType())){
            if(item.getType().equals(Constant.DETAIL_TYPE_LINE_UPLOAD)){//自建线路
                Intent intent = new Intent(getActivity(), LinesDetailUserUploadActivity.class);
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getId());
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_MY_TYPE,item.getType());
                startActivity(intent);
            }else if (item.getType().equals(Constant.DETAIL_TYPE_IMAGES) || item.getType().equals(Constant.DETAIL_TYPE_VIDEO)){//图片组-11   小视频-14
                Intent intent = new Intent(getActivity(), InfoDetailActivity.class);
                intent.putExtra(InfoDetailActivity.EXTRA_DOCID, item.getId());
                intent.putExtra(InfoDetailActivity.EXTRA_URL, item.getUrl());
                intent.putExtra(InfoDetailActivity.EXTRA_DETAIL_TYPE, item.getType());
                startActivity(intent);
            }else if (item.getType().equals(Constant.HOME_TYPE_DRIVE)){//首页目的地、线路、精选文章
                WebViewActivity.TITLE_NAME=item.getTitle();
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", item.getUrl());
                startActivity(intent);
            } else if(item.getType().equals(Constant.DETAIL_TYPE_LINE)){//常规线路
                Intent intent = new Intent(getActivity(), LinesDetailUserUploadActivity.class);
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getId());
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, LinesDetailUserUploadActivity.OfficialLines);
                startActivity(intent);
            }else if(item.getType().equals(Constant.DETAIL_TYPE_DES)){//目的地
                    Intent intent = new Intent(getActivity(), LinesDetailUserUploadActivity.class);
                    intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, LinesDetailUserUploadActivity.Destination);
                    intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getId());
                    getActivity().startActivity(intent);
            }else if(item.getType().equals(Constant.DETAIL_TYPE_ViewSpot)){//看点
                Intent intent = new Intent(getActivity(), ViewSpotDetailActivity.class);
                intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, item.getId());
                getActivity().startActivity(intent);
            }else if(item.getType().equals(Constant.DETAIL_TYPE_PARK)){//停车发呆地

                Intent intent = new Intent(getActivity(), ViewSpotDetailActivity.class);
                intent.putExtra(ViewSpotDetailActivity.EXTRA_TYPE, 1);
                intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, item.getId());
                getActivity().startActivity(intent);
            }else if(item.getType().equals(Constant.DETAIL_TYPE_DRIVE)){//手机自驾团
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("id", item.getId());
                getActivity().startActivity(intent);
            }

        }
    }

    @Override
    protected int getViewID() {
        return R.layout.fragment_home_list_interact;
    }

    /**
     * 创建父Adapter
     * @return
     */
    @Override
    protected AbsListAdapter createAdapter() {
        return new InteractListAdapter(getActivity());
    }

    /**
     * 创建InteractListAdapter
     */
    private class InteractListAdapter extends AbsListAdapter {
        public InteractListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getViewID() {

            return R.layout.list_item_interact;
        }
        private ImageView list_delete;

        /**
         * 填充互动ListView布局文件
         * @param view
         * @param position
         * @param convertView
         * @param parent
         */
        @Override
        public void updateView(View view, final int position, View convertView, ViewGroup parent) {
            v.findViewById(R.id.no_data).setVisibility(View.GONE);
            final ListItem item = getItem(position);
            UserInfoEntity user = item.getUser();

            ImageView icon_portrait = (ImageView) view.findViewById(R.id.icon_portrait);
            icon_portrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InfoActivity.TUID=String.valueOf(item.getUser().getUid());
                    Intent intent = new Intent(getActivity(), InfoActivity.class);
                    intent.putExtra(InfoActivity.TUID,String.valueOf(item.getUser().getUid()));
                    getActivity().startActivity(intent);
                }
            });

             list_delete = (ImageView) view.findViewById(R.id.list_delete);
            if (String.valueOf(item.getUser().getUid()).equals(sp.getStringValue(Const.UID))){
                list_delete.setVisibility(convertView.VISIBLE);
            }else {
                list_delete.setVisibility(convertView.GONE);
            }
            list_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
                    RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.dialog_delete, null);

                    final Dialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setCancelable(false);
                    dialog.show();
                    dialog.getWindow().setContentView(layout);

                    TextView dialog_msg = (TextView) layout.findViewById(R.id.dialog_msg);
                    dialog_msg.setText("是否删除该帖子？");
                    TextView dialog_ok= (TextView) layout.findViewById(R.id.dialog_ok);
                    dialog_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doDeleletInteract(Integer.parseInt(item.getsetHuDongIdId()),position);
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

            TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
            ImageView icon_gender = (ImageView) view.findViewById(R.id.icon_gender);

            TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
            TextView txt_perfect = (TextView) view.findViewById(R.id.txt_perfect);
            TextView txt_comment = (TextView) view.findViewById(R.id.txt_comment);
            TextView txt_forward = (TextView) view.findViewById(R.id.txt_forward);
            TextView txt_date = (TextView) view.findViewById(R.id.txt_date);

            new ImageDownloader.Builder().
                    setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                    build(user.getAvatar(), icon_portrait).start();

            txt_name.setText(user.getName());
            if(user.getSex() == 1){
                icon_gender.setImageResource(R.drawable.icon_gender_male);
            } else {
                icon_gender.setImageResource(R.drawable.icon_gender_female);
            }

           if(item.getType().equals("13")){
               txt_title.setVisibility(View.GONE);
            }else {
               txt_title.setVisibility(View.VISIBLE);
               txt_title.setText(item.getSummary());
           }

            txt_perfect.setText(item.getLike());
            txt_comment.setText(item.getComment());
            txt_forward.setText(item.getShare());
            txt_date.setText(item.getDate());

            if(item.getType().equals("11") || item.getType().equals("14")){
                //图片Grid形式
                RelativeLayout summary_layout = (RelativeLayout) view.findViewById(R.id.summary_layout);
                summary_layout.setVisibility(View.GONE);
                RelativeLayout image_layout = (RelativeLayout) view.findViewById(R.id.image_layout);
                image_layout.setVisibility(View.VISIBLE);
                RelativeLayout bottom_layout = (RelativeLayout) view.findViewById(R.id.bottom_layout);
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) bottom_layout.getLayoutParams();
                rlp.addRule(RelativeLayout.BELOW, R.id.image_layout);
                RelativeLayout rl = (RelativeLayout) view;
                RelativeLayout pa = (RelativeLayout) bottom_layout.getParent();
                if(pa!=null){
                    pa.removeView(bottom_layout);
                }
                rl.addView(bottom_layout, rlp);

                createImageListView(item, image_layout, item.getType());
            } else {
                //图文形式
                RelativeLayout summary_layout = (RelativeLayout) view.findViewById(R.id.summary_layout);
                summary_layout.setVisibility(View.VISIBLE);
                RelativeLayout image_layout = (RelativeLayout) view.findViewById(R.id.image_layout);
                image_layout.setVisibility(View.GONE);
                RelativeLayout bottom_layout = (RelativeLayout) view.findViewById(R.id.bottom_layout);
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) bottom_layout.getLayoutParams();
                rlp.addRule(RelativeLayout.BELOW, R.id.summary_layout);
                RelativeLayout rl = (RelativeLayout) view;
                RelativeLayout pa = (RelativeLayout) bottom_layout.getParent();
                if(pa!=null){
                    pa.removeView(bottom_layout);
                }
                rl.addView(bottom_layout, rlp);

                ImageView img_summary = (ImageView) view.findViewById(R.id.img_summary);
                img_summary.setScaleType(ImageView.ScaleType.CENTER_CROP);
                new ImageDownloader.Builder().
                        setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                        build(item.getImgUrl(), img_summary).start();
                TextView txt_summary_title = (TextView) view.findViewById(R.id.txt_summary_title);
                if(item.getType().equals("1")|| item.getType().equals("13") || item.getType().equals("2")||item.getType().equals("17")|| item.getType().equals("16")) {
                    txt_summary_title.setText(item.getTitle());
                } else {
                   txt_summary_title.setText(item.getSummary());
                }
            }
        }


        /**
         * 删除我的帖子
         *
         * @param id
         * @param position
         *
         * Created by Wu hexulan
         */
    private void doDeleletInteract(final int id,final int position ){
        final String url= Const.DELETE_INTERACTION;

        final String token=sp.getStringValue(Const.AUTH);
        final int uid= Integer.parseInt(sp.getStringValue(Const.UID));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String state= HttpUtils.delInteraction(url, id, Uri.encode(token),uid);
                //执行在主线程上
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        try {
                            JSONObject json = new JSONObject(state);
                            int code=json.getInt("code");
                            String message=json.getString("message");
                            if(code==0){
                                loadData(true);
                                InteractListAdapter.this.notifyDataSetChanged();
                                if (String.valueOf(getItem(position).getUser().getUid()).equals(sp.getStringValue(Const.UID))){
                                    list_delete.setVisibility(View.VISIBLE);
                                }else {
                                    list_delete.setVisibility(View.GONE);
                                }

                            }
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

        /**
         * 创建图片组的listView
         * @param item
         * @param view
         * @param type
         */
        private void createImageListView(ListItem item, RelativeLayout view, String type){
            view.removeAllViews();
            ArrayList<ListItem.GridImage> list = item.getImageList();
            if(list ==null || list.size()==0){
                return;
            }

            int imageCount = list.size();
            int rows;
            if (imageCount % 3 == 0) {
                rows = imageCount / 3;
            } else {
                rows = imageCount / 3 + 1;
            }

            for (int r = 0; r < rows; r++) {
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                View v = getRow(r, list, type);
                v.setId(r + 1);

                if (r == 0) {
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                } else {
                    rlp.addRule(RelativeLayout.BELOW, r);
                    rlp.addRule(RelativeLayout.ALIGN_LEFT, r);
                }
                rlp.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.size5);

                view.addView(v, rlp);
            }
        }

        /**
         * 获取List的布局区域
         * @param row
         * @param list
         * @param type
         * @return
         */
        private View getRow(int row, final ArrayList<ListItem.GridImage> list, String type) {
            LinearLayout ll = new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = row * 3; i < (row + 1) * 3; i++) {
                if (i >= list.size()) {
                    break;
                }

                int screen_width = ScreenUtil.getScreenWidth(getActivity());
                int image_space = screen_width
                        - getResources().getDimensionPixelOffset(R.dimen.size70)  //左边留出的空白
                        - getResources().getDimensionPixelOffset(R.dimen.size5)*2 //两个间隔距离
                        - getResources().getDimensionPixelOffset(R.dimen.size8);  //右边距
                int width = image_space / 3;

                RelativeLayout thumbnail = (RelativeLayout) LayoutInflater.from(getActivity())
                        .inflate(R.layout.grid_item_interact, null);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(width, width);
                int mod = i % 3;
                if(mod == 1 || mod == 2){
                    llp.leftMargin = getResources().getDimensionPixelOffset(R.dimen.size5);
                }
                ll.addView(thumbnail, llp);

                ImageView image = (ImageView) thumbnail.findViewById(R.id.image);
                image.setMaxWidth(width);
                image.setMaxHeight(width);
                new ImageDownloader.Builder().
                        setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                        build(list.get(i).getPic(), image).start();

                if((list.get(i).getGeo()==null || list.get(i).getGeo().equals(""))
                        &&(list.get(i).getAddr()==null || list.get(i).getAddr().equals(""))) {
                    RelativeLayout bg_location = (RelativeLayout) thumbnail.findViewById(R.id.bg_location);
                    bg_location.setVisibility(View.INVISIBLE);
                } else {
                    TextView txt_location =(TextView) thumbnail.findViewById(R.id.txt_location);
                    txt_location.setText(list.get(i).getAddr());

                    ImageView icon_location = (ImageView) thumbnail.findViewById(R.id.icon_location);
                    if(list.get(i).getGeo()!=null && !list.get(i).getGeo().equals("")){
                        icon_location.setVisibility(View.VISIBLE);
                    } else {
                        icon_location.setVisibility(View.INVISIBLE);
                    }
                }

                if(type.equals("14")){
                    ImageView icon_video_play = (ImageView) thumbnail.findViewById(R.id.icon_video_play);
                    icon_video_play.setVisibility(View.VISIBLE);
                }
            }
            return ll;
        }
    }

    private class HomeInteractListBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.INTERACT_LIST_UPDATE)) {
                loadData(true);
            }
        }
    }
}
