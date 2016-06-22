package net.endlessstudio.util.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class JSONObjectHelper {
	private static final Class[] ALLOWED_VALUE_TYPE = { Boolean.class,
			Double.class, Integer.class, Long.class };

	JSONObject jsonObject;

	public JSONObjectHelper(String json) throws JSONException {
		this(new JSONObject(json));
	}

	public JSONObjectHelper(JSONObject jsonObject) {
		if(jsonObject == null){
			throw new NullPointerException();
		}
		this.jsonObject = jsonObject;
	}

	public Object get(String name, Object defaultValue) {
		if (jsonObject.has(name)) {
			try {
				return jsonObject.get(name);
			} catch (JSONException e) {
				// do nothing
			}
		}

		return defaultValue;
	}

	private Object get(Class clazz, String name, Object defaultValue) {
		boolean typeAllowed = false;
		for (Class allowedValueType : ALLOWED_VALUE_TYPE) {
			if (allowedValueType == clazz) {
				typeAllowed = true;
				break;
			}
		}

		if (typeAllowed) {
			Object value = get(name, defaultValue);
			if (value instanceof String) {
				try {
					Method valueOf = clazz.getMethod("valueOf",
							new Class[] { String.class });
					return valueOf.invoke(clazz, (String) value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (clazz.isInstance(value)) {
				return value;
			}
		}

		return defaultValue;
	}

	public boolean getBoolean(String name, boolean defaultValue) {
		return (Boolean) get(Boolean.class, name, defaultValue);
	}

	public double getDouble(String name, double defaultValue) {
		Object value = get(name, defaultValue);
		if (value instanceof String && value.equals("")) {
			return defaultValue;
		}
		return (Double) get(Double.class, name, defaultValue);
	}

	public int getInt(String name, int defaultValue) {
		return (Integer) get(Integer.class, name, defaultValue);
	}

	public long getLong(String name, long defaultValue) {
		return (Long) get(Long.class, name, defaultValue);
	}

	public String getString(String name, String defaultValue) {
		Object value = get(name, defaultValue);
		return value == null ? null : String.valueOf(value);
	}

	public String getString(String[] names, String defaultValue) {
		for(String n: names){
			if(jsonObject.has(n)){
				return getString(n, defaultValue);
			}
		}

		return defaultValue;
	}

	public JSONArray getJSONArray(String name, JSONArray defaultValue){
		try {
			return jsonObject.getJSONArray(name);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	public JSONArray getJSONArray(String[] names, JSONArray defaultValue){
		for(String n: names){
			if(jsonObject.has(n)){
				try {
					return jsonObject.getJSONArray(n);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return defaultValue;
	}

	public JSONArrayHelper getJSONArrayHelper(String name){
		return new JSONArrayHelper(getJSONArray(name, new JSONArray()));
	}

	public JSONArrayHelper getJSONArrayHelper(String[] name){
		return new JSONArrayHelper(getJSONArray(name, new JSONArray()));
	}

	public JSONObject getJSONObject(String name, JSONObject defaultValue){
		try{
			return jsonObject.getJSONObject(name);
		}
		catch (JSONException e){
			return defaultValue;
		}
	}

	public JSONObject getJSONObject(String[] names, JSONObject defaultValue){
		for(String n: names){
			if(jsonObject.has(n)){
				try {
					return jsonObject.getJSONObject(n);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return defaultValue;
	}

	public JSONObjectHelper getJSONObjectHelper(String name){
		return new JSONObjectHelper(getJSONObject(name, new JSONObject()));
	}

	public JSONObjectHelper getJSONObjectHelper(String[] name){
		return new JSONObjectHelper(getJSONObject(name, new JSONObject()));
	}
}
