package com.ccpress.izijia.dfy.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.Collect;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.vo.UserVo;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmz1024 on 2016/3/31.
 */
public class CollectAdapter extends BaseAdapter {
    private ViewHolder holder;
    private ImageOptions options;
    private List<Collect> list;
    private Context ctx;
    private UserVo vo;

    public CollectAdapter(List<Collect> list,Context ctx) {
        this.list = list;
        this.ctx=ctx;
        options = new ImageOptions.Builder()
                .setIgnoreGif(false)
                .build();
        vo=Util.getUserInfo();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int i) {
        return list.get(i).getObj_id();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(Util.getMyApplication(), R.layout.dfy_item_collect, null);
            holder = new ViewHolder();
            x.view().inject(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final Collect collect=list.get(i);
        holder.tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd=new ProgressDialog(ctx);
                pd.setMessage("正在取消收藏");
                pd.show();
                Map<String,Object> map=new HashMap<String, Object>();
                map.put("uid",vo.getUid());
                map.put("fav_ids",collect.getId());
                map.put("auth",vo.getAuth());
                NetUtil.Post(Constant.DFY_COLLECT_DEL,map,new MyCallBack(){
                    @Override
                    public void onSuccess(String s) {
                        Log.d("s",s);//{"result":true,"msg":"\u53d6\u6d88\u6536\u85cf\u6210\u529f","data":[]}取消成功
                        //{"result":false,"msg":"\u53d6\u6d88\u6536\u85cf\u5931\u8d25","data":[]}//取消失败
                        try {
                            JSONObject jsonObject=new JSONObject(s);
                            boolean result=jsonObject.getBoolean("result");
                            if(result){
                                list.remove(i);
                                notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFinished() {
                        pd.dismiss();
                    }
                });
            }
        });

        holder.tv_goods_name.setText(collect.getTitle());
        x.image().bind(holder.iv_left,collect.getImage());
        return view;
    }

    class ViewHolder {
        @ViewInject(R.id.iv_left)
        ImageView iv_left;
        @ViewInject(R.id.tv_goods_name)
        TextView tv_goods_name;
        @ViewInject(R.id.tv_cancle)
        TextView tv_cancle;
    }
}
