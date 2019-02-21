package com.rd.qnz.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.qnz.R;

/**
 * Created by Evonne on 2016/7/27.
 */
public class MineShow {

    static private Toast toast;
    static private long lastClick;

    //弹出一个吐司
    public static void toastShow(String value, Context context) {
        if (null == toast) {
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.toast, null);
            TextView textView = (TextView) view.findViewById(R.id.toast_txt);
            textView.setText(value);
            toast.setView(view);
            toast.show();
        } else {
            TextView textView = (TextView) toast.getView().findViewById(R.id.toast_txt);
            textView.setText(value);
            toast.show();
        }
    }

    //判断两次点击的时间间隔不能小于1秒
    public static boolean fastClick() {
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

}
