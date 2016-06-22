package com.trs.collect;

/**
 * Created by john on 14-2-27.
 */
public class CollectManager {
	private static CollectManager sInstance;
	public static CollectManager getInstance(){
		if(sInstance == null){
			sInstance = new CollectManager();
		}

		return sInstance;
	}
}
