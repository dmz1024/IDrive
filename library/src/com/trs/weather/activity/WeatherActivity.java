package com.trs.weather.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.trs.app.TRSFragmentActivity;
import com.trs.mobile.R;
import com.trs.util.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wubingqian on 14-3-3.
 */
public class WeatherActivity extends TRSFragmentActivity{
    private String mJsonCity = "http://61.4.185.48:81/g/";
    private String mJsonUrl = "http://m.weather.com.cn/data/101270101.html";
    private Map<String,String> mWeatherDataMap = new HashMap<String, String>();
    private Map<String,String[]> mWeatherGatherMap = new HashMap<String, String[]>();
    private Task weatherTask;

    private TextView mCiry,mTodayTemperature,mTodayTemperatureDesc,mTodayWind;
    private ImageView mTodayImg;

    private TextView mWeek1,mWeek2,mWeek3;
    private ImageView mImg1,mImg2,mImg3;
    private TextView mTemp1,mTemp2,mTemp3;
    private TextView mDesc1,mDesc2,mDesc3;
    private TextView mWind1,mWind2,mWind3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.weather);

        initViewParams();

        weatherTask = new Task();
        weatherTask.execute();
    }

    private void initViewParams(){
        mCiry = (TextView)findViewById(R.id.city);

        mTodayTemperature = (TextView)findViewById(R.id.temperatureTextId);
        mTodayTemperatureDesc = (TextView)findViewById(R.id.temperatureDescTextId);
        mTodayWind = (TextView)findViewById(R.id.windTextId);
        mTodayImg = (ImageView)findViewById(R.id.todayImgId);

        mWeek1 = (TextView) findViewById(R.id.week1);
        mWeek2 = (TextView) findViewById(R.id.week2);
        mWeek3 = (TextView) findViewById(R.id.week3);
        mImg1 = (ImageView) findViewById(R.id.icon1);
        mImg2 = (ImageView) findViewById(R.id.icon2);
        mImg3 = (ImageView) findViewById(R.id.icon3);
        mTemp1 = (TextView)findViewById(R.id.temperatureText1);
        mTemp2 = (TextView)findViewById(R.id.temperatureText2);
        mTemp3 = (TextView)findViewById(R.id.temperatureText3);
        mDesc1 = (TextView)findViewById(R.id.temperatureDescText1);
        mDesc2 = (TextView)findViewById(R.id.temperatureDescText2);
        mDesc3 = (TextView)findViewById(R.id.temperatureDescText3);
        mWind1 = (TextView)findViewById(R.id.windText1);
        mWind2 = (TextView)findViewById(R.id.windText2);
        mWind3 = (TextView)findViewById(R.id.windText3);
    }

    private void dataFactory(String data) throws JSONException{
        mWeatherDataMap.clear();
        JSONObject objAll = new JSONObject(data);
        JSONObject obj = objAll.getJSONObject("weatherinfo");
        // 城市名称
        String city = obj.getString("city");
        mWeatherDataMap.put("city",city);
        // 城市拼音
        String cityPinyin = obj.getString("city_en");
        mWeatherDataMap.put("cityPinyin",cityPinyin);
        // 日期
        String date = obj.getString("date_y");
        mWeatherDataMap.put("date_y",date);
        // 星期几
        String week = obj.getString("week");
        mWeatherDataMap.put("week",week);
        // 今天穿衣指数
        String dressIndex = obj.getString("index");
        mWeatherDataMap.put("dressIndex",dressIndex);
        // 今天穿衣建议。
        String dressIndexDesc = obj.getString("index_d");
        mWeatherDataMap.put("dressIndexDesc",dressIndexDesc);
        // 48小时内穿衣指数。
        String dressIndex48 = obj.getString("index48");
        mWeatherDataMap.put("dressIndex48",dressIndex48);
        // 48小时内穿衣指数描述。
        String dressIndexDesc48 = obj.getString("index48_d");
        mWeatherDataMap.put("dressIndexDesc48",dressIndexDesc48);
        // 今天紫外线指数。
        String ultravioletRay = obj.getString("index_uv");
        mWeatherDataMap.put("ultravioletRay",ultravioletRay);
        // 48小时内紫外线指数。
        String ultravioletRay48 = obj.getString("index48_uv");
        mWeatherDataMap.put("ultravioletRay48",ultravioletRay48);
        // 洗车
        String cleanCar = obj.getString("index_xc");
        mWeatherDataMap.put("cleanCar",cleanCar);
        // 旅游
        String travelIndex = obj.getString("index_tr");
        mWeatherDataMap.put("travelIndex",travelIndex);
        // 舒适指数
        String comfortableIndex = obj.getString("index_co");
        mWeatherDataMap.put("comfortableIndex",comfortableIndex);
        // 晨练
        String morningExerciseIndex = obj.getString("index_cl");
        mWeatherDataMap.put("morningExerciseIndex",morningExerciseIndex);
        // 晾晒
        String AirIndex = obj.getString("index_ls");
        mWeatherDataMap.put("AirIndex",AirIndex);
        // 过敏
        String allergicIndex = obj.getString("index_ag");
        mWeatherDataMap.put("allergicIndex",allergicIndex);

        mWeatherGatherMap.clear();
        // 一周内摄氏度从今年天算起。
        String[] weekTemC = new String[]{obj.getString("temp1"),obj.getString("temp2"),obj.getString("temp3"),
                obj.getString("temp4"),obj.getString("temp5"),obj.getString("temp6")};
        mWeatherGatherMap.put("weekTemC",weekTemC);
        // 一周内华氏度从今天算起。
        String[] weekTemF = new String[]{obj.getString("tempF1"),obj.getString("tempF2"),obj.getString("tempF3"),
                obj.getString("tempF4"),obj.getString("tempF5"),obj.getString("tempF6")};
        mWeatherGatherMap.put("weekTemF",weekTemF);
        // 一周内天气描述从今天算起。
        String[] weekDesc = new String[]{obj.getString("weather1"),obj.getString("weather2"),obj.getString("weather3"),
                obj.getString("weather4"),obj.getString("weather5"),obj.getString("weather6")};
        mWeatherGatherMap.put("weekDesc",weekDesc);
        String[] windDesc = new String[]{obj.getString("wind1"),obj.getString("wind2"),obj.getString("wind3"),
                obj.getString("wind4"),obj.getString("wind5"),obj.getString("wind6")};
        mWeatherGatherMap.put("windDesc",windDesc);
    }

    private void showView(){
        mCiry.setText(mWeatherDataMap.get("city"));                                           // 城市名

        View mWeatherMainLayout = findViewById(R.id.weatherLayoutId);
        char s = mWeatherDataMap.get("date_y").replaceAll("\\[a-zA-Z\\]","").charAt(5);       // main background
        String resourceIdStr = "weather_" + s;
        Resources resource = getResources();
        int resourceId = resource.getIdentifier(resourceIdStr,"drawable",getPackageName());
        mWeatherMainLayout.setBackgroundResource(resourceId);

        mTodayImg.setBackgroundResource(                                                      // today picture.
                resource.getIdentifier(getIconFromText(mWeatherGatherMap.get("weekDesc")[0]), "drawable", getPackageName()));
        mTodayTemperature.setText(mWeatherGatherMap.get("weekTemC")[0]);                      // today tempareture
        mTodayTemperatureDesc.setText(mWeatherGatherMap.get("weekDesc")[0]);                  // today tempareture desc.
        mTodayWind.setText(mWeatherGatherMap.get("windDesc")[0]);                             // today wind desc.

        mWeek1.setText(getWeekFromText(mWeatherDataMap.get("week"),false,false));                                                              // 星期
        mWeek2.setText(getWeekFromText(mWeatherDataMap.get("week"),true,false));
        mWeek3.setText(getWeekFromText(mWeatherDataMap.get("week"),false,true));
        mImg1.setBackgroundResource(                                                          // 图片
                resource.getIdentifier(getIconFromText(mWeatherGatherMap.get("weekDesc")[1]),"drawable",getPackageName())
        );
        mImg2.setBackgroundResource(
                resource.getIdentifier(getIconFromText(mWeatherGatherMap.get("weekDesc")[2]),"drawable",getPackageName())
        );
        mImg3.setBackgroundResource(
                resource.getIdentifier(getIconFromText(mWeatherGatherMap.get("weekDesc")[3]),"drawable",getPackageName())
        );
        mTemp1.setText(mWeatherGatherMap.get("weekTemC")[1]);                                 // 温度
        mTemp2.setText(mWeatherGatherMap.get("weekTemC")[2]);
        mTemp3.setText(mWeatherGatherMap.get("weekTemC")[3]);
        mDesc1.setText(mWeatherGatherMap.get("weekDesc")[1]);                                 // 温度描述
        mDesc2.setText(mWeatherGatherMap.get("weekDesc")[2]);
        mDesc3.setText(mWeatherGatherMap.get("weekDesc")[3]);
        mWind1.setText(mWeatherGatherMap.get("windDesc")[1]);                                 // 风力描述
        mWind2.setText(mWeatherGatherMap.get("windDesc")[2]);
        mWind3.setText(mWeatherGatherMap.get("windDesc")[3]);
    }

    private String getIconFromText(String weatherString){
        if(weatherString.contains("转")){
            String temp = weatherString.replace("转","$");
            weatherString = temp.split("\\$")[0];
        }
        String returnStr = "icon_weather_";
        String resourceStr = "";

        if(weatherString.equals("晴")){
            resourceStr = returnStr + "clear";
        }
        if(weatherString.equals("多云"))
        {
            resourceStr = returnStr + "cloudy";
        }
        if(weatherString.equals("阴"))
        {
            resourceStr = returnStr + "overcast";
        }

        if(weatherString.equals("雾"))
        {
            resourceStr = returnStr + "fog";
        }

        if(weatherString.equals("阵雨"))
        {
            resourceStr = returnStr + "showers";
        }
        if(weatherString.equals("雷阵雨"))
        {
            resourceStr = returnStr + "thunderstorm";
        }
        if(weatherString.equals("雨夹雪"))
        {
            resourceStr = returnStr + "rainandsnow";
        }
        if(weatherString.equals("小雨"))
        {
            resourceStr = returnStr + "lightrain";
        }
        if(weatherString.equals("中雨") || weatherString.equals("大雨"))
        {
            resourceStr = returnStr + "rain";
        }
        if(weatherString.equals("暴雨")||weatherString.equals("大暴雨")||weatherString.equals("特大暴雨"))
        {
            resourceStr = returnStr + "storm";
        }
        if(weatherString.equals("阵雪"))
        {
            resourceStr = returnStr + "scatteredsnow";
        }
        if(weatherString.equals("小雪"))
        {
            resourceStr = returnStr + "flurries";
        }
        if(weatherString.equals("中雪"))
        {
            resourceStr = returnStr + "icesnow";
        }
        if(weatherString.equals("大雪"))
        {
            resourceStr = returnStr + "snow";
        }
        if(weatherString.equals("暴雪"))
        {
            resourceStr = returnStr + "icy";
        }
        return resourceStr.length() == 0 ? "icon_weather_clear" :resourceStr;
    }

    //fasle false ; true false ;false true
    private String getWeekFromText(String currWeekString,boolean plus,boolean doublePlus){
        String temp = currWeekString.replace("星期","");
        if(temp.equals("一")){
           return plus ? "星期三" :(doublePlus ? "星期四" : "星期二");
        }
        if(temp.equals("二")){
            return plus ? "星期四" :(doublePlus ? "星期五" : "星期三");
        }
        if(temp.equals("三")){
            return plus ? "星期五" :(doublePlus ? "星期六" : "星期四");
        }
        if(temp.equals("四")){
            return plus ? "星期六" :(doublePlus ? "星期天" : "星期五");
        }
        if(temp.equals("五")){
            return plus ? "星期天" :(doublePlus ? "星期一" : "星期六");
        }
        if(temp.equals("六")){
            return plus ? "星期一" :(doublePlus ? "星期二" : "星期天");
        }
        if(temp.equals("天")){
            return plus ? "星期二" :(doublePlus ? "星期三" : "星期一");
        }
        return "";
    }

    class Task extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try{
                HttpGet get = new HttpGet(mJsonUrl);
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if(result == null || result.length() == 0){
                    Toast.makeText(WeatherActivity.this,"net error!",Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    dataFactory(result);
                    showView();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
