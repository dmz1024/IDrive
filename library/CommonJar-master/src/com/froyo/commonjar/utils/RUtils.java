package com.froyo.commonjar.utils;

import java.lang.reflect.Field;

public  class RUtils {

	public static int getId(String name,Class<?> cls) {
		try {
//			Class<R.id> cls = R.id.class;
			Field f = cls.getDeclaredField(name);
			f.setAccessible(true);
			return f.getInt(cls);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
