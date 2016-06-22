package com.ccpress.izijia.dfy.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.activity.DetailsActivity;
import com.ccpress.izijia.dfy.adapter.IdriveAdapter;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.Idrive;
import com.ccpress.izijia.dfy.util.DensityUtil;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.RefreshListView;
import com.froyo.commonjar.network.BitmapCache;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;
import org.xutils.x;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/4/17.
 * 领队页
 */
public class AkelaFragment extends FragmentBase<Idrive, IdriveAdapter> {
    private View v;

    private Map<String, Object> map = new HashMap<String, Object>();
    private String brand_goods_id;
    private NetworkImageView iv_image;
    private TextView tv_brand_name;
    private TextView tv_dec;
    private RelativeLayout.LayoutParams params;
    @SuppressLint("ValidFragment")
    public AkelaFragment(String brand_goods_id) {
        this.brand_goods_id = brand_goods_id;
    }

    public AkelaFragment() {

    }

    @Override
    protected void onItemClick(List<Idrive> list, int i) {
        if (i < 2) {
            return;
        }
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("id", mlist.get(i - 2).getGoods_id());
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 2 || i - 1 > mlist.size()) {
            return;
        }
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("id", mlist.get(i - 2).getGoods_id());
        startActivity(intent);
    }


    @Override
    protected RefreshListView.Mode getMode() {
        return RefreshListView.Mode.END;
    }

    @Override
    protected Map<String, Object> post() {
        map.put("brand", brand_goods_id);
        map.put("page", page);
        return map;
    }


    @Override
    protected boolean getSubmitType() {
        return true;
    }


    @Override
    protected List<Idrive> getList(String json) {
        return JsonUtil.json2List(json, Idrive.class, "data");
    }

    @Override
    protected String getUrl() {
        return Constant.DFY_BRAND_GOODS;
    }

    @Override
    protected IdriveAdapter getAdapter(List<Idrive> list) {
        return new IdriveAdapter(list);
    }

    @Override
    protected View getHeaderView() {
        v = View.inflate(getActivity(), R.layout.dfy_item_header_alela, null);
        iv_image = (NetworkImageView) v.findViewById(R.id.iv_image);
        tv_brand_name = (TextView) v.findViewById(R.id.tv_brand_name);
        tv_dec = (TextView) v.findViewById(R.id.tv_dec);
        getBrandInfo();
        return v;
    }

    private void getBrandInfo() {
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("id", brand_goods_id);
        NetUtil.Post(Constant.DFY_BRAND_INFO, map1, new com.ccpress.izijia.dfy.callBack.MyCallBack() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    String brand_logo = object.getString("brand_logo");
                    String brand_name = object.getString("brand_name");
                    String brand_desc = object.getString("brand_desc");
                    ImageOptions options = new ImageOptions.Builder()
                            //设置加载过程中的图片
                            .setLoadingDrawableId(R.drawable.dfy_icon_de_1)
                                    //设置加载失败后的图片
                            .setFailureDrawableId(R.drawable.dfy_icon_de_1)
                                    //设置使用缓存
                            .setUseMemCache(false)
                                    //设置显示圆形图片
//                .setCircular(true)

                                    //设置支持gif
                            .setIgnoreGif(false)
                . setSize(100, 100)
                            .build();
                    RequestQueue mQueue = Volley.newRequestQueue(getActivity());

                    ImageLoader imageLoader = new ImageLoader(mQueue,new BitmapCache());

//                    ImageListener listener = ImageLoader.getImageListener(imageView,R.drawable.default_image, R.drawable.default_image);
                    iv_image.setImageUrl(brand_logo,imageLoader);
//                    iv_image.setScaleType(ImageView.ScaleType.FIT_XY);
//                    x.image().bind(iv_image,brand_logo,options);
                    tv_brand_name.setText(brand_name);
                    tv_dec.setText(brand_desc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }
}
