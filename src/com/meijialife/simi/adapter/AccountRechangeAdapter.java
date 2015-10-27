package com.meijialife.simi.adapter;

import java.util.ArrayList;

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
import com.meijialife.simi.activity.PayOrderActivity;
import com.meijialife.simi.bean.RechangeList;

/**
 * 充值订单适配器
 * 
 */
public class AccountRechangeAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<RechangeList> mList;
    private Context context;

    public AccountRechangeAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mList = new ArrayList<RechangeList>();
    }

   
    public void setData(ArrayList<RechangeList> secData) {
        this.mList = secData;
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
        holder.tv_price.setText(mList.get(position).getDescription());

        holder.tv_buy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, mList.get(position).getPrice()+"元", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, PayOrderActivity.class);
                intent.putExtra("from",PayOrderActivity.FROM_MEMBER);
                intent.putExtra("name", mList.get(position).getName()+mList.get(position).getDescription());
                intent.putExtra("card_pay", mList.get(position).getCard_pay());
                intent.putExtra("card_value", mList.get(position).getCard_value());
                intent.putExtra("card_id", mList.get(position).getId());
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
