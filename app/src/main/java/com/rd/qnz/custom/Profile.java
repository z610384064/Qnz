package com.rd.qnz.custom;

import android.content.Context;
import android.content.SharedPreferences;

import com.rd.qnz.tools.BaseParam;

/**
 *  主要用来操作qz_user_status.xml
 */
public class Profile {
    public static String TAG_USER_STATUE_NAME = "qz_user_status";//个人信息保存

    public static void setUserRealNameStatus(String realNameStatus) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(BaseParam.QIAN_USER_STATUS_INFO_REAL_NAME, realNameStatus).commit();
    }

    public static String getUserRealNameStatus() {
        String realStatus = "";
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        realStatus = sp.getString(BaseParam.QIAN_USER_STATUS_INFO_REAL_NAME, "0");
        return realStatus;
    }

    public static void setUserPayPassWordStatus(String PayPassWord) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(BaseParam.QIAN_USER_STATUS_INFO_PAY_PWD, PayPassWord).commit();
    }

    public static String getUserPayPassWordStatus() {
        String realStatus = "";
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        realStatus = sp.getString(BaseParam.QIAN_USER_STATUS_INFO_PAY_PWD, "0");
        return realStatus;
    }

    public static void setUserBankCardStatus(String bankCardStatus) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(BaseParam.QIAN_USER_STATUS_INFO_BANK_CARD, bankCardStatus).commit();
    }

    public static String getUserBankCardStatus() {
        String Status = "";
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        Status = sp.getString(BaseParam.QIAN_USER_STATUS_INFO_BANK_CARD, "0");
        return Status;
    }

    public static void setUserNeedPopStatus(String NeedPop) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(BaseParam.QIAN_USER_STATUS_INFO_NEED_POP, NeedPop).commit();
    }

    public static String getUserNeedPopStatus() {
        String Status = "";
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        Status = sp.getString(BaseParam.QIAN_USER_STATUS_INFO_NEED_POP, "0");
        return Status;
    }

    public static void setUserIsNewHandStatus(String isNewHand) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(BaseParam.QIAN_USER_STATUS_INFO_NEWHAND_STATUS, isNewHand).commit();
    }

    public static String getUserIsNewHandStatus() {
        String Status = "";
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        Status = sp.getString(BaseParam.QIAN_USER_STATUS_INFO_NEWHAND_STATUS, "0");  //默认0代表不是新手
        return Status;
    }

    public static void setUserShare(String share) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_SHARE, share).commit();
    }

    public static String getUserShare() {
        String Status = "";
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, Context.MODE_PRIVATE);
        Status = sp.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_SHARE, "");
        return Status;
    }

    //返回oauthToken
    public static String getoAutoToken() {
        SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences(
                BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        String oauthToken = preferences.getString(
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        return oauthToken;
    }

}
