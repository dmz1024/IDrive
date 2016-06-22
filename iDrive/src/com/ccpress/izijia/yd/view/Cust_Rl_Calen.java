package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;


import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by dengmingzhi on 16/4/25.
 */
public abstract class Cust_Rl_Calen extends LinearLayout implements View.OnClickListener {
    private TextView tv_title;
    private RelativeLayout rl_1;
    private RelativeLayout rl_2;
    private TextView tv_time_1;
    private TextView tv_time_2;
    private Button bt_filter;
    private View v;
    private PopupWindow pop_calen;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public Cust_Rl_Calen(Context context) {
        this(context, null);
    }

    public Cust_Rl_Calen(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Cust_Rl_Calen(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View.inflate(getContext(), R.layout.yd_item_cust_calen, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_time_1 = (TextView) findViewById(R.id.tv_time_1);
        tv_time_2 = (TextView) findViewById(R.id.tv_time_2);
        bt_filter = (Button) findViewById(R.id.bt_filter);
        bt_filter.setOnClickListener(this);
        rl_1 = (RelativeLayout) findViewById(R.id.rl_1);
        rl_2 = (RelativeLayout) findViewById(R.id.rl_2);
        rl_1.setOnClickListener(this);
        rl_2.setOnClickListener(this);

    }

    public void setTvtTime(String[] time) {
        tv_time_1.setText(time[0]);
        tv_time_2.setText(time[1]);
    }


    public String[] getTvTime() {
        return new String[]{tv_time_1.getText().toString(), tv_time_2.getText().toString()};
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_1:
                rl_1();
                break;
            case R.id.rl_2:
                rl_2();
                break;
            case R.id.bt_filter:
                String time1 = tv_time_1.getText().toString();
                String time2 = tv_time_2.getText().toString();
                if (TextUtils.isEmpty(time1) || TextUtils.isEmpty(time2)) {
                    CustomToast.showToast("请选择时间");
                    return;
                }
                filter(time1,time2);
                break;
        }


    }

    public abstract void filter(String time1,String time2);

    private void rl_1() {
        chooseTime(1);
    }

    private void rl_2() {
        if (TextUtils.isEmpty(tv_time_1.getText())) {
            CustomToast.showToast("请先选择入住时间");
            return;
        }
        chooseTime(2);
    }

    private void chooseTime(final int type) {
        pop_calen = new PopupWindow(new CustCalen(getContext()) {
            @Override
            public void returnTime(String time) {
                if (type == 1) {
                    tv_time_1.setText(time);
                    String endTime = tv_time_2.getText().toString();
                    if (!TextUtils.isEmpty(endTime)) {
                        if (timeContrast(time, endTime)) {
                            tv_time_2.setText("");
                        }
                    }
                    pop_calen.dismiss();
                } else {
                    String startTime = tv_time_1.getText().toString();
                    if (timeContrast(startTime, time)) {
                        CustomToast.showToast("离开时间不能小于入住时间");
                    } else {
                        tv_time_2.setText(time);
                        pop_calen.dismiss();
                    }
                }

            }
        }.getCalenView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, true);
        pop_calen.setBackgroundDrawable(new ColorDrawable());
        pop_calen.showAtLocation(this, Gravity.CENTER, 0, 0);
    }


    /**
     * 时间大小比较
     *
     * @param start
     * @param end
     * @return
     */
    private boolean timeContrast(String start, String end) {
        try {
            long timeStart = sdf.parse(start).getTime();
            long timeEnd = sdf.parse(end).getTime();
            return timeStart > timeEnd;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
