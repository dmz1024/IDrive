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
public class GDLocationUtil {
    public static void setGDGpsLocation(Context ctx, AMapLocation location){
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.GD_SP_LOCATION_GPS_CITY_CODE, location.getCityCode());
            editor.putString(Constant.GD_SP_LOCATION_GPS_CITY, location.getCity());
            editor.putString(Constant.GD_SP_LOCATION_GPS_PROVINCE, location.getProvince());
            editor.commit();
        }
    }

    public static String getGDGpsCityCode(Context ctx) {
        String cityCode = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            cityCode = sharedPref.getString(Constant.GD_SP_LOCATION_GPS_CITY_CODE,
                    Constant.GD_SP_LOCATION_CURRENT_SET_CITY_CODE_DEFAULT);
        }
        return cityCode;
    }

    public static String getGDGpsCity(Context ctx){
        String city = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            city = sharedPref.getString(Constant.GD_SP_LOCATION_GPS_CITY,
                    Constant.GD_SP_LOCATION_CURRENT_SET_CITY_DEFAULT);
        }
        return city;
    }
    public static String getGDGCity(Context ctx){
        String city = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            city = sharedPref.getString(Constant.GD_SP_LOCATION_GPS_CITY,
                    Constant.GD_SP_LOCATION_CURRENT_SET_CITY_DEFAULT);
        }
        return city;
    }

    public static String getGDGpsProvince(Context ctx) {
        String province = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            province = sharedPref.getString(Constant.GD_SP_LOCATION_GPS_PROVINCE,
                    Constant.GD_SP_LOCATION_CURRENT_SET_PROVINCE_DEFAULT);
        }
        return province;
    }

    public static String getGDGpsProvinceCode(Context ctx) {
        String provinceCode = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            provinceCode = sharedPref.getString(Constant.SP_LOCATION_GPS_PROVINCE_CODE,
                    Constant.GD_SP_LOCATION_CURRENT_SET_PROVINCE_CODE_DEFAULT);
        }
        return provinceCode;
    }

    public static void setGDCurrentLocation(Context ctx, CityEntity entity) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_CITY_CODE, entity.getCode());
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_CITY, entity.getName());
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_PROVINCE, entity.getProvince());
            editor.commit();
        }
    }

    public static void setGDCurrentSetCity(Context ctx, String city) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_CITY, city);
            editor.commit();
        }
    }

    public static void setGDCurrentSetCityCode(Context ctx, String cityCode) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_CITY_CODE, cityCode);
            editor.commit();
        }
    }

    public static void setGDCurrentSetProvince(Context ctx, String province) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_PROVINCE, province);
            editor.commit();
        }
    }

    public static void setGDCurrentSetProvinceCode(Context ctx, String provinceCode) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_PROVINCE_CODE, provinceCode);
            editor.commit();
        }
    }

    public static String getGDCurrentSetCityCode(Context ctx) {
        String cityCode = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            cityCode = sharedPref.getString(Constant.GD_SP_LOCATION_CURRENT_SET_CITY_CODE,
                    "");
        }
        return cityCode;
    }

    public static String getGDCurrentSetCity(Context ctx) {
        String city = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            city = sharedPref.getString(Constant.GD_SP_LOCATION_CURRENT_SET_CITY,
                    "");
        }
        return city;
    }

    public static String getGDCurrentSetProvince(Context ctx) {
        String province = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            province = sharedPref.getString(Constant.GD_SP_LOCATION_CURRENT_SET_PROVINCE,
                    "");
        }
        return province;
    }

    public static String getGDCurrentSetProvinceCode(Context ctx) {
        String province = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            province = sharedPref.getString(Constant.GD_SP_LOCATION_CURRENT_SET_PROVINCE_CODE,
                    "");
        }
        return province;
    }

    public static void clearGDCurrentLocation(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constant.GD_SP_LOCATION, Context.MODE_PRIVATE);
        if(sharedPref != null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_CITY_CODE, "");
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_CITY, "");
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_PROVINCE, "");
            editor.putString(Constant.GD_SP_LOCATION_CURRENT_SET_PROVINCE_CODE, "");
            editor.commit();
        }
    }


    public static String getGDProvinceCode(Context ctx) {
        String provinceCode = "";
        String provinceCode_sp = GDLocationUtil.getGDCurrentSetProvinceCode(ctx);
        if(provinceCode_sp.equals("")){
            provinceCode = GDLocationUtil.getGDGpsProvinceCode(ctx);
        } else {
            provinceCode = provinceCode_sp;
        }

        return provinceCode;
    }

    public static String getGDProvince(Context ctx) {
        String province = "";
        String province_sp = GDLocationUtil.getGDCurrentSetProvince(ctx);
        if(province_sp.equals("")){
            province = GDLocationUtil.getGDGpsProvince(ctx);
        } else {
            province = province_sp;
        }

        return province;
    }

    public static String getGDCityCode(Context ctx) {
        String cityCode = "";
        String cityCode_sp = GDLocationUtil.getGDCurrentSetCityCode(ctx);
        if(cityCode_sp.equals("")){
            cityCode = GDLocationUtil.getGDGpsCityCode(ctx);
        } else {
            cityCode = cityCode_sp;
        }

        return cityCode;
    }

    public static String getGDCity(Context ctx) {
        String city = "";
        String city_sp = GDLocationUtil.getGDCurrentSetCity(ctx);
        if(city_sp.equals("")){
            city = GDLocationUtil.getGDGpsCity(ctx);
        } else {
            city = city_sp;
        }

        return city;
    }


}
