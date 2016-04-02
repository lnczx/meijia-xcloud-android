package com.meijialife.simi.ui;

import android.content.Context;
import android.content.Intent;

import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.activity.CardListActivity;
import com.meijialife.simi.activity.CompanyListsActivity;
import com.meijialife.simi.activity.DiscountCardActivity;
import com.meijialife.simi.activity.DynamicDetailsActivity;
import com.meijialife.simi.activity.FriendApplyActivity;
import com.meijialife.simi.activity.FriendPageActivity;
import com.meijialife.simi.activity.MainPlusActivity;
import com.meijialife.simi.activity.MainPlusApplicationActivity;
import com.meijialife.simi.activity.MainPlusAssetListActivity;
import com.meijialife.simi.activity.MainPlusCarOrderActivity;
import com.meijialife.simi.activity.MainPlusCleanActivity;
import com.meijialife.simi.activity.MainPlusExpressActivity;
import com.meijialife.simi.activity.MainPlusLeaveDetailActivity;
import com.meijialife.simi.activity.MainPlusLeaveListActivity;
import com.meijialife.simi.activity.MainPlusSignInActivity;
import com.meijialife.simi.activity.MainPlusTeamActivity;
import com.meijialife.simi.activity.MainPlusWasterActivity;
import com.meijialife.simi.activity.MainPlusWaterActivity;
import com.meijialife.simi.activity.MyOrderActivity;
import com.meijialife.simi.activity.MyWalletActivity;
import com.meijialife.simi.activity.PointsShopActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.publish.PublishDynamicActivity;
import com.meijialife.simi.utils.StringUtils;

/**
 * @description：扫码路由跳转
 * @author： kerryg
 * @date:2016年3月19日
 */
public class RouteUtil {

    private Context context;

    public RouteUtil(Context context) {
        super();
        this.context = context;
    }
    public void Routing(String category, String action, String goto_url, String params) {
        Intent intent ;
        if (!StringUtils.isEmpty(category) && !StringUtils.isEmpty(action)) {
            if (category.equals("h5")) {
                intent = new Intent(context, WebViewsActivity.class);
                intent.putExtra("url", goto_url);
                context.startActivity(intent);
            } else if (category.equals("app")) {
                if (action.equals("index")) {//首页
                     intent = new Intent(context,MainActivity.class);
                    context.startActivity(intent);
                } else if (action.equals("discover")) {//发现
                    MainActivity mainActivity = (MainActivity)context;
                    mainActivity.changeFind();
                } else if (action.equals("sns")) {//圈子
                    MainActivity mainActivity = (MainActivity)context;
                    mainActivity.change2Contacts();
                } else if (action.equals("mine")) {//我的
                    MainActivity mainActivity = (MainActivity)context;
                    mainActivity.changePersonal();
                } else if (action.equals("work")) {//加号
                    Intent intent2 = new Intent(context, MainPlusActivity.class);
                    MainActivity mainActivity = (MainActivity)context;
                    context.startActivity(intent2);
                    mainActivity.overridePendingTransition(R.anim.activity_open, 0);
                } else if (action.equals("alarm")) {//事务提醒
                    intent = new Intent(context, CardListActivity.class);
                    intent.putExtra("cardType", "3");
                    context.startActivity(intent);
                } else if (action.equals("meeting")) {//会议安排
                     intent = new Intent(context, CardListActivity.class);
                     intent.putExtra("cardType", "1");
                    context.startActivity(intent);
                } else if (action.equals("notice")) {//通知公告
                    intent = new Intent(context, CardListActivity.class);
                    intent.putExtra("cardType", "2");
                    context.startActivity(intent);
                } else if (action.equals("interview")) {//面试邀约
                     intent = new Intent(context, CardListActivity.class);
                     intent.putExtra("cardType", "4");
                    context.startActivity(intent);
                } else if (action.equals("trip")) {//差旅规划
                     intent = new Intent(context, CardListActivity.class);
                     intent.putExtra("cardType", "5");
                    context.startActivity(intent);
                } else if (action.equals("feed_add")) {//发布动态
                    intent = new Intent(context, PublishDynamicActivity.class);
                    context.startActivity(intent);
                } else if (action.equals("card_detail")) {//跳转到卡片详情
                     intent = new Intent(context,CardDetailsActivity.class);
                     intent.putExtra("card_id",params);
                    context.startActivity(intent);
                }else if (action.equals("feed_detail")) {//跳转到动态详情
                     intent = new Intent(context,DynamicDetailsActivity.class);
                     intent.putExtra("feedId",params);
                    context.startActivity(intent);
                }else if (action.equals("feed")) {//跳转到动态列表
                   /*  intent = new Intent(context,FriendApplyActivity.class);
                    context.startActivity(intent);*/
                }else if (action.equals("checkin")) {//跳转到考勤
                     intent = new Intent(context,MainPlusSignInActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("friends")) {//跳转到好友审批页面
                     intent = new Intent(context,FriendApplyActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("im")) {//跳转到IM消息页面
                     intent = new Intent(context,FriendApplyActivity.class);
                     MainActivity mainActivity =(MainActivity)context;
                     mainActivity.change2IM();
                }else if (action.equals("leave")) {//跳转到请假列表页面
                    intent = new Intent(context,MainPlusLeaveListActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("leave_pass")) {//跳转到请假详情页面
                     intent = new Intent(context,MainPlusLeaveDetailActivity.class);
                     intent.putExtra("leave_id",params);
                     context.startActivity(intent);
                }else if (action.equals("express")) {//跳转到快递列表页面
                     intent = new Intent(context,MainPlusExpressActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("water")) {//跳转到送水订单列表
                     intent = new Intent(context,MainPlusWaterActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("teamwork")) {//跳转到团队建设详情
                     intent = new Intent(context,MainPlusTeamActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("expr")) {//速通宝
                     intent = new Intent(context,MainPlusCarOrderActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("company_pass")) {//跳转到公司申请列表
                     intent = new Intent(context,CompanyListsActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("app_tools")) {//跳转到应用中心
                     intent = new Intent(context,MainPlusApplicationActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("wallet")) {//跳转到钱包
                     intent = new Intent(context,MyWalletActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("coupons")) {//跳转到我的优惠券
                     intent = new Intent(context,DiscountCardActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("order")) {//跳转到订单列表
                    intent = new Intent(context,MyOrderActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("score")) {//跳转到积分商城
                    Intent intent6 = new Intent();
                    intent6.setClass(context, PointsShopActivity.class);
                    intent6.putExtra("navColor", "#E8374A");    //配置导航条的背景颜色，请用#ffffff长格式。
                    intent6.putExtra("titleColor", "#ffffff");    //配置导航条标题的颜色，请用#ffffff长格式。
                    intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP+"?user_id="+DBHelper.getUserInfo(context).getUser_id());    //配置自动登陆地址，每次需服务端动态生成。
                    context.startActivity(intent6);
                }else if (action.equals("boon")) {//跳转到福利商城
                     
                }else if(action.equals("add_friend")){
                    intent = new Intent(context,FriendPageActivity.class);
                    intent.putExtra("friend_id",params);
                    context.startActivity(intent);
                }else if(action.equals("clean")){//保洁列表页面
                    intent = new Intent(context,MainPlusCleanActivity.class);
                    context.startActivity(intent);
                }else if(action.equals("recycle")){//废品回收列表页面
                    intent = new Intent(context,MainPlusWasterActivity.class);
                    context.startActivity(intent);
                }else if (action.equals("card")) {
                    intent = new Intent(context, CardDetailsActivity.class);
                    intent.putExtra("card_id", params);
                    context.startActivity(intent);
                }else if (action.equals("asset")) {
                    intent = new Intent(context, MainPlusAssetListActivity.class);
                    context.startActivity(intent);
                }
            }
        }
    }
}
