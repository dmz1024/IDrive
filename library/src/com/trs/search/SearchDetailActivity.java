package com.trs.search;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.trs.constants.Constants;
import com.trs.frontia.FrontiaAPI;
import com.trs.mobile.R;
import com.trs.types.ListItem;
import com.trs.util.AsyncTask;
import com.trs.util.ImageDownloader;
import com.trs.util.RegularExpression;
import com.trs.util.log.Log;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-7-14.
 */
public class SearchDetailActivity extends Activity{
    private Context mContext = this;
    public static String EXTRA_DATA = "data";
    private static String IMG_START_FLAG = "<IMAGE";
    private static String IMG_START_FLAG_SEARCH = "<img";

    private static String IMG_END_FLAG = ">\n";
    private static String IMG_END_FLAG_SEARCH = "\" /><br/>";

    private static String IMG_URL_START = "http:";
    private View mLoadingView;
    private TextView mTitle,mTime,mSource,mTopTitle;
    private LinearLayout mContent;
    private ListItem listItem;
    private String mData;
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cas_service_detail);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        listItem = (ListItem) getIntent().getExtras().getSerializable(EXTRA_DATA);
        mLoadingView = findViewById(R.id.loading_view);

        initView();
        initDate();
    }

    private void initView(){
        mTitle = (TextView) findViewById(R.id.title);
        mTime = (TextView) findViewById(R.id.time);
        mSource = (TextView) findViewById(R.id.source);
        mContent = (LinearLayout) findViewById(R.id.content);
//        mTopTitle = (TextView) findViewById(R.id.document_title_text);
    }

    private void initDate(){
        mLoadingView.setVisibility(View.VISIBLE);
        soapTask soapTask = new soapTask();
        soapTask.execute();
    }

    class soapTask extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            SoapObject request = new SoapObject(Constants.serviceNameSpace, Constants.detail);
            String paramsStr = listItem.getId()+"|"+listItem.getType()+"|"+listItem.getChannelname()+"|"+listItem.getTitle()+"|"+
                    listItem.getDate()+"|"+listItem.getSource()+"|"+" | ";
            Log.e("params",paramsStr);
            request.addProperty("param",paramsStr);
            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = request;
            (new MarshalBase64()).register(envelope);

            //Android传输对象
            AndroidHttpTransport transport = new AndroidHttpTransport(Constants.serviceURL);
            transport.debug = true;

            //调用
            try {
                transport.call(Constants.serviceNameSpace + Constants.detail, envelope);
                if(envelope.getResponse()!=null){
                    Log.e("xi lan data",envelope.bodyIn.toString());
                    return envelope.bodyIn.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mData = s;
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    mLoadingView.setVisibility(View.GONE);
                    if(mData == null || mData.length() == 0){
                        Toast.makeText(SearchDetailActivity.this,"服务器数据错误!",Toast.LENGTH_LONG).show();
                        return;
                    }
                    String data = mData.substring(24, mData.length() - 3);
                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        if(jsonObject.length() == 0){
                            Toast.makeText(mContext,"服务器数据错误!",Toast.LENGTH_LONG).show();
                            return;
                        }
                        JSONObjectHelper helper = new JSONObjectHelper(jsonObject);
                        String topTitle = helper.getString("title","");
                        mTitle.setText(topTitle);
                        mTime.setText(jsonObject.has("pubTime")?jsonObject.getString("pubTime").split(" ")[0]:"");
                        // 返回数据有两个来源，其一没有sitename字段，便从listitem里面取.
                        mSource.setText(helper.getString("sitename","").equals("")?listItem.getType():helper.getString("sitename",""));
//                        mTopTitle.setText(topTitle.length() > 11 ? topTitle.substring(0,11)+"...":topTitle);

                        // content
                        String contentStr = helper.getString("content","");
                        List<String> imgList = new ArrayList<String>();
                        // ZAKER 财新网 看重庆 三个栏目的数据特殊，去掉html标签直接显示文字内容.
                        if(listItem.getType()!=null &&
                                (listItem.getType().equals("zaker") || listItem.getType().equals("财新网") || listItem.getType().equals("看重庆"))){
                            TextView textView = createContentText(RegularExpression.delHTMLTag(contentStr));
                            mContent.addView(textView);
                        }
                        // 符合有图片规则可以显得的包括搜索出来的细览解析.
                        else if(contentStr.contains(IMG_START_FLAG) || contentStr.contains(IMG_START_FLAG_SEARCH) ){
                            String imgStartFlag = "";
                            String imgEndFlag = "";
                            if(contentStr.contains(IMG_START_FLAG)){
                                imgStartFlag = IMG_START_FLAG;
                                imgEndFlag = IMG_END_FLAG;
                                imgList = createImgData(contentStr,imgStartFlag,imgEndFlag);
                            } else{
                                imgStartFlag = IMG_START_FLAG_SEARCH;
                                imgEndFlag = IMG_END_FLAG_SEARCH;
                                imgList = createImgDataSearch(contentStr, imgStartFlag, imgEndFlag);
                            }
                            // 设置到界面
                            String[] contentArray = imgList.get(imgList.size() - 1).split("\\|");
                            imgList.remove(imgList.size() - 1);

                            for(int i = 0;i < imgList.size();i++){
                                mContent.addView(createContentImage(imgList.get(i)));
                                // 图片下加载一个空白间隔区域.
                                View view = new View(mContext);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                        (int) (metrics.density*10));
                                view.setLayoutParams(params);
                                mContent.addView(view);
                                mContent.addView(createContentText(RegularExpression.delEnder(contentArray[i + 1])));
                            }
                        }
                        // 除了那三个特殊的栏目数据且不包含图片信息的直接显示文字内容.
                        else{
                            TextView textView = createContentText(contentStr);
                            mContent.addView(textView);
                        }
                        // 界面底部增加一块空白区域.
                        View emptyEmpty = new View(mContext);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,30);
                        emptyEmpty.setLayoutParams(params);
                        mContent.addView(emptyEmpty);
                    }catch(Exception e){
                        e.printStackTrace();
                }
            }
        }
    };

    private List<String> createImgData(String contentStr,String imgStartFlag,String imgEndFlag){
        String mImgEndFlag;
        List<String> imgList = new ArrayList<String>();
        do {
            int urlStartIndex = contentStr.indexOf(imgStartFlag);
            if(contentStr.indexOf(imgEndFlag) == -1){
                mImgEndFlag = ">";
            } else{
                mImgEndFlag = imgEndFlag;
            }
            String wcmImgUrl = contentStr.substring(urlStartIndex, contentStr.indexOf(mImgEndFlag) - 2);
            String imgUrl = wcmImgUrl.substring(wcmImgUrl.indexOf(IMG_URL_START), wcmImgUrl.length());
            imgList.add(imgUrl);
            contentStr = contentStr.replace(contentStr.substring(urlStartIndex,contentStr.indexOf(mImgEndFlag) + 1),"|");
        }while (contentStr.contains(imgStartFlag));
        // 最后一个值是需要显示的文本字符.
        imgList.add(contentStr);
        return imgList;
    }

    private List<String> createImgDataSearch(String contentStr,String imgStartFlag,String imgEndFlag){
        List<String> imgList = new ArrayList<String>();
        do {
            int urlStartIndex = contentStr.indexOf(imgStartFlag);
            String wcmImgUrl = contentStr.substring(urlStartIndex, contentStr.indexOf(imgEndFlag));
            String imgUrl = wcmImgUrl.substring(wcmImgUrl.indexOf(IMG_URL_START), wcmImgUrl.length());
            imgList.add(imgUrl);
            contentStr = contentStr.replace(contentStr.substring(urlStartIndex,contentStr.indexOf(imgEndFlag) + 4),"|");
        }while (contentStr.contains(imgStartFlag));
        // 最后一个值是需要显示的文本字符.
        imgList.add(contentStr);
        return imgList;
    }

    private RelativeLayout createContentImage(String imgUrl){
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.cqsw_detail_image,null);
        ImageView imageView = (ImageView)relativeLayout.findViewById(R.id.img);
        imageView.setBackgroundResource(R.drawable.cqsw_default_pic);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (metrics.widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        params.setMargins(0,10,0,100);
        new ImageDownloader.Builder().
                setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                build(imgUrl, imageView).start();
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(params);
        return relativeLayout;
    }

    private TextView createContentText(String textContent){
        TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.cqsw_detail_text,null);
        textView.setText(textContent);
        textView.setTextSize(15);
        textView.setPadding(11,5,7,0);
        return textView;
    }

    public void onBtnBackClick(View view){
        finish();
    }

    public void BtnShareClick(View view){
        if(mData == null || mData.length() == 0){
            return;
        }
        String content = listItem.getSummary().length() > 20 ? listItem.getSummary().substring(0,18)+"...":listItem.getSummary();
//        GoShare share = new GoShare(SearchDetailActivity.this,listItem.getTitle(),content,listItem.getUrl(),listItem.getImgUrl());
//        share.shareStart();

        FrontiaAPI frontia = FrontiaAPI.getInstance(getApplicationContext());
        frontia.goShare(this, listItem.getTitle(), content, listItem.getUrl(), Uri.parse(listItem.getImgUrl()));
    }
}
