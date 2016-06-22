package com.ccpress.izijia.yd.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.vo.UserVo;
import com.ccpress.izijia.yd.activity.CommentActivity;
import com.ccpress.izijia.yd.activity.YdOrderInfoActivity;
import com.ccpress.izijia.yd.activity.YdPayActivity;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.Desc;
import com.ccpress.izijia.yd.entity.SerializableList;
import com.ccpress.izijia.yd.entity.YdOrder;
import com.ccpress.izijia.yd.view.MaxListView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/6/1.
 */
public class YdOrderAdapter extends BaseAdapter {

    private List<YdOrder.Data> list;
    private Context ctx;
    private UserVo vo;

    public YdOrderAdapter(List<YdOrder.Data> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
        vo = Util.getUserInfo();
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
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Holder holder;
        if (view == null) {
            view = View.inflate(Util.getMyApplication(), R.layout.yd_item_order, null);
            holder = new Holder();
            holder.tv_sn = (TextView) view.findViewById(R.id.tv_sn);
            holder.tv_add_time = (TextView) view.findViewById(R.id.tv_add_time);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.lv_goods = (MaxListView) view.findViewById(R.id.lv_goods);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            holder.tv_right = (TextView) view.findViewById(R.id.tv_right);
            holder.tv_left = (TextView) view.findViewById(R.id.tv_left);
            holder.tv_order_state = (TextView) view.findViewById(R.id.tv_order_state);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        final YdOrder.Data order = list.get(i);
        holder.tv_sn.setText("订单编号：" + order.order_sn);//订单编号
        holder.tv_title.setText(order.supplier_name);
        holder.tv_add_time.setText("预订时间：" + order.add_time);
        holder.tv_price.setText("￥" + order.order_amount);
        holder.tv_time.setText("入住 " + order.gotime + "  离开" + order.outtime);
        holder.tv_order_state.setText(order.order_status);
        holder.lv_goods.setAdapter(new OrderGoodsAdapter(ctx, order.goods));
        String state = order.order_status;
        if (state.equals("已完成")) {
            holder.tv_left.setVisibility(View.GONE);
            holder.tv_right.setVisibility(View.GONE);
            if (order.dianping == 0) {
                holder.tv_right.setVisibility(View.VISIBLE);
                holder.tv_right.setText("评价");
                holder.tv_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ctx, CommentActivity.class);
                        intent.putExtra("goodsid", order.order_id)
                                .putExtra("order_sn", order.order_sn)
                                .putExtra("thumb", order.logo)
                                .putExtra("goods_name", order.supplier_name)
                                .putExtra("desc", order.supplier_title);
                        ctx.startActivity(intent);
                        //                    ((OrderActivity) ctx).finish();
                    }
                });
            }
        } else if (state.equals("待支付")) {
            holder.tv_left.setVisibility(View.VISIBLE);
            holder.tv_right.setVisibility(View.VISIBLE);
            holder.tv_right.setText("付款");
            holder.tv_left.setText("取消");
            holder.tv_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("是否确定取消订单?").setTitle("取消订单")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, final int j) {
                                    final ProgressDialog pd = new ProgressDialog(ctx);
                                    pd.setMessage("正在取消...");
                                    pd.show();
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("act", "order_cancel");
                                    map.put("ocid", vo.getUid());//23734  23142 116
                                    map.put("order_id", order.order_id);
                                    NetUtil.Post(ConstantApi.OCENTENUSER, map, new MyCallBack() {
                                        @Override
                                        public void onSuccess(String s) {
                                            try {
                                                JSONObject object = new JSONObject(s);
                                                int result = object.getInt("error");
                                                if (result == 1) {
                                                    CustomToast.showToast("已取消");
                                                    holder.tv_order_state.setText("已取消");
                                                    list.get(i).order_status = "已取消";
                                                    holder.tv_left.setVisibility(View.GONE);
                                                    holder.tv_right.setVisibility(View.GONE);
                                                } else {
                                                    CustomToast.showToast(object.getString("datas"));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFinished() {
                                            pd.cancel();
                                        }
                                    });
                                }
                            }).setPositiveButton("取消", null).create().show();

                }
            });
            holder.tv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, YdPayActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("order_id", order.order_id);
                    String[] times = {order.gotime, order.outtime};
                    bundle.putStringArray("times", times);
                    bundle.putString("title", order.supplier_name);
                    bundle.putString("name", order.consignee);
                    bundle.putString("tel", order.mobile);
                    bundle.putInt("peosonCount", order.renshu);
                    bundle.putDouble("totalMoney", Double.parseDouble(order.order_amount));
                    bundle.putDouble("baoxian", Double.parseDouble(order.pack_fee));

                    List<YdOrder.Goods> goodsList = order.goods;
                    List<Desc> descs = new ArrayList<Desc>();
                    SerializableList<Desc> serializableList = new SerializableList<Desc>();
                    for (int j = 0; j < goodsList.size(); j++) {
                        Desc desc = new Desc();
                        desc.name = goodsList.get(j).goods_name;
                        desc.count = goodsList.get(j).goods_number;
                        desc.price = goodsList.get(j).amount_price;
                        descs.add(desc);
                    }
                    serializableList.setMap(descs);
                    bundle.putSerializable("list", serializableList);
                    bundle.putInt("count", goodsList.size() + (Double.parseDouble(order.pack_fee) > 0 ? 1 : 0));
                    intent.putExtra("bundle", bundle);
                    ctx.startActivity(intent);
                }
            });
        } else if (state.equals("已确认") || state.equals("待确认")) {
            holder.tv_left.setVisibility(View.GONE);
            holder.tv_right.setVisibility(View.VISIBLE);
            holder.tv_right.setText("退款");
            holder.tv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("是否确定申请退款?").setTitle("退款")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, final int j) {
                                    final ProgressDialog pd = new ProgressDialog(ctx);
                                    pd.setMessage("正在申请退款...");
                                    pd.show();
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("act", "refund_order");
                                    map.put("ocid", vo.getUid());//23734  23142 116
                                    map.put("order_id", order.order_id);
                                    NetUtil.Post(ConstantApi.OCENTENUSER, map, new MyCallBack() {
                                        @Override
                                        public void onSuccess(String s) {
                                            Log.d("json", s);
                                            try {
                                                JSONObject object = new JSONObject(s);
                                                int result = object.getInt("error");
                                                if (result == 1) {
                                                    CustomToast.showToast("已退款");
                                                    holder.tv_order_state.setText("退款申请中");
                                                    list.get(i).order_status = "退款申请中";
                                                    holder.tv_left.setVisibility(View.GONE);
                                                    holder.tv_right.setVisibility(View.GONE);
                                                } else {
                                                    CustomToast.showToast(object.getString("datas"));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFinished() {
                                            pd.cancel();
                                        }
                                    });
                                }
                            }).setPositiveButton("取消", null).create().show();

                }
            });
        } else if (state.equals("退款申请中") || state.equals("已取消"))
        {
            holder.tv_left.setVisibility(View.GONE);
            holder.tv_right.setVisibility(View.GONE);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, YdOrderInfoActivity.class);
                intent.putExtra("order_id", list.get(i).order_id);
                intent.putExtra("uid", vo.getUid());
                intent.putExtra("img",list.get(i).logo);
                ctx.startActivity(intent);
            }
        });
        return view;
    }

    class Holder {
        TextView tv_sn;
        TextView tv_add_time;
        TextView tv_title;
        TextView tv_time;
        MaxListView lv_goods;
        TextView tv_price;
        TextView tv_order_state;
        TextView tv_right;
        TextView tv_left;
    }
}
