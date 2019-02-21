package com.rd.qnz.util;

import android.content.Context;

/**
 *状态栏工具栏 2017/9/18 0018.
 */

public class StatusBarCompat {



    public static int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
