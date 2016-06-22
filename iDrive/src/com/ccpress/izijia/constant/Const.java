package com.ccpress.izijia.constant;

public interface Const {

    public static final String DOMAIN = "http://member.izijia.cn/";


    /**
     * 登录
     */
    public static final String LOGIN = DOMAIN
            + "index.php?s=/ucenter/app/login";

    /**
     * 注册获取验证码
     */
    public static final String VERIFY = DOMAIN
            + "index.php?s=/ucenter/app/get_verify";

    /**
     * 注册
     */
    public static final String REGISTER = DOMAIN
            + "index.php?s=/ucenter/app/register";

    /**
     * 注册之后补充信息
     */
    public static final String SUPPLY_INFO = DOMAIN
            + "index.php?s=/ucenter/app/save_info";

    /**
     * 重置密码
     */
    public static final String RESET_PASS = DOMAIN
            + "index.php?s=/ucenter/app/reset_pwd";

    /**
     * 修改密码
     */
    public static final String ALTER_PASS = DOMAIN
            + "index.php?s=/ucenter/app/alterPwd";

    /**
     * 我的分组
     */
    public static final String MY_GROUP = DOMAIN
            + "index.php?s=/interaction/service/myGroups";

    /**
     * 新建分组
     */
    public static final String NEW_GROUP = DOMAIN
            + "index.php?s=/interaction/service/createGroup";

    /**
     * 删除分组
     */
    public static final String DEL_GROUP = DOMAIN
            + "index.php?s=/interaction/service/delGroup";

    /**
     * 编辑分组
     */
    public static final String EDIT_GROUP = DOMAIN
            + "index.php?s=/interaction/service/editGroup";

    /**
     * 新信息提醒设置*
     */
    public static final String SWITCH_GROUP_MESSAGE = DOMAIN
            + "index.php?s=/interaction/service/switchGroupMessage";

    /**
     * 分组成员
     */
    public static final String GET_USER = DOMAIN
            + "index.php?s=/interaction/service/getUserFromGroup";

    /**
     * 搜索成员
     */
    public static final String SEARCH_USER = DOMAIN
            + "index.php?s=/interaction/service/searchUser";

    /**
     * 删除成员
     */
    public static final String DEL_USER = DOMAIN
            + "index.php?s=/interaction/service/delUserFromGroup";

    /**
     * 添加成员
     */
    public static final String ADD_USER = DOMAIN
            + "index.php?s=/interaction/service/addUserToGroup";


    /**
     * 保存需要上传车队地理位置的车队ID
     */
    public static final String CAR_TEAM_ID = "car_team_id";

    /**
     * 上传车队地理位置action
     */
    public static final String ACTION_UPLOAD_CAR_LOCAL = "com.ccpress.izijia.upload_car_local";

    /**
     * 我的收藏-看点列表
     */
    public static final String COLLECT_VIEW = DOMAIN
            + "index.php?s=/favorite/app/fav_focus_list";

    /**
     * 我的收藏-私享列表
     */
    public static final String COLLECT_SHARE = DOMAIN
            + "index.php?s=/favorite/app/fav_own_share_list";

    /**
     * 我的收藏-高德地图点列表
     */
    public static final String COLLECT_MAP = DOMAIN
            + "index.php?s=/favorite/app/fav_amap_list";
//	http://member.izijia.cn/index.php?s=/favorite/app/fav_zjt_list
    /** 我的收藏-停车发呆地列表 */
    /**
     * 我的收藏-自驾团列表
     */
    public static final String COLLECT_ZJT = DOMAIN
            + "index.php?s=/favorite/app/fav_zjt_list";
    public static final String COLLECT_YD = DOMAIN
            + "index.php?s=/favorite/app/fav_yd_list";
    public static final String COLLECT_STOP = DOMAIN
            + "index.php?s=/favorite/app/fav_stop_list";

    /**
     * 我的收藏-照片列表
     */
    public static final String COLLECT_PIC = DOMAIN
            + "index.php?s=/favorite/app/fav_pic_list";

    /**
     * 修改个性签名
     */
    public static final String ALTER_SIGNATURE = DOMAIN
            + "index.php?s=/ucenter/app/alter_signature";

    /**
     * 我的车队
     */
    public static final String MY_CAR_TEAM = DOMAIN
            + "index.php?s=/fleet/app/all_team";

    /**
     * 上传个人位置
     */
    public static final String UPLOAD_LOCAL = DOMAIN
            + "index.php?s=/fleet/app/upload_loc";

    /**
     * 开启车队
     */
    public static final String SWITCH_ON = DOMAIN
            + "index.php?s=/fleet/app/switch_on";

    /**
     * 我的收藏-线路列表
     */
    public static final String COLLECT_ROUTE = DOMAIN
            + "index.php?s=/favorite/app/fav_route_list";

    /**
     * 我的收藏-删除相应收藏的内容
     */
    public static final String DEL_FAV = DOMAIN
            + "index.php?s=/favorite/app/delFav";

    /**
     * 删除我的线路
     */
    public static final String DEL_ROUTE = DOMAIN
            + "index.php?s=/route/app/deleteMyRoute";

    /**
     * 我的订单列表
     */
    public static final String ORDER_LIST = DOMAIN
            + "index.php?s=/order/app/order_list";

    /**
     * 取消我的订单（允许多id取消）
     */
    public static final String CANCEL_ORDER = DOMAIN
            + "index.php?s=/order/app/cancel_order";

    /**
     * 取消收藏
     */
    public static final String CANCEL_COLLECT = DOMAIN
            + "index.php?s=/favorite/app/delFav";

    /**
     * 上传，修改头像图片
     */
    public static final String ALTER_AVATAR = DOMAIN
            + "index.php?s=/ucenter/app/alter_avatar";

    /**
     * 我的关注
     */
    public static final String MY_ATTENTION = DOMAIN
            + "index.php?s=/ucenter/app/following";

    /**
     * 我的粉丝
     */
    public static final String MY_FANS = DOMAIN
            + "index.php?s=/ucenter/app/fans";

    /**
     * 添加关注
     */
    public static final String ADD_ATTENTION = DOMAIN
            + "index.php?s=/ucenter/app/follow";

    /**
     * 取消关注
     */
    public static final String CANCEL_ATTENTION = DOMAIN
            + "index.php?s=/ucenter/app/unfollow";

    /**
     * 我的照片列表
     */
    public static final String MY_PHOTO_LIST = DOMAIN
            + "index.php?s=/interaction/appAdmin/pic_list";

    /**
     * 我的照片详情
     */
    public static final String MY_PHOTO_DETAIL = DOMAIN
            + "index.php?s=/interaction/app/pic_detail";

    /**
     * 发送私信
     */
    public static final String SEND_MSG = DOMAIN
            + "index.php?s=/ucenter/app/send_message";

    /**
     * 我的照片详情页的评论列表
     */
    public static final String MY_PHOTO_COMMENT = DOMAIN
            + "index.php?s=/home/addons/execute/_addons/LocalComment/_controller/App/_action/comment_list/app/Interaction/mod/interaction/row_id/";

    /**
     * 个人信息中心
     */
    public static final String MY_INFO = DOMAIN
            + "index.php?s=/ucenter/app/user_center_info";

    /**
     * 定制中所有的看点列表
     */
    public static final String VIEW_LIST = DOMAIN
            + "index.php?s=/route/app/getFocusList";

    /**
     * 定制中所有的线路列表
     */
    public static final String LINE_LIST = DOMAIN
            + "index.php?s=/route/app/getRouteList";
    /**
     * 定制-线路-看点
     */
    public static final String LINE_LINE = "http://182.92.79.205:8080/coreapp/mobilesearch/getlineallviews.do?";
    /**
     * 定制中删除看点
     */
    public static final String LINE_DELETE = DOMAIN
            + "index.php?s=/route/app/del_custom_view_spot";

    /**
     * 定制中删除线路
     */
    public static final String LINE_TOUTE = DOMAIN
            + "index.php?s=/route/app/del_custom_route";

    /**
     * 获取我的对话列表，并获取最新一条消息数据
     */
    public static final String MY_MSG_LIST = DOMAIN
            + "index.php?s=/ucenter/app/message_users";

    /**
     * 创建线路
     */
    public static final String CRETE_ROUTE = DOMAIN
            + "index.php?s=/route/app/create_route";
    /**
     * 存线路看点，用于在添加看点后、拖动排序后、删除看点后，点击页面右上角保存按钮
     */
    public static final String SAVE_NEW_POINT = DOMAIN
            + "index.php?s=/route/app/edit";

    /**
     * 我的线路
     */
    public static final String ROUTE_LIST = DOMAIN
            + "index.php?s=/route/app/getMyRouteList";

    /**
     * 点击查看我与某人的所有聊天记录内容
     */
    public static final String TALK_MESSAGE_LIST = DOMAIN
            + "index.php?s=/ucenter/app/talk_message_list";

    /**
     * 举报
     */
    public static final String REPORT = DOMAIN
            + "index.php?s=/home/addons/execute/_addons/Report/_controller/App/_action/report";

    /**
     * 线路详情
     */
    public static final String LINE_DETAIL = DOMAIN
            + "index.php?s=/Interaction/index/get_line_detail";

    public static final String LINE_ABC = "http://182.92.79.205:8080/coreapp/mobilesearch/gettripdetail.do?";
    /**
     * 前编辑的线路中的看点
     */
    public static final String DELETE_LINEDETAIL_POINT = DOMAIN
            + "index.php?s=/route/app/delSpots";

    /**
     * 修改线路名，线路封面，线路描述，线路标签
     */
    public static final String EDIT_ROUTE = DOMAIN
            + "index.php?s=/route/app/editRoute";

    /**
     * 张照片上传，含描述。返回上传后的id号
     */
    public static final String UPLOAD_PIC = DOMAIN
            + "index.php?s=/route/app/upload_pic";

    /**
     * 互动页面，删除我的帖子
     */
    public static final String DELETE_INTERACTION = DOMAIN
            + "index.php?s=/interaction/service/delInteraction";

    /**
     * 批量提交照片id号到编辑线路下，构成游记
     */
    public static final String ADD_ROUTE_TRAVAL = DOMAIN
            + "index.php?s=/route/app/addRouteTraval";

    /**
     * QQ登录
     */
    public static final String QQ_LOGIN = DOMAIN
            + "index.php?s=/home/addons/execute/_addons/sync_login/_controller/app/_action/callback/type/qq";

    /**
     * 微信登录
     */
    public static final String WX_LOGIN = DOMAIN
            + "index.php?s=/home/addons/execute/_addons/sync_login/_controller/app/_action/callback/type/weixin";

    /**
     * 新浪登录
     */
    public static final String SINA_LOGIN = DOMAIN
            + "index.php?s=/home/addons/execute/_addons/sync_login/_controller/app/_action/callback/type/sina";

    /**
     * 意见反馈
     */
    public static final String FEED_BACK = DOMAIN
            + "index.php?s=/ucenter/app/feedback";
    /**
     * 关于我们
     */
    public static final String ABOUT = DOMAIN
            + "aboutus.html";
    /**
     * 用户协议
     */
    public static final String PROTOCOL = DOMAIN
            + "service.html";
    /**
     * 用户帮助
     */
    public static final String HELP = DOMAIN
            + "help.html";
    /**
     * 发布线路至互动
     */
    public static final String PUBLISH_LINE = DOMAIN
            + "index.php?s=/interaction/appAdmin/pub_to_interaction";

    /**
     * 用户登录id
     */
    public static final String UID = "com.ccpress.izijia.uid";

    /**
     * 用户登录auth
     */
    public static final String AUTH = "com.ccpress.izijia.auth";

    public static final String USERNAME = "com.ccpress.izijia.username";

    public static final String AVATAR = "com.ccpress.izijia.avatar";
    /**
     * 用户地理城市位置
     */
    public static final String CITY = "com.ccpress.izijia.city";

    public static final int REQUEST_CODE_IMAGE_CAPTURE = 0;

    public static final int REQUEST_CODE_IMAGE_SELECTE = 1;

    public static final int REQUEST_CODE_IMAGE_CROP = 2;

    /**
     * 定制删除了看点，更新定制界面
     */
    public static final String ACTION_UPDATE_VIEW = "com.ccpress.izijia.update_view";
}
