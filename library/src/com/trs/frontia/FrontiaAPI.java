package com.trs.frontia;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaUser;
import com.baidu.frontia.api.*;
import com.trs.util.StringUtil;
import net.endlessstudio.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Wu Jingyu
 * Date: 2014/9/28
 * Time: 16:20
 */
public class FrontiaAPI {
    private static FrontiaAPI instance = null;
    private FrontiaPush mPush;
    private FrontiaSocialShare mSocialShare;
    private Activity mShareContext;
    private FrontiaAuthorization mAuthorization;

    public static FrontiaAPI getInstance(Context ctx) {
        if(instance == null){
            instance = new FrontiaAPI(ctx);
        }
        return instance;
    }
    private FrontiaAPI(Context ctx) {
        String apiKey = getMetaValue(ctx, "api_key");
        if(!StringUtil.isEmpty(apiKey)) {
            Frontia.init(ctx, apiKey);
        }

        //初始化授权
        mAuthorization = Frontia.getAuthorization();

        //初始化推送
        mPush = Frontia.getPush();
        SharedPreferences sp = ctx.getSharedPreferences("Push_Service", Context.MODE_PRIVATE);
        Boolean isPush = sp.getBoolean("PushService", false);
        if(isPush) {
            if(isPushWorking()){
                resumePush();
            } else {
                startPush();
            }
        } else {
            stopPush();
        }
        sp.edit().putBoolean("PushService", isPush).commit();

        //初始化分享
        mSocialShare = Frontia.getSocialShare();
        try {
            String objStr = Util.getString(ctx, "file://android_asset/config.json", "utf-8");
            JSONObject mShareObject = new JSONObject(objStr);
            mSocialShare.setClientName(FrontiaAuthorization.MediaType.QQFRIEND.toString(),
                    mShareObject.getString("app_name"));
            JSONArray arr = mShareObject.getJSONArray("app_key");
            for(int i=0; i<arr.length(); i++){
                JSONObject obj = arr.getJSONObject(i);
                Iterator it = obj.keys();
                while(it.hasNext()){
                    String key = (String) it.next();
                    String value = obj.getString(key);
                    mSocialShare.setClientId(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void resumePush() {
        mPush.resume();
    }

    public void startPush() {
        mPush.start();
    }

    public void stopPush() {
        mPush.stop();
    }

    public boolean isPushWorking() {
        return mPush.isPushWorking();
    }

    public void goShare(Context ctx, String title, String content, String linkUrl, Uri imgUrl) {
        mSocialShare.setContext(ctx);
        mShareContext = (Activity) ctx;
        FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();
        mImageContent.setTitle(title);
        mImageContent.setContent(content);
        mImageContent.setLinkUrl(linkUrl);
        mImageContent.setImageUri(imgUrl);
        mSocialShare.show(mShareContext.getWindow().getDecorView(), mImageContent,
                FrontiaSocialShare.FrontiaTheme.DARK, new ShareListener());
    }

    private class ShareListener implements FrontiaSocialShareListener {
        @Override
        public void onSuccess() {
            Toast.makeText(mShareContext, "分享成功", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onFailure(int errCode, String errMsg) {
            Toast.makeText(mShareContext, "分享失败: " + errMsg, Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCancel() {
            Toast.makeText(mShareContext, "取消分享", Toast.LENGTH_LONG).show();
        }
    }

    public void clearAllAuthorizationInfos() {
        mAuthorization.clearAllAuthorizationInfos();
    }

    public void clearAuthorizationInfo(String mediaType) {
        mAuthorization.clearAuthorizationInfo(mediaType);
    }

    public boolean isAuthorizationReady(String mediatype) {
        return mAuthorization.isAuthorizationReady(mediatype);
    }

    public void authorize(final Activity activity, String mediaType,
                          FrontiaAuthorizationListener.AuthorizationListener listener) {
        mAuthorization.authorize(activity, mediaType, listener);
    }

    // 获取ApiKey
    private String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return apiKey;
    }
}
