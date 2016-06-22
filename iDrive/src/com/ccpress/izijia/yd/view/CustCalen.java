package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.DateUtils;
import com.ccpress.izijia.dfy.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/4/26.
 * 自定义日历
 */
public abstract class CustCalen {
    private Context ctx;
    private TextView tv_time;
    private ViewPager vp_day;
    private int screenWidth;
    private View v;
    private int currenYear;
    private int currenMonth;
    private int currenDay;

    public CustCalen(Context ctx) {
        this.ctx = ctx;
    }

    public View getCalenView() {
        v = View.inflate(this.ctx, R.layout.yd_item_pop_calen, null);
        screenWidth = Util.getWeight() / 7;
        vp_day = (ViewPager) v.findViewById(R.id.vp_day);
        tv_time = (TextView) v.findViewById(R.id.tv_time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currenDate = sdf.format(new Date());
        currenYear = Integer.parseInt(currenDate.split("-")[0]);
        currenMonth = Integer.parseInt(currenDate.split("-")[1]);
        currenDay = Integer.parseInt(currenDate.split("-")[2]);
        vp_day.setAdapter(new VpAdapter());
        vp_day.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tv_time.setText(currenYear + "年" + (currenMonth < 10 ? "0" + currenMonth : currenMonth) + "月");
                } else {
                    int[] times = getTime(currenYear, currenMonth, position);
                    tv_time.setText(times[0] + "年" + (times[1] < 10 ? "0" + times[1] : times[1]) + "月");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return v;
    }

    /**
     * 返回文本显示的时间
     *
     * @param currenYear
     * @param currenMonth
     * @param position
     * @return
     */
    private int[] getTime(int currenYear, int currenMonth, int position) {
        int month = currenMonth + position;
        int year = currenYear;
        if (month > 12) {
            int addYear = month / 12;
            year = currenYear + addYear;
            if (month % 12 == 0) {
                if (month > 23) {
                    year = currenYear + (addYear - 1);
                    month = 12;
                } else {
                    month = 1;
                }
            } else {
                month = month % 12;
            }
        }
        return new int[]{year, month};
    }

    /**
     * 返回选择的时间
     *
     * @param time
     */
    public abstract void returnTime(String time);


    class GridAdapter extends BaseAdapter {
        private int week;
        private int days;
        private int currenYear;
        private int currenMonth;
        private int currenDay;

        public GridAdapter(int year, int month, int day) {
            week = DateUtils.returnFirstdayIsWeek(year, month);
            days = DateUtils.maxDayOfMonth(year, month);
            this.currenYear = year;
            this.currenMonth = month;
            this.currenDay = day;
        }

        @Override
        public int getCount() {
            return (int) (((week + days) / 6) * 7);
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
            TextView tv = new TextView(ctx);
            tv.setWidth(screenWidth);
            tv.setHeight((screenWidth/5)*4);
            tv.setTextColor(Util.getColor(R.color.yd_333));
            tv.setTextSize(screenWidth / 9);
            tv.setGravity(Gravity.CENTER);
            String text = "";
            tv.setBackgroundColor(ctx.getResources().getColor(R.color.yd_ddd));
            if (i >= week && i < days + week) {
                text = (i - week + 1) + "";
                if (currenDay != 0) {
                    if (Integer.parseInt(text) < currenDay) {
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CustomToast.showToast("所选时间不能低于今天");
                            }
                        });
                    } else {
                        if (currenDay == Integer.parseInt(text)) {
                            text = "今天";
                            tv.setBackgroundColor(ctx.getResources().getColor(R.color.yd_fff));
                        }
                        final String finalText = text;
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                returnTime(currenYear + "-" + (currenMonth < 10 ? "0" + currenMonth : currenMonth) + "-" +
                                        (finalText.equals("今天") ? (currenDay<10?"0"+currenDay:currenDay+"") + "" : (Integer.parseInt(finalText)<10?"0"+finalText:finalText)));
                            }
                        });
                    }
                } else {
                    final String finalText1 = text;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            returnTime(currenYear + "-" + (currenMonth < 10 ? "0" + currenMonth : currenMonth) + "-" + (Integer.parseInt(finalText1)<10?"0"+finalText1:finalText1));
                        }
                    });
                }

            }

            tv.setText(text);
            return tv;
        }
    }


    private class VpAdapter extends PagerAdapter {
        private Map<Integer, View> map = new HashMap<>();
        private boolean isFirst = true;

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView = new GridView(ctx);
            gridView.setNumColumns(7);
            gridView.setVerticalSpacing(1);
            gridView.setHorizontalSpacing(1);

            if (position == 0) {
                gridView.setAdapter(new GridAdapter(currenYear, currenMonth, currenDay));
                if (isFirst) {
                    isFirst = false;
                    tv_time.setText(currenYear + "年" + (currenMonth < 10 ? "0" + currenMonth : currenMonth) + "月");
                }
            } else {
                int[] times = getTime(currenYear, currenMonth, position);
                gridView.setAdapter(new GridAdapter(times[0], times[1], 0));
            }

            container.addView(gridView);
            map.put(position, gridView);
            return gridView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(map.get(position));
            map.remove(position);
        }
    }


}
