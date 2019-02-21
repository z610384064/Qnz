package com.rd.qnz.custom;

import java.util.Arrays;

import android.util.Log;

import com.rd.qnz.tools.BaseParam;

/**
 * 签名
 */
public class APIModel {

    private static String appkey = "543BEA8998468882A58E5741AF380710";

    private String[] arrayToSort = new String[]{};

    public String jsonSign;

    public String sortStringArray(String[] array) {
        arrayToSort = array;
        StringBuffer sb = new StringBuffer();
        // 调用数组的静态排序方法sort,且不区分大小写
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < arrayToSort.length; i++) {
            sb.append(arrayToSort[i]);
            if (i < arrayToSort.length - 1) {
                sb.append("&");
            }
        }
        sb.append(appkey);
        jsonSign = MD5Util.MD5Encode(sb.toString(), "utf-8");
        return jsonSign;
    }

}
