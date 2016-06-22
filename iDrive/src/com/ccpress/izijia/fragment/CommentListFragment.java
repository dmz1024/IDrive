package com.ccpress.izijia.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.trs.adapter.AbsListAdapter;
import com.trs.fragment.DocumentListFragment;
import com.trs.types.ListItem;
import com.trs.types.Page;
import com.trs.types.UserInfoEntity;
import com.trs.util.ImageDownloader;
import com.trs.util.log.Log;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WLH on 2015/5/13 16:00.
 * 评论列表页面
 */
public class CommentListFragment extends DocumentListFragment{
    @Override
    protected AbsListAdapter createAdapter() {
        return new CommentListAdapter(getActivity());
    }

    @Override
    protected String getRequestUrl(int requestIndex) {
        Log.e("WLH", "CommentListFragment url："+getUrl()+requestIndex);
        return getUrl()+requestIndex;
    }

    @Override
    protected void onItemClick(ListItem item) {
    }

    @Override
    protected int getViewID() {
        return R.layout.fragment_comment;
    }

    @Override
    protected void onDataReceived(Page page) {
        super.onDataReceived(page);

    }

    /**
     * 评论列表页面的Adapter
     */
    private class CommentListAdapter extends AbsListAdapter {
        public CommentListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getViewID() {
            return R.layout.item_comment;
        }

        @Override
        public void updateView(View view, int position, View convertView, ViewGroup parent) {
            ListItem item = getItem(position);

            CircleImageView img = (CircleImageView) view.findViewById(R.id.user_img);
            TextView name = (TextView) view.findViewById(R.id.user_name);
            TextView date = (TextView) view.findViewById(R.id.txt_date);
            TextView content = (TextView) view.findViewById(R.id.txt_comment);
            UserInfoEntity user = null;
            if(item !=null){
                user = item.getUser();
                date.setText(item.getDate());
                content.setText(item.getSummary());
            }
            if(user!= null){
                new ImageDownloader.Builder()
                        .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                        .build(user.getAvatar(), img)
                        .start();
                name.setText(user.getName());
                name.getPaint().setFakeBoldText(true);
            }
        }
    }
}
