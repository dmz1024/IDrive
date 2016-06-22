package com.ccpress.izijia.dfy.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.activity.EvaluateActivity;
import com.ccpress.izijia.dfy.activity.PayActivity;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.Order;
import com.ccpress.izijia.dfy.entity.PayInfo;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.vo.UserVo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by administror on 2016/3/29 0029.
 */
public class OrderAdapter extends BaseAdapter {

    private List<Order> list;
    private Context ctx;
    private UserVo vo;

    public OrderAdapter(List<Order> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
        vo=Util.getUserInfo();
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
            view = View.inflate(Util.getMyApplication(), R.layout.dfy_item_personal_order, null);
            holder = new Holder();
            holder.tv_order_num = (TextView) view.findViewById(R.id.tv_order_num);
            holder.tv_order_name = (TextView) view.findViewById(R.id.tv_order_name);
            holder.tv_zjy = (TextView) view.findViewById(R.id.tv_zjy);
            holder.tv_go_time = (TextView) view.findViewById(R.id.tv_go_time);
            holder.tv_order_money = (TextView) view.findViewById(R.id.tv_order_money);
            holder.tv_right = (TextView) view.findViewById(R.id.tv_right);
            holder.tv_left = (TextView) view.findViewById(R.id.tv_left);
            holder.tv_order_state = (TextView) view.findViewById(R.id.tv_order_state);
            holder.tv_order_add_time = (TextView) view.findViewById(R.id.tv_order_add_time);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        final Order order = list.get(i);
        holder.tv_order_num.setText(order.getOrder_sn());//订单编号
        holder.tv_order_name.setText(order.getGoods_name());
        holder.tv_order_money.setText(order.getOrder_amount());
        holder.tv_zjy.setText(order.getCat_name() == null ? "自驾游" : order.getCat_name());
        holder.tv_order_state.setText(order.getOrder_status());
        holder.tv_go_time.setText(order.getCyrq() + "出发");
        holder.tv_order_add_time.setText(order.getAdd_time());

        String state = order.getOrder_status();
        if (state.equals("已取消")) {
            holder.tv_left.setVisibility(View.GONE);
            holder.tv_right.setVisibility(View.GONE);
        } else if (state.equals("待支付")) {
            holder.tv_left.setVisibility(View.VISIBLE);
            holder.tv_right.setVisibility(View.VISIBLE);
            holder.tv_right.setText("付款");
            holder.tv_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("您确定删除订单号:"+order.getOrder_sn()+"的订单吗？").setTitle("订单删除")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, final int j) {
                                    //TODO 此处可以将http提交和取消订单进行封装，时间关系就不封装了
                                    final ProgressDialog pd=new ProgressDialog(ctx);
                                    pd.setMessage("正在取消订单...");
                                    pd.show();
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("act", "order_cancel");
                                    map.put("ocid", vo.getUid());//23734  23142 116
                                    map.put("order_id", order.getOrder_id());
                                    NetUtil.Post(Constant.DFY_ORDER_LIST, map, new MyCallBack() {
                                        @Override
                                        public void onSuccess(String s) {
                                            try {
                                                JSONObject object = new JSONObject(s);
                                                int result = object.getInt("result");
                                                if (result == 0) {
                                                    CustomToast.showToast("已取消");
                                                    holder.tv_order_state.setText("已取消");
                                                    list.get(i).setOrder_status("已取消");
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
                            }).setPositiveButton("取消",null).create().show();

                }
            });

            holder.tv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, PayActivity.class);
                    PayInfo payInfo = new PayInfo();
                    intent.putExtra("order_id", order.getOrder_id())
                            .putExtra("totalMoney", order.getOrder_amount());
                    ctx.startActivity(intent);
//                    ((OrderActivity) ctx).finish();
                }
            });
        } else if (state.equals("待点评")) {
            holder.tv_left.setVisibility(View.GONE);
            holder.tv_right.setVisibility(View.VISIBLE);
            holder.tv_right.setText("点评");
            holder.tv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, EvaluateActivity.class);
                    intent.putExtra("goodsid", order.getGoods_id())
                            .putExtra("order_sn", order.getOrder_sn())
                            .putExtra("thumb", order.getThumb())
                            .putExtra("goods_name", order.getGoods_name());
                    ctx.startActivity(intent);
//                    ((OrderActivity) ctx).finish();
                }
            });
        } else if (state.equals("已支付") || state.equals("已完成")) {
            holder.tv_left.setVisibility(View.GONE);
            holder.tv_right.setVisibility(View.GONE);
        }

        return view;
    }

    class Holder {
        TextView tv_order_num;
        TextView tv_order_name;
        TextView tv_zjy;
        TextView tv_go_time;
        TextView tv_order_money;
        TextView tv_right;
        TextView tv_left;
        TextView tv_order_state;
        TextView tv_order_add_time;
    }
}
