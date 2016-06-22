package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.api.MyImageLoader;


import java.util.List;

/**
 * Created by dengmingzhi on 16/5/23.
 */
public class GridViewComment extends MaxGridView {
    private MyImageLoader loader;
    private List<String> list;

    public GridViewComment(Context context) {
        this(context, null);
    }

    public GridViewComment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridViewComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setAdapter(List<String> list, MyImageLoader loader) {
        this.loader = loader;
        this.list = list;
        setAdapter(new MyAdapter(this.list));
    }

    class MyAdapter extends BaseAdapter {
        private List<String> list;

        public MyAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = View.inflate(getContext(), R.layout.item_comment_img, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_img);
            loader.get(iv, list.get(i));
            return view;
        }
    }
}
