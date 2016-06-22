package com.ccpress.izijia.dfy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.AffirmOrder;
import com.ccpress.izijia.dfy.entity.Tourist;
import com.ccpress.izijia.dfy.entity.WriteInfo;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.ListViewUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.JiaAndJianRelativeLayout;
import com.ccpress.izijia.dfy.view.TopView;
import com.ccpress.izijia.vo.UserVo;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by administror on 2016/3/24 0024.
 */
public class Order_WriteActivity extends BaseActivity implements View.OnClickListener, JiaAndJianRelativeLayout.JiaAndJianInterface {

    @ViewInject(R.id.tv_goods_name)
    private TextView tv_goods_name;
    @ViewInject(R.id.tv_brand_name)
    private TextView tv_brand_name;
    @ViewInject(R.id.tv_date)
    private TextView tv_date;
    @ViewInject(R.id.tv_count)
    private TextView tv_count;
    @ViewInject(R.id.tv_house_count)
    private TextView tv_house_count;
    @ViewInject(R.id.iv_spell_yes)
    private ImageView iv_spell_yes;
    @ViewInject(R.id.iv_spell_no)
    private ImageView iv_spell_no;
    @ViewInject(R.id.c1)
    private TextView tv_fysm;
    @ViewInject(R.id.c2)
    private TextView tv_tdgz;
    @ViewInject(R.id.iv_tab_1)
    private ImageView iv_tab_1;
    @ViewInject(R.id.iv_tab_2)
    private ImageView iv_tab_2;
    @ViewInject(R.id.tv_explain)
    private TextView tv_explain;
    @ViewInject(R.id.et_name)
    private EditText et_name;
    @ViewInject(R.id.iv_sex_1)
    private ImageView iv_sex_1;
    @ViewInject(R.id.iv_sex_2)
    private ImageView iv_sex_2;
    @ViewInject(R.id.et_tel)
    private EditText et_tel;
    @ViewInject(R.id.et_email)
    private EditText et_email;
    @ViewInject(R.id.lv_tourist)
    private ListView lv_tourist;
    @ViewInject(R.id.iv_fp_1)
    private ImageView iv_fp_1;
    @ViewInject(R.id.iv_fp_2)
    private ImageView iv_fp_2;
    @ViewInject(R.id.et_bill_title)
    private EditText et_bill_title;
    @ViewInject(R.id.et_bill_detail)
    private EditText et_bill_detail;
    @ViewInject(R.id.et_bill_address)
    private EditText et_bill_address;
    @ViewInject(R.id.iv_checked_agree)
    private ImageView iv_checked_agree;
    @ViewInject(R.id.tv_total)
    private TextView tv_total;
    @ViewInject(R.id.tv_submit)
    private TextView tv_submit;
    @ViewInject(R.id.rl_fp_yes)
    private RelativeLayout rl_fp_yes;
    @ViewInject(R.id.count)
    private JiaAndJianRelativeLayout houseCount;
    @ViewInject(R.id.tv_date_update)
    private TextView tv_date_update;
    @ViewInject(R.id.et_bill_name)
    private EditText et_bill_name;
    @ViewInject(R.id.et_bill_tel)
    private EditText et_bill_tel;
    @ViewInject(R.id.tv_price_spread)
    private TextView tv_price_spread;
    @ViewInject(R.id.tv_agree)
    private TextView tv_agree;
    @ViewInject(R.id.j)
    private TextView j;
    @ViewInject(R.id.rl_fp)
    private RelativeLayout rl_fp;
    private WriteInfo writeInfo;
    private boolean isSpell = true;
    private boolean isSex = true;
    private boolean isFp = false;
    private boolean isRead = true;
    private TouristAdapter mTouristAdapter;
    private int minHouseCount;
    private AffirmOrder affir;

    @Override
    protected int getRid() {
        return R.layout.dfy_activity_write;
    }

    @Override
    protected void initView() {
        super.initView();
        writeInfo = (WriteInfo) getIntent().getSerializableExtra("writeInfo");
        initData();
        initOnclick();
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("填写订单");
    }

    private void initData() {
        if(writeInfo.getFangcha().equals("0")){
            tv_price_spread.setVisibility(View.GONE);
            iv_spell_no.setVisibility(View.GONE);
            j.setVisibility(View.GONE);
        }

        if(TextUtils.equals(writeInfo.getFapiao(),"0")){
            rl_fp.setVisibility(View.GONE);
        }
        tv_price_spread.setText("房差费用￥" + writeInfo.getFangcha() + "元/间");
        minHouseCount = writeInfo.getCheng_count() / 2 + writeInfo.getCheng_count() % 2;
        tv_house_count.setText(minHouseCount + "");
        tv_goods_name.setText("【爱自驾】" + "<" + writeInfo.getGood_name() + ">");
        tv_brand_name.setText(writeInfo.getGood_name());
        String attr_value=writeInfo.getAttr_value();
        String firstYear=writeInfo.getAttr_value().split("-")[0];
        int firstMonth=Integer.parseInt(writeInfo.getAttr_value().split("-")[1]);
        int firstDay=Integer.parseInt(writeInfo.getAttr_value().split("-")[2]);
        tv_date.setText(firstYear+"-"+(firstMonth<10?"0"+firstMonth:firstMonth+"")+"-"+(firstDay<10?"0"+firstDay:firstDay+""));
        tv_count.setText(writeInfo.getCheng_count() + "位成人," + writeInfo.getEr_count() + "位儿童");
        tv_explain.setText(Html.fromHtml(writeInfo.getGoods_fysm()));
        tv_total.setText("总费用￥" + (writeInfo.getCheng_count() * writeInfo.getCheng_price() + writeInfo.getEr_count() * writeInfo.getEr_price()));
        lv_tourist.setAdapter(mTouristAdapter = new TouristAdapter());
        ListViewUtil.setListViewHeightBasedOnChildren(lv_tourist);

    }

    private void initOnclick() {
        tv_agree.setOnClickListener(this);
        iv_spell_yes.setOnClickListener(this);
        iv_spell_no.setOnClickListener(this);
        iv_sex_1.setOnClickListener(this);
        iv_sex_2.setOnClickListener(this);
        iv_fp_1.setOnClickListener(this);
        iv_fp_2.setOnClickListener(this);
        tv_fysm.setOnClickListener(this);
        tv_tdgz.setOnClickListener(this);
        iv_checked_agree.setOnClickListener(this);
        tv_date_update.setOnClickListener(this);
        houseCount.setJiaAndJianInterface(this);
        tv_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_spell_yes:
                spell(iv_spell_yes);//是否愿意拼房
                break;
            case R.id.iv_spell_no:
                spell(iv_spell_no);//是否愿意拼房
                break;
            case R.id.c1:
                explain(tv_fysm);//费用说明
                break;
            case R.id.c2:
                explain(tv_tdgz);//退订规则
                break;
            case R.id.iv_sex_1:
                isSex(iv_sex_1);//联系人性别
                break;
            case R.id.iv_sex_2:
                isSex(iv_sex_2);
                break;
            case R.id.iv_fp_1:
                isFp(iv_fp_1);//是否需要发票
                break;
            case R.id.iv_fp_2:
                isFp(iv_fp_2);
                break;
            case R.id.tv_date_update://修改日期
                Intent intent=new Intent(this,CalendarActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_checked_agree://是否同意相关协议
                isRead();
                break;
            case R.id.tv_submit:
                submit();//提交订单
                break;
            case R.id.tv_agree:
                Intent intent1=new Intent(this,ProtocolActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void submit() {

        if (!isRead) {
            CustomToast.showToast("请阅读并同意爱自驾旅游协议");
            return;
        }


        affir = new AffirmOrder();
        List<Tourist> listTourist = new ArrayList<Tourist>();


        String linkman_name = et_name.getText().toString();
        if (linkman_name.equals("") || linkman_name.contains(" ")) {
            CustomToast.showToast("请输入联系人姓名");
            return;
        }

        String linkman_email = et_email.getText().toString();
        if (linkman_email.equals("") || linkman_email.contains(" ")) {
            Toast.makeText(this, "请输入联系人邮箱", Toast.LENGTH_SHORT).show();
            return;
        }

        String linkman_tel = et_tel.getText().toString();
        if (linkman_tel.equals("") || linkman_tel.contains(" ")) {
            Toast.makeText(this, "请输入联系人手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < writeInfo.getCheng_count(); i++) {
            View v = lv_tourist.getChildAt(i);
            EditText edName = (EditText) v.findViewById(R.id.et_name);
            TextView tv_card_type = (TextView) v.findViewById(R.id.tv_card_type);
            EditText edCard = (EditText) v.findViewById(R.id.et_card);
            String tourist_name = edName.getText().toString();
            String tourist_card_type = tv_card_type.getText().toString();
            String tourist_card = edCard.getText().toString();
            if (TextUtils.isEmpty(tourist_name)) {
                CustomToast.showToast("请输入第" + (i + 1) + "位游客姓名");
                return;
            }
            if (TextUtils.isEmpty(tourist_card)) {
                CustomToast.showToast("请输入第" + (i + 1) + "位游客" + tourist_card_type + "号");
                return;
            }


            Tourist tourist = new Tourist();
            tourist.setName(tourist_name);
            tourist.setCard(tourist_card_type);
            tourist.setCardNum(tourist_card);
            listTourist.add(tourist);
        }
        affir.setListTourist(listTourist);

        if (isFp) {
            String bill_title = et_bill_title.getText().toString();
            if (bill_title.equals("") || bill_title.contains(" ")) {
                CustomToast.showToast("请输入发票抬头");
                return;
            }
            String bill_detail = et_bill_detail.getText().toString();
            if (bill_detail.equals("") || bill_detail.contains(" ")) {
                CustomToast.showToast("请输入发票明细");
                return;
            }

            String bill_address = et_bill_address.getText().toString();
            if (bill_address.equals("") || bill_address.contains(" ")) {
                CustomToast.showToast("请输入发票配送地址");
                return;
            }

            String addressee = et_bill_name.getText().toString();
            String fpTel = et_bill_tel.getText().toString();

            if (addressee.equals("")) {
                addressee = linkman_name;
            }

            if (fpTel.equals("")) {
                fpTel = linkman_tel;
            }

            affir.setFpAddressee(addressee);
            affir.setFpTel(fpTel);
            affir.setFpTitle(bill_title);
            affir.setFpAddress(bill_address);
            affir.setFpDetail(bill_detail);

        }

        affir.setGoods_name(writeInfo.getGood_name());
        affir.setBrand_name(writeInfo.getBrand_name());
        affir.setDate(writeInfo.getAttr_value());
        affir.setCount(tv_count.getText().toString());
        affir.setSpell(isSpell);
        affir.setLinkman_name(linkman_name);
        affir.setLinkman_eamil(linkman_email);
        affir.setLinkman_sex(isSex);
        affir.setLinkman_tel(linkman_tel);
        affir.setFp(isFp);
        affir.setTotal(tv_total.getText().toString());
        affir.setPeoCount(tv_count.getText().toString());
        getOrderInformation();
    }

    /**
     * 获取支付订单信息
     */
    private void getOrderInformation() {
        final ProgressDialog pdlog = new ProgressDialog(this);
        pdlog.setMessage("正在生成订单...");
        pdlog.show();
        final Map<String, Object> map = new HashMap<String, Object>();
        UserVo vo=Util.getUserInfo();
        map.put("goodsid", writeInfo.getGoods_id());
        map.put("uid", vo.getUid());
        map.put("username", vo.getUserName());
        map.put("usermobile", vo.getMobile());
        map.put("renshu", writeInfo.getCheng_count());
        map.put("laiyuan","android");

        StringBuffer youke = new StringBuffer();

        for (int i = 0; i < affir.getListTourist().size(); i++) {
            Tourist tourist = affir.getListTourist().get(i);
            youke = youke.append("~成人|中文|" + tourist.getName() + "|||" + tourist.getCard() + "|" + tourist.getCardNum());
        }
        map.put("youke", youke.toString());
        map.put("crnum", writeInfo.getCheng_count());
        map.put("etnum", writeInfo.getEr_count());
        map.put("crprice", writeInfo.getCheng_price());
        map.put("etprice", writeInfo.getEr_price());
        map.put("cyrq", tv_date.getText());
        String key = Util.getMa5("123456+"+vo.getUid()+"+"+ writeInfo.getGoods_id() + "+" + writeInfo.getCheng_price());
        map.put("key", key);
        map.put("fangjian", tv_house_count.getText().toString());
        if (isSpell) {
            map.put("fangcha", "0");
        } else {
            map.put("fangcha", "" + Integer.parseInt(writeInfo.getFangcha()) * (Integer.parseInt(tv_house_count.getText().toString()) * 2 - writeInfo.getCheng_count()));
        }

        map.put("consignee", affir.getLinkman_name());
        map.put("sex", affir.getLinkman_sex() ? "男" : "女");
        map.put("mobile", affir.getLinkman_tel());
        map.put("email", affir.getLinkman_eamil());
//        map.put("pack_crfee","0");
//        map.put("pack_etfee","0");
        if (affir.isFp()) {
            map.put("need_inv", 1);
            map.put("inv_payee", affir.getFpTitle());
            map.put("inv_content", affir.getFpDetail());
            map.put("inv_address", affir.getFpAddress());
            map.put("inv_name", affir.getFpAddressee());
            map.put("inv_mobile", affir.getFpTel());
        } else {
            map.put("need_inv", 0);
        }
        map.put("order_amount", tv_total.getText().toString().substring(4));
        NetUtil.Post(Constant.DFY_ORDER, map, new MyCallBack() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    int result = object.getInt("result");
                    if (result == 0) {
                        JSONObject data = object.getJSONObject("data");
                        Intent intent = new Intent(Order_WriteActivity.this, AffirmActivity.class);
                        intent.putExtra("order_id", data.getString("order_id"))
                                .putExtra("totalMonry", affir.getTotal())
                                .putExtra("affirm", affir).putExtra("fangcha", map.get("fangcha").toString());
                        startActivity(intent);
                        finish();
                    } else {
                        CustomToast.showToast("订单生成失败，请重试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinished() {
                pdlog.dismiss();
            }

        });


    }

    private void isRead() {
        if (isRead) {
            iv_checked_agree.setImageResource(R.drawable.dfy_order_agree_check);
            isRead = false;
        } else {
            iv_checked_agree.setImageResource(R.drawable.dfy_order_agree_checked);
            isRead = true;
        }
    }


    private void isFp(ImageView iv) {
        iv_fp_1.setImageResource(R.drawable.dfy_icon_zhi_circle);
        iv_fp_2.setImageResource(R.drawable.dfy_icon_zhi_circle);
        if (iv == iv_fp_1) {
            iv_fp_1.setImageResource(R.drawable.dfy_order_selected_gray);
            isFp = true;
            rl_fp_yes.setVisibility(View.VISIBLE);
        } else {
            iv_fp_2.setImageResource(R.drawable.dfy_order_selected_gray);
            isFp = false;
            rl_fp_yes.setVisibility(View.GONE);
        }
    }

    private void isSex(ImageView iv) {
        iv_sex_1.setImageResource(R.drawable.dfy_icon_zhi_circle);
        iv_sex_2.setImageResource(R.drawable.dfy_icon_zhi_circle);
        if (iv == iv_sex_1) {
            iv_sex_1.setImageResource(R.drawable.dfy_order_selected_gray);
            isSex = true;
        } else {
            iv_sex_2.setImageResource(R.drawable.dfy_order_selected_gray);
            isSex = false;
        }
    }

    private void explain(TextView tv) {
        iv_tab_1.setVisibility(View.GONE);
        iv_tab_2.setVisibility(View.GONE);
        if (tv == tv_fysm) {
            iv_tab_1.setVisibility(View.VISIBLE);
            tv_explain.setText(Html.fromHtml(writeInfo.getGoods_fysm()));
        } else {
            iv_tab_2.setVisibility(View.VISIBLE);
            tv_explain.setText(Html.fromHtml(writeInfo.getTdgz()));
        }
    }

    private void spell(ImageView iv) {
        iv_spell_yes.setImageResource(R.drawable.dfy_icon_zhi_circle);
        iv_spell_no.setImageResource(R.drawable.dfy_icon_zhi_circle);
        if (iv == iv_spell_yes) {
            isSpell = true;
            iv_spell_yes.setImageResource(R.drawable.dfy_icon_zhi_dian);
            tv_total.setText("总费用￥" + (writeInfo.getCheng_count() * writeInfo.getCheng_price() + writeInfo.getEr_count() * writeInfo.getEr_price()));
        } else {
            isSpell = false;
            Log.d("fangcha", writeInfo.getFangcha());
            iv_spell_no.setImageResource(R.drawable.dfy_icon_zhi_dian);
            int noSpellMoney = Integer.parseInt(writeInfo.getFangcha()) * (Integer.parseInt(tv_house_count.getText().toString()) * 2 - writeInfo.getCheng_count());
            tv_total.setText("总费用￥" + (writeInfo.getCheng_count() * writeInfo.getCheng_price() + writeInfo.getEr_count() * writeInfo.getEr_price() + noSpellMoney));
        }
    }

    @Override
    public void jia() {

        int count = Integer.parseInt(tv_house_count.getText().toString()) + 1;
        if (count > writeInfo.getCheng_count()) {
            count = writeInfo.getCheng_count();
            Toast.makeText(this, "最多选择" + writeInfo.getCheng_count() + "间房", Toast.LENGTH_SHORT).show();
        }
        tv_house_count.setText(count + "");
        if (isSpell) {
            spell(iv_spell_yes);
        } else {
            spell(iv_spell_no);
        }

    }

    @Override
    public void jian() {
        int count = Integer.parseInt(tv_house_count.getText().toString()) - 1;

        if (count < minHouseCount) {
            count = minHouseCount;
            Toast.makeText(this, "最少选择" + minHouseCount + "间房", Toast.LENGTH_SHORT).show();
        }

        tv_house_count.setText(count + "");

        if (isSpell) {
            spell(iv_spell_yes);
        } else {
            spell(iv_spell_no);
        }
    }


    class TouristAdapter extends BaseAdapter {
        private Map<Integer, View> map = new HashMap<Integer, View>();

        @Override
        public int getCount() {
            return writeInfo.getCheng_count();
        }

        @Override
        public View getItem(int i) {
            return map.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(Order_WriteActivity.this, R.layout.dfy_tourist_item, null);
            EditText edName = (EditText) v.findViewById(R.id.et_name);
            final TextView tv_card_type = (TextView) v.findViewById(R.id.tv_card_type);
            EditText edCard = (EditText) v.findViewById(R.id.et_card);
            v.setTag(R.id.et_name, edName);
            v.setTag(R.id.tv_card_type, tv_card_type);
            v.setTag(R.id.et_card, edCard);

            tv_card_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPup();
                }

                private void showPup() {
                    View contentView = View.inflate(Order_WriteActivity.this,
                            R.layout.dfy_card_item, null);

                    TextView tv_sfz = (TextView) contentView.findViewById(R.id.tv_sfz);
                    TextView tv_jgz = (TextView) contentView.findViewById(R.id.tv_jgz);
                    TextView tv_hz = (TextView) contentView.findViewById(R.id.tv_hz);
                    TextView tv_gatxz = (TextView) contentView.findViewById(R.id.tv_gatxz);
                    final PopupWindow popupWindow = new PopupWindow(contentView,
                            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
                    tv_sfz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tv_card_type.setText(R.string.dfy_idcard);
                            popupWindow.dismiss();
                        }
                    });

                    tv_jgz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tv_card_type.setText(R.string.dfy_soldier);
                            popupWindow.dismiss();
                        }
                    });
                    tv_hz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tv_card_type.setText(R.string.dfy_passort);
                            popupWindow.dismiss();
                        }
                    });
                    tv_gatxz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tv_card_type.setText(R.string.dfy_gaCard);
                            popupWindow.dismiss();
                        }
                    });

                    popupWindow.setTouchable(true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());//给pop设置一个背景，这样才能在点击其他地方的时候关闭pop
                    popupWindow.showAsDropDown(tv_card_type);

                }
            });

            if (!map.containsKey(i)) {
                map.put(i, v);
            }

            return v;
        }
    }
}
