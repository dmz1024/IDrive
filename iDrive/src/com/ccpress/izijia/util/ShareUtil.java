package com.ccpress.izijia.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.*;
import com.ccpress.izijia.iDriveApplication;
import com.trs.app.TRSApplication;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by WLH on 2015/5/15 10:55.
 */
public class ShareUtil {



    public static void showShare(final Context context, final String docid, final String type, final String title, final String imgurl, final String url, final String content){
        final Dialog dialog = new Dialog(context,R.style.popToCenterDialog);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
        View btnWechat = contentView.findViewById(R.id.btn_wechat);
        View btnWechatMoment = contentView.findViewById(R.id.btn_wechatmoment);
        View btnWeibo = contentView.findViewById(R.id.btn_weibo);
        View btnInside = contentView.findViewById(R.id.btn_share_inside);
        View btnCancel = contentView.findViewById(R.id.btn_cancel);

        btnWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Share(context, Wechat.NAME,title,imgurl,url,content);
            }
        });
        btnWechatMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Share(context, WechatMoments.NAME,title,imgurl,url,content);
            }
        });
        btnWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Share(context, SinaWeibo.NAME,title,imgurl,url,content);
            }
        });
        btnInside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Intent intent = new Intent(context, ReportActivity.class);
                intent.putExtra(InfoDetailActivity.EXTRA_DOCID, docid);
                intent.putExtra(CommentActivity.EXTRA_TYPE, type);
                intent.putExtra(ReportActivity.EXTRA_TITLE, title);
                intent.putExtra(ReportActivity.EXTRA_IS_SHARE_INSIDE, true);
                intent.putExtra(ReportActivity.EXTRA_IMGURL, imgurl);
                intent.putExtra(ReportActivity.EXTRA_URL, url);

                context.startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(contentView);
        dialog.show();
    }

    public static void Share(Context context, String platform, String title, String imgurl, String url, final String content ){

        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        if(platform != null){
            oks.setPlatform(platform);
        }
        //关闭sso授权
       // oks.disableSSOWhenAuthorize();

        String content1 = StringUtil.isEmpty(content) ? context.getResources().getString(R.string.share_content) : content;
        String title1 = StringUtil.isEmpty(title) ? context.getResources().getString(R.string.app_name) : title;
        final String url1 = StringUtil.isEmpty(url) ? "http://www.izijia.cn/":url;
        if(content1.length() > 140){
            content1 = content1.substring(0, 136) + "...";
        }

        // title标题，微信使用
        oks.setTitle(title1);

        // text是分享文本，所有平台都需要这个字段

        if (platform.equals(SinaWeibo.NAME)) {//新浪微博的url是通过content分享
            if (content1.length() >= 138) {
                int urllenth = url1.length();
                oks.setText(content1.substring(0, urllenth - 1) + url1);
                Toast.makeText(context,"11111111",Toast.LENGTH_LONG).show();
            } else {
                oks.setText(content1 + url1);
            }
            Log.e("WLH", "platform text:" + oks.getText());
        }else {
            oks.setText(content1);
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(url1);
        }
        if(!StringUtil.isEmpty(imgurl)){
            // imagePath是图片的url，Linked-In以外的平台都支持此参数
            oks.setImageUrl(imgurl);
        }else {
            oks.setImagePath(getImgpath(context));
        }
        oks.setDialogMode();

        // 启动分享GUI
        oks.show(context);
    }
    public static String getImgpath(Context mContext){
        File f = new File(TRSApplication.app().getCacheDir()+"/" +"logo" + ".png");
        if(f.exists()){
            return f.getPath();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getPath();
    }
}
