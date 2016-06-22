package com.ccpress.izijia.dfy.activity;

        import android.content.Intent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ListView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import com.ccpress.izijia.R;
        import com.ccpress.izijia.dfy.entity.AffirmOrder;
        import com.ccpress.izijia.dfy.util.ListViewUtil;
        import com.ccpress.izijia.dfy.view.TopView;
        import org.xutils.view.annotation.ViewInject;

/**
 * Created by dmz1024 on 2016/3/27.
 */
public class AffirmActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.tv_goods_name)
    private TextView tv_goods_name;
    @ViewInject(R.id.tv_brand_name)
    private TextView tv_brand_name;
    @ViewInject(R.id.tv_date)
    private TextView tv_date;
    @ViewInject(R.id.tv_spell)
    private TextView tv_spell;
    @ViewInject(R.id.tv_name)
    private TextView tv_name;
    @ViewInject(R.id.tv_sex)
    private TextView tv_sex;
    @ViewInject(R.id.tv_tel)
    private TextView tv_tel;
    @ViewInject(R.id.tv_email)
    private TextView tv_email;
    @ViewInject(R.id.lv_tourist)
    private ListView lv_tourist;
    @ViewInject(R.id.tv_count)
    private TextView tv_count;
    @ViewInject(R.id.tv_fp)
    private TextView tv_fp;
    @ViewInject(R.id.tv_fp_title)
    private TextView tv_fp_title;
    @ViewInject(R.id.tv_fp_detail)
    private TextView tv_fp_detail;
    @ViewInject(R.id.tv_fp_address)
    private TextView tv_fp_address;
    @ViewInject(R.id.tv_total)
    private TextView tv_total;
    @ViewInject(R.id.tv_submit)
    private TextView tv_submit;
    @ViewInject(R.id.rl_fp_yes)
    private RelativeLayout rl_fp_yes;
    @ViewInject(R.id.tv_fp_addressee)
    private TextView tv_fp_addressee;
    @ViewInject(R.id.tv_fp_tel)
    private TextView tv_fp_tel;
    @ViewInject(R.id.rl_fp)
    private RelativeLayout rl_fp;

    private AffirmOrder affirm;
    private TouristAdapter mAdpter;

    @Override
    protected int getRid() {
        return R.layout.dfy_activity_affirm;
    }

    @Override
    protected void initView() {
        super.initView();
        tv_submit.setOnClickListener(this);
        initData();
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("确认订单");
    }

    private void initData() {
        affirm = (AffirmOrder) getIntent().getSerializableExtra("affirm");
        tv_goods_name.setText(affirm.getGoods_name());
        tv_brand_name.setText(affirm.getBrand_name());
        tv_date.setText(affirm.getDate());
        tv_count.setText(affirm.getPeoCount());
        tv_spell.setText(affirm.isSpell() == true ? "两人间,愿意拼房" : "不愿意拼房,愿意支付房差费用¥"+getIntent().getStringExtra("fangcha"));
        tv_name.setText(affirm.getLinkman_name());
        tv_sex.setText(affirm.getLinkman_sex() == true ? "男" : "女");
        tv_tel.setText(affirm.getLinkman_tel());
        tv_email.setText(affirm.getLinkman_eamil());
        lv_tourist.setAdapter(mAdpter = new TouristAdapter());
        ListViewUtil.setListViewHeightBasedOnChildren(lv_tourist);
        tv_fp.setText(affirm.isFp() == true ? "是" : "否");
        rl_fp.setVisibility(affirm.isFp() == true ? View.VISIBLE : View.GONE);
        rl_fp_yes.setVisibility(affirm.isFp() == true ? View.VISIBLE : View.GONE);
        tv_fp_addressee.setText(affirm.getFpAddressee());
        tv_fp_tel.setText(affirm.getFpTel());
        tv_fp_title.setText(affirm.getFpTitle());
        tv_fp_detail.setText(affirm.getFpDetail());
        tv_fp_address.setText(affirm.getFpAddress());
        tv_total.setText(affirm.getTotal());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                goPay();//去支付
                break;

        }
    }

    private void goPay() {
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra("order_id", getIntent().getStringExtra("order_id"))
                .putExtra("totalMoney", affirm.getTotal().substring(4));
        startActivity(intent);
    }


    class TouristAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return affirm.getListTourist().size();
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
            View v = View.inflate(AffirmActivity.this, R.layout.dfy_item_affirm, null);
            TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_name.setText(affirm.getListTourist().get(i).getName());
            return v;
        }
    }
}
