package com.trs.readhistory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.trs.types.ListItem;
import com.trs.util.log.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by john on 14-3-18.
 */
public class ReadHistoryManager extends SQLiteOpenHelper {
	public static final String TAG = "ReadHistoryManager";
	public static final String NAME = "read_history.db";
	public static final int VERSION = 2;
	public static final String SQL_CREATE_TABLE = "create table read_history(" +
			"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"hash INTEGER, " +
			"title TEXT, " +
			"url TEXT, " +
			"type TEXT, " +
			"time INGETER);";
	private static ReadHistoryManager sInstance;

	public static ReadHistoryManager getInstance(Context context){
		if(sInstance == null){
			sInstance = new ReadHistoryManager(context);
		}

		return sInstance;
	}

	public ReadHistoryManager(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql;
		switch(oldVersion){
			case 1:
				sql = "ALTER TABLE read_history ADD COLUMN time TEXT;";
				db.execSQL(sql);
		}
	}


	public void markAsRead(String url){
		markAsRead(null, url, null, System.currentTimeMillis());
	}

	public void markAsRead(String title, String url, String type){
		markAsRead(title, url, type, 0l);
	}

	public void markAsRead(String title, String url, String type, long date){
		ContentValues cv = new ContentValues();
		int hash = getHash(title, url, type);
		cv.put("hash", hash);
		cv.put("title", title);
		cv.put("url", url);
		cv.put("type", type);
		cv.put("time", date);
		if(hasRead(title, url, type)){
			boolean result = getWritableDatabase().update("read_history", cv, "hash=?", new String[]{String.valueOf(hash)}) >= 0;
			if(!result){
				Log.w(TAG, String.format("update as read failed: %s|%s|%s"));
			}
		}
		else{
			boolean result = getWritableDatabase().insert("read_history", null, cv) >= 0;
			if(!result){
				Log.w(TAG, String.format("insert as read failed: %s|%s|%s"));
			}
		}
	}

	public void markAsRead(ListItem item){
		markAsRead(item.getTitle(), item.getUrl(), item.getType(), 0l);
	}

	public boolean hasRead(String title, String url, String type){
		int hash = getHash(title, url, type);
		Cursor c = getReadableDatabase().query("read_history", new String[]{"_id"}, "hash=?", new String[]{String.valueOf(hash)}, null, null, null);
		boolean result = c.getCount() > 0;
		c.close();
		return result;
	}

	public boolean hasRead(ListItem item){
		return hasRead(item.getTitle(), item.getUrl(), item.getType());
	}

	private int getHash(String title, String url, String type){
		int hash = String.format("%s|%s|%s", title, url, type).hashCode();
		return hash;
	}

	public long getTime(String url){
		int hash = getHash(null, url, null);
		Cursor c = getReadableDatabase().query("read_history", /*new String[]{"time"}*/null, "hash=?", new String[]{String.valueOf(hash)}, null, null, null);

		long time = 0l;
		if(c.getCount() > 0){
			c.moveToFirst();
			time = c.getLong(c.getColumnIndex("time"));
		}

		c.close();
		return time;
	}

}
