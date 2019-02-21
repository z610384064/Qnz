package com.rd.qnz.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.tools.webservice.JsonRequeatThreadGetUserStatus;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.jpush.android.api.JPushInterface;

public class AppTool {

    // 获取SDCard路径
    public static String getSDPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    // 删除已存在的文件
    public static void removeExpiredCache(String path) {
        if (checkFileExists(path)) {
            File file = new File(path);
            file.delete();
        }
    }

    /**
     * 一行一行读取文件，解决读取中文字符时出现乱码
     * 流的关闭顺序：先打开的后关，后打开的先关， 否则有可能出现java.io.IOException: Stream closed异常
     *
     * @throws IOException
     */
    public static String readFileByLine(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        String arrs = null;
        while ((line = br.readLine()) != null) {
            arrs = line;
        }
        br.close();
        isr.close();
        fis.close();
        return arrs;
    }

    public static void writeFile(String path, String value) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(path));
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(value);
        bw.close();
        osw.close();
        fos.close();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // 检查文件是否存在（无时间戳）
    public static boolean checkFileExists(String path) {
        if (new File(path).exists()) {
            return true;
        }
        return false;
    }

    // 检查文件是否存在（有时间戳）
    public static boolean checkFileExists(String path, String timestamp) {
        if (timestamp == null) {
            if (new File(path).exists()) {
                return true;
            }
        } else {
            File f = new File(path);
            Date fileTime = new Date(f.lastModified());
            long fileTimestamp = fileTime.getTime();
            long newTimestamp = Long.valueOf(timestamp) * 1000;
            long error = Long.valueOf(60000000);
            if (new File(path).exists()
                    && fileTimestamp - error >= newTimestamp) {
                return true;
            }
        }
        return false;
    }

    public static String saveBitmapToFile(Bitmap bitmap, String path,
                                          String fileName) {
        String filePath = path + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            FileOutputStream fos;
            fos = new FileOutputStream(filePath);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path + fileName;
    }

    public static Bitmap getHttpBitmapAndSave(String url, String path,
                                              MyApplication myApp, boolean isSave) throws IOException {
        InputStream in = null;
        try {
            // 获取网络图片
            HttpGet http = new HttpGet(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) client.execute(http);
            BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(
                    response.getEntity());
            in = bufferedHttpEntity.getContent();
        } catch (Exception e) {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e2) {
            }
        }
        Bitmap bitmap = null;

        if (in == null)
            return bitmap;

        BitmapFactory.Options fo = new BitmapFactory.Options();
        fo.inPurgeable = true;
        fo.inInputShareable = true;
        bitmap = BitmapFactory.decodeStream(in, new Rect(), fo);
        if (!isSave) {
            return bitmap;
        }

        File file = new File(AppTool.getSDPath() + BaseParam.QIAN_CATALOGUE
                + myApp.PLATFORM_ID);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(path);
        try {
            bitmap.compress(CompressFormat.JPEG, 50, fos);
        } catch (NullPointerException exception) {

        }
        fos.flush();
        fos.close();
        // 关闭数据流
        in.close();
        return bitmap;
    }

    @SuppressWarnings("deprecation")
    public static Drawable getHttpDrawableAndSave(String url, String path,
                                                  MyApplication myApp, boolean isSave) throws IOException {
        Drawable drawable = null;
        Bitmap bitmap = getHttpBitmapAndSave(url, path, myApp, isSave);
        if (bitmap != null) {
            drawable = new BitmapDrawable(getHttpBitmapAndSave(url, path,
                    myApp, isSave));
        }
        return drawable;
    }

    // 检查是否是时间
    public static boolean checkTime(String string) {
        try {

            if (string.length() == 13) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // 是否存在SDCard
    public static boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    @SuppressLint("NewApi")
    public static Boolean checkEmpty(String string) {
        if (string == null || string.equals("")) {
            return true;
        } else
            return false;
    }

    //获取IMEI的时候排除全是0的可能性
    @SuppressLint("NewApi")
    public static Boolean checkEmpty1(String string) {
        if (string == null || string.equals("") || string.equals("0000000000000000")) {
            return true;
        } else
            return false;
    }

    // 时间
    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String getInformationTwoDateDistance(String subtime) {
        if (AppTool.checkTime(subtime)) {
            long nowTime = System.currentTimeMillis();
            long subTime = Long.valueOf(subtime) * 1000;
            long distance = nowTime - subTime;
            Date nowDate = new Date(nowTime);
            Date subDate = new Date(subTime);
            if (subDate.getDate() == nowDate.getDate()) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                return sdf.format(new Date(Long.valueOf(subtime) * 1000));
            } else if (subDate.getDate() == nowDate.getDate() - 1) {
                return "昨天";
            } else if (distance < 1000 * 60 * 60 * 24 * 5 && distance > 0) {
                return distance / (1000 * 60 * 60 * 24) + "天前";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                return sdf.format(new Date(subTime));
            }
        } else {
            return "";
        }
    }

    // 时间
    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String getInformationTwoDateDistanceDay(String subtime) {
        if (AppTool.checkTime(subtime)) {//检查是否时间
            long nowTime = System.currentTimeMillis();
            long subTime = Long.valueOf(subtime);
            long distance = subTime - nowTime;
            Log.e("distance----distance", "" + distance);

            if ((distance / (1000 * 60 * 60 * 24)) > 0) {
                return (distance / (1000 * 60 * 60 * 24)) + "天后过期";
            } else {
                return "1天后过期";
            }

            // if((distance / (1000 * 60 * 60 * 24))>0){
            // return (distance / (1000 * 60 * 60 * 24)) + "天后过期";
            // }else if ((distance / (1000 * 60 * 60 * 24))==0){
            // return (distance / (1000 * 60 * 60)) + "小时后过期";
            // }else if ((distance / (1000 * 60 * 60))==0){
            // return (distance / (1000 * 60)) + "分钟后过期";
            // }else{
            // if(distance>0){
            // return "马上到期";
            // }else{
            // return "时间错误";
            // }
            //
            // }

        } else {
            return "";
        }
    }

    public static String getHomePageTimeIcon(String subtime) {
        System.out.println("subtime = " + subtime);
        if (AppTool.checkTime(subtime)) {
            long nowTime = System.currentTimeMillis();
            long subTime = Long.valueOf(subtime);
            long distance = nowTime - subTime;
            Date nowDate = new Date(nowTime);
            Date subDate = new Date(subTime);
            System.out.println("subDate.getDate() =" + subDate.getDate()
                    + ",  subDate.getDate() = " + nowDate.getDate());
            if (subDate.getDate() == nowDate.getDate()) {
                return "今";
            } else if (subDate.getDate() == nowDate.getDate() + 1) {
                return "明";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getHomePageTime(String subtime) {
        if (AppTool.checkTime(subtime)) {
            long nowTime = System.currentTimeMillis();
            long subTime = Long.valueOf(subtime);
            long distance = nowTime - subTime;
            Date nowDate = new Date(nowTime);
            Date subDate = new Date(subTime);
            if (subDate.getDate() == nowDate.getDate()) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                return "今天  " + sdf.format(new Date(Long.valueOf(subtime)))
                        + " 准时发布";
            } else if (subDate.getDate() == nowDate.getDate() + 1) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                return "明天 " + sdf.format(new Date(Long.valueOf(subtime)))
                        + " 准时发布";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd号   HH:mm");
                return sdf.format(new Date(Long.valueOf(subtime))) + " 准时发布";
            }
        } else {
            return "";
        }
    }

    public static class MyTimeBean {
        public String date;// 今天 明天，等等
        public String time;// 14点或者10点
    }

    /**
     * 将 明天_10:10拆开为 明天 10:10
     *
     * @param subtime
     * @return
     */
    public static MyTimeBean getMyTimeBean(String subtime) {
        // TODO LIU
        MyTimeBean bean = new MyTimeBean();
        if (!TextUtils.isEmpty(subtime)) {
            int index = subtime.indexOf("_");
            if (index > -1) {
                bean.time = subtime.substring(index + 1);
                bean.date = subtime.substring(0, index);
            }
        }
        return bean;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String getMsgTwoDateDistance(String subtime) {

        if (AppTool.checkTime(subtime)) {
            long nowTime = System.currentTimeMillis();

            // long subTime = Long.valueOf(subtime)*1000;
            long subTime = Long.valueOf(subtime);

            long distance = nowTime - subTime;
            Date nowDate = new Date(nowTime);
            Date subDate = new Date(subTime);
            // if (subDate.getDate() == nowDate.getDate()){
            // SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            // return sdf.format(new Date(Long.valueOf(subtime)));
            // }
            // else if(subDate.getDate() == nowDate.getDate()-1){
            // return "昨天";
            // }
            // else if(distance < 1000*60*60*24*7 && distance > 0){
            // return weekOfDay(subDate.getDay());
            // }
            // else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(new Date(Long.valueOf(subtime)));
            // }
        } else {
            return "";
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String getMsgTwoDateDistance_miao(String subtime) {

        if (AppTool.checkTime(subtime)) {
            long nowTime = System.currentTimeMillis();

            // long subTime = Long.valueOf(subtime)*1000;
            long subTime = Long.valueOf(subtime);

            long distance = nowTime - subTime;
            Date nowDate = new Date(nowTime);
            Date subDate = new Date(subTime);
            // if (subDate.getDate() == nowDate.getDate()){
            // SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            // return sdf.format(new Date(Long.valueOf(subtime)));
            // }
            // else if(subDate.getDate() == nowDate.getDate()-1){
            // return "昨天";
            // }
            // else if(distance < 1000*60*60*24*7 && distance > 0){
            // return weekOfDay(subDate.getDay());
            // }
            // else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(new Date(Long.valueOf(subtime)));
            // }
        } else {
            return "";
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String getMsgTwoDateDistance1(String subtime) {
        if (AppTool.checkTime(subtime)) {
            long nowTime = System.currentTimeMillis();
            long subTime = Long.valueOf(subtime);
            long distance = nowTime - subTime;
            Date nowDate = new Date(nowTime);
            Date subDate = new Date(subTime);
            // if(distance < 1000*60*60*24*7 && distance > 0){
            // return weekOfDay(subDate.getDay());
            // }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new Date(Long.valueOf(subtime)));
            // }
        } else {
            return "";
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String getMsgTwoDateDistance4(String subtime) {
        if (AppTool.checkTime(subtime)) {
            long nowTime = System.currentTimeMillis();
            long subTime = Long.valueOf(subtime);
            long distance = nowTime - subTime;
            Date nowDate = new Date(nowTime);
            Date subDate = new Date(subTime);
            // if(distance < 1000*60*60*24*7 && distance > 0){
            // return weekOfDay(subDate.getDay());
            // }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new Date(Long.valueOf(subtime)));
            // }
        } else {
            return "";
        }
    }

    // 计算收益的时间
    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String getMsgTwoDateDistance3(String subtime) {
        if (AppTool.checkTime(subtime)) {
            long nowTime = System.currentTimeMillis();
            long subTime = Long.valueOf(subtime);
            long distance = nowTime - subTime;
            Date nowDate = new Date(nowTime);
            Date subDate = new Date(subTime);
            // if(distance < 1000*60*60*24*7 && distance > 0){
            // return weekOfDay(subDate.getDay());
            // }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            return sdf.format(new Date(Long.valueOf(subtime)));
            // }
        } else {
            return "";
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String getMsgTwoDateDistance2(String subtime) {
        if (AppTool.checkTime(subtime)) {
            long nowTime = System.currentTimeMillis();
            long subTime = Long.valueOf(subtime);
            long distance = nowTime - subTime;
            Date nowDate = new Date(nowTime);
            Date subDate = new Date(subTime);
            // if(distance < 1000*60*60*24*7 && distance > 0){
            // return weekOfDay(subDate.getDay());
            // }else{
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            return sdf.format(new Date(Long.valueOf(subtime)));
            // }
        } else {
            return "";
        }
    }

    private static String weekOfDay(int day) {
        String weekOfDay = "";
        switch (day) {
            case 1:
                weekOfDay = "星期一";
                break;
            case 2:
                weekOfDay = "星期二";
                break;
            case 3:
                weekOfDay = "星期三";
                break;
            case 4:
                weekOfDay = "星期四";
                break;
            case 5:
                weekOfDay = "星期五";
                break;
            case 6:
                weekOfDay = "星期六";
                break;
            case 0:
                weekOfDay = "星期日";
                break;
        }
        return weekOfDay;
    }

    //主要是为了返回手机的imei号
    public static String imeiSave(Context context) {
        SharedPreferences.Editor localEditor = context.getSharedPreferences(BaseParam.QIAN, 0).edit();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); // 获取IMEI号
        String imei;
        String deviceid="";
        String simNumber="";
        String ANDROID="";
        try{
             deviceid=tm.getDeviceId();
             ANDROID=Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            simNumber=tm.getSimSerialNumber();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            ANDROID=Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        }catch (Exception e){
            e.printStackTrace();
        }


        if (!AppTool.checkEmpty1(deviceid)) {   //判断iemi号是否为空或者全部是0这种极端情况
            imei =deviceid;
        } else if (!AppTool.checkEmpty1(simNumber)) {  //得到sim卡信息
            imei =simNumber;
        } else if (!AppTool.checkEmpty1(ANDROID)){
            imei=ANDROID;  //得到android设备id
        }else {
            imei="";
        }
        JPushInterface.setAlias(context, imei, null);
        localEditor.putString(BaseParam.QIAN_IMEI, imei);
        localEditor.commit();
        return imei;
    }

    public static boolean checkInt(String param) {
        Boolean isInt = false;
        try {
            if (Integer.valueOf(param) > 0)
                isInt = true;
        } catch (Exception exception) {
        }
        return isInt;
    }

    public static String getDate() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        Date dd = new Date();
        return ft.format(dd);
    }

    public static long getQuot(String time1, String time2) {
        long quot = 0;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date1 = ft.parse(time1);
            Date date2 = ft.parse(time2);
            quot = date1.getTime() - date2.getTime();
            quot = quot / 1000 / 60 / 60 / 24;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return quot;
    }

    /**
     * 等额本息
     *
     * @param touBianJinE
     * @param liLv
     * @param qiXian
     * @return
     */
    public static String denge(double touBianJinE, double liLv, double qiXian) {
        DecimalFormat df = new DecimalFormat("0.00");
        double receipts = touBianJinE * liLv / 1200
                * (Math.pow((1 + liLv / 1200), qiXian))
                / ((Math.pow((1 + liLv / 1200), qiXian) - 1)) * qiXian
                - touBianJinE;
        return df.format(receipts);
    }

    /**
     * 计算收益
     *
     * @param money
     * @param rate
     * @param isDay
     * @param day
     * @param mouth
     * @return
     */
    public static String rateReceipts(double money, double rate, String isDay,
                                      int day, int mouth) {
        DecimalFormat df = new DecimalFormat("0.00");
        double receipts;
        if (isDay.equals("1")) {
            receipts = money * rate * day / 36500;
        } else {
            receipts = money * rate * mouth / 1200;
        }
        return df.format(receipts);
    }
    public static String rateReceipt(double money, float rate, float extraAwardApr, String isDay,
                                     int day, int mouth) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double receipts;
        double receipt1, receipt2;
        if (isDay.equals("1")) {
            receipt1 = money * rate * day / 36500;
            receipt2 = money * extraAwardApr * day / 36500;
        } else {
            receipt1 = money * rate * mouth / 1200;
            receipt2 = money * extraAwardApr * mouth / 1200;
        }
        Log.e("######", "" + receipt1 + "-------" + receipt2);
        Log.e("######", "" + Double.parseDouble(df.format(receipt1)) + "-------" + Double.parseDouble(df.format(receipt2)));
        receipts = Double.parseDouble(df.format(receipt1)) + Double.parseDouble(df.format(receipt2));

        return df.format(receipts);
    }

    /**
     *  使用了加息劵
     * @param money         投资金额
     * @param rate        本息
     * @param extraAwardApr  加息劵的利息
     * @param isDay
     * @param day           标剩余期限
     * @param jiaxi_day  加息时长
     * @param mouth
     * @return
     */
    public static String rateReceipt2(double money, float rate, float extraAwardApr, String isDay, int day,int jiaxi_day, int mouth) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double receipts;
        double receipt1, receipt2;
        if (isDay.equals("1")) {
            receipt1 = money * rate * day / 36500;
            receipt2 = money * extraAwardApr * jiaxi_day / 36500;
        } else {
            receipt1 = money * rate * mouth / 1200;
            receipt2 = money * extraAwardApr * mouth / 1200;
        }
        Log.e("######", "" + receipt1 + "-------" + receipt2);
        Log.e("######", "" + Double.parseDouble(df.format(receipt1)) + "-------" + Double.parseDouble(df.format(receipt2)));
        receipts = Double.parseDouble(df.format(receipt1)) + Double.parseDouble(df.format(receipt2));

        return df.format(receipts);
    }

    /**
     *  额外加息金额
     * @param money
     * @param extraAwardApr
     * @param isDay
     * @param jiaxi_day
     * @param mouth
     * @return
     */
    public static String rateReceipt3(double money, float extraAwardApr, String isDay, int jiaxi_day, int mouth) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double receipts;
        double receipt2;
        if (isDay.equals("1")) {

            receipt2 = money * extraAwardApr * jiaxi_day / 36500;
        } else {

            receipt2 = money * extraAwardApr * mouth / 1200;
        }

        receipts = Double.parseDouble(df.format(receipt2));

        return df.format(receipts);
    }

    // 钱的显示
    public static String textMoney(String money) {
        String result = "";
        long intPart = 0;
        if (money.contains(".")) {
            String[] part = money.split("\\.");
            intPart = Long.valueOf(part[0]);
            result = "." + part[1];
        } else {
            intPart = Long.valueOf(money);
        }
        boolean isDo = true;
        do {
            String include = intPart % 1000 + "";
            if (intPart / 1000 > 0) {
                if (include.length() == 1) {
                    include = "00" + include;
                } else if (include.length() == 2) {
                    include = "0" + include;
                }
                result = "," + include + result;
                intPart = intPart / 1000;
                isDo = true;
            } else {
                result = include + result;
                isDo = false;
            }
        } while (isDo);

        return result;
    }

    // 连连支付
    public static int BankIcon2(String bankId) {
        if (bankId.equals("工商银行")) {// 工商银行
            return R.drawable.gongshang;
        }
        if (bankId.equals("农业银行")) {// 农业银行
            return R.drawable.nongye;
        }
        if (bankId.equals("交通银行")) {// 交通银行
            return R.drawable.jiaotong;
        }
        if (bankId.equals("中国银行")) {// 中国银行
            return R.drawable.zhongguo;
        }
        if (bankId.equals("光大银行")) {// 光大银行
            return R.drawable.guangda;
        }
        if (bankId.equals("民生银行")) {// 民生银行
            return R.drawable.mingsheng;
        }
        if (bankId.equals("中信银行")) {// 中信银行
            return R.drawable.zhongxin;
        }
        if (bankId.equals("华夏银行")) {// 华夏银行
            return R.drawable.huaxia;
        }
        if (bankId.equals("兴业银行")) {// 兴业银行
            return R.drawable.xingye;
        }
        if (bankId.equals("招商银行")) {// 招商银行
            return R.drawable.zhaoshang;
        }
        if (bankId.equals("建设银行")) {// 建设银行
            return R.drawable.jianshe;
        }
        if (bankId.equals("平安银行")) {// 平安银行
            return R.drawable.pingan;
        }
        if (bankId.equals("浦发银行")) {// 浦发银行
            return R.drawable.pufa;
        }
        if (bankId.equals("邮政储蓄银行")) {// 邮政储蓄银行
            return R.drawable.youzheng;
        }
        if (bankId.equals("邮政银行")) {// 邮政银行
            return R.drawable.youzheng;
        }
        if (bankId.equals("北京银行")) {// 北京银行
            return R.drawable.beijing;
        }
        if (bankId.equals("渤海银行")) {// 渤海银行
            return R.drawable.bohai;
        }
        if (bankId.equals("成都银行")) {// 成都银行
            return R.drawable.chengdu;
        }
        if (bankId.equals("东亚银行")) {// 东亚银行
            return R.drawable.dongya;
        }
        if (bankId.equals("南京银行")) {// 南京银行
            return R.drawable.nanjing;
        }
        if (bankId.equals("上海银行")) {// 上海银行
            return R.drawable.shanghai;
        }
        if (bankId.equals("宁波银行")) {// 宁波银行
            return R.drawable.ningbo;
        }
        if (bankId.equals("上海农村商业银行")) {// 上海农村商业银行
            return R.drawable.shanghainongshang;
        }
        if (bankId.equals("投资准备金")) {// 投资准备金
            return R.drawable.touzi_bank;
        }
        if (bankId.equals("广发银行")) {// 广发银行
            return R.drawable.guangfa;
        }
        if (bankId.equals("浙商银行")) {// 浙商银行
            return R.drawable.zheshang;
        }
        if (bankId.equals("杭州银行")) {// 杭州银行
            return R.drawable.hangzhou;
        }
        if (bankId.equals("广州银行")) {// 广州银行
            return R.drawable.guangzhou;
        }
        return R.drawable.moren;
    }

    // 将100000 转化10万
    public static String zhuanhua(String account) {
        double d = Double.parseDouble(account);
        if (5 <= account.length()) {

            if (d % 10000 == 0) {
                return account.substring(0, account.length() - 4);
            } else {
                return d / 10000 + "";
            }

        }
        return d / 10000 + "";
        //
    }

    // 调试
    public static void debug(String tag, String log) {
        // System.out.println(tag + ": " + log);
        Log.d(tag, log);
    }

    public static String changProductName(String name) {

        if (name.length() > 17) {
            return name.substring(0, 17);
        } else {
            return name;
        }

    }

    /**
     * 获取用户状态信息
     */
    public static void getUserStatusInfoRequest() {
        if (Check.hasInternet(null)) {  //判断网络状态
            MyApplication myApp = MyApplication.getInstance();
            SharedPreferences preferences = MyApplication.getInstance()
                    .getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            ArrayList<String> param = new ArrayList<String>();
            ArrayList<String> value = new ArrayList<String>();
            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            value.add(oauthToken);
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(MyApplication.getInstance().appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("getUserInfoStatus");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(MyApplication.getInstance().signType);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "="
                            + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=getUserInfoStatus",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
            APIModel apiModel = new APIModel();
            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);
            new Thread(new JsonRequeatThreadGetUserStatus(
                    myApp.getApplicationContext(),
                    myApp,
                    myApp.getBaseHandler(),
                    param,
                    value)
            ).start();
        }
    }

    /**
     * 根据id 获取 字符串
     *
     * @param id 例： r.string.x
     * @return str 字符串
     */
    public static String getStringById(int id) {
        String str = "";
        str = MyApplication.getInstance().getApplicationContext().getString(id);
        return str;
    }

    /**
     * 在url后面添加分享 参数
     *
     * @param webUrl
     * @return
     * @author lht
     */
    public static String addShareIdAtLastUrl(String webUrl) {
        if (TextUtils.isEmpty(webUrl)) {
            return webUrl;
        }
        String webUrlNew = "";
        String and = "?";
        int index = webUrl.indexOf("?");
        if (index > 0) {
            and = "&";
        } else {
            and = "?";
        }
        if ("".equals(Profile.getUserShare()) || null == Profile.getUserShare()) {
            webUrlNew = webUrl;
        } else {
            webUrlNew = webUrl + and + "share=" + Profile.getUserShare();
        }
        return webUrlNew;
    }

}
