package net.endlessstudio.util.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class JSONArrayHelper{
	private static final Class[] ALLOWED_VALUE_TYPE = {Boolean.class,
		Double.class, Integer.class, Long.class};
	
	JSONArray jsonArray;
	public JSONArrayHelper(String json) throws JSONException {
		this(new JSONArray(json));
	}
	
	public JSONArrayHelper(JSONArray jsonArray) {
		if(jsonArray == null){
			throw new NullPointerException();
		}
		this.jsonArray = jsonArray;
	}

	public Object get(int index, Object defaultValue){
		try{
			return jsonArray.get(index);
		}
		catch(Exception e){
			//do nothing
		}
		
		return defaultValue;
	}
	
	private Object get(Class clazz, int index, Object defaultValue){
		boolean typeAllowed = false;
		for(Class allowedValueType : ALLOWED_VALUE_TYPE){
			if(allowedValueType == clazz){
				typeAllowed = true;
				break;
			}
		}
		
		if(typeAllowed){
			Object value;
			try {
				value = get(index, defaultValue);
				if(value instanceof String){
					Method valueOf = clazz.getMethod("valueOf", new Class[]{String.class});
					return valueOf.invoke(clazz, (String)value);
				}
				else if(clazz.isInstance(value)){
					return value;
				}
			} catch (Exception e) {
				//do nothing
			}
		}
		
		return defaultValue;
	}
	
	public boolean getBoolean(int index, boolean defaultValue){
		return (Boolean) get(Boolean.class, index, defaultValue);
	}
	
	public double getDouble(int index, Double defaultValue){
		return (Double)get(Double.class, index, defaultValue);
	}
	
	public int getInt(int index, int defaultValue){
		return (Integer)get(Integer.class, index, defaultValue);
	}
	
	public long getLong(int index, long defaultValue){
		return (Long)get(Long.class, index, defaultValue);
	}
	
	public String getString(int index, String defaultValue){
		Object value = get(index, defaultValue);
		return String.valueOf(value);
	}

	public JSONObject getJSONObject(int index, JSONObject defaultValue){
		try{
			return jsonArray.getJSONObject(index);
		}
		catch(JSONException e){
			return defaultValue;
		}
	}

	public JSONObjectHelper getJSONObjectHelper(int index){
		return new JSONObjectHelper(getJSONObject(index, new JSONObject()));
	}

	public JSONArray getJSONArray(int index, JSONArray defaultValue){
		try{
			return jsonArray.getJSONArray(index);
		}
		catch(JSONException e){
			return defaultValue;
		}
	}

	public JSONArrayHelper getJSONArrayHelper(int index){
		return new JSONArrayHelper(getJSONArray(index, new JSONArray()));
	}

	public int length(){
		return jsonArray.length();
	}
}
