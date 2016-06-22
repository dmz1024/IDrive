package com.trs.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.trs.adapter.AbsListAdapter;
import com.trs.mobile.R;
import com.trs.types.ListItem;
import com.trs.util.ImageDownloader;

/**
 * Created by john on 14-5-22.
 */
public class PictureListFragment extends DocumentListFragment {
    @Override
    protected AbsListAdapter createAdapter() {
        return new adapter(getActivity());
    }

    @Override
    protected int getViewID() {
        return super.getViewID();
    }

    class adapter extends AbsListAdapter{

        @Override
        public void updateView(View view, int position, View convertView, ViewGroup parent) {
            ListItem listItem = getItem(position);

            ImageView imageView = (ImageView) view.findViewById(R.id.imgId);
            TextView textView = (TextView) view.findViewById(R.id.textId);

            textView.setText(listItem.getTitle());
            new ImageDownloader.Builder().
                    setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                    build(listItem.getImgUrl(), imageView).start();
        }

        @Override
        public int getViewID() {
            return 0;
        }

        public adapter(Context context) {
            super(context);
        }

    }
}
