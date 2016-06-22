package com.trs.weather;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by john on 14-3-25.
 */
public class WeatherInfo implements Serializable{
	public String City;
	public String Cityid;
	public String Week;
	public String Index_d;

	private String Date_1;
	private String Temp1;
	private String Weather1;
	private String Wind1;
	private String Fl1;

	private String Date_2;
	private String Temp2;
	private String Weather2;
	private String Wind2;
	private String Fl2;

	private String Date_3;
	private String Temp3;
	private String Weather3;
	private String Wind3;
	private String Fl3;

	private String Date_4;
	private String Temp4;
	private String Weather4;
	private String Wind4;
	private String Fl4;

	private String Date_5;
	private String Temp5;
	private String Weather5;
	private String Wind5;
	private String Fl5;

	private String Date_6;
	private String Temp6;
	private String Weather6;
	private String Wind6;
	private String Fl6;

	public String Img1;
	public String Img2;
	public String Img3;
	public String Img4;
	public String Img5;
	public String Img6;
	public String Img7;
	public String Img8;
	public String Img9;
	public String Img10;
	public String Img11;
	public String Img12;

	public static class Weather implements Serializable{
		public String date;
		public String temp;
		public String weather;
		public String wind;
		public String fl;

		public Weather(String date, String temp, String weather, String wind, String fl) {
			this.date = date;
			this.temp = temp;
			this.weather = weather;
			this.wind = wind;
			this.fl = fl;
		}
	}

	public int getTodayIndex(){
		//remove old days
		Calendar calendar = Calendar.getInstance();
		int[] today = {calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)};
		int todayIndex = -1;
		for(int i = 0; i < weatherList.length; i ++){
			WeatherInfo.Weather weather = weatherList[i];
			if(weather != null){
				if(todayIndex < 0){
					// today index not found find today index
					String strDate = weather.date;
					if(strDate != null){
						int[] d = parseDate(strDate);
						if(Arrays.equals(d, today)){
							return i;
						}
					}
				}
			}
		}

		return -1;
	}

	public Weather getTodayWeather(){
		int todayIndex = getTodayIndex();
		return todayIndex >= 0? weatherList[todayIndex]: null;
	}

	private int[] parseDate(String date){
		int[] result = {0, 0, 0};
		Pattern p = Pattern.compile("^(\\d{4})[^\\d]+(\\d{1,2})[^\\d]+(\\d{1,2})[^\\d]*$");
		Matcher m = p.matcher(date);

		ArrayList<String> dateString = new ArrayList<String>();
		while(m.find()){
			for(int i = 0; i < m.groupCount(); i ++){
				dateString.add(m.group(i + 1));
			}
		}

		if(dateString.size() == 3){
			result[0] = Integer.valueOf(dateString.get(0));
			result[1] = Integer.valueOf(dateString.get(1)) - 1;
			result[2] = Integer.valueOf(dateString.get(2));
		}

		return result;
	}




	public Weather[] weatherList = new Weather[6];

	public static WeatherInfo create(String xml) throws JSONException {
		JSONObject obj = XML.toJSONObject(xml);
		WeatherInfo info = new Gson().fromJson(obj.getJSONObject("MoreWeather").toString(), WeatherInfo.class);

		info.weatherList[0] = new Weather(info.Date_1, info.Temp1, info.Weather1, info.Wind1, info.Fl1);
		info.weatherList[1] = new Weather(info.Date_2, info.Temp2, info.Weather2, info.Wind2, info.Fl2);
		info.weatherList[2] = new Weather(info.Date_3, info.Temp3, info.Weather3, info.Wind3, info.Fl3);
		info.weatherList[3] = new Weather(info.Date_4, info.Temp4, info.Weather4, info.Wind4, info.Fl4);
		info.weatherList[4] = new Weather(info.Date_5, info.Temp5, info.Weather5, info.Wind5, info.Fl5);
		info.weatherList[5] = new Weather(info.Date_6, info.Temp6, info.Weather6, info.Wind6, info.Fl6);

		return info;
	}


}
