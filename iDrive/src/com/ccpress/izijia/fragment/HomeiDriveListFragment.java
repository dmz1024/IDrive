package com.ccpress.izijia.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.ChoosenDetailActivity;
import com.ccpress.izijia.activity.LinesDetailUserUploadActivity;
import com.ccpress.izijia.activity.WebViewActivity;
import com.trs.adapter.AbsListAdapter;
import com.trs.fragment.DocumentListFragment;
import com.trs.types.ListItem;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;

/**
 * Created by hexulan
 * Date: 2015/5/6
 * Time: 11:50
 * 首页-爱自驾的Fragment
 */
public class HomeiDriveListFragment extends DocumentListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    protected String getRequestUrl(int requestIndex) {
        return getUrl() + String.valueOf(requestIndex);
    }

    @Override
    protected void onItemClick(ListItem item) {
        if(getActivity() != null && !StringUtil.isEmpty(item.getType())){

            WebViewActivity.web_title=item.getTitle();
            WebViewActivity.id=item.getId();

            if(item.getType().equals(Constant.CType_Des)){//目的地
                WebViewActivity.image= item.getImgUrl();
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                WebViewActivity.TITLE_NAME="目的地";
                WebViewActivity.type="17";
                WebViewActivity.FLAG="目的地";
                WebViewActivity.web_url=item.getUrl();
                intent.putExtra("url", item.getUrl());
                startActivity(intent);
            }else if(item.getType().equals(Constant.CType_SelfDrive) ){//自驾团
                WebViewActivity.image= item.getImgUrl();
                Intent intent = new Intent(getActivity(), ChoosenDetailActivity.class);
                intent.putExtra(ChoosenDetailActivity.EXTRA_TYPE, 1);
                 WebViewActivity.TITLE_NAME="自驾团";
                WebViewActivity.FLAG="自驾团";
                ChoosenDetailActivity.web_image=item.getImgUrl();
                ChoosenDetailActivity.web_tile=item.getTitle();
                ChoosenDetailActivity.web_url=item.getUrl();
                ChoosenDetailActivity.web_id=item.getId();
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getId());
                getActivity().startActivity(intent);

            } else if(item.getType().equals(Constant.CType_Line)){//线路
                WebViewActivity.image= item.getImgUrl();
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                WebViewActivity.type="17";
                WebViewActivity.web_url=item.getUrl();
                WebViewActivity.TITLE_NAME="线路";
                WebViewActivity.FLAG="线路";
                intent.putExtra("url", item.getUrl());
                startActivity(intent);
            }
        }
    }

    @Override
    protected int getViewID() {
        return R.layout.fragment_home_list_idrive;
    }

    @Override
    protected AbsListAdapter createAdapter() {
        return new IDriveListAdapter(getActivity());
    }

    private class IDriveListAdapter extends AbsListAdapter {
        public IDriveListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getViewID() {
            return R.layout.list_item_idrive;
        }

        @Override
        public void updateView(View view, int position, View convertView, ViewGroup parent) {
            ListItem item = getItem(position);

            TextView title = (TextView) view.findViewById(R.id.title);
            ImageView idrive_list_img = (ImageView) view.findViewById(R.id.idrive_list_img);

            title.setText(item.getTitle());

            new ImageDownloader.Builder().
                    setOptionsType(ImageDownloader.OptionsType.TOP_PIC).
                    build(item.getImgUrl(), idrive_list_img).start();
        }
    }
}
