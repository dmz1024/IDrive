package com.trs.collect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;
import com.trs.db.DBHelper;
import com.trs.types.ListItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14-2-27.
 */
public class CollectItem implements Serializable {
	public static final String KEY__ID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_TIME = "time";
	public static final String KEY_URL = "url";
	public static final String KEY_PIC = "pic";
	public static final String KEY_TYPE = "type";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_EXTRA_DATA = "extra_data";

	private long _id;
	private String title;
	private String time;
	private String url;
	private String pic;
	private String type;
	private String userID;
	private String extraData;

	public static String TABLE_NAME = "collect";
	public static String CREATE_TABLE_SQL =
					"CREATE TABLE " + TABLE_NAME + "(" +
					KEY__ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_TITLE + " TEXT, " +
					KEY_TIME + " TEXT, " +
					KEY_URL + " TEXT," +
					KEY_PIC + " TEXT," +
					KEY_TYPE + " TEXT," +
					KEY_USER_ID + " TEXT," +
					KEY_EXTRA_DATA + " TEXT);";

	public static String UPDATE_TABLE_SQL_1_2 = "ALTER TABLE \"" + TABLE_NAME + "\" ADD COLUMN \"" + KEY_PIC + "\" TEXT;";


	public CollectItem() {

	}

	public CollectItem(Cursor c){
		for(int i = 0; i < c.getColumnCount(); i ++){
			String cName = c.getColumnName(i);
			if(KEY__ID.equals(cName)){
				set_id(c.getLong(i));
			}
			else if(KEY_TITLE.equals(cName)){
				setTitle(c.getString(i));
			}
			else if(KEY_TIME.equals(cName)){
				setTime(c.getString(i));
			}
			else if(KEY_URL.equals(cName)){
				setUrl(c.getString(i));
			}
			else if(KEY_PIC.equals(cName)){
				setPic(c.getString(i));
			}
			else if(KEY_TYPE.equals(cName)){
				setType(c.getString(i));
			}
			else if(KEY_USER_ID.equals(cName)){
				setUserID(c.getString(i));
			}
			else if(KEY_EXTRA_DATA.equals(cName)){
				extraData = c.getString(i);
			}
		}
	}

	public ContentValues getCV(){
		ContentValues cv = new ContentValues();
		cv.put(KEY_TITLE, getTitle());
		cv.put(KEY_TIME, getTime());
		cv.put(KEY_URL, getUrl());
		cv.put(KEY_PIC, getPic());
		cv.put(KEY_TYPE, getType());
		cv.put(KEY_USER_ID, getUserID());
		cv.put(KEY_EXTRA_DATA, extraData);

		return cv;
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Object getExtraData() {
		if(extraData != null && extraData.length() > 0){
			String className = extraData.substring(0, extraData.indexOf("|"));
			String json = extraData.substring(extraData.indexOf("|") + 1);

			try {
				Class clazz = Class.forName(className);
				return new Gson().fromJson(json, clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public String getStringExtraData() {
		return extraData;
	}

	public void setExtraData(Object extraData) {
		Class clazz = extraData.getClass();
		String json = new Gson().toJson(extraData);
		this.extraData = clazz.getName() + "|" + json;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public static void insert(Context context, CollectItem item){
		DBHelper helper = DBHelper.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();

		db.insertOrThrow(TABLE_NAME, null, item.getCV());
	}

	public static void delete(Context context, CollectItem item){
		DBHelper helper = DBHelper.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();

		String[] names = {KEY_TYPE, KEY_TITLE, KEY_URL, KEY_EXTRA_DATA};
		String[] values = {item.getType(), item.getTitle(), item.getUrl(), item.getStringExtraData()};

		String whereSql = getWhereSql(names, values);
		String[] whereArgs = getWhereArgs(values);
		db.delete(TABLE_NAME, whereSql, whereArgs);
	}

	public static void clear(Context context){
		DBHelper helper = DBHelper.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();

		db.delete(TABLE_NAME, null, null);
	}

	public static List<CollectItem> get(Context context){
		return get(context, null);
	}

	public static List<CollectItem> get(Context context, String type){
		Cursor c = getCursor(context, type);
		ArrayList<CollectItem> itemList = new ArrayList<CollectItem>(c.getCount());
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			CollectItem item = new CollectItem(c);
			itemList.add(item);
		}

		return itemList;
	}

	public static Cursor getCursor(Context context){
		return getCursor(context, null);
	}

	public static Cursor getCursor(Context context, String type){
		DBHelper helper = DBHelper.getInstance(context);
		SQLiteDatabase db = helper.getReadableDatabase();

		String whereSql = null;
		String[] whereArgs = null;
		if(type != null && type.length() > 0){
			whereSql = String.format("%s=?", KEY_TYPE);
			whereArgs = new String[]{type};
		}

		Cursor c = db.query(TABLE_NAME, null, whereSql, whereArgs, null, null, String.format("%s desc", KEY__ID));

		return c;
	}

	public static boolean hasCollect(Context context, CollectItem item){
		DBHelper helper = DBHelper.getInstance(context);
		SQLiteDatabase db = helper.getReadableDatabase();

		String[] names = {KEY_TYPE, KEY_TITLE, KEY_URL, KEY_EXTRA_DATA};
		String[] values = { item.getType(), item.getTitle(), item.getUrl(), item.getStringExtraData()};

		String whereSql = getWhereSql(names, values);
		String[] whereArgs = getWhereArgs(values);

		Cursor c = db.query(TABLE_NAME, new String[]{KEY__ID}, whereSql, whereArgs, null, null, null);

		return c.getCount() > 0;
	}

	private static String getWhereSql(String[] names, String[] values){
		if(names != null && values != null && names.length != values.length){
			throw new IllegalArgumentException("names' length must equal to values' length");
		}

		String sql = "";
		if(names != null && values != null && names.length > 0){
			for(int i = 0; i < names.length; i ++){
				if(i > 0){
					sql += " and ";
				}

				sql += names[i] + (values[i] != null? "=?": " is null");
			}
		}

		return sql;
	}

	private static String[] getWhereArgs(String[] values){
		if(values != null && values.length > 0){
			ArrayList<String> array = new ArrayList<String>();
			for(String v: values){
				if(v != null){
					array.add(v);
				}
			}

			return array.toArray(new String[array.size()]);
		}

		return null;
	}

	public static CollectItem create(ListItem listItem, String type){
		CollectItem item = new CollectItem();
		item.setTime(listItem.getDate());
		item.setTitle(listItem.getTitle());
		item.setType(type);
		item.setUrl(listItem.getUrl());
		item.setPic(listItem.getImgUrl());

		return item;
	}

	public static ListItem convertToListItem(CollectItem item){
		ListItem listItem = new ListItem();
		listItem.setDate(item.getTime());
		listItem.setTitle(item.getTitle());
		listItem.setUrl(item.getUrl());
		listItem.setImgUrl(item.getPic());

		return listItem;
	}
}
