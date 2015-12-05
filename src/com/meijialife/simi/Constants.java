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
    /**绑定手机号接口**/
    public static final String URL_POST_BIND_MOBILE = ROOT_URL + "user/bind_mobile.json";
    /**获取我的二维码接口**/
    public static final String URL_GET_MY_RQ_CODE = ROOT_URL +"user/get_qrcode.json";
    /**添加好友接口**/
    public static final String URL_GET_ADD_FRIEND = ROOT_URL +"user/add_friend.json";
    
    
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

    /**服务人员搜索**/
    public static final String URL_GET_PARTNER_LIST_BY_KW =ROOT_URL +"partner/search.json";
    /**获得热搜列表**/
    public static final String URL_GET_HOT_KW_LIST = ROOT_URL +"partner/get_hot_keyword.json";
    
    
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
    
    public static  String CARD_ADD_TREAVEL_CONTENT = "";
    public static  String CARD_ADD_MEETING_CONTENT = "";
    public static  String CARD_ADD_MORNING_CONTENT = "";
    public static  String CARD_ADD_AFFAIR_CONTENT = "";
    public static  String CARD_ADD_NOTIFICATION_CONTENT = "";
    public static  String ADDRESS_NAME_CONTENT = ""; 
    public static  String DISCOUNT_CARD_CONTENT = ""; 
    public static  String REAL_PAY_CONTENT = ""; 

   
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
     
     
     
     
     
     
     
     
     
     
}
