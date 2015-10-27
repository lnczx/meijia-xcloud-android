package com.meijialife.simi;

import android.os.Environment;

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
    /** 获取秘书列表接口 **/
    public static final String URL_GET_SEC = ROOT_URL + "sec/get_list.json";
    /** 秘书服务接口 **/
    public static final String URL_GET_SENIOR = ROOT_URL + "dict/get_seniors.json";
    
    /** 添加通讯录好友接口 **/
    public static final String URL_POST_FRIEND = ROOT_URL + "user/post_friend.json";
    /** 用户信息修改接口 **/
    public static final String URL_POST_USERINFO = ROOT_URL + "user/post_userinfo.json";
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

    // 用户协议
    public final static String URL_WEB_AGREE = HOST + "/html/simi-inapp/agreement.htm";
    public final static String URL_USER_HELP = HOST + "/html/simi-inapp/help.htm";
    public final static String URL_ABOUT_US = HOST + "/html/simi-inapp/about-us.htm";
    public final static String URL_MORE_INFO = HOST + "/html/simi-inapp/app-faxian-list.htm";
  
 
    
    
    
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
    
    public static  String CARD_ADD_TREAVEL_CONTENT = "";
    public static  String CARD_ADD_MEETING_CONTENT = "";
    public static  String CARD_ADD_MORNING_CONTENT = "";
    public static  String CARD_ADD_AFFAIR_CONTENT = "";
    public static  String CARD_ADD_NOTIFICATION_CONTENT = "";

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
    
     /** 会员充值在线支付成功同步接口 **/
     public static final String URL_POST_PUSH_BIND = ROOT_URL + "user/post_push_bind.json";
     
     
     //微信支付部分   
     //微信预支付接口   
     public static final String URL_ORDER_WEIXIN_PRE = ROOT_URL + "order/wx_pre.json";
     //微信查询接口
     public static final String URL_ORDER_WEIXIN_QUERY = ROOT_URL + "order/wx_order_query.json";
     //微信异步通知接口
     public static final String URL_ORDER_WEIXIN_NOTIFY = HOST + "/simi/wxpay-notify-ordercard.do";


}
