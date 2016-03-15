package com.meijialife.simi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;

import com.meijialife.simi.bean.ContactBean;

public class Constants {

    //我们的用户系统的 user_id = 1

//如果是环信的， 用户为  simi-user-1
    public static final String SERVICE_ID = "1";
    public static final String SERVICE_NUMBER = "400-169-1615";
    public static final String DESCRIPTOR = "com.umeng.share";

    public final static String AlipayHOST = "http://182.92.160.194";
    
    // 主机地址
    public static final String HOST = "http://123.57.173.36";
    // 基础接口
    public static final String ROOT_URL = HOST + "/simi/app/";

    // 获取验证码接口
    public final static String URL_GET_SMS_TOKEN = ROOT_URL + "user/get_sms_token.json";
    // 登录
    public final static String URL_LOGIN = ROOT_URL + "user/login.json";
    // 第三方登陆
    public final static String URL_THIRD_PARTY_LOGIN = ROOT_URL + "user/login-3rd.json";
    /** 用户详情接口 **/
    public static final String URL_GET_USER_INFO = ROOT_URL + "user/get_userinfo.json";
    /**绑定手机号接口**/
    public static final String URL_POST_BIND_MOBILE = ROOT_URL + "user/bind_mobile.json";
    /**获取我的二维码接口**/
    public static final String URL_GET_MY_RQ_CODE = ROOT_URL +"user/get_qrcode.json";
    /**添加好友接口**/
    public static final String URL_GET_ADD_FRIEND = ROOT_URL +"user/add_friend.json";
    /**获取频道列表接口**/
    public static final String URL_GET_CHANEL_LIST = ROOT_URL +"op/get_channels.json";
    /**获取频道内广告信息接口**/
    public static final String URL_GET_ADS_LIST = ROOT_URL + "op/get_ads.json";
    /**获得应用列表接口**/
    public static final String URL_GET_APP_TOOLS = ROOT_URL + "op/get_appTools.json";
    /**获得导航列表接口**/
    public static final String URL_GET_APP_INDEXS = ROOT_URL + "op/get_appIndexList.json";
    /**新增应用显示配置接口**/
    public static final String URL_GET_USER_APP_TOOLS = ROOT_URL + "op/user_app_tools.json";
    /**企业-员工考勤记录列表接口**/
    public static final String URL_GET_CHECKIN_LISTS = ROOT_URL + "company/get_checkins.json";
    /**企业-员工考勤记录接口**/
    public static final String URL_POST_CHECKIN = ROOT_URL + "company/checkin.json";
    
    
    /** app更新接口 **/
    public static final String URL_GET_VERSION = "http://182.92.160.194/" + "d/version.xml";// 测试用，需更换
    /** 城市列表接口 **/
    public static final String URL_GET_CITY_LIST = ROOT_URL + "city/get_list.json";
    /** 意见反馈接口 **/
    public static final String URL_POST_FEEDBACK = ROOT_URL + "user/post_feedback.json";
    /** 积分明细接口 **/
    public static final String URL_GET_SCORE_DETAILS = ROOT_URL + "user/get_score.json";
    //卡片列表接口
    public final static String URL_GET_CARD_LIST = ROOT_URL + "card/get_list.json";
    //卡片添加接口
    public final static String URL_GET_ADD_CARD = ROOT_URL + "card/post_card.json";
    //卡片取消接口 
    public final static String URL_GET_CANCEL_CARD = ROOT_URL + "card/card_cancel.json";
    /** 卡片详情接口 **/
    public static final String URL_GET_CARD_DETAILS = ROOT_URL + "card/get_detail.json";
    /** 卡片评论接口 **/
    public static final String URL_POST_CARD_COMMENT = ROOT_URL + "card/post_comment.json";
    /** 卡片评论列表 **/
    public static final String URL_GET_CARD_COMMENT_LIST = ROOT_URL + "card/get_comment_list.json";
    /** 卡片点赞接口 **/
    public static final String URL_POST_CARD_ZAN = ROOT_URL + "card/post_zan.json";
    /** 个人主页接口 **/
    public static final String URL_GET_USER_INDEX = ROOT_URL + "user/get_user_index.json";
    /** 获取好友列表接口 **/
    public static final String URL_GET_FRIENDS = ROOT_URL + "user/get_friends.json";
    /**获取好友动态列表接口**/
    public static final String URL_GET_FRIEND_DYNAMIC_LIST = ROOT_URL + "feed/get_list.json";
    /** 获取秘书列表接口 **/
    public static final String URL_GET_SEC = ROOT_URL + "sec/get_list.json";
    /** 秘书服务接口 **/
    public static final String URL_GET_SENIOR = ROOT_URL + "dict/get_seniors.json";
    /**用户获取用户图片接口**/
    public static final String URL_GET_USER_IMAGES = ROOT_URL + "user/get_user_imgs.json";
    /**封面相册上传多张图片接口**/
    public static final String URL_POST_COVER_ALBUM = ROOT_URL + "user/post_user_img.json";
    /**服务商列表接口**/
    public static final String URL_GET_USER_LIST = ROOT_URL+"partner/get_user_list.json";
    /**服务人员详情接口**/
    public static final String URL_GET_USER_DETAIL = ROOT_URL+"partner/get_user_detail.json";
    /**订单列表接口**/
    public static final String URL_GET_ORDER_GET_LIST = ROOT_URL +"order/get_list.json";
    /**订单详情**/
    public static final String URL_GET_ORDER_DETAIL = ROOT_URL +"order/get_detail.json";
    /**我的优惠券列表接口**/
    public static final String URL_GET_MY_DISCOUNT_CARD_LIST = ROOT_URL +"user/get_coupons.json";
    /**兑换优惠券接口**/
    public static final String URL_POST_EXCHANGE_DISCOUNT_CARD = ROOT_URL +"user/post_coupon.json";
    /**我的钱包接口（用户消费明细）**/
    public static final String URL_GET_WALLET_LIST = ROOT_URL +"user/get_detail_pay.json";
    /**app启动时获取当前地理位置接口**/
    public static final String URL_POST_TRAIL = ROOT_URL +"user/post_trail.json";

  
    
    //关于动态的接口
    /**动态点赞接口**/
    public static final String URL_POST_FEED_ZAN = ROOT_URL +"feed/post_zan.json";
    /**动态评论接口**/
    public static final String URL_POST_FEED_COMMENT = ROOT_URL +"feed/post_comment.json";
    /**发表动态接口**/
    public static final String URL_POST_FRIEND_DYNAMIC = ROOT_URL + "feed/post_feed.json";
    /**动态上传图片接口**/
    public static final String URL_POST_FEED_IMGS = ROOT_URL + "feed/post_feed_imgs.json";
    /**获取动态详情接口**/
    public static final String URL_GET_DYNAMIC_DETAIL = ROOT_URL + "feed/get_detail.json";
    /**获取动态评论的列表**/
    public static final String URL_GET_DYNAMIC_COMMENT_LIST = ROOT_URL + "feed/get_comment_list.json";
    /**动态发表评论的接口**/
    public static final String URL_POST_DYNAMIC_COMMENT = ROOT_URL + "feed/post_comment.json";
    //关于好友的接口
    public static final String URL_GET_COMPANY_DETAIL =ROOT_URL +"company/get_detail.json";
    
    //关于用户消息接口
    /**用户消息列表接口**/
    public static final String URL_GET_USER_MSG_LIST = ROOT_URL + "user/get_msg.json";
    
    //app帮助类接口
    /**送水列表接口**/
    public static final String URL_GET_WATER_LIST = ROOT_URL + "order/get_list_water.json";
    /**送水签收接口**/
    public static final String URL_POST_WATER_DONE = ROOT_URL + "order/post_done_water.json";
    /**送水下单接口**/
    public static final String URL_POST_ADD_WATER = ROOT_URL + "order/post_add_water.json";
    /**送水水产品口**/
    public static final String URL_GET_SERVICE_PRICE_LIST = ROOT_URL + "partner/get_default_service_price_list.json";
    /**送水订单详情接口**/
    public static final String URL_GET_DETAIL_WATER = ROOT_URL + "order/get_detail_water.json";
    /**送水订单状态**/
    public static final int WATER_ORDER_CLOSE = 0; // 已关闭
    public static final int WATER_ORDER_NOT_PAY = 1; // 未支付
    public static final int WATER_ORDER_HAS_PAY = 2; // 已支付
    public static final int WATER_ORDER_PAYING = 3; // 处理中
    //智能配置h5链接
    public static final String WATER_ORDER_H5 = "http://123.57.173.36/simi-h5/show/water-set.html"; // 处理中
    
    
    //加号中应用接口
    public static final String URL_GET_APP_HELP_DATA = ROOT_URL + "op/get_appHelp.json";

    /**服务人员搜索**/
    public static final String URL_GET_PARTNER_LIST_BY_KW =ROOT_URL +"partner/search.json";
    /**获得热搜列表**/
    public static final String URL_GET_HOT_KW_LIST = ROOT_URL +"partner/get_hot_keyword.json";
    /**获得用户所属企业列表**/
    public static final String URL_GET_COMPANY_LIST =ROOT_URL +"company/get_by_user.json";
    /**获得企业员工列表**/
    public static final String URL_GET_STAFF_LIST = ROOT_URL +"company/get_staffs.json";
    /**设置默认企业**/
    public static final String URL_POST_SET_DEFAULT = ROOT_URL +"company/set_default.json";
    /**用户-公司配置信息接口**/
    public static final String URL_GET_COMPANY_SETTING = ROOT_URL +"company/get_company_setting.json";
    
    /** 添加通讯录好友接口 **/
    public static final String URL_POST_FRIEND = ROOT_URL + "user/post_friend.json";
    /** 用户信息修改接口 **/
    public static final String URL_POST_USERINFO = ROOT_URL + "user/post_userinfo.json";
    /** 用户头像上传接口 **/
    public static final String URL_POST_USERIMG = ROOT_URL + "user/post_user_head_img.json";
    /** 获取用户地址接口 **/
    public static final String URL_GET_ADDRS = ROOT_URL + "user/get_addrs.json";
    /** 地址提交接口 **/
    public static final String URL_POST_ADDRS = ROOT_URL + "user/post_addrs.json";
    /** 地址删除接口 **/
    public static final String URL_POST_DEL_ADDRS = ROOT_URL + "user/post_del_addrs.json";
    /** 按月份获取卡片日期分布接口 **/
    public static final String URL_GET_TOTAL_BY_MONTH = ROOT_URL + "card/total_by_month.json";
    /** 获取提醒闹钟的卡片列表 **/
    public static final String URL_GET_REMINDS = ROOT_URL + "card/get_reminds.json";
    /** 秘书处理卡片接口 **/
    public static final String URL_POST_SEC_DO = ROOT_URL + "card/sec_do.json";
    /** 获取用户接口 **/
    public static final String URL_GET_SEC_USER = ROOT_URL + "sec/get_users.json";

    // 用户协议
    public final static String URL_WEB_AGREE = HOST + "/html/simi-inapp/agreement.htm";
    public final static String URL_USER_HELP = HOST + "/html/simi-inapp/help.htm";
    public final static String URL_ABOUT_US = HOST + "/html/simi-inapp/about-us.htm";
    public final static String URL_MORE_INFO = HOST + "/html/simi-inapp/app-faxian-list.htm";
   //行政人学院
    public final static String URL_XUEYUAN = "http://mishuzhuli.com";
     
    /**订单状态**/
    public static final int ORDER_NOT_PAY = 1; // 未支付
    public static final int ORDER_HAS_PAY = 2; // 已支付
    
    
    /*** 网络返回状态码 ***/
    public static final int STATUS_SUCCESS = 0; // 成功
    public static final int STATUS_SERVER_ERROR = 100; // 服务器错误
    public static final int STATUS_PARAM_MISS = 101; // 缺失必选参数
    public static final int STATUS_PARAM_ILLEGA = 102; // 参数值非法
    public static final int STATUS_OTHER_ERROR = 999; // 其他错误

    /** 微信分享APPid **/
    public static final String WX_APP_ID = "wx93aa45d30bf6cba3";
    
    
    /** 常量 **/
    public static  String MAIN_PLUS_FLAG = "flag";
    public static final String MEETTING = "meeting";//会议
    public static final String MORNING = "morning"; 
    public static final String AFFAIR = "affair"; 
    public static final String NOTIFICATION = "notification"; 
    public static final String TRAVEL = "travel";//旅行
    public static final String REMARK = "remark";//
    public static final String LEAVE = "leave";//

    //应用中心菜单类别
    public static final String MENU_TYPE_T = "t";//t=工具与服务
    public static final String MENU_TYPE_D = "d";//t=成长与赚钱
    
    public static  String CARD_ADD_TREAVEL_CONTENT = "";
    public static  String CARD_ADD_MEETING_CONTENT = "";
    public static  String CARD_ADD_MEETING_SETTING = "";
    public static  String CARD_ADD_MORNING_CONTENT = "";
    public static  String CARD_ADD_AFFAIR_CONTENT = "";
    public static  String CARD_ADD_NOTIFICATION_CONTENT = "";
    public static  String ADDRESS_NAME_CONTENT = ""; 
    public static  String DISCOUNT_CARD_CONTENT = ""; 
    public static  String REAL_PAY_CONTENT = ""; 
    public static  String WATER_ADD_REMARK = "";
    public static  String WATER_ADD_ADDRESS = "";
    public static  String WATER_BAND_NAME = "";
    public static  String WATER_BAND_MONEY = "";
    public static  String WATER_TYPE_NAME = "";
    public static  String LEAVE_TYPE_REMARK = "";
    public static  String LEAVE_TYPE_NAME= "";



   
    /**封面相册常量**/
    public static String COVER_ALBUM_INTRODUCE_CONTENT ="";
    
    /**二维码扫描返回常量**/
    public static String SCAN_RQ_TAG = "xcloud";
    public static String RQ_TAG_FRIEND = "xcloud://";//标识加好友扫描
    public static String RQ_TAG_OTHER = "xcloud-h5://";//标识其他类型扫描

    /**企业注册H5页面**/
    public final static String HAS_COMPANY ="http://123.57.173.36/simi-h5/show/company-reg.html";
    /**二维码扫描类型识别**/
    public static final String QR_TAG_COMPANY ="company";//公司注册或人员加入
    public static final String QR_TAG_MEETING = "meeting";//会以或会议室
    public static final String QR_TAG_EXPRESS = "express";//快递
    public static final String QR_TAG_WATER = "water";//送水
    public static final String QR_TAG_CLEAN = "clean";//保洁
    public static final String QR_TAG_GREEN = "green";//绿植
    public static final String QR_TAG_FRIEND = "friend";//绿植
    public static final String QR_ACTION_NEW = "add";//添加
    public static final String QR_ACTION_SEE = "see";//查看

    /** 本地临时文件根目录 **/
     public static final String PATH_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Simi";
    //
    //
    // /** 基础数据接口 **/
    // public static final String URL_GET_BASE = ROOT_URL + "dict/get_base_datas.json";
    // /** 订单列表接口 **/
     public static final String URL_GET_ORDER_LIST = ROOT_URL + "order/get_list.json";
    // /** 订单详情接口 **/
    // public static final String URL_GET_ORDER_DETAIL = ROOT_URL + "order/get_detail.json";
    // /** 订单是否可取消验证 */
    // public static final String URL_POST_ORDER_PRE_CANCEL = ROOT_URL + "order/pre_order_cancel.json";
    // /** 订单取消接口 **/
    // public static final String URL_POST_ORDER_CANCEL = ROOT_URL + "order/post_order_cancel.json";
    // /** 订单评价接口 **/
    // public static final String URL_POST_ORDER_RATE = ROOT_URL + "order/post_rate.json";
    // /** 账号信息 **/
    // public static final String URL_GET_USERINFO = ROOT_URL + "user/get_userinfo.json";
    // /** 获取地址列表 **/
    // public static final String URL_GET_ADDRS = ROOT_URL + "user/get_addrs.json";
    // /** 地址提交接口 **/
    // public static final String URL_POST_ADDRS = ROOT_URL + "user/post_addrs.json";
    // /** 地址删除接口 **/
    // public static final String URL_POST_DEL_ADDRS = ROOT_URL + "user/post_del_addrs.json";
     /**私密卡购买 **/
     public static final String URL_POST_SENIOR_BUY = ROOT_URL + "user/senior_buy.json";
     /** 私密卡在线支付成功同步接口 **/
     public static final String URL_POST_SENIOR_ONLINE = ROOT_URL + "user/senior_online_pay.json";
     /** 获取充值卡列表 **/ 
     public static final String URL_GET_CARDS = ROOT_URL + "dict/get_cards.json";
     /** 会员充值接口 **/
     public static final String URL_POST_CARD_BUY = ROOT_URL + "user/card_buy.json";
     /** 会员充值在线支付成功同步接口 **/
     public static final String URL_POST_CARD_ONLINE = ROOT_URL + "user/card_online_pay.json";
    
     /**服务订单下单接口**/
     public static final String URL_POST_PARTNER_SERVICE_BUY=ROOT_URL + "order/post_add.json";
     /**已存在的订单下单接口**/
     public static final String URL_POST_EXISTED_PARTNER_SERVICE_BUY=ROOT_URL + "order/post_pay.json";
  
     
     
     /** 会员充值在线支付成功同步接口 **/
     public static final String URL_POST_PUSH_BIND = ROOT_URL + "user/post_push_bind.json";
     public static final String URL_POST_SCORE_SHOP = ROOT_URL + "user/score_shop";
     
     //微信支付部分   
     //微信预支付接口   
     public static final String URL_ORDER_WEIXIN_PRE = ROOT_URL + "order/wx_pre.json";
     //微信查询接口
     public static final String URL_ORDER_WEIXIN_QUERY = ROOT_URL + "order/wx_order_query.json";
     //微信异步通知接口
     public static final String URL_ORDER_WEIXIN_NOTIFY = HOST + "/simi/wxpay-notify-ordercard.do";

     /*
      * H5页面链接
      */
     //知识库
     public static final String SHOP_URL = "http://mishuzhuli.com/category/xingzhengbaike";
     //认证考试
     public static final String ATTEST_URL = "http://mishuzhuli.com/category/renzhengkaoshi";
     //培训
     public static final String MONEY_URL = "http://123.57.173.36/simi-h5/sec/#!/register.html";
     //积分赚钱
     public static final String TRAIN_URL = "http://mishuzhuli.com/category/wendangfanwen";
     //云考勤
     public static final String YUN_KAO_QIN = "http://123.57.173.36/simi-h5/show/order-checkin.html";
     //会议室
     public static final String HUI_YI_SHI= "http://123.57.173.36/simi-h5/show/order-meeting.html";
     //送水
     public static final String SONG_SHUI = "http://123.57.173.36/simi-h5/show/order-water.html";
     //保洁
     public static final String BAO_JIE = "http://light.yunjiazheng.com/oncecleaning/";
     //快递
     public static final String KUAI_DI = "http://m.kuaidi100.com/";
     //绿植
     public static final String LV_ZHI = "http://123.57.173.36/simi-h5/show/order-green.html";
     //开店
     public static final String KAI_DIAN = "http://123.57.173.36/simi-h5/show/store-my-index.html?user_id=";
     
     
     /**保存卡片创建的联系人**/
     public static ArrayList<String> finalContactList = new ArrayList<String>();
   
     public static HashMap<String,ContactBean> finalContacBeantMap = new HashMap<String,ContactBean>();
    
     /**分享跳转链接**/
     public static String SHARE_TARGET_URL = "http://123.57.173.36//simi-h5/show/card-share.html?card_id=";
     
     public static String SHARE_CUSTOMER_TARGET_URL = "http://51xingzheng.cn/h5-app-download.html";
     /**分享标题**/
     public static String SHARE_TITLE = "云行政，企业行政人力服务平台";
     /**分享内容**/
     public static String SHARE_CONTENT ="有来自好友的分享，点击查看详情。云行政，极大降低企业运行成本，极速提升企业工作效率，快来试试吧！";
     
     //checkdIndex标记用于切换=动态，好友，消息
     public static int checkedIndex = 0;
     //动态标题
     public static String FEED_TITLE ="";
     /**加号签到h5链接**/
     public static String PLUS_SIGN_URL = "http://123.57.173.36/simi-h5/show/checkin-index.html?user_id=";

     //废品回收
     /**废品回收图片链接**/
     public static final String WASTER_ICON_URL = "http://123.57.173.36/simi-h5/icon/icon-dingdan-caolv.png";
     /**废品回收订单列表接口**/
     public static final String URL_GET_LIST_WASTER = ROOT_URL + "order/get_list_recycle.json";
     /**废品回收H5链接**/
     public static final String H5_WASTER_URL = "http://123.57.173.36/simi-h5/show/recycle-price.html";
     /**废品回收下单接口**/
     public static final String POST_WASTER_ORDER_URL = ROOT_URL + "order/ post_add_recycle.json";
     
     //保洁订单
     /**保洁订单列表**/
     public static final String GET_CLEAN_ORDER_URL = ROOT_URL + "order/get_list_clean.json";
     /**保洁图片链接**/
     public static final String CLEAN_ICON_URL = "http://123.57.173.36/simi-h5/icon/icon-dingdan-qianlan.png";
     /**保洁H5链接**/
     public static final String H5_CLEAN_URL = "http://123.57.173.36/simi-h5/show/clean-set.html";
     /**保洁下单接口**/
     public static final String POST_CLEAN_ORDER_URL = ROOT_URL + "order/post_add_clean.json";
     /**保洁订单详情**/
     public static final String URL_GET_DETAIL_CLEAN = ROOT_URL + "order/get_detail_clean.json";

     
     //团建
     /**团建图片链接**/
     public static final String TEAM_ICON_URL = "http://123.57.173.36/simi-h5/icon/icon-dingdan-chenghuang.png";
     /**团建订单列表**/
     public static final String GET_TEAM_ORDER_URL = ROOT_URL + "order/get_list_team.json";
     /**团建H5链接**/
     public static final String H5_TEAM_URL = "http://123.57.173.36/simi-h5/show/teamwork-set.html";
     /**团队建设下单接口**/
     public static final String POST_ADD_TEAM = ROOT_URL + "order/post_add_team.json";
     /**团队建设订单详情**/
     public static final String URL_GET_DETAIL_TEAM = ROOT_URL + "order/get_detail_team.json";
    
     //请假
     /**请假订单列表**/
     public static final String GET_LEAVE_ORDER_URL = ROOT_URL + "user/leave_list.json";
     /**请假下单接口**/
     public static final String POST_LEAVE_ORDER_URL = ROOT_URL + "user/post_leave.json";
     /**请假详情接口**/
     public static final String GET_LEAVE_DETAIL_URL = ROOT_URL + "user/leave_detail.json";
     /**请假审批接口**/
     public static final String POSE_LEAVE_PASS_URL = ROOT_URL + "user/leave_pass.json";
     /**请假撤销接口**/
     public static final String POSE_LEAVE_CANCEL_URL = ROOT_URL + "user/leave_cancel.json";
     
     //快递
     /**快递订单列表**/
     public static final String GET_LIST_EXPRESS_URL = ROOT_URL + "record/get_list_express.json";
     /**快递H5链接**/
     public static final String H5_EXPRESS_URL = "http://m.kuaidi100.com/courier/search.jsp";
     /**快递下单接口**/
     public static final String POST_ADD_EXPRESS_URL = ROOT_URL + "record/post_add_express.json";
     /**快递详情**/
     public static final String GET_DETAIL_EXPRESS_URL = ROOT_URL + "record/get_detail_express.json";
     /**快递图片链接**/
     public static final String EXPRESS_ICON_URL = "http://123.57.173.36/simi-h5/icon/icon-dingdan-molv.png";
     
}
