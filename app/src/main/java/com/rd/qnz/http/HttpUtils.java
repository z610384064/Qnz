package com.rd.qnz.http;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.PostRequest;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.tools.BaseParam;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class HttpUtils {
   static APIModel apiModel = new APIModel();
    public static PostRequest getRequest(String url, Context context, String[] str){
        int old_length=str.length;  //原来8个参数,现在4个参数
        int new_length=str.length/2;  //原来8个参数,现在4个参数
        String[] array = new String[new_length];  //4个参数
        for (int i=0;i<new_length;i++){
                array[i]=str[i*2+0]+"="+str[i*2+1];
        }
        HttpParams params=new HttpParams();
        for (int i=0;i<old_length;i+=2){
            params.put(str[i],str[i+1]);
        }
        String sign = apiModel.sortStringArray(array);
        params.put(BaseParam.URL_QIAN_API_SIGN,sign);


      return   OkGo.post(url)     // 请求方式和请求url
                .tag(context)      // 请求的 tag, 主要用于取消对应的请求
                .params(params)

                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT) ;   // 缓存模式，详细请看缓存介绍
    }
    public static PostRequest getRequestSign(String url, Context context,  HashMap<String,String> map){
//        int old_length=str.length;  //原来8个参数,现在4个参数
//        int new_length=str.length/2;  //原来8个参数,现在4个参数
//        String[] array = new String[new_length];  //4个参数
//        for (int i=0;i<new_length;i++){
//            array[i]=str[i*2+0]+"="+str[i*2+1];
//        }

//        for (int i=0;i<old_length;i+=2){
//            params.put(str[i],str[i+1]);
//        }
        HttpParams params=new HttpParams();
        String[] array=new String[map.size()];
        Set<String> keySet = map.keySet();			//获取所有键的集合
        Iterator<String> it = keySet.iterator();	//获取迭代器
        while(it.hasNext()) {
            String key = it.next();		//判断集合中是否有元素
            params.put(key,map.get(key));
           			//获取每一个键
        }

        String sign = apiModel.sortStringArray(array);
        params.put(BaseParam.URL_QIAN_API_SIGN,sign);


        return   OkGo.post(url)     // 请求方式和请求url
                .tag(context)      // 请求的 tag, 主要用于取消对应的请求
                .params(params)

                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT) ;   // 缓存模式，详细请看缓存介绍
    }
}
