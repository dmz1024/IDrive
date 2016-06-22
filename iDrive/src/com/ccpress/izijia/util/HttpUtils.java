package com.ccpress.izijia.util;

import com.ccpress.izijia.constant.Const;
import com.froyo.commonjar.utils.SpUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.trs.util.log.Log;
/**
 * Created by Administrator on 2015/10/14.
 */
  public class HttpUtils {

        public static String newGroup(String urlpath,String gname,String token,int uid){

            HttpURLConnection conn=null;
            try {
                URL url=new URL(urlpath);
                conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);
                //post请求的参数
                String data="gname="+gname+"&token="+token+"&uid="+uid;
                OutputStream out=conn.getOutputStream();
                out.write(data.getBytes());
                out.flush();
                out.close();
                conn.connect();
                int code=conn.getResponseCode();
                if(code==200){
                    InputStream is=conn.getInputStream();
                    String state=getStringFromInputStream(is);
                    return state;
                }



            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                if(conn!=null){
                    conn.disconnect();
                }
            }
            return null;
        }
    //获取分组成员
    public static String getUser(String urlpath,int gid,String token,int uid){

        HttpURLConnection conn=null;
        try {
            URL url=new URL(urlpath);
            conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            //post请求的参数
            String data="gid="+gid+"&token="+token+"&uid="+uid;
            OutputStream out=conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();
            int code=conn.getResponseCode();
            if(code==200){
                InputStream is=conn.getInputStream();
                String state=getStringFromInputStream(is);
                return state;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }
      //删除分组
    public static String delGroup(String urlpath,int gid,String token,int uid){

        HttpURLConnection conn=null;
        try {
            URL url=new URL(urlpath);
            conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            //post请求的参数
            String data="gid="+gid+"&token="+token+"&uid="+uid;
            OutputStream out=conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();
            int code=conn.getResponseCode();
            if(code==200){
                InputStream is=conn.getInputStream();
                String state=getStringFromInputStream(is);
                return state;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }

    //删除分组成员
    public static String delUser(String urlpath,int gid,int mid,String token,int uid){

        HttpURLConnection conn=null;
        try {
            URL url=new URL(urlpath);
            conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            //post请求的参数
            String data="gid="+gid+"&mid="+mid+"&token="+token+"&uid="+uid;
            OutputStream out=conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();
            int code=conn.getResponseCode();
            if(code==200){
                InputStream is=conn.getInputStream();
                String state=getStringFromInputStream(is);
                Log.e(state, "dsssssssssssssssssssssssssssssssssssssssssss ");
                return state;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }
//新消息提醒状态更新

    public static String editSwtichGroupMessage(String urlpath,int gid,String token,int uid){

        HttpURLConnection conn=null;
        try {
            URL url=new URL(urlpath);
            conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            //post请求的参数
            String data="gid="+gid+"&token="+token+"&uid="+uid;
            OutputStream out=conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();
            int code=conn.getResponseCode();
            if(code==200){
                InputStream is=conn.getInputStream();
                String state=getStringFromInputStream(is);

                return state;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }
    //编辑分组
    public static String editGroup(String urlpath,String gname,int gid,String token,int uid){

        HttpURLConnection conn=null;
        try {
            URL url=new URL(urlpath);
            conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            //post请求的参数
            String data="gid="+gid+"&gname="+gname+"&gid="+gid+"&token="+token+"&uid="+uid;
            OutputStream out=conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();
            int code=conn.getResponseCode();
            if(code==200){
                InputStream is=conn.getInputStream();
                String state=getStringFromInputStream(is);
                return state;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }
    //添加分组成员
    public static String addUser(String urlpath,int gid,int mid,String token,int uid){

        HttpURLConnection conn=null;
        try {
            URL url=new URL(urlpath);
            conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            //post请求的参数
            String data="gid="+gid+"&mid="+mid+"&token="+token+"&uid="+uid;
            OutputStream out=conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();
            int code=conn.getResponseCode();
            if(code==200){
                InputStream is=conn.getInputStream();
                String state=getStringFromInputStream(is);
                return state;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }

    //删除我的帖子
    public static String delInteraction(String urlpath,int id,String token,int uid){

        HttpURLConnection conn=null;
        try {
            URL url=new URL(urlpath);
            conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            //post请求的参数
            String data="id="+id +"&token="+token+"&uid="+uid;
            OutputStream out=conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();
            int code=conn.getResponseCode();
            if(code==200){
                InputStream is=conn.getInputStream();
                String state=getStringFromInputStream(is);
                return state;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * 根据输入流返回一个字符串
     * @param is
     * @return
     * @throws Exception
     */
    private static String getStringFromInputStream(InputStream is) throws Exception{

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] buff=new byte[1024];
        int len=-1;
        while((len=is.read(buff))!=-1){
            baos.write(buff, 0, len);
        }
        is.close();
        String html=baos.toString();
        baos.close();
        return html;
    }
  }
