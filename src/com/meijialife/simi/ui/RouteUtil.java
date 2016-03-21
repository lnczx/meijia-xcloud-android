package com.meijialife.simi.ui;

import android.content.Context;
import android.content.Intent;

import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.activity.DynamicDetailsActivity;
import com.meijialife.simi.activity.FriendApplyActivity;
import com.meijialife.simi.activity.MainPlusLeaveListActivity;
import com.meijialife.simi.activity.OrderDetailsActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.utils.StringUtils;
import com.simi.easemob.EMConstant;
import com.simi.easemob.ui.ChatActivity;

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
        if (!StringUtils.isEmpty(category) && !StringUtils.isEmpty(action)) {
            if (category.equals("h5")) {
                Intent intent = new Intent(context, WebViewsActivity.class);
                intent.putExtra("url", goto_url);
                context.startActivity(intent);
            } else if (category.equals("app")) {
                if (action.equals("card")) {
                    Intent intent = new Intent(context, CardDetailsActivity.class);
                    intent.putExtra("card_id", params);
                    context.startActivity(intent);
                } else if (action.equals("feed")) {
                    Intent intent = new Intent(context, DynamicDetailsActivity.class);
                    intent.putExtra("feedId", params);
                    context.startActivity(intent);
                } else if (action.equals("checkin")) {

                } else if (action.equals("im")) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    /*
                     * if(conversation.isGroup()){ if(conversation.getType() == EMConversationType.ChatRoom){ // it's group chat
                     * intent.putExtra(EMConstant.EXTRA_CHAT_TYPE, EMConstant.CHATTYPE_CHATROOM); }else{ intent.putExtra(EMConstant.EXTRA_CHAT_TYPE,
                     * EMConstant.CHATTYPE_GROUP); } }
                     */
                    intent.putExtra(EMConstant.EXTRA_USER_ID, params);
                    context.startActivity(intent);

                } else if (action.equals("leave_pass")) {
                    Intent intent = new Intent(context, MainPlusLeaveListActivity.class);
                    context.startActivity(intent);
                } else if (action.equals("water")) {
                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderId", params);
                    intent.putExtra("orderType", 99);
                    context.startActivity(intent);
                } else if (action.equals("recycle")) {
                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderId", params);
                    intent.putExtra("orderType", 1);
                    context.startActivity(intent);
                } else if (action.equals("clean")) {
                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderId", params);
                    intent.putExtra("orderType", 2);
                    context.startActivity(intent);
                } else if (action.equals("teamwork")) {
                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderId", params);
                    intent.putExtra("orderType", 3);
                    context.startActivity(intent);
                } else if (action.equals("express")) {
                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderId", params);
                    intent.putExtra("orderType", 4);
                    context.startActivity(intent);
                } else if (action.equals("expy")) {
                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderId", params);
                    intent.putExtra("orderType", 1);
                    context.startActivity(intent);
                } else if (action.equals("friends")) {
                    Intent intent = new Intent(context,FriendApplyActivity.class);
                    context.startActivity(intent);
                }
            }
        }
    }
}
