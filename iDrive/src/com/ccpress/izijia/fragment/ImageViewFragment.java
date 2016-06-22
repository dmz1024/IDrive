package com.ccpress.izijia.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.util.ImageUtil;
import com.ccpress.izijia.util.ScreenUtil;

/**
 * Created by Wu Jingyu
 * Date: 2014/11/4
 * Time: 10:40
 */

public class ImageViewFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_image_view, container, false);
        ImageView mImageView = (ImageView) view.findViewById(R.id.preview_image);
        Bundle bundle = getArguments();
        String path = bundle.getString("Media_entity");

        Bitmap bitmap = ImageUtil.decodeSampledBitmapFromPath(path,
                ScreenUtil.getScreenWidth(getActivity()),
                (ScreenUtil.getScreenHeight(getActivity()) / 13 * 11));
        mImageView.setImageBitmap(bitmap);
        return view;
    }
}
