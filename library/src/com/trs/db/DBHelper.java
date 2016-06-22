package com.trs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.trs.collect.CollectItem;

/**
 * Created by john on 14-2-21.
 */
public class DBHelper extends SQLiteOpenHelper {
	public static final int VERSION = 1;
	public static final String NAME = "database.db";

	public static DBHelper sInstance;
	public static DBHelper getInstance(Context context){
		if(sInstance == null){
			sInstance = new DBHelper(context);
		}

		return sInstance;
	}

	private DBHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CollectItem.CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch(oldVersion){
			case 1:
				db.execSQL(CollectItem.UPDATE_TABLE_SQL_1_2);
		}
	}
}
