package com.rd.qnz.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.ImageLoader;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;
import java.util.Map;

/**
 * 活动页面对应的adapter
 */
public class ActiveListAdapter extends BaseAdapter {
    private static final int LIST = 0; //活动item
    private static final int TEXT = 1; //活动已结束文字

    private List<Map<String, String>> list1; //正在进行活动
    private List<Map<String, String>> list2;//已结束活动
    private Context context;
    private int width;

    public ActiveListAdapter(Context context, List<Map<String, String>> list1,List<Map<String, String>> list2, int width) {
        this.context = context;
        this.list1 = list1;
        this.list2 = list2;
        this.width = width;
    }

    public void notifyDataSetChanged(List<Map<String, String>> list1,List<Map<String, String>> list2, String type) {
        this.list1 = list1;
        this.list2 = list2;
        super.notifyDataSetChanged();

    }

    public void clear() {
        // TODO Auto-generated method stub
        list1.clear();
        list2.clear();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list1.size()+list2.size()+1;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewHolder viewHolder=null;
        TitleHolder textHolder=null;
        int type=getItemViewType(position);
        if (convertView==null){
            switch (type){
                case TEXT:
                    convertView=View.inflate(context,R.layout.activity_end,null);
                    textHolder=new TitleHolder();
                    textHolder.title= (TextView) convertView.findViewById(R.id.title);
                    convertView.setTag(textHolder);
                    AutoUtils.autoSize(convertView);
                    break;
                case LIST:
                    convertView=View.inflate(context,R.layout.community_active_item2,null);
                    viewHolder=new ListViewHolder();
                    viewHolder.activity_title= (TextView) convertView.findViewById(R.id.activity_title);
                    viewHolder.activity_time= (TextView) convertView.findViewById(R.id.activity_time);
                    viewHolder.active_image= (ImageView) convertView.findViewById(R.id.active_image);
                    viewHolder.active_image.getLayoutParams().height = width / 3;
                    viewHolder.active_image.getLayoutParams().width = width;
                    viewHolder.active_image.setScaleType(ImageView.ScaleType.FIT_XY);
                    convertView.setTag(viewHolder);
                    AutoUtils.autoSize(convertView);
                    break;
            }
        }else {
            switch (type){
                case TEXT:
                    textHolder= (TitleHolder) convertView.getTag();
                    break;
                case LIST:
                    viewHolder= (ListViewHolder) convertView.getTag();
                    break;
            }

        }
     //进行item的显示
        switch (type){
            case TEXT:  //文字
                if (list2.size()==0){ //没有已结束活动,那么文本不显示
                    textHolder.title.setVisibility(View.GONE);
                }else if (position==list1.size()){
                    textHolder.title.setVisibility(View.VISIBLE);
                    textHolder.title.setText("已结束活动");
                }



                break;
            case LIST:  //item

                if (list1.size()>0&&position<list1.size()){ //正在进行的活动

                    ImageLoader.getInstances(context).DisplayImage(BaseParam.URL_QIAN + "/"+ list1.get(position).get(BaseParam.QIAN_MORE_ACTIVE_URL), viewHolder.active_image);

                    viewHolder.activity_title.setText(list1.get(position).get(BaseParam.QIAN_MORE_ACTIVE_INTRO));
                    viewHolder.activity_time.setText(AppTool.getMsgTwoDateDistance1(list1.get(position).get(BaseParam.QIAN_MORE_ACTIVE_ADDTIME)));
                }else if (list2.size()>0&&position>list1.size()){  //已结束的活动
                    int newposition=position-1-list1.size();
                    ImageLoader.getInstances(context).DisplayImage(BaseParam.URL_QIAN + "/"+ list2.get(newposition).get(BaseParam.QIAN_MORE_ACTIVE_URL), viewHolder.active_image);
                    viewHolder.activity_title.setText(list2.get(newposition).get(BaseParam.QIAN_MORE_ACTIVE_INTRO));
                    viewHolder.activity_time.setText(AppTool.getMsgTwoDateDistance1(list2.get(newposition).get(BaseParam.QIAN_MORE_ACTIVE_ADDTIME)));
                }
                break;
        }

        return convertView;

    }
    @Override
    public int getViewTypeCount() {  //该方法返回多少种不同的布局,目前有两种布局
        return 2;
    }

    @Override
    public int getItemViewType(int position) {   //根据position返回相应的Item
        if (position==list1.size()){
            return TEXT;
        }else {
            return  LIST;
        }

    }

    private class ListViewHolder {
        TextView activity_time;
        TextView activity_title;
        ImageView active_image;
    }
    class TitleHolder{
        TextView title;
    }

}
