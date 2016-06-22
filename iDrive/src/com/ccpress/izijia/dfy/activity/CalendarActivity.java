package com.ccpress.izijia.dfy.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.entity.Cyrp;
import com.ccpress.izijia.dfy.entity.Goodinfo;
import com.ccpress.izijia.dfy.entity.WriteInfo;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.DateUtils;
import com.ccpress.izijia.dfy.util.DensityUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.JiaAndJianRelativeLayout;
import com.ccpress.izijia.dfy.view.TopView;
import com.trs.util.log.Log;
import org.xutils.view.annotation.ViewInject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by administror on 2016/3/22 0022.
 */
public class CalendarActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.vp_day)
    private ViewPager vp_calendar;
    @ViewInject(R.id.tv_time)
    private TextView tv_time;
    @ViewInject(R.id.tv_next)
    private TextView tv_next;
    @ViewInject(R.id.tv_cheng_crPrice)
    private TextView tv_cheng_crPrice;
    @ViewInject(R.id.tv_er_crPrice)
    private TextView tv_er_crPrice;
    @ViewInject(R.id.tv_count)
    private TextView tv_count;
    @ViewInject(R.id.tv_date)
    private TextView tv_date;
    @ViewInject(R.id.rl_et)
    private JiaAndJianRelativeLayout rl_et;
    @ViewInject(R.id.rl_cheng)
    private JiaAndJianRelativeLayout rl_cheng;
    @ViewInject(R.id.tv_cheng_count)
    private TextView tv_cheng_count;
    @ViewInject(R.id.tv_er_count)
    private TextView tv_er_count;
    @ViewInject(R.id.iv_next_month)
    private ImageView iv_next_month;
    @ViewInject(R.id.iv_er)
    private ImageView iv_er;

    private List<Cyrp> listCyrp = new ArrayList<Cyrp>();
    private List<Cyrp> listCyrp1 = new ArrayList<Cyrp>();
    private Goodinfo goodinfo;

    private Map<View, View> isChooseMap = new HashMap<View, View>();
    private List<GridView> listGrideView;
    private List<String> listTime = new ArrayList<String>();
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void initView() {
        super.initView();
        Bundle bundle = getIntent().getExtras();
        goodinfo = (Goodinfo) bundle.getSerializable("goodInfo");
        listCyrp1 = goodinfo.getRili();

        long currenZero = new Date().getTime();
        for(int i=0;i<listCyrp1.size();i++){
            String time=listCyrp1.get(i).getAttr_end().equals("")?listCyrp1.get(i).getAttr_value():listCyrp1.get(i).getAttr_end();


            long endMillionSeconds=0;
            try {
                endMillionSeconds = sdf.parse(time).getTime()+100;
                if (currenZero < endMillionSeconds) {
                    listCyrp.add(listCyrp1.get(i));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        initVp();
        initClick();
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("选择日期");
    }

    @Override
    protected int getRid() {
        return R.layout.dfy_activity_calendar;
    }

    private void initClick() {
        iv_er.setOnClickListener(this);
        iv_next_month.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        rl_cheng.setJiaAndJianInterface(new JiaAndJianRelativeLayout.JiaAndJianInterface() {
            @Override
            public void jia() {
                cheng(1);
            }

            @Override
            public void jian() {
                cheng(-1);
            }
        });

        rl_et.setJiaAndJianInterface(new JiaAndJianRelativeLayout.JiaAndJianInterface() {
            @Override
            public void jia() {
                er(1);
            }

            @Override
            public void jian() {
                er(-1);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_next:
                goNext();
                break;
            case R.id.iv_next_month:
                int nextItem = vp_calendar.getCurrentItem() + 1;
                if (nextItem == listTime.size()) {
                    vp_calendar.setCurrentItem(0);
                } else {
                    vp_calendar.setCurrentItem(nextItem);
                }
                break;
            case R.id.iv_er:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String er_ts = goodinfo.getGoods_brief();
                if (er_ts.length() > 0) {
                    builder.setMessage(er_ts).show();
                }
                break;
        }
    }

    /**
     * 成人数量加减
     */
    private void cheng(int count) {
        erAndcheng(tv_cheng_count, count);
    }

    /**
     * 儿童数量加减
     */
    private void er(int count) {
        erAndcheng(tv_er_count, count);
    }

    private void erAndcheng(TextView t, int count) {
        if (isChooseMap.size() > 0) {
            int oldCount = Integer.parseInt(t.getText().toString());
            int newcount = oldCount + count;
            int minCount = 1;
            if (t.getId() == tv_er_count.getId()) {
                minCount = 0;
            }
            if (newcount < minCount) {
                newcount = minCount;
            }
            t.setText(newcount + "");
            tv_count.setText(tv_cheng_count.getText() + "位成人," + tv_er_count.getText() + "位儿童");
        } else {
            CustomToast.showToast("请先选择出行日期");
        }
    }

    /**
     * 跳转到填写订单界面
     */
    private void goNext() {
        if (isChooseMap.size() > 0) {
            String chengCount = tv_cheng_count.getText().toString();
            Intent intent = new Intent(CalendarActivity.this, Order_WriteActivity.class);
            WriteInfo writeInfo = new WriteInfo();
            writeInfo.setAttr_value(tv_date.getText().toString().substring(3, tv_date.length()));
            writeInfo.setBrand_name(goodinfo.getBrand_name());
            writeInfo.setCheng_count(Integer.parseInt(tv_cheng_count.getText().toString()));
            writeInfo.setEr_count(Integer.parseInt(tv_er_count.getText().toString()));
            writeInfo.setGoods_fysm(goodinfo.getGoods_fysm());
            writeInfo.setGood_name(goodinfo.getGoods_appname());
            writeInfo.setTdgz(goodinfo.getGoods_tggz());
            writeInfo.setFangcha(goodinfo.getFangcha());
            writeInfo.setCheng_price(Integer.parseInt(tv_cheng_crPrice.getText().toString().substring(1)));
            writeInfo.setEr_price(Integer.parseInt(tv_er_crPrice.getText().toString().substring(1)));
            writeInfo.setGoods_id(goodinfo.getGoodsid());
            writeInfo.setFapiao(goodinfo.getFapiao());
            intent.putExtra("writeInfo", writeInfo);
            startActivity(intent);
        } else {
            CustomToast.showToast("请先选择出行日期");
        }


    }

    private void initVp() {
        Map<String, String> countMap = new HashMap<String, String>();
        listGrideView = new ArrayList<GridView>();
        int j = 0;
        for (int i = 0; i < listCyrp.size(); i++) {
            //获取到第一个出发日期
            Cyrp cyrp = listCyrp.get(i);
            Log.d("end",listCyrp1.get(i).getAttr_end());
            Log.d("attr",listCyrp1.get(i).getAttr_value());
            String attr_value = cyrp.getAttr_value();
            int firstYear = Integer.parseInt(attr_value.split("-")[0]);
            int firstMonth = Integer.parseInt(attr_value.split("-")[1]);
            int firstDay = Integer.parseInt(attr_value.split("-")[2]);
            if (!countMap.containsKey(firstYear + "-" + firstMonth)) {
                countMap.put(firstYear + "-" + firstMonth, firstYear + "-" + firstMonth);
                listTime.add(j, firstYear + "-" + firstMonth);
                GridView gridView = new GridView(Util.getMyApplication());
                gridView.setNumColumns(7);
                gridView.setVerticalSpacing(1);
                gridView.setHorizontalSpacing(1);
                gridView.setAdapter(new gvAdapter(firstYear + "-" + firstMonth));
                listGrideView.add(gridView);
                j++;
            }
        }

//
//
        vp_calendar.setAdapter(new CalenVpAdapter());
        vp_calendar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_time.setText(listTime.get(position).split("-")[0] + "年" + listTime.get(position).split("-")[1] + "月");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        vp_calendar.setOffscreenPageLimit(listTime.size());


    }


    class CalenVpAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return listGrideView.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(listGrideView.get(position));
            if (position == 0) {
                tv_time.setText(listTime.get(position).split("-")[0] + "年" + listTime.get(position).split("-")[1] + "月");
            }
            return listGrideView.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

    }


    class gvAdapter extends BaseAdapter {
        private int height;
        private String value;
        private int fistDayWeek;
        private int maxDay;
        private AbsListView.LayoutParams params;
        private int year;
        private int month;
        private ColorStateList color_fff;
        private ColorStateList color_ef8619;
        private ColorStateList color_333;
        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        public gvAdapter(String value) {
            this.value = value;
            this.height = (int) ((DensityUtil.getScreenWidth() / 7 - 6) / 1.38);
            year = Integer.parseInt(value.split("-")[0]);
            month = Integer.parseInt(value.split("-")[1]);
            fistDayWeek = DateUtils.returnFirstdayIsWeek(year, month);
            maxDay = DateUtils.maxDayOfMonth(year, month);
        }

        @Override
        public int getCount() {
            return (int) (((fistDayWeek + maxDay) / 6) * 7);
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final View v = View.inflate(CalendarActivity.this, R.layout.dfy_item_calendar, null);
            if (params == null) {
                params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height + 10);
            }
            v.setLayoutParams(params);

            final TextView tv_time = (TextView) v.findViewById(R.id.tv_time);
            final TextView tv_price = (TextView) v.findViewById(R.id.tv_price);

            if (fistDayWeek > i) {
                tv_time.setText("");
            } else if (i + 1 - fistDayWeek <= maxDay) {
                final int time = i + 1 - fistDayWeek;
                for (int j = 0; j < listCyrp.size(); j++) {
                    String attr_value = listCyrp.get(j).getAttr_value();
                    String[] values = attr_value.split("-");
                    int attr_year = Integer.parseInt(values[0]);
                    int attr_month = Integer.parseInt(values[1]);
                    int attr_day = Integer.parseInt(values[2]);
                    if (attr_year == year && attr_month == month && attr_day == time) {
                        tv_time.setText(time + "");
                        final Cyrp cyrp = listCyrp.get(j);
                        String cheng = cyrp.getAttr_price();
                        String er = cyrp.getAttr_etprice();
                        if ("".equals(cheng)) {
                            cheng = "0";
                        } else {
                            cheng = "￥" + cheng + "起";
                        }

                        if ("".equals(er)) {
                            er = "￥0";
                        } else {
                            er = "￥" + er;
                        }

                        if (color_fff == null) {
                            color_fff = Util.getColor(R.color.dfy_fff);
                        }
                        if (color_ef8619 == null) {
                            color_ef8619 = Util.getColor(R.color.dfy_ef8619);
                        }

                        if (color_333 == null) {
                            color_333 = Util.getColor(R.color.dfy_333);
                        }

                        v.setBackgroundResource(R.color.dfy_fff);
                        tv_price.setTextColor(color_ef8619);
                        tv_time.setTextColor(color_333);
                        tv_price.setText(cheng);
                        TextPaint tp = tv_time.getPaint();
                        tp.setFakeBoldText(true);

                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                long endMillionSeconds = 0;
                                try {
                                    Log.d("attr",cyrp.getAttr_value());
                                    Log.d("end",cyrp.getAttr_end());
                                    String endTime = cyrp.getAttr_end() == "" ? cyrp.getAttr_value() : cyrp.getAttr_end();
                                    endMillionSeconds = sdf.parse(endTime).getTime() + 100;
                                    Log.d("endtime", endTime);
                                    Log.d("endtime", endMillionSeconds + "");

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                //获取当日零时时间戳
//                                long currenZero = new Date().getTime() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
                                long currenZero = new Date().getTime();
                                Log.d("endtime", currenZero + "");
                                if (endMillionSeconds > currenZero) {
                                    View tagView = isChooseMap.get(view);
                                    if (tagView == null) {
                                        String cheng = cyrp.getAttr_price();
                                        String er = cyrp.getAttr_etprice();
                                        if ("".equals(cheng)) {
                                            cheng = "￥0";
                                        } else {
                                            cheng = "￥" + cheng;
                                        }
                                        if ("".equals(er)) {
                                            er = "￥0";
                                        } else {
                                            er = "￥" + er;
                                        }
                                        tv_cheng_count.setText("1");
                                        tv_cheng_crPrice.setText(cheng);
                                        tv_er_crPrice.setText(er);
                                        view.setBackgroundResource(R.color.dfy_ef8619);
                                        tv_price.setTextColor(color_fff);
                                        tv_time.setTextColor(color_fff);
                                        tv_date.setText("团期:" + year + "-" + month + "-" + time);
                                        TextPaint tp = tv_time.getPaint();
                                        tp.setFakeBoldText(false);

                                        for (View view1 : isChooseMap.keySet()) {
                                            view1.setBackgroundResource(R.color.dfy_fff);
                                            TextView tv_time = (TextView) view1.findViewById(R.id.tv_time);
                                            TextView tv_price = (TextView) view1.findViewById(R.id.tv_price);
                                            tv_price.setTextColor(color_ef8619);
                                            tv_time.setTextColor(color_333);
                                            tp.setFakeBoldText(true);
                                        }

                                        isChooseMap.clear();
                                        isChooseMap.put(view, view);
                                    } else {
                                        tagView.setBackgroundResource(R.color.dfy_fff);
                                        tv_price.setTextColor(color_ef8619);
                                        tv_time.setTextColor(color_333);
                                        tv_er_crPrice.setText("￥0");
                                        tv_cheng_crPrice.setText("￥0");
                                        tv_cheng_count.setText("0");
                                        tv_er_count.setText("0");
                                        tv_count.setText("");
                                        tv_date.setText("团期:");
                                        TextPaint tp = tv_time.getPaint();
                                        tp.setFakeBoldText(true);
                                        isChooseMap.clear();
                                    }
                                    tv_count.setText(tv_cheng_count.getText() + "位成人," + tv_er_count.getText() + "位儿童");


                                } else {

                                    CustomToast.showToast("报名时间已于:" + (cyrp.getAttr_end() == "" ? cyrp.getAttr_value() : cyrp.getAttr_end()) + "截止");
                                }

                            }
                        });
                    } else {
                        tv_time.setText((time + ""));
                    }
                }

            } else {
                tv_time.setText("");
            }
            return v;
        }
    }
}
