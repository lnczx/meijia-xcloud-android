package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.activity.BindMobileActivity;
import com.meijialife.simi.activity.PartnerActivity;
import com.meijialife.simi.activity.PayOrderActivity;
import com.meijialife.simi.activity.PayZeroOrderActivity;
import com.meijialife.simi.activity.WebViewActivity;
import com.meijialife.simi.bean.PartnerDetail;
import com.meijialife.simi.bean.SecretarySenior;
import com.meijialife.simi.bean.ServicePrices;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.StringUtils;

/**
 * 秘书服务订单适配器
 * 
 */
public class SecretaryServiceAdapter extends BaseAdapter {
    private LayoutInflater inflater;
   // private ArrayList<SecretarySenior> mList;
    private List<ServicePrices> mList;
    private Context context;
    private String sec_id;
    private PartnerDetail partnerDetail;
    private ServicePrices servicePrices;//服务报价
    private UserInfo userInfo;

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    
    public SecretaryServiceAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mList = new ArrayList<ServicePrices>();
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
    }

    /**
     * 
     * @param contactList
     *            手机联系人数据
     * @param friendList
     *            现有好友数据
     */
    public void setData(List<ServicePrices> secData,PartnerDetail partnerDetail,UserInfo userInfo) {
        this.mList = secData;
        this.partnerDetail = partnerDetail;
        this.userInfo = userInfo;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.secraty_detail, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.item_tv_title);
            holder.tv_price = (TextView) convertView.findViewById(R.id.item_tv_prices);
            holder.tv_sub_title = (TextView) convertView.findViewById(R.id.item_tv_sub_title);
            holder.m_sec_icon = (ImageView) convertView.findViewById(R.id.m_sec_icon);
            holder.ll_partner_service = (LinearLayout)convertView.findViewById(R.id.ll_partner_service);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        ServicePrices sp = mList.get(position);
        holder.tv_title.setText(sp.getName());
        holder.tv_price.setText(sp.getDis_price() + "元");
        holder.tv_sub_title.setText(mList.get(position).getService_title());
        finalBitmap.display(holder.m_sec_icon,sp.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
        
        final String mobile = userInfo.getMobile();
        final String name = userInfo.getName();
        final Double disPrice =mList.get(position).getDis_price();
           /* holder.tv_buy.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(name)){//手机号为空，跳转绑定手机号
                        Intent intent = new Intent(context,BindMobileActivity.class);
                        context.startActivity(intent);
                    }else {
                        if(disPrice>0){//普通金额支付界面
                            Intent intent = new Intent(context, PayOrderActivity.class);
                            intent.putExtra("PartnerDetail",partnerDetail);
                            intent.putExtra("from", PayOrderActivity.FROM_MISHU);
                            intent.putExtra("flag", PayOrderActivity.FROM_FIND);
                            intent.putExtra("servicePrices",mList.get(position));
                            context.startActivity(intent);
                        }else {//免费咨询跳转到0元支付界面
                            Intent intent = new Intent(context, PayZeroOrderActivity.class);
                            intent.putExtra("PartnerDetail",partnerDetail);
                            intent.putExtra("from", PayOrderActivity.FROM_MISHU);
                            intent.putExtra("flag", PayOrderActivity.FROM_FIND);
                            intent.putExtra("servicePrices",mList.get(position));
                            context.startActivity(intent);
                        }
                      
                    }
                }
            });*/
        
        holder.ll_partner_service.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String detailUrl = mList.get(position).getDetail_url();
                if(StringUtils.isNotEmpty(detailUrl)){
                    Intent intent = new Intent(context,WebViewActivity.class);
                    intent.putExtra("url", detailUrl);
                    intent.putExtra("title","服务详情");
                    intent.putExtra("dis_price",disPrice);
                    intent.putExtra("partnerDetail", partnerDetail);
                    intent.putExtra("servicePrices", mList.get(position));
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    class Holder {
        TextView tv_title;
        TextView tv_price;
        LinearLayout ll_partner_service;
        ImageView m_sec_icon;
        TextView  tv_sub_title;
    }

    private ProgressDialog m_pDialog;

    public void showDialog() {
        if (m_pDialog == null) {
            m_pDialog = new ProgressDialog(context);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(true);
        }
        m_pDialog.show();
    }

    public void dismissDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.hide();
        }
    }

}
