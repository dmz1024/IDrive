package com.ccpress.izijia.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.amap.api.location.AMapLocation;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.entity.CityEntity;

/**
 * Created by Wu Jingyu
 * Date: 2015/9/8
 * Time: 10:27
 */
public class LocationUtil {
    public static void setGpsLocation(Context ctx, AMapLocation location){
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.SP_LOCATION_GPS_CITY_CODE, location.getCityCode());
            editor.putString(Constant.SP_LOCATION_GPS_CITY, location.getCity());
            editor.putString(Constant.SP_LOCATION_GPS_PROVINCE, location.getProvince());
            editor.commit();
        }
    }

    public static String getGpsCityCode(Context ctx) {
        String cityCode = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            cityCode = sharedPref.getString(Constant.SP_LOCATION_GPS_CITY_CODE,
                    Constant.SP_LOCATION_CURRENT_SET_CITY_CODE_DEFAULT);
        }
        return cityCode;
    }

    public static String getGpsCity(Context ctx){
        String city = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            city = sharedPref.getString(Constant.SP_LOCATION_GPS_CITY,
                    Constant.SP_LOCATION_CURRENT_SET_CITY_DEFAULT);
        }
        return city;
    }
    public static String getGCity(Context ctx){
        String city = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            city = sharedPref.getString(Constant.SP_LOCATION_GPS_CITY,
                    Constant.SP_LOCATION_CURRENT_SET_CITY_DEFAULT);
        }
        return city;
    }

    public static String getGpsProvince(Context ctx) {
        String province = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            province = sharedPref.getString(Constant.SP_LOCATION_GPS_PROVINCE,
                    Constant.SP_LOCATION_CURRENT_SET_PROVINCE_DEFAULT);
        }
        return province;
    }

    public static String getGpsProvinceCode(Context ctx) {
        String provinceCode = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            provinceCode = sharedPref.getString(Constant.SP_LOCATION_GPS_PROVINCE_CODE,
                    Constant.SP_LOCATION_CURRENT_SET_PROVINCE_CODE_DEFAULT);
        }
        return provinceCode;
    }

    public static void setCurrentLocation(Context ctx, CityEntity entity) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_CITY_CODE, entity.getCode());
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_CITY, entity.getName());
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_PROVINCE, entity.getProvince());
            editor.commit();
        }
    }

    public static void setCurrentSetCity(Context ctx, String city) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_CITY, city);
            editor.commit();
        }
    }

    public static void setCurrentSetCityCode(Context ctx, String cityCode) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_CITY_CODE, cityCode);
            editor.commit();
        }
    }

    public static void setCurrentSetProvince(Context ctx, String province) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_PROVINCE, province);
            editor.commit();
        }
    }

    public static void setCurrentSetProvinceCode(Context ctx, String provinceCode) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_PROVINCE_CODE, provinceCode);
            editor.commit();
        }
    }

    public static String getCurrentSetCityCode(Context ctx) {
        String cityCode = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            cityCode = sharedPref.getString(Constant.SP_LOCATION_CURRENT_SET_CITY_CODE,
                    "");
        }
        return cityCode;
    }

    public static String getCurrentSetCity(Context ctx) {
        String city = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            city = sharedPref.getString(Constant.SP_LOCATION_CURRENT_SET_CITY,
                    "");
        }
        return city;
    }

    public static String getCurrentSetProvince(Context ctx) {
        String province = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            province = sharedPref.getString(Constant.SP_LOCATION_CURRENT_SET_PROVINCE,
                    "");
        }
        return province;
    }

    public static String getCurrentSetProvinceCode(Context ctx) {
        String province = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            province = sharedPref.getString(Constant.SP_LOCATION_CURRENT_SET_PROVINCE_CODE,
                    "");
        }
        return province;
    }

    public static void clearCurrentLocation(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_CITY_CODE, "");
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_CITY, "");
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_PROVINCE, "");
            editor.putString(Constant.SP_LOCATION_CURRENT_SET_PROVINCE_CODE, "");
            editor.commit();
        }
    }


    public static String getProvinceCode(Context ctx) {
        String provinceCode = "";
        String provinceCode_sp = LocationUtil.getCurrentSetProvinceCode(ctx);
        if(provinceCode_sp.equals("")){
            provinceCode = LocationUtil.getGpsProvinceCode(ctx);
        } else {
            provinceCode = provinceCode_sp;
        }

        return provinceCode;
    }

    public static String getProvince(Context ctx) {
        String province = "";
        String province_sp = LocationUtil.getCurrentSetProvince(ctx);
        if(province_sp.equals("")){
            province = LocationUtil.getGpsProvince(ctx);
        } else {
            province = province_sp;
        }

        return province;
    }

    public static String getCityCode(Context ctx) {
        String cityCode = "";
        String cityCode_sp = LocationUtil.getCurrentSetCityCode(ctx);
        if(cityCode_sp.equals("")){
            cityCode = LocationUtil.getGpsCityCode(ctx);
        } else {
            cityCode = cityCode_sp;
        }

        return cityCode;
    }

    public static String getCity(Context ctx) {
        String city = "";
        String city_sp = LocationUtil.getCurrentSetCity(ctx);
        if(city_sp.equals("")){
            city = LocationUtil.getGpsCity(ctx);
        } else {
            city = city_sp;
        }

        return city;
    }


}
