package com.trs.constants;

import com.trs.app.TRSApplication;

/**
 * Created by john on 14-3-14.
 */
public class Constants {

	public static final String ACTION_ABOUT = "com.trs.mobile.action.about";
	public static final String ACTION_FEEDBACK = "com.trs.mobile.action.feedback";

	public static final TRSApplication.SourceType WCM_DATA_TYPE = TRSApplication.SourceType.XML;
	public static final String FIRST_CLASS_MENU_URL = "http://app.moe.gov.cn/channels.xml";

	public static final String BASE_TYPE_FRAGMENT_MAP_PATH = "file://android_raw/type_fragment_map_base";
	public static final String EXT_TYPE_FRAGMENT_MAP_PATH = "file://android_raw/type_fragment_map";
	public static final String BASE_TYPE_ACTIVITY_MAP_PATH = "raw://type_activity_map_base";
	public static final String EXT_TYPE_ACTIVITY_MAP_PATH = "raw://type_activity_map";
	public static final String AIRPORT_INFO_URL = "assets://airport_cities.json";
	public static final String AIRWAYS_INFO_URL = "assets://airways.json";

	public static final String TRAIN_CITY_INFO_URL = "assets://train_city.json";

	public static final String[] TYPE_NAMES = {"type", "t"};
	public static final String[] IMAGE_URL_NAMES = {"picture", "pic", "image", "img","imgurl", "icon", "spic", "channellogo", "images", "ic", };
	public static final String[] URL_NAMES = {"url", "URL", "link", "Url", };
	public static final String[] TITLE_NAMES = {"title", "name", "cname","car", "n", };
	public static final String[] DATE_NAMES = {"date", "lastupdatetime", "rt", "time", };
	public static final String[] SUMMARY_NAMES = {"abstract", "summary", "phoneno","docabstract","datas", "content", "ab", "line"};
	public static final String[] PAGE_INFO_NAMES = {"pageinfo", "page_info"};
    public static final String[] ID_NAMES= {"docid", "id", "lid"};

	public static final String GET_WEATHER_INFO_URL = "http://webservice.36wu.com/weatherService.asmx/getAutomaticMoreWeather?ip=%s&UserId=257804F8B4D540D0BE207F62E8770277";
    public static final String GET_WEATHER_CURRENT_INFO_URL = "http://webservice.36wu.com/weatherService.asmx/getRealTimeWeatherByCityId?CityId=101170101&UserId=257804F8B4D540D0BE207F62E8770277";
    public static final String GET_BUS_LINE_INFO_URL = "http://webservice.36wu.com/mapService.asmx/GetLinesInfoByLine?city=%s&line=%s&UserId=257804F8B4D540D0BE207F62E8770277";
	public static final String GET_BUS_STATION_INFO_URL = "http://webservice.36wu.com/mapService.asmx/GetStationInfoByName?city=%s&name=%s&UserId=257804F8B4D540D0BE207F62E8770277";
	public static final String GET_BUS_ROUTE_INFO_URL = "http://webservice.36wu.com/mapService.asmx/GetTransferInfoByStation?city=%s&startStation=%s&endStation=%s&UserId=257804F8B4D540D0BE207F62E8770277";
    public static final String GET_VIOLATION_CAR_TYPES_URL = "http://v.juhe.cn/wz/hpzl?key=60ea1720911433ebe94bf460b23292e6";
	public static final String CHECK_UPDATE_URL = "http://www.ycen.com.cn/ycxwwmobile/bbgx/index.json";
    public static final String GET_VIOLATION_CAR_RESULT_URL = "http://v.juhe.cn/wz/query?key=60ea1720911433ebe94bf460b23292e6";
	public static final String GET_AIRLINE_INFO_URL = "http://42.120.16.248:8025/incxml/av.aspx?BeginCity=%S&EndCity=%S&BeginDate=%s&Key=731F100BD670DB8A&DataType=1";

    public static final String GET_TRAIN_LINE_INFO_URL = "http://webservice.36wu.com/TrainService.asmx/GetJsonTrainStationToStation?startStation=%s&arriveStation=%s&UserId=257804F8B4D540D0BE207F62E8770277";
    public static final String GET_TRAIN_NUMBER_INFO_URL = "http://webservice.36wu.com/TrainService.asmx/GetTrainDetailInfoByTrainNumber?trainNumber=%s&UserId=257804F8B4D540D0BE207F62E8770277";

	public static final String LOGIN_URL = "http://bbs.ycen.com.cn/api/mobile/index.php?module=login";
	public static final String AVATAR_URL = "http://bbs.ycen.com.cn/uc_server/avatar.php?uid=%s&size=big";
	public static final String UPLOAD_PICTURE = "http://bbs.ycen.com.cn/api/mobile/?module=forumupload&type=image&simple=1&formhash=%s";
	public static final String SUBMIT_MESSAGE_URL = "http://bbs.ycen.com.cn/api/mobile/index.php?module=newthread&fid=110&topicsubmit=yes";
	public static final String SUBMIT_PHOTO_URL = "http://bbs.ycen.com.cn/api/mobile/index.php?module=newthread&fid=111&topicsubmit=yes";
	public static final String SUBMIT_FEEDBACK_URL = "http://bbs.ycen.com.cn/api/mobile/index.php?module=newthread&fid=49&topicsubmit=yes";

	public static final String PUBLIC_SUBMIT_PHOTO_LIST_URL = "http://bbs.ycen.com.cn/api.php?mod=json&bid=66&imageWidth=%s&imageHeight=%s";
	public static final String MY_SUBMIT_PHOTO_LIST_URL = "http://bbs.ycen.com.cn/api.php?mod=json&bid=66&uid=%s&imageWidth=%s&imageHeight=%s";
	public static final String PUBLIC_SUBMIT_MESSAGE_LIST_URL = "http://bbs.ycen.com.cn/api.php?mod=json&bid=65";
	public static final String MY_SUBMIT_MESSAGE_LIST_URL = "http://bbs.ycen.com.cn/api.php?mod=json&bid=65&uid=%s";
	public static final String LOGIN_AS_ADMIN_URL = "http://bbs.ycen.com.cn/api/mobile/index.php?module=login";
	public static final String PRE_REGISTER_URL = "http://bbs.ycen.com.cn/api.php?mod=user&action=preregister";
	public static final String REGISTER_URL = "http://bbs.ycen.com.cn/member.php?mod=register&inajax=yes&ajaxdata=json";

    //SOAP
    //WSDL文档中的URL
    public static final String serviceURL="http://trscom.vicp.cc/appnews/ws/mobileWebService?wsdl";
    //首页头部
    public static final String MainMenu="getAllSite";
    //Detail
    public static final String DocumentDetail = "getXiLan";
    //
    public static final String ChannleBySitName = "getChannelBySiteName";
    //WSDL文档中的命名空间
    public static final String serviceNameSpace="http://trscom.vicp.cc/appnews/ws";
    public static final String ListBySiteAndChannel = "getGaiLanBySiteAndChannel";
    public static final String detail = "getXiLan";
    public static final String GetGanLanByTime = "findGaiLanBYTime";
    public static final String search = "findMessageByCondition";

    // upload
    public static final String SERVER_ADDRESS = "http://192.168.1.102:8082/MyApplicationServer/MainServer";
    public static final String mXZCMFolder = "/mnt/sdcard/XZCM/";
}
