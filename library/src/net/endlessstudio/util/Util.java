package net.endlessstudio.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.FloatMath;
import net.endlessstudio.util.httpclient.FilePart;
import net.endlessstudio.util.httpclient.MultipartEntity;
import net.endlessstudio.util.httpclient.Part;
import net.endlessstudio.util.httpclient.StringPart;
import net.sf.jmimemagic.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by John on 13-6-24.
 */
public class Util {

	public static InputStream getRawStream(Context context, String rawName) throws IOException {

		int id = context.getResources().getIdentifier(rawName, "raw", context.getPackageName());
		if (id != 0) {
            try{
                return context.getResources().openRawResource(id);
            } catch (Exception e){
                e.printStackTrace();
            }
		}

		throw new IOException(String.format("raw of id: %s from %s not found", id, rawName));
	}

	public static InputStream getDrawableStream(Context context, String rawName) throws IOException {

		int id = context.getResources().getIdentifier(rawName, "drawable", context.getPackageName());
		if (id != 0) {
			BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(id);
			Bitmap bitmap = drawable.getBitmap();

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
			return new ByteArrayInputStream(os.toByteArray());
		}

		throw new IOException(String.format("bitmap of id: %s from %s not found", id, rawName));
	}


	public static String readFile(String path, String encoding) throws IOException {
		InputStream is = getFileStream(path);
		String result = readStreamString(is, encoding);
		is.close();

		return result;
	}

	public static InputStream getFileStream(String path) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		return fis;
	}

	public static InputStream getAssetsStream(Context context, String path) throws IOException {
		InputStream is = context.getAssets().open(path);
		return is;
	}

	public static String readStreamString(InputStream is, String encoding) throws IOException {
		return 	new String(readStream(is), encoding);
	}

	public static byte[] readStream(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024 * 10];
		int readlen;
		while ((readlen = is.read(buf)) >= 0) {
			baos.write(buf, 0, readlen);
		}

		baos.close();

		return baos.toByteArray();
	}

	private static final int HTTP_CONNECTION_TIMEOUT = 4 * 1000;
	private static final int HTTP_READ_TIMEOUT = HTTP_CONNECTION_TIMEOUT * 2;

	public static String readHttp(String url, String encoding) throws IOException {
		InputStream is = getHttpStream(url);
		String result = readStreamString(is, encoding);
		is.close();

		return result;

	}

	public static InputStream getHttpStream(String url) throws IOException {
		URLConnection conn = new URL(url).openConnection();

		conn.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
		conn.setReadTimeout(HTTP_READ_TIMEOUT);
		conn.connect();
		InputStream is = conn.getInputStream();

		return is;
	}

	private static final String HTTP_PREFIX = "http://";
	private static final String ASSETS_PREFIX = "file://android_assets/";
	private static final String ASSETS_PREFIX2 = "file://android_asset/";
	private static final String ASSETS_PREFIX3 = "assets://";
	private static final String ASSETS_PREFIX4 = "asset://";
	private static final String RAW_PREFIX = "file://android_raw/";
	private static final String RAW_PREFIX2 = "raw://";
	private static final String FILE_PREFIX = "file://";
	private static final String DRAWABLE_PREFIX = "drawable://";

	/**
	 * Get a string from url
	 *
	 * @param url      supported url:
	 *                 http - http://xxx
	 *                 asset file - file://android_asset/xxx
	 *                 raw file - file://android_raw/xxx
	 *                 file - file://xxx
	 * @param encoding
	 * @return
	 */
	public static String getString(Context context, String url, String encoding) throws IOException {
		String result = readStreamString(getStream(context, url), encoding);

		if (result.startsWith("\ufeff")) {
			result = result.substring(1);
		}

		return result;
	}

	public static InputStream getStream(Context context, String url) throws IOException {
		String lowerUrl = url.toLowerCase();

		InputStream is;
		if (lowerUrl.startsWith(HTTP_PREFIX)) {
			is = getHttpStream(url);
		} else if (lowerUrl.startsWith(ASSETS_PREFIX)) {
			String assetPath = url.substring(ASSETS_PREFIX.length());
			is = getAssetsStream(context, assetPath);
		} else if (lowerUrl.startsWith(ASSETS_PREFIX2)) {
			String assetPath = url.substring(ASSETS_PREFIX2.length());
			is = getAssetsStream(context, assetPath);
		} else if (lowerUrl.startsWith(ASSETS_PREFIX3)) {
			String assetPath = url.substring(ASSETS_PREFIX3.length());
			is = getAssetsStream(context, assetPath);
		} else if (lowerUrl.startsWith(ASSETS_PREFIX4)) {
			String assetPath = url.substring(ASSETS_PREFIX4.length());
			is = getAssetsStream(context, assetPath);
		} else if (lowerUrl.startsWith(RAW_PREFIX)) {
			String rawName = url.substring(RAW_PREFIX.length());
			is = getRawStream(context, rawName);
		} else if (lowerUrl.startsWith(RAW_PREFIX2)) {
			String rawName = url.substring(RAW_PREFIX2.length());
			is = getRawStream(context, rawName);
		} else if (lowerUrl.startsWith(FILE_PREFIX)) {
			String filePath = url.substring(FILE_PREFIX.length());
			is = getFileStream(filePath);
		} else if (lowerUrl.startsWith(DRAWABLE_PREFIX)) {
			String drawableName = url.substring(DRAWABLE_PREFIX.length());
			is = getDrawableStream(context, drawableName);
		} else {
			throw new IllegalArgumentException(String.format("Unsupported url: %s \n" +
					"Supported: \n%sxxx\n%sxxx\n%sxxx\n%sxxx", url, HTTP_PREFIX, ASSETS_PREFIX, RAW_PREFIX, FILE_PREFIX));
		}

		return is;
	}

	/**
	 * MD5的算法在RFC1321 中定义
	 * 在RFC 1321中，给出了Test suite用来检验你的实现是否正确：
	 * MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
	 * MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
	 * MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
	 * MD5 ("message digest") = f96b697d7cb7938d525a2f31aaf161d0
	 * MD5 ("abcdefghijklmnopqrstuvwxyz") = c3fcd3d76192e4007dfb496cca67e13b
	 *
	 * @author haogj
	 * <p/>
	 * 传入参数：一个字节数组
	 * 传出参数：字节数组的 MD5 结果字符串
	 */
	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = {       // 用来将字节转换成 16 进制表示的字符
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static Bitmap loadPreferedResizedBitmap(String filePath, int preferWidth, int preferHeight) {
		int wSample = 1;
		int hSample = 1;
		int sample;

		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filePath, op);
		if (preferWidth > 0 && op.outWidth > preferWidth) {
			wSample = getPreferSampeSize(op.outWidth, preferWidth);
		}

		if (preferHeight > 0 && op.outHeight > preferHeight) {
			hSample = getPreferSampeSize(op.outHeight, preferHeight);
		}

		sample = wSample > hSample ? hSample : wSample;
		System.out.println(String.format("pw: %s ph: %s sample: %s", preferWidth, preferHeight, sample));
		BitmapFactory.Options op2 = new BitmapFactory.Options();
		op2.inSampleSize = sample;

		return BitmapFactory.decodeFile(filePath, op2);
	}

	private static int getPreferSampeSize(int actureSize, int preferSize) {
		float sample = (float) actureSize / (float) preferSize;
		if (sample - FloatMath.floor(sample) > 0.0001) {
			sample += 1;
		}

		return (int) (sample > 1 ? FloatMath.floor(sample) : 1);
	}

	public static File getTempDir(Context context) {
		File tempDir = new File(context.getCacheDir(), "temp");
		tempDir.mkdirs();
		return tempDir;
	}

	public static File createTempFile(Context context) throws IOException {
		File tempDir = getTempDir(context);
		File tempFile = new File(tempDir, createTempFileName());

		tempFile.createNewFile();
		tempFile.deleteOnExit();
		return tempFile;
	}

	private static String createTempFileName() {
		return String.format("%s.tmp", System.currentTimeMillis());
	}

	public static boolean isRemoteUrl(String url) {
		return url.toLowerCase().startsWith("http");
	}

	public static class HttpResult{
		public List<Cookie> cookie;
		public byte[] responseData;
		public String getResponseString(String encoding){
			try {
				return new String(responseData, encoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	public static HttpResult doUrlEncodedFormPost(String url, Map<String, String> params) throws IOException {
			return doUrlEncodedFormPost(url, params, null);
	}

	public static HttpResult doUrlEncodedFormPost(String url, Map<String, String> params, List<Cookie> cookie) throws IOException {
		ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
		for (String name : params.keySet()) {
			values.add(new BasicNameValuePair(name, params.get(name)));
		}

		return doPost(url, new UrlEncodedFormEntity(values, HTTP.UTF_8), cookie);
	}

	public static HttpResult doMultiPartDataPost(String url, Map<String, String> stringValue, Map<String, File> fileValue) throws IOException {
		return doMultiPartDataPost(url, stringValue, fileValue, null);
	}

	public static HttpResult doMultiPartDataPost(String url, Map<String, String> stringValue, Map<String, File> fileValue, List<Cookie> cookie) throws IOException {
		ArrayList<Part> parts = new ArrayList<Part>();

		if (stringValue != null) {
			for (String key : stringValue.keySet()) {
				parts.add(new StringPart(key, stringValue.get(key)));
			}
		}

		if (fileValue != null) {
			for (String key : fileValue.keySet()) {
				try {
					File file = fileValue.get(key);
					parts.add(new FilePart(key, file, getMimeType(file), HTTP.UTF_8));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		MultipartEntity entity = new MultipartEntity(parts.toArray(new Part[parts.size()]));

		return doPost(url, entity, cookie);
	}

	public static HttpResult doPost(String url, HttpEntity entity, List<Cookie> cookie) throws IOException {
		HttpPost post = new HttpPost(url);
		post.setEntity(entity);
		return doHttpRequest(post, cookie);
	}

	public static HttpResult doGet(String url, List<Cookie> cookie) throws IOException {
		HttpGet get = new HttpGet(url);
		return doHttpRequest(get, cookie);
	}

	private static HttpResult doHttpRequest(HttpUriRequest request, List<Cookie> cookie) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		if(cookie != null){
			for(Cookie c: cookie){
				client.getCookieStore().addCookie(c);
			}
		}
		HttpResponse response = client.execute(request);

		int responseCode = response.getStatusLine().getStatusCode();
		if (200 <= responseCode && responseCode < 300) {
			HttpResult result = new HttpResult();
			result.responseData = readStream(response.getEntity().getContent());
			result.cookie = client.getCookieStore().getCookies();
			return result;
		} else {
			throw new IOException("Response code: " + responseCode);
		}

	}

	public static String getMimeType(File file) {
		try {
			MagicMatch match = Magic.getMagicMatch(file, true, true);
			return match.getMimeType();
		} catch (MagicParseException e) {
			e.printStackTrace();
		} catch (MagicMatchNotFoundException e) {
			e.printStackTrace();
		} catch (MagicException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String formatDate(long milli){
		//TODO
		return String.valueOf(milli);
	}

	public static void download(String url, File file) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

		conn.setRequestMethod("GET");
		conn.connect();

		int responseCode = conn.getResponseCode();
		if (!(200 <= responseCode && responseCode < 300)) {
			throw new IOException("Response code: " + responseCode);
		}

		InputStream is = conn.getInputStream();
		if(file.exists()){
			file.delete();
		}

		file.getParentFile().mkdirs();
		file.createNewFile();

		OutputStream os = new FileOutputStream(file);

		byte[] buf = new byte[1024 * 10];
		int readlen;
		while((readlen = is.read(buf)) >= 0){
			os.write(buf, 0, readlen);
		}

		os.close();
		is.close();
	}

	/**
	 * 删除Html标签
	 *
	 * @param inputString
	 * @return
	 */
	public static String removeHtmlTag(String inputString) {
		if (inputString == null){
			return null;
		}

		if(!inputString.contains("<") || !inputString.contains(">")){
			return inputString;
		}

		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		try {
			//定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			//定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签
			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return textStr;// 返回文本字符串
	}

	public static String getRefreshDisplayTime(long time){
		//1 小时内: xx 分钟前
		//24 小时内: xx 小时前
		//7 天内: xx天前
		//日期

		long currentTime = System.currentTimeMillis();
		long timeDiff = (currentTime - time) / 1000;

		final int MINUTE = 60;
		final int HOUR = 60 * MINUTE;
		final int DAY = 24 * 3600;
		final int WEEK = 7 * DAY;
		if(timeDiff > 0){
			if(timeDiff < HOUR){
				int minuteCount = Math.round((float)timeDiff / (float)MINUTE);
				return String.format("%s分钟前", minuteCount == 0? 1: minuteCount);
			}
			else if(timeDiff < DAY){
				int hourCount = Math.round((float)timeDiff / (float)HOUR);
				return String.format("%s小时前", hourCount);
			}
			else if(timeDiff < WEEK){
				int dayCount = Math.round((float)timeDiff / (float)DAY);
				return String.format("%s天前", dayCount);
			}
			else{
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(time);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);

				return String.format("%04d-%02d-%02d", year, month + 1, day);
			}
		}
		else{
			return "-";
		}
	}

    /**
     * properties 处理成HashMap,支持段结构，顺序排列。
     */
    public static HashMap<String,Object> property2HashMap(InputStream in) throws Exception{
        LinkedHashMap<String,Object> hashMap = new LinkedHashMap<String, Object>();
        OrderProperty properties = new OrderProperty();
        properties.load(in);
        in.close();

        List<String> keyList = new ArrayList<String>();
        Enumeration enumeration = properties.keys();
        do {
            keyList.add((String) enumeration.nextElement());
        }while(enumeration.hasMoreElements());

        for(int i = 0;i < keyList.size();i++){
            if(keyList.get(i).startsWith("[")){
                LinkedHashMap<String,Object> childMap = new LinkedHashMap<String, Object>();
                int j = i + 1;
                do {
                    if(j == keyList.size() || keyList.get(j).startsWith("[")){
                        break;
                    }
                    childMap.put(keyList.get(j),properties.get(keyList.get(j)));
                    j++;
                }while(j != keyList.size() && !keyList.get(j).startsWith("["));
                hashMap.put(keyList.get(i), childMap);
                i = j - 1;
            } else{
                hashMap.put(keyList.get(i),properties.get(keyList.get(i)));
            }
        }
        return hashMap;
    }

	public static HashMap<String, String> simpleProperty2HashMap(Context context, String path){
		try {
			InputStream is = getStream(context, path);
			HashMap<String, String> map = simpleProperty2HashMap(is);
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new HashMap<String, String>();
	}

    private static HashMap<String, String> simpleProperty2HashMap(InputStream in) throws IOException {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        Properties properties = new Properties();
		properties.load(in);
		in.close();
		Set keyValue = properties.keySet();
		for (Iterator it = keyValue.iterator(); it.hasNext();){
			String key = (String) it.next();
			hashMap.put(key, (String) properties.get(key));
		}

        return hashMap;
    }

    public static HashMap<String,Object> jsonObj2HashMap(JSONObject jsonObject){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        Iterator iterator = jsonObject.keys();
        for(jsonObject.keys();iterator.hasNext();){
            String key = (String) iterator.next();
            try {
                hashMap.put(key,jsonObject.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }

    public static List<Object> jsonArr2ArrayList(JSONArray jsonArray){
        List<Object> list = new ArrayList<Object>();
        for(int i = 0;i < jsonArray.length();i++){
            try {
                if(jsonArray.get(i) instanceof JSONObject){
                    list.add(jsonObj2HashMap(jsonArray.getJSONObject(i)));
                } else{
                    list.add(jsonArray.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

	public static boolean equals(Object o1, Object o2){
		if(o1 == o2){
			return true;
		}

		if(o1 != null){
			return o1.equals(o2);
		}

		if(o2 != null){
			return o2.equals(o1);
		}

		return false;
	}
}