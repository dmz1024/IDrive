package com.trs.app;

import android.app.ActivityManager;
import android.support.v4.app.Fragment;
import com.trs.util.log.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by john on 14-6-17.
 */
public class FragmentCache {
	public static final boolean ENABLED = false;
	public static final int MAX_COUNT_IF_NO_MEMORY_INFO = 20;
	public static final long MIN_AVAIL_MEMORY = 10 * 1024 * 1024;

	public static class Entity{
		Object key;
		Fragment fragment;

		public Entity(Object key, Fragment fragment) {
			this.key = key;
			this.fragment = fragment;
		}
	}

	public static FragmentCache sInstance;

	public static FragmentCache getInstance(){
		if(sInstance == null){
			sInstance = new FragmentCache();
		}

		return sInstance;
	}

	private ActivityManager mActivityManager;
	private LinkedList<Entity> mEntityList = new LinkedList<Entity>();
	ActivityManager.MemoryInfo mMemoryInfo = new ActivityManager.MemoryInfo();


	private FragmentCache(){

	}

	public void put(Object key, Fragment fragment){
		if(ENABLED){
			removeReusedFragment(fragment);
			Entity entity = popOrCreateEntity(key, fragment);
			mEntityList.add(0, entity);
			releaseFragmentIfNeeded();
		}
	}

	public Fragment get(Object key){
		if(ENABLED){
			for(Entity entity: mEntityList){
				if(entity.key.equals(key)){
					return entity.fragment;
				}
			}
		}

		return null;
	}

	private Entity popOrCreateEntity(Object key, Fragment fragment){
		Entity entity = null;
		for(Entity e: mEntityList){
			if(e.key.equals(key)){
				entity = e;
				break;
			}
		}

		if(entity != null){
			mEntityList.remove(entity);
		}
		else{
			entity = new Entity(key, fragment);
		}

		return entity;
	}

	private void removeReusedFragment(Fragment fragment){
		ArrayList<Entity> entityToRemove = new ArrayList<Entity>();
		for(Entity entity: mEntityList){
			if(entity.fragment == fragment){
				entityToRemove.add(entity);
			}
		}

		mEntityList.removeAll(entityToRemove);
	}

	private void releaseFragmentIfNeeded(){
		if(needRelease()){
			releaseFragment();
		}
	}

	private boolean needRelease(){
		if(mActivityManager != null){
			mActivityManager.getMemoryInfo(mMemoryInfo);
			printMemoryInfo();
			return mMemoryInfo.lowMemory || mMemoryInfo.availMem < MIN_AVAIL_MEMORY;
		}
		else{
			return mEntityList.size() > MAX_COUNT_IF_NO_MEMORY_INFO;
		}
	}

	private void releaseFragment(){
		ArrayList<Entity> entityToRemove = new ArrayList<Entity>();
		for(int i = mEntityList.size() / 2; i < mEntityList.size(); i ++){
			entityToRemove.add(mEntityList.get(i));
		}

		entityToRemove.removeAll(mEntityList);
	}

	public void setActivityManager(ActivityManager activityManager) {
		this.mActivityManager = activityManager;
	}

	private void printMemoryInfo(){
//		/** @hide */
//		public long hiddenAppThreshold;
//		/** @hide */
//		public long secondaryServerThreshold;
//		/** @hide */
//		public long visibleAppThreshold;
//		/** @hide */
//		public long foregroundAppThreshold;
		StringBuilder sb = new StringBuilder().append("Memory info: \n");
		sb.append("availMem: ").append(mMemoryInfo.availMem).append('\n');
		sb.append("lowMemory: ").append(mMemoryInfo.lowMemory).append('\n');
		sb.append("threshold: ").append(mMemoryInfo.threshold).append('\n');
		Class memoryInfoClass = ActivityManager.MemoryInfo.class;
		try {
			long hiddenAppThreshold = memoryInfoClass.getField("hiddenAppThreshold").getLong(mMemoryInfo);
			long secondaryServerThreshold = memoryInfoClass.getField("secondaryServerThreshold").getLong(mMemoryInfo);
			long visibleAppThreshold = memoryInfoClass.getField("visibleAppThreshold").getLong(mMemoryInfo);
			long foregroundAppThreshold = memoryInfoClass.getField("foregroundAppThreshold").getLong(mMemoryInfo);
			sb.append("hiddenAppThreshold: ").append(hiddenAppThreshold).append('\n');
			sb.append("secondaryServerThreshold: ").append(secondaryServerThreshold).append('\n');
			sb.append("visibleAppThreshold: ").append(visibleAppThreshold).append('\n');
			sb.append("foregroundAppThreshold: ").append(foregroundAppThreshold).append('\n');
		} catch (Exception e) {}

		Log.i("FragmentCache", sb.toString());
	}
}
