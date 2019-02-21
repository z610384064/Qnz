package com.rd.qnz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.util.MyImageSpan;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 消息公告adapter
 */
public class AnnouncementListAdapter extends BaseAdapter {

    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private Context context;

    public AnnouncementListAdapter(Context context, List<Map<String, String>> list) {
        this.list = list;
        this.context = context;
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

    public void clear() {
        // TODO Auto-generated method stub
        list.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.community_announcement_item2, null);
            holder.notice_title = (TextView) convertView.findViewById(R.id.notice_title);
            holder.notice_time = (TextView) convertView.findViewById(R.id.notice_time);
            holder.notice_content= (TextView) convertView.findViewById(R.id.notice_content);//内容
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.size() > 0) {
            String title=list.get(position).get(BaseParam.QIAN_MORE_NOTICE_NAME);
            holder.notice_title.setText(title);
            Drawable drawable= context.getResources().getDrawable(R.drawable.xiaoxi);
                // 调用setCompoundDrawables时。必须调用Drawable.setBounds()方法,否则图片不显示
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            holder.notice_title.setCompoundDrawables(drawable, null, null, null); //设置左图标
//           Bitmap bitmap=zoomImg(BitmapFactory.decodeResource(context.getResources(),R.drawable.xiaoxi),60,60);

            MyImageSpan is=new MyImageSpan(BitmapFactory.decodeResource(context.getResources(),R.drawable.xiaoxi),3);
            String str =title;
//            if (str.length()>30){
//                str="  "+str.substring(0,30);//拿到最后30位+三个点
//            }else {
//                str="  "+str;
//            }
            str="  "+str;
            int strLength = str.length();

            SpannableString ss = new SpannableString(str);
            ss .setSpan(is,0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            holder.notice_title.setText(ss.subSequence(0,strLength));


            holder.notice_time.setText(AppTool.getMsgTwoDateDistance1(list.get(position).get(BaseParam.QIAN_MORE_NOTICE_ADDTIME)));
            holder.notice_content.setText(list.get(position).get(BaseParam.QIAN_MORE_NOTICE_CONTENT));
        }
        return convertView;
    }

    public Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片   www.2cto.com
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    private class ViewHolder {
        TextView notice_title;
        TextView notice_time;
        TextView notice_content;
    }

}
