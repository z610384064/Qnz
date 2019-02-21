package com.rd.qnz.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;

import java.util.List;
import java.util.Map;

public class ProductContentQuotaAllBankAdapter extends BaseAdapter {

    private List<Map<String, String>> list;

    private Context context;

    public ProductContentQuotaAllBankAdapter(Context context, List<Map<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    public void notifyDataSetChanged(List<Map<String, String>> list) {
        this.list = list;
        super.notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.buy_quota_all_bank_item, null);
            holder.all_icon = (ImageView) convertView.findViewById(R.id.all_icon);
            holder.bank_name = (TextView) convertView.findViewById(R.id.bank_name);
            holder.bank_card = (TextView) convertView.findViewById(R.id.bank_card);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.size() > 0) {
            holder.bank_name.setText(list.get(position).get(BaseParam.QIAN_ALL_BANK_BANKNAME));
            holder.bank_card.setText("单笔最高" + AppTool.zhuanhua(list.get(position).get(BaseParam.QIAN_ALL_BANK_PERDEALLIMIT)) + "万");
            holder.all_icon.setBackgroundResource(AppTool.BankIcon2(list.get(position).get(BaseParam.QIAN_ALL_BANK_BANKNAME)));
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView all_icon;
        TextView bank_name;
        TextView bank_card;
    }

}
