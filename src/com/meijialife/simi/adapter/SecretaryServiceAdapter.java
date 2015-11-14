package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.activity.PartnerActivity;
import com.meijialife.simi.activity.PayOrderActivity;
import com.meijialife.simi.bean.PartnerDetail;
import com.meijialife.simi.bean.SecretarySenior;
import com.meijialife.simi.bean.ServicePrices;

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

    public SecretaryServiceAdapter(Context context) {
        this.context = context;
        //this.sec_id = sec_id;
        inflater = LayoutInflater.from(context);
        mList = new ArrayList<ServicePrices>();
    }

    /**
     * 
     * @param contactList
     *            手机联系人数据
     * @param friendList
     *            现有好友数据
     */
    public void setData(List<ServicePrices> secData,PartnerDetail partnerDetail) {
        this.mList = secData;
        this.partnerDetail = partnerDetail;
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
            convertView = inflater.inflate(R.layout.secreteary_service_list_item, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.item_tv_title);
            holder.tv_price = (TextView) convertView.findViewById(R.id.item_tv_price);
            holder.tv_buy = (TextView) convertView.findViewById(R.id.item_tv_buy);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.tv_title.setText(mList.get(position).getName() + "：");
        holder.tv_price.setText(mList.get(position).getPrice() + "元");

        holder.tv_buy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, mList.get(position).getPrice()+"元", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, PayOrderActivity.class);
                intent.putExtra("PartnerDetail",partnerDetail);
                intent.putExtra("from", PayOrderActivity.FROM_MISHU);
                intent.putExtra("servicePrices",mList.get(position));
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class Holder {
        TextView tv_title;
        TextView tv_price;
        TextView tv_buy;
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
