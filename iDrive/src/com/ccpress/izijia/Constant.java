package com.ccpress.izijia;

/**
 * Created by Wu Jingyu
 * Date: 2015/3/24
 * Time: 14:51
 */
public class Constant {
    public static final boolean IS_PUBLISH = false;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int POST_EDIT_REQUEST_PICKER_CODE = 101;

    public static final int LIST_PAGE_SIZE = 20;
    public static final String  CType_ALL = "0";
    public static final String  CType_Des = "1";
    public static final String  CType_Line = "2";
    public static final String  CType_SelfDrive = "3";
    public static final String  CType_Choose = "4";
    public static final String SP_LOCATION = "SP_LOCATION";
    public static final String SP_LOCATION_GPS_CITY_CODE = "SP_LOCATION_GPS_CITY_CODE";
    public static final String SP_LOCATION_GPS_CITY = "SP_LOCATION_GPS_CITY";
    public static final String SP_LOCATION_GPS_PROVINCE_CODE = "SP_LOCATION_GPS_PROVINCE_CODE";
    public static final String SP_LOCATION_GPS_PROVINCE = "SP_LOCATION_GPS_PROVINCE";
    public static final String SP_LOCATION_CURRENT_SET_CITY_CODE = "SP_LOCATION_CURRENT_SET_CITY_CODE";
    public static final String SP_LOCATION_CURRENT_SET_CITY = "SP_LOCATION_CURRENT_SET_CITY";
    public static final String SP_LOCATION_CURRENT_SET_PROVINCE_CODE = "SP_LOCATION_CURRENT_SET_PROVINCE_CODE";
    public static final String SP_LOCATION_CURRENT_SET_PROVINCE = "SP_LOCATION_CURRENT_SET_PROVINCE";
    public static final String SP_LOCATION_CURRENT_SET_CITY_CODE_DEFAULT = "010";
    public static final String SP_LOCATION_CURRENT_SET_CITY_DEFAULT = "北京";
    public static final String SP_LOCATION_CURRENT_SET_PROVINCE_CODE_DEFAULT = "11";
    public static final String SP_LOCATION_CURRENT_SET_PROVINCE_DEFAULT = "北京市";

    public static final String GD_SP_LOCATION = "GD_SP_LOCATION";
    public static final String GD_SP_LOCATION_GPS_CITY_CODE = "GD_SP_LOCATION_GPS_CITY_CODE";
    public static final String GD_SP_LOCATION_GPS_CITY = "GD_SP_LOCATION_GPS_CITY";
    public static final String GD_SP_LOCATION_GPS_PROVINCE_CODE = "GD_SP_LOCATION_GPS_PROVINCE_CODE";
    public static final String GD_SP_LOCATION_GPS_PROVINCE = "GD_SP_LOCATION_GPS_PROVINCE";
    public static final String GD_SP_LOCATION_CURRENT_SET_CITY_CODE = "GD_SP_LOCATION_CURRENT_SET_CITY_CODE";
    public static final String GD_SP_LOCATION_CURRENT_SET_CITY = "GD_SP_LOCATION_CURRENT_SET_CITY";
    public static final String GD_SP_LOCATION_CURRENT_SET_PROVINCE_CODE = "GD_SP_LOCATION_CURRENT_SET_PROVINCE_CODE";
    public static final String GD_SP_LOCATION_CURRENT_SET_PROVINCE = "GD_SP_LOCATION_CURRENT_SET_PROVINCE";
    public static final String GD_SP_LOCATION_CURRENT_SET_CITY_CODE_DEFAULT = "010";
    public static final String GD_SP_LOCATION_CURRENT_SET_CITY_DEFAULT = "北京";
    public static final String GD_SP_LOCATION_CURRENT_SET_PROVINCE_CODE_DEFAULT = "11";
    public static final String GD_SP_LOCATION_CURRENT_SET_PROVINCE_DEFAULT = "北京市";

    /**
     * URLs
     */
    public static final String IDRIVE_URL_BASE = "http://182.92.79.205:8080/coreapp/mobilesearch/";
    public static final String INTERACT_URL_BASE = "http://member.izijia.cn/index.php?s=/interaction/index/";
    public static final String INTERACT_URL_BASE_SERVICE = "http://member.izijia.cn/index.php?s=/interaction/service/";
    public static final String UPLOAD_DATA = "http://member.izijia.cn/index.php?s=/interaction/service/upload_data";
    public static final String CARTEAM_URL = "http://member.izijia.cn/index.php?s=/interaction/index/get_team_cars";

    public static final String IDRIVE_CHANNELS_URL = "idrive_channels.json";

    public static final String EDIT_GROUP_URL = "edit_group.json";
    public static final String CITY_LIST_URL = "city.json";
    public static final String MYCITY_LIST_URL = "asd.json";
    public static final String IDRIVE_TAGS_URL = "getbarlabels.do";
    public static final String CHANNELS_URL = "getchannels.do";
    public static final String CHANNELS_CONTENTS = "getchannelcontent.do";
    public static final String CHANNELS_HOME_CONTENTS = "getchnlrecordlist.do";
    public static final String LOCATION_LIST_URL = "getdistrictarea.do?pid=%s";

    public static final String INTERACT_CHANNELS_URL = "get_interaction_channel";
    public static final String INTERACT_CHANNELS_CONTENTS = "get_interaction_list";
    public static final String INTERACT_TAGS_URL = "get_interaction_tags";

    public static final String INTERACT_DETAIL = "get_video_pic_detail&docid=";//互动详情

    public static final String INTERACT_LINES_UPLOAD = "get_line_detail";//网友上传线路详情

    public static final String INTERACT_DETAIL_STATUS = "detail_status";//详情状态 &docid=%s&type=%s&token=%s&uid=%s


    public static final String INTERACT_PRAISE = "doSupport";//点赞
    public static final String INTERACT_PRAISE_CANCEL = "cancelSupport";//取消点赞

    public static final String INTERACT_COMMENTLIST = "getCommentList&docid=%s&type=%s&token=%s&uid=%s&pageIndex=";//评论列表
    public static final String INTERACT_ADDCOMMENT="addComment";//写评论

    public static final String INTERACT_REPORT = "report";//举报

    public static final String INTERACT_SHARE = "share";//分享到站内

    public static final String INTERACT_COLLECT = "addFav";//收藏
    public static final String INTERACT_COLLECT_CANCEL = "delFav";//取消收藏


    public static final String ADD_CUSTOM_INNER = "http://member.izijia.cn/index.php?s=/route/app/add_custom";//添加收藏点到我的定制 &docid=%s&token=%s&uid=%s
    public static final String CHECK_CUSTOM_INNER = "http://member.izijia.cn/index.php?s=/route/app/get_fav_iscustom"; //查询 添加收藏点到我的定制的状态
    public static final String INTERACT_CUSTOM = "addCustom";//定制
    public static final String INTERACT_CUSTOM_CANCEL = "delCustom";//取消定制


    //Details
    public static final String DestinationDetail_URL = "getdestination.do?lid=%s";
    public static final String Des_ViewSpotList_Url = "getdestinationpoint.do?lid=%s&tripid=";
    public static final String Des_ViewSpotDetail_Url = "getfocusinfo.do?spotid=%s";

    public static final String LineDetail_URL = "getrouteinfo.do?lid=%s";
    public static final String LineTrip_Url = "gettripdetail.do?lid=%s&tripid=";

    public static final String ViewSpot_Detail_url="getfocusinfo.do?spotid=";
    public static final String ParkDaze_Detail_url="getStopplace.do?pid=";

    public static final String SelfDrive_Detail_Url = "getSelfdrivegroup.do?lid=";
    public static final String CareChoose_Detail_Url = "getActivity.do?lid=";

    public static final String Moments_Detail_Url = "getWonderfulInfo.do?mid=";


    //List
    public static final String LineList_Url = "getchannelcontent.do?channelType=%s&city=%s&pageIndex=";


    /**
     * Broadcast Action
     */

    public static final String INIT_LEFT_MENU_ACTION = "com.ccpress.izijia.HomeTabFragment.InitLeftMenuAction";

    public static final String RADIO_CHECK_ACTION = "com.ccpress.izijia.HomeFragment.RadioCheckAction";
    public static final String RADIO_CHECK_ID = "com.ccpress.izijia.HomeFragment.RadioCheckId";

    public static final String TAG_CLICK_ACTION = "com.ccpress.izijia.LeftMenuFragment.TagClickAction";
    public static final String TAG_CLICK_INDEX = "com.ccpress.izijia.LeftMenuFragment.TagClickIndex";

    public static final String TAB_CHANGE_ACTION = "com.ccpress.izijia.HomeTabFragment.TabChangeAction";
    public static final String TAB_CHANGE_INDEX = "com.ccpress.izijia.HomeTabFragment.TagChangeIndex";

    public static final String MAIN_TAB_CHANGE_ACTION = "com.ccpress.izijia.MainTabFragment.MainTabChangeAction";
    public static final String MAIN_TAB_CHANGE_INDEX = "com.ccpress.izijia.MainTabFragment.TagChangeIndex";

    public static final String DISPLAY_PHOTO_LAYOUT_ACTION = "com.ccpress.izijia.HomeFragment.DisplayPhotoLayoutAction";

    public static final String GD_CITY_CHANGE_ACTION = "com.ccpress.izijia.HomeFragment.GDCityChangeAction";
    public static final String GD_CITY_CHANGE_CITY_STRING = "com.ccpress.izijia.HomeFragment.GDCityChangeCityString";
    public static final String GD_CITY_CHANGE_CODE_STRING = "com.ccpress.izijia.HomeFragment.GDCityChangeCodeString";

    public static final String CITY_CHANGE_ACTION = "com.ccpress.izijia.HomeFragment.CityChangeAction";
    public static final String CITY_CHANGE_CITY_STRING = "com.ccpress.izijia.HomeFragment.CityChangeCityString";
    public static final String CITY_CHANGE_CODE_STRING = "com.ccpress.izijia.HomeFragment.CityChangeCodeString";
    public static final String EDIT_CHANG_ACTION = "com.ccpress.izijia.HomeFragment.EditChangeAction";
    public static final String EDIT_CHANG_LIST_SIZE = "com.ccpress.izijia.HomeFragment.EditChangeListSize";
    public static final String EDIT_CHANG_LIST_NAME = "com.ccpress.izijia.HomeFragment.EditChangeListName";

    public static final String TOP_POPUP_LIST_CLICK_ACTION = "com.ccpress.izijia.HomeFragment.TopPopupListClickAction";
    public static final String TOP_POPUP_INTERACT_LIST_CLICK_ACTION = "com.ccpress.izijia.HomeFragment.TopPopupInteractListClickAction";

    public static final String TOP_POPUP_LIST_CLICK_INDEX = "com.ccpress.izijia.HomeFragment.TopPopupListClickIndex";
    public static final String TOP_POPUP_INTERACT_LIST_CLICK_INDEX = "com.ccpress.izijia.HomeFragment.TopPopupInteractListClickIndex";

    public static final String CART_TEAM_STATUS_ACTION = "com.ccpress.izijia.MapFragment.CaeTeamStatusAction";
    public static final String USER_INFO_CHANGE_ACTION= "com.ccpress.izijia.UserCenterFragment.UserInfoChangeAction";
    public static final String INTERACT_LIST_UPDATE = "com.ccpress.izijia.HomeInteractListFragment.InteractListUpdate";
    public static final String MAP_ROUTE_CLEAR_ACTION = "com.ccpress.izijia.MapFragment.MapRouteClearAction";

    /**
     * detail types
     * */
    public static String DETAIL_TYPE_LINE = "1";//常规线路
    public static String DETAIL_TYPE_DES = "2";//目的地
    public static String DETAIL_TYPE_ViewSpot = "3";//看点
    public static String DETAIL_TYPE_PARK = "4";//停车发呆地
    public static String DETAIL_TYPE_IMAGES = "11";//图片组
    public static String DETAIL_TYPE_LINE_UPLOAD = "13";//自建线路
    public static String DETAIL_TYPE_VIDEO = "14";//小视频
    public static String DETAIL_TYPE_DRIVE = "16";//手机自驾团
    public static String HOME_TYPE_DRIVE = "17";//目的地、线路文章
}
