package com.rd.qnz.mine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nineoldandroids.view.ViewHelper;
import com.rd.qnz.R;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyBankListAdapter extends BaseAdapter {

    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private Context context;
    private Map<Integer, Boolean> mapViewDelete = new HashMap<Integer, Boolean>();

    public MyBankListAdapter(Context context, List<Map<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    public void notifyDataSetChanged(List<Map<String, String>> list) {
        this.list = list;
        super.notifyDataSetChanged();
    }

    public void setInfoList(List<Map<String, String>> list) {
        this.list = list;
        setInfoList();
    }

    private void setInfoList() {

        for (int i = 0; i < list.size(); i++) {
            mapViewDelete.put(i, false);
        }
    }

    public List<Map<String, String>> getInfoList() {
        return this.list;
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
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.mybank_list_item_gai, null);
            holder.back_icon = (ImageView) convertView.findViewById(R.id.back_icon);
            holder.bank_name = (TextView) convertView.findViewById(R.id.bank_name);
            holder.bank_wei = (TextView) convertView.findViewById(R.id.bank_wei);
            holder.bank_moren = (TextView) convertView.findViewById(R.id.bank_moren);
//			holder.bank_set = (TextView) convertView.findViewById(R.id.bank_set);
            holder.bank_delete = (TextView) convertView.findViewById(R.id.bank_delete);
            holder.bank_wanshan = (TextView) convertView.findViewById(R.id.bank_wanshan);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.size() > 0) {
            final View showView = convertView.findViewById(R.id.show_item);
            final View hideView = convertView.findViewById(R.id.hide_item);
            ViewHelper.setTranslationX(showView, 0);
            ViewHelper.setTranslationX(hideView, 0);


            holder.bank_name.setText(list.get(position).get(BaseParam.QIAN_MY_BANK_BANKSHORTNAME));
            holder.back_icon.setBackgroundResource(AppTool.BankIcon2(list.get(position).get(BaseParam.QIAN_MY_BANK_BANKSHORTNAME)));

            holder.bank_wei.setText("尾号" + list.get(position).get(BaseParam.QIAN_MY_BANK_HIDDENCARDNO));
            if (list.get(position).get(BaseParam.QIAN_MY_BANK_ISDEFAULT).equals("1")) {
                holder.bank_moren.setVisibility(View.VISIBLE);
                holder.bank_delete.setVisibility(View.GONE);
//				hideView.setVisibility(View.GONE);
            } else {
//				hideView.setVisibility(View.VISIBLE);
                holder.bank_delete.setVisibility(View.VISIBLE);
                holder.bank_moren.setVisibility(View.GONE);
            }

        }
        return convertView;
    }

    private class ViewHolder {
        ImageView back_icon;
        TextView bank_name;
        TextView bank_wei;
        TextView bank_moren;
        //		TextView bank_set;
        TextView bank_delete;
        TextView bank_wanshan;

    }
}
