package com.rd.qnz.tools.webservice;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.http.SSLSocketFactoryEx;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class JsonRequest {
    private Context context;
    private MyApplication myApp;
    private String userAgent = "";

    public JsonRequest(Context context, MyApplication myApp) {
        this.context = context;
        this.myApp = myApp;
        this.userAgent = "(Android " + android.os.Build.VERSION.RELEASE + ";" + "QZW/" + myApp.QIAN_VERSIONNAME + ")";
    }

    public String generalRequestPost1(String urlHost, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2) {


        HttpClient httpClient =  SSLSocketFactoryEx.getNewHttpClient();
        HttpPost post = new HttpPost(urlHost);
        //第一次一般是还未被赋值，若有值则将SessionId发给服务器
        if (null != myApp.JSESSIONID) {
            post.setHeader("Cookie", "JSESSIONID=" + myApp.JSESSIONID);
        }
//        SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
        post.setHeader("User-Agent", String.format(userAgent, "", "", "", "", ""));
        String data = "";
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();

        for (int i = 0; i < paramArrayList1.size(); i++) {
            NameValuePair pair = new BasicNameValuePair(paramArrayList1.get(i), paramArrayList2.get(i));
            pairs.add(pair);
        }
        try {
            Log.i("请求中 ...", "请求中 ...");
            HttpEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
            post.setEntity(entity);
            Log.i("entity", entity.toString());
            HttpResponse response = httpClient.execute(post);//TODO 测试环境使用
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                entity = response.getEntity();
                data = EntityUtils.toString(entity);
                /* CookieStore 是 Java API 中用来处理 HTTP 客户端的 Cookie 存储策略的类 */
                CookieStore mCookieStore = ((AbstractHttpClient) httpClient).getCookieStore();
                List<org.apache.http.cookie.Cookie> cookies = mCookieStore.getCookies();
                for (int i = 0; i < cookies.size(); i++) {
                    //这里是读取Cookie['JSESSIONID']的值存在静态变量中，保证每次都是同一个值
                    if ("JSESSIONID".equals(cookies.get(i).getName())) {
                        myApp.JSESSIONID = cookies.get(i).getValue();
                        break;
                    }
                }
            }
            Log.i("data", data);//返回的json数据
        } catch (Exception e) {
            Log.i("请求异常", "请求异常");
            e.printStackTrace();
            return "unusual";
        }
        return data;
    }

    /**
     * 登陆 输入手机号下一步输入密码之后的登录
     *
     * @param param ArrayList
     * @param value ArrayList
     * @return HashMap
     */
    public HashMap<String, String> getWebserviceResult_getQIANLogin(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_LOGIN, param, value);
        Log.i("登陆 json格式返回", result);

        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_BANKSTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_BANKSTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_SHARE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_SHARE));
                ret_list.put("cardId", Check.jsonGetStringAnalysis(oj1, "cardId"));
            } else {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
                ret_list.put("resultMsg", Check.jsonGetStringAnalysis(oj, "resultMsg"));
                ret_list.put("errorMsg", Check.jsonGetStringAnalysis(oj1, "errorMsg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 刷新token
     *
     * @param param ArrayList
     * @param value ArrayList
     * @return HashMap
     */
    public HashMap<String, String> getWebserviceResult_getQIANRefreshToken(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_REFRESHTOKEN, param, value);
        Log.i("刷新token  json格式返回", result);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_BANKSTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_BANKSTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE));
            } else {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 注册-手机验证码regPhoneCode
     */
    public HashMap<String, String> getWebserviceResult_getQIANRegVerify(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_REG_VERIFY, param, value);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("注册的验证码  json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("0")) {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 找回的验证码
     */
    public HashMap<String, String> getWebserviceResult_getQIANReturnVerify(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_RETURN_VERIFY, param, value);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("找回的验证码  json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("0")) {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }


    /**
     * 重置密码
     *
     * @param param
     * @param value
     * @return
     */
    public HashMap<String, String> getWebserviceResult_getQIANReseting(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_RETRIEVE_RESETING, param, value);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("重置密码 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("0")) {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 余额明细列表
     */
    public List<Map<String, String>> getWebserviceResult_getQIANBalanceRecordList(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_BALANCE_RECORD, param, value);
        List<Map<String, String>> ret_list = new ArrayList<Map<String, String>>();
        if (result.equals("unusual")) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            ret_list.add(map);
            return ret_list;
        }
        Log.i("余额明细列表 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                JSONArray product_list = oj1.getJSONArray("balanceDetail");
                if (product_list.length() == 0) { //没有记录
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "0");
                    map.put("errorCode", "no");
                    ret_list.add(map);
                    return ret_list;
                }
                for (int i = 0; i < product_list.length(); i++) {
                    JSONObject product = product_list.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_BALANCE_RECORD_DATA, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_BALANCE_RECORD_DATA));
                    map.put(BaseParam.QIAN_BALANCE_RECORD_LATEST_BALANCE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_BALANCE_RECORD_LATEST_BALANCE));
                    map.put(BaseParam.QIAN_BALANCE_RECORD_MONEY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_BALANCE_RECORD_MONEY));
                    map.put(BaseParam.QIAN_BALANCE_RECORD_TITLE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_BALANCE_RECORD_TITLE));
                    map.put(BaseParam.QIAN_BALANCE_RECORD_TYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_BALANCE_RECORD_TYPE));
                    ret_list.add(map);
                }
            } else {  //服务器返回resultcode0  代表错误了
                Map<String, String> map = new HashMap<String, String>();
                map.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    map.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        map.put("errorCode", resultMsg);
                    } else {
                        map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
                ret_list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
            ret_list.add(map);
        }
        return ret_list;
    }

    /**
     * 提现记录
     */
    public List<Map<String, String>> getWebserviceResult_getQIANCashRecordList(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_CASH_RECORD, param, value);
        List<Map<String, String>> ret_list = new ArrayList<Map<String, String>>();
        if (result.equals("unusual")) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            ret_list.add(map);
            return ret_list;
        }
        Log.i("提现记录 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                JSONArray product_list = oj1.getJSONArray("cashDetail");
                if (product_list.length() == 0) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "0");
                    map.put("errorCode", "no");
                    ret_list.add(map);
                    return ret_list;
                }
                for (int i = 0; i < product_list.length(); i++) {
                    JSONObject product = product_list.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "1");
                    map.put("new", Check.jsonGetStringAnalysis(oj1, "new"));
                    map.put(BaseParam.QIAN_CASH_RECORD_ADD_TIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_CASH_RECORD_ADD_TIME));
                    map.put(BaseParam.QIAN_CASH_RECORD_BANK_NAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_CASH_RECORD_BANK_NAME));
                    map.put(BaseParam.QIAN_CASH_RECORD_BANK_NO, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_CASH_RECORD_BANK_NO));
                    map.put(BaseParam.QIAN_CASH_RECORD_ID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_CASH_RECORD_ID));
                    map.put(BaseParam.QIAN_CASH_RECORD_MONEY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_CASH_RECORD_MONEY));
                    map.put(BaseParam.QIAN_CASH_RECORD_STATUS_DESC, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_CASH_RECORD_STATUS_DESC));
                    map.put(BaseParam.QIAN_CASH_RECORD_STATUS, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_CASH_RECORD_STATUS));
                    ret_list.add(map);
                }
            } else {
                Map<String, String> map = new HashMap<String, String>();
                map.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    map.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        map.put("errorCode", resultMsg);
                    } else {
                        map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
                ret_list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
            ret_list.add(map);
        }
        return ret_list;
    }

    /**
     * 产品详情
     */
    public Map<String, String> getWebserviceResult_getQIANGetProductContent(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_GET_PRODUCT_CONTENT, param, value);
        Map<String, String> map = new HashMap<String, String>();
        if (result.equals("unusual")) {
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            return map;
        }
        Log.i("产品详情 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                map.put(BaseParam.QIAN_PRODUCT_ACT_TITLE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_PRODUCT_ACT_TITLE));
                map.put(BaseParam.QIAN_PRODUCT_ACT_URL, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_PRODUCT_ACT_URL));
                map.put("tenderCount", Check.jsonGetStringAnalysis(oj1, "tenderCount"));
                map.put("repayment", Check.jsonGetStringAnalysis(oj1, "repayment"));
                map.put("valueDay", Check.jsonGetStringAnalysis(oj1, "valueDay"));
                map.put("productDes", Check.jsonGetStringAnalysis(oj1, "productDes"));
                map.put("phoneList", Check.jsonGetStringAnalysis(oj1, "phoneList"));

                map.put("fastestTenderTime", Check.jsonGetStringAnalysis(oj1, "fastestTenderTime"));
                map.put("largestTenderTime", Check.jsonGetStringAnalysis(oj1, "largestTenderTime"));
                map.put("lastTenderTime", Check.jsonGetStringAnalysis(oj1, "lastTenderTime"));

                map.put("fastestTender", Check.jsonGetStringAnalysis(oj1, "fastestTender"));
                map.put("largestTenderUser", Check.jsonGetStringAnalysis(oj1, "largestTenderUser"));
                map.put("largestTenderSum", Check.jsonGetStringAnalysis(oj1, "largestTenderSum"));
                map.put("lastTender", Check.jsonGetStringAnalysis(oj1, "lastTender"));
                map.put("sendFlag", Check.jsonGetStringAnalysis(oj1, "sendFlag"));
                map.put("showStatus", Check.jsonGetStringAnalysis(oj1, "showStatus"));
                map.put("interestDay", Check.jsonGetStringAnalysis(oj1, "interestDay"));
                map.put("firstRedPacket",Check.jsonGetStringAnalysis(oj1, "firstRedPacket")); //首投红包金额
                map.put("highestRedPacket",Check.jsonGetStringAnalysis(oj1, "highestRedPacket")); //最高额红包金额
                map.put("lastRedPacket",Check.jsonGetStringAnalysis(oj1, "lastRedPacket")); //最高额红包金额
                JSONObject product = new JSONObject(Check.jsonGetStringAnalysis(oj1, "product"));
                if (product == null || product.equals("")) {
                    map.put("resultCode", "0");
                    map.put("errorCode", "没有数据可获取");
                    return map;
                } else {
                    map.put("vouchList", Check.jsonGetStringAnalysis(oj1, "vouchList"));
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_PRODUCT_ACCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ACCOUNT));
                    map.put(BaseParam.QIAN_PRODUCT_ACCOUNTYES, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ACCOUNTYES));
                    map.put(BaseParam.QIAN_PRODUCT_ADDTIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ADDTIME));
                    map.put(BaseParam.QIAN_PRODUCT_APR, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_APR));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWID));
                    map.put(BaseParam.QIAN_PRODUCT_CITY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_CITY));
                    map.put(BaseParam.QIAN_PRODUCT_COMPANY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_COMPANY));
                    map.put(BaseParam.QIAN_PRODUCT_CONTENT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_CONTENT));
                    map.put(BaseParam.QIAN_PRODUCT_DEBTORINFO, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_DEBTORINFO));
                    map.put("countDownTime", Check.jsonGetStringAnalysis(product, "countDownTime"));
                    map.put(BaseParam.QIAN_PRODUCT_FLOWCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_FLOWCOUNT));
                    map.put(BaseParam.QIAN_PRODUCT_FLOWMONEY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_FLOWMONEY));
                    map.put(BaseParam.QIAN_PRODUCT_FLOWYESCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_FLOWYESCOUNT));
                    map.put(BaseParam.QIAN_PRODUCT_FRANCHISEEID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_FRANCHISEEID));
                    map.put(BaseParam.QIAN_PRODUCT_FRANCHISEENAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_FRANCHISEENAME));
                    map.put(BaseParam.QIAN_PRODUCT_FRANCHISEETIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_FRANCHISEETIME));
                    map.put(BaseParam.QIAN_PRODUCT_FUNDUSAGE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_FUNDUSAGE));
                    map.put(BaseParam.QIAN_PRODUCT_HASREPAIDPERIOD, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_HASREPAIDPERIOD));
                    map.put(BaseParam.QIAN_PRODUCT_ISEXPERIENCE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ISEXPERIENCE));
                    map.put(BaseParam.QIAN_PRODUCT_ISDAY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ISDAY));
                    map.put(BaseParam.QIAN_PRODUCT_LOGO, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_LOGO));
                    map.put(BaseParam.QIAN_PRODUCT_NAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_NAME));
                    map.put(BaseParam.QIAN_PRODUCT_REPAYMENTSOURCE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_REPAYMENTSOURCE));
                    map.put(BaseParam.QIAN_PRODUCT_RISKEVALUATION, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_RISKEVALUATION));
                    map.put(BaseParam.QIAN_PRODUCT_STATUS, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_STATUS));
                    map.put(BaseParam.QIAN_PRODUCT_STYLE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_STYLE));
                    map.put(BaseParam.QIAN_PRODUCT_TENDERTIMES, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TENDERTIMES));
                    map.put(BaseParam.QIAN_PRODUCT_TIMELIMIT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TIMELIMIT));
                    map.put(BaseParam.QIAN_PRODUCT_TIMELIMITDAY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TIMELIMITDAY));
                    map.put(BaseParam.QIAN_PRODUCT_TOTALPERIOD, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TOTALPERIOD));
                    map.put(BaseParam.QIAN_PRODUCT_TYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TYPE));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWTYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWTYPE));
                    map.put(BaseParam.QIAN_PRODUCT_USERID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_USERID));
                    map.put(BaseParam.QIAN_PRODUCT_USERSHOWNAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_USERSHOWNAME));
                    map.put(BaseParam.QIAN_PRODUCT_USETYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_USETYPE));
                    map.put(BaseParam.QIAN_PRODUCT_PRODUCTSTATUS, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_PRODUCTSTATUS));
                    map.put(BaseParam.QIAN_PRODUCT_LASTREPAYTIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_LASTREPAYTIME));
                    map.put(BaseParam.QIAN_PRODUCT_LOWESTACCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_LOWESTACCOUNT));
                    map.put(BaseParam.QIAN_PRODUCT_ISNEWHAND, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ISNEWHAND));
                    map.put(BaseParam.QIAN_PRODUCT_PRESALETIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_PRESALETIME));
                    map.put(BaseParam.QIAN_PRODUCT_BR_TYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BR_TYPE));
                    map.put(BaseParam.QIAN_PRODUCT_EXTRAAWARDAPR, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_EXTRAAWARDAPR));
                    map.put(BaseParam.QIAN_PRODUCT_IS_ADVANCEREPAY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_IS_ADVANCEREPAY));
                    map.put("secVerifyTime", Check.jsonGetStringAnalysis(product, "secVerifyTime"));
                    map.put("apr", Check.jsonGetStringAnalysis(product, "apr"));
                    map.put("validTime",Check.jsonGetStringAnalysis(product, "validTime")); //募集周期
                    map.put("interestStartTime",Check.jsonGetStringAnalysis(product, "interestStartTime")); //计息时间
                    map.put("interestEndTime",Check.jsonGetStringAnalysis(product, "interestEndTime")); //回款日

                }
            } else {
                map.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    map.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        map.put("errorCode", resultMsg);
                    } else {
                        map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return map;
    }

    /**
     * 账户余额
     */
    public Map<String, String> getWebserviceResult_getQIANGetAccountBalance(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_ACCOUNT_BALANCE, param, value);
        Map<String, String> map = new HashMap<String, String>();
        if (result.equals("unusual")) {
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            return map;
        }
        Log.i("账户余额 json格式返回", result);

        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                map.put("resultCode", "1");
                map.put(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_BALANCE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_ACCOUNT_BALANCE_KEY_BALANCE));
                map.put(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_DISPOSECASH, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_ACCOUNT_BALANCE_KEY_DISPOSECASH));
                map.put(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_CASHFEE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_ACCOUNT_BALANCE_KEY_CASHFEE));
                map.put(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_CURRENMONTHHASCASH, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_ACCOUNT_BALANCE_KEY_CURRENMONTHHASCASH));
                map.put(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_FREECASHCOUNT, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_ACCOUNT_BALANCE_KEY_FREECASHCOUNT));
            } else {
                map.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    map.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        map.put("errorCode", resultMsg);
                    } else {
                        map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return map;
    }


    /**
     * 项目投资记录
     */
    public List<Map<String, String>> getWebserviceResult_getQIANProductMoreList(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_PRODUCT_MORE_JL, param, value);
        List<Map<String, String>> ret_list = new ArrayList<Map<String, String>>();
        if (result.equals("unusual")) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            ret_list.add(map);
            return ret_list;
        }
        Log.i("项目投资记录 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                JSONArray product_list = oj1.getJSONArray("tenderList");
                if (product_list.length() == 0) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "0");
                    map.put("errorCode", "no");
                    ret_list.add(map);
                    return ret_list;
                }
                for (int i = 0; i < product_list.length(); i++) {
                    JSONObject product = product_list.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_PRODUCT_ACCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ACCOUNT));
                    map.put(BaseParam.QIAN_PRODUCT_ADDTIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ADDTIME));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWID));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWNAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWNAME));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWSTATUS, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWSTATUS));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWTYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWTYPE));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWUSERID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWUSERID));
                    map.put(BaseParam.QIAN_PRODUCT_FRANCHISEENAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_FRANCHISEENAME));
                    map.put(BaseParam.QIAN_PRODUCT_HASCOLLECTEDPERIOD, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_HASCOLLECTEDPERIOD));
                    map.put(BaseParam.QIAN_PRODUCT_ISEXPERIENCE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ISEXPERIENCE));
                    map.put(BaseParam.QIAN_PRODUCT_MONEY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_MONEY));
                    map.put(BaseParam.QIAN_PRODUCT_TENDERID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TENDERID));
                    map.put(BaseParam.QIAN_PRODUCT_TENDERSTATUS, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TENDERSTATUS));
                    map.put(BaseParam.QIAN_PRODUCT_TENDERTYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TENDERTYPE));
                    map.put(BaseParam.QIAN_PRODUCT_TOTALPERIOD, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TOTALPERIOD));
                    map.put(BaseParam.QIAN_PRODUCT_USERID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_USERID));
                    map.put(BaseParam.QIAN_PRODUCT_USERSHOWNAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_USERSHOWNAME));
                    ret_list.add(map);
                }
            } else {
                Map<String, String> map = new HashMap<String, String>();
                map.put("resultCode", "0");
                map.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
                ret_list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
            ret_list.add(map);
        }
        return ret_list;
    }

    /**
     * 获取余额提现页需要的信息
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> getWebserviceResult_getGoWithdrawInfo(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_BALANCE_WITHDROW_SHOW, param, value);
        Map<String, String> map = new HashMap<String, String>();
        if (result.equals("unusual")) {
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            return map;
        }
        Log.i("获取余额提现页需要的信息 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                map.put("resultCode", "1");
                map.put(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_BALANCE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_BALANCE));
                map.put(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_TOK, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_TOK));
                map.put(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_IS_NEED_POPSETCARD, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_IS_NEED_POPSETCARD));
                String bankObjectStr = Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_BANKCARD);
                if (!TextUtils.isEmpty(bankObjectStr)) {
                    JSONObject bankBeanObject = new JSONObject(bankObjectStr);
                    if (bankBeanObject != null) {
                        map.put(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_IS_NEED, Check.jsonGetStringAnalysis(bankBeanObject, BaseParam.QIAN_BALANCE_WITHDROW_SHOW_IS_NEED));
                        map.put(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_BANK_SHORT_NAME, Check.jsonGetStringAnalysis(bankBeanObject, BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_BANK_SHORT_NAME));
                        map.put(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_HIDDEN_CARD_NO, Check.jsonGetStringAnalysis(bankBeanObject, BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_HIDDEN_CARD_NO));
                        map.put(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_ID, Check.jsonGetStringAnalysis(bankBeanObject, BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_ID));
                    }
                }
            } else {
                map.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    map.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        map.put("errorCode", resultMsg);
                    } else {
                        map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return map;
    }

    /**
     * 获取余额提现 提交
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> getWebserviceResult_commitWithdrawCash(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_BALANCE_WITHDROW_COMMIT, param, value);
        Map<String, String> map = new HashMap<String, String>();
        if (result.equals("unusual")) {
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            return map;
        }
        Log.i("获取余额提现提交 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                map.put("resultCode", "1");
                map.put(BaseParam.QIAN_BALANCE_WITHDROW_COMMIT_STATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BALANCE_WITHDROW_COMMIT_STATUS));
            } else {
                map.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    map.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        map.put("errorCode", resultMsg);
                    } else {
                        map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return map;
    }

    /**
     * 修改密码
     */
    public Map<String, String> getWebserviceResult_getQIANMyUpdatePassword(
            ArrayList<String> param, ArrayList<String> value, String login) {
        String result = "";
        if (login.equals("1")) {   //修改登录密码
            result = generalRequestPost1(BaseParam.URL_QIAN_MY_LOGINPASSWORD, param, value);
        } else {  //修改交易密码
            result = generalRequestPost1(BaseParam.URL_QIAN_MY_JYPASSWORD, param, value);
        }
        Map<String, String> map = new HashMap<String, String>();
        if (result.equals("unusual")) {
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            return map;
        }
        Log.i("修改密码 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject product = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                if (product == null || product.equals("")) {
                    map.put("resultCode", "0");
                    map.put("errorCode", "没有数据可获取");
                    return map;
                } else {
                    map.put("resultCode", "1");
                }
            } else {
                map.put("resultCode", "0");
                map.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return map;
    }

    /**
     * 消息 公告
     */
    public String getWebserviceResult_getQIANMoreAnnouncementList(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_NOTICE, param, value);
        return result;
    }

    /**
     * 充值到投资准备金
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> JsonRequeatThreadRechargeBalance(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_RECHARGE_BALANCE, param, value);
        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("充值到投资准备金 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject json = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_RECHARGE_BALANCE_PAYMENTINFO, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_RECHARGE_BALANCE_PAYMENTINFO));
                String paymentStr = Check.jsonGetStringAnalysis(json, BaseParam.QIAN_RECHARGE_BALANCE_PAYMENTINFO);
                if (!TextUtils.isEmpty(paymentStr)) {
                    JSONObject paymentObject = new JSONObject(paymentStr);
                    ret_list.put(BaseParam.QIAN_RECHARGE_BALANCE_NO_ORDER, paymentObject.getString(BaseParam.QIAN_RECHARGE_BALANCE_NO_ORDER));
                }
            } else {
                ret_list.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    ret_list.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        ret_list.put("errorCode", resultMsg);
                    } else {
                        ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 充值到准备金轮询
     * 充查询充值订单结果
     *
     * @return
     */
    public Map<String, String> JsonRequstThreadRechargeResult(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_RECHARGE_RESULT, param, value);

        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("充值到准备金轮询 json格式返回---", result);
        Log.i("---充查询充值订单结果 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject json = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_RECHARGE_RESULT_STATUS, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_RECHARGE_RESULT_STATUS));
            } else if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("2")) {
                ret_list.put("resultCode", "2");
            } else {
                ret_list.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    ret_list.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        ret_list.put("errorCode", resultMsg);
                    } else {
                        ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 投资准备金申购
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> JsonRequestThreadPayProduct(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_TENDER_1XV4, param, value);
        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("投资准备金申购1.4.0 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject json = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_TENDER_1XV4_ORDER_NO, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_TENDER_1XV4_ORDER_NO));
            } else {
                ret_list.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    ret_list.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        ret_list.put("errorCode", resultMsg);
                    } else {
                        ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 投资准备金申购 结果查询
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> JsonRequestThreadPayProductCheckOrder(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(
                BaseParam.URL_QIAN_TENDER_RESULT_1XV4, param, value);
        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("投资准备金申购结果查询1.4.0", "接下");
        Log.i("接上 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject json = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_TENDER_RESULT_STATUS, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_TENDER_RESULT_STATUS));
            } else if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("2")) {
                ret_list.put("resultCode", "2");
            } else {
                ret_list.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    ret_list.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        ret_list.put("errorCode", resultMsg);
                    } else {
                        ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 获取用户状态信息
     *
     * @return
     */
    public Map<String, String> getUserStatusInfo(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_USER_STATUS_INFORMATION, param, value);
        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("获取用户状态信息1.4.0 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject json = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_USER_STATUS_INFO_BANK_CARD, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_USER_STATUS_INFO_BANK_CARD));
                ret_list.put(BaseParam.QIAN_USER_STATUS_INFO_NEED_POP, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_USER_STATUS_INFO_NEED_POP));
                ret_list.put(BaseParam.QIAN_USER_STATUS_INFO_PAY_PWD, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_USER_STATUS_INFO_PAY_PWD));
                ret_list.put(BaseParam.QIAN_USER_STATUS_INFO_REAL_NAME, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_USER_STATUS_INFO_REAL_NAME));
                ret_list.put(BaseParam.QIAN_USER_STATUS_INFO_NEWHAND_STATUS, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_USER_STATUS_INFO_NEWHAND_STATUS));
            } else {
                ret_list.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    ret_list.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        ret_list.put("errorCode", resultMsg);
                    } else {
                        ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 推广
     *
     * @param param
     * @param value
     * @return
     */
    public String getWebserviceResult_getQIANDefault(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_PROMOTIONSTAT, param, value);
        Log.i("推广(导航页) json格式返回", result);
        return result;
    }

    /**
     * 刷新
     * 账户管理 -刷新安全中心
     */
    public String getWebserviceResult_getQIANRefresh(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_SALE, param, value);
        Log.i("刷新 json格式返回", result);
        return result;
    }

    /**
     * 意见反馈
     */
    public String getWebserviceResult_getQIANMoreSuggest(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_FEEDBACK, param, value);
        Log.i("意见反馈 json格式返回", result);
        return result;
    }

    /**
     * 浮动广告
     */
    public String getWebserviceResult_getClickActivity(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_CLICKACTIVITY, param, value);
        Log.i("浮动广告 URL_CLICK json格式返回", result);
        return result;
    }


    /**
     * 首页
     *
     * @param param
     * @param value
     * @return
     */
    public String getWebserviceResult_getQIANMyHomePageGaiList(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_HOMEPAGEGAI, param, value);
        Log.i("首页 json格式返回", result);
        Logger.i( result);
        return result;
    }


    /**
     * 登陆 点击输入手机号的下一步按钮调用，手机存在校验
     *
     * @param param
     * @param value
     * @return
     */
    public HashMap<String, String> getWebserviceResult_getQIANPhoneCheckLogin(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_PHONECHECK, param, value);
        Log.i("登录(校验手机号) json格式返回", result);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_LOGIN_PHONEEXIST, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_LOGIN_PHONEEXIST));
                ret_list.put(BaseParam.QIAN_LOGIN_CARDEXIST, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_LOGIN_CARDEXIST));
                ret_list.put("userIcon", Check.jsonGetStringAnalysis(oj1,"userIcon"));
            } else {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 我的内助(我的钱袋)
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> getWebserviceResult_getQIANProductMyGai(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_MY, param, value);
        Map<String, String> map = new HashMap<String, String>();
        if (result.equals("unusual")) {
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            return map;
        }
        Log.i("我的钱内助", "-------");
        Logger.json(result.toString());
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                if (oj == null) {
                    map.put("resultCode", "0");
                    map.put("errorCode", "没有数据可获取");
                    return map;
                }
                JSONObject product = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                if (product == null || product.equals("")) {
                    map.put("resultCode", "0");
                    map.put("errorCode", "没有数据可获取");
                    return map;
                } else {
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_MY_BALANCE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_BALANCE));
                    map.put(BaseParam.QIAN_MY_INVESTINGWAITINTEREST, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_INVESTINGWAITINTEREST));
                    map.put(BaseParam.QIAN_MY_INVESTINGCAPITAL, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_INVESTINGCAPITAL));
                    map.put(BaseParam.QIAN_MY_COLLECTION, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_COLLECTION));
                    map.put(BaseParam.QIAN_MY_EARNEDTHISMOUTH, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_EARNEDTHISMOUTH));
                    map.put(BaseParam.QIAN_MY_ACCUMULATEDINCOME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_ACCUMULATEDINCOME));
                    map.put(BaseParam.QIAN_MY_REPAYACCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAYACCOUNT));
                    map.put(BaseParam.QIAN_MY_REPAYTIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAYTIME));
                    map.put(BaseParam.QIAN_MY_TENDERACCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_TENDERACCOUNT));
                    map.put(BaseParam.QIAN_MY_TENDERTIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_TENDERTIME));
                    map.put(BaseParam.QIAN_MY_USERMONEY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_USERMONEY));
                    map.put(BaseParam.QIAN_MY_USERNAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_USERNAME));
                    map.put(BaseParam.QIAN_MY_PHONE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_PHONE));
                    map.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE));
                    map.put(BaseParam.QIAN_MY_REDPACKETCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REDPACKETCOUNT));
                    map.put(BaseParam.QIAN_MY_AVAYARURL, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_AVAYARURL));
                    map.put(BaseParam.QIAN_MY_TENDER_YES_INTEREST, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_TENDER_YES_INTEREST));
                    map.put(BaseParam.QIAN_MY_INVESTING_WAIT_INTEREST, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_INVESTING_WAIT_INTEREST));
                    map.put(BaseParam.QIAN_MY_INVESTING_CAPITAL, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_INVESTING_CAPITAL));
                    map.put(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_DISPOSECASH, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_ACCOUNT_BALANCE_KEY_DISPOSECASH));
                    map.put("borrowSum", Check.jsonGetStringAnalysis(product, "borrowSum"));
                    map.put("useRedpacketSum", Check.jsonGetStringAnalysis(product, "useRedpacketSum"));
                    map.put("accumulatedIncome", Check.jsonGetStringAnalysis(product, "accumulatedIncome"));
                    map.put("hasGetExtraAward", Check.jsonGetStringAnalysis(product, "hasGetExtraAward"));
                    map.put("notGetExtraAward", Check.jsonGetStringAnalysis(product, "notGetExtraAward"));
                    map.put("balanceChangeTimes", Check.jsonGetStringAnalysis(product, "balanceChangeTimes"));
                    map.put("todayCollectionNum", Check.jsonGetStringAnalysis(product, "todayCollectionNum"));
                    map.put("currentMonthHasCash", Check.jsonGetStringAnalysis(product, "currentMonthHasCash"));
                    map.put("borrowAuto", Check.jsonGetStringAnalysis(product, "borrowAuto"));
                    map.put("activityList", Check.jsonGetStringAnalysis(product, "activityList"));
                    map.put("interestNormal", Check.jsonGetStringAnalysis(product, "interestNormal"));
                    map.put("interestExtra", Check.jsonGetStringAnalysis(product, "interestExtra"));
                    map.put("waitInterestNormal", Check.jsonGetStringAnalysis(product, "waitInterestNormal"));
                    map.put("waitInterestExtra", Check.jsonGetStringAnalysis(product, "waitInterestExtra"));
//                    map.put("activityList",Check.jsonGetStringAnalysis(product,"activityList"));
                    map.put("couponCount",Check.jsonGetStringAnalysis(product,"couponCount"));
                    map.put("score",Check.jsonGetStringAnalysis(product,"score"));
                    String activityObject = Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_ACTIVITY);
                    if (activityObject != null) {
                        JSONObject activityPic = new JSONObject(activityObject);
                        if (activityPic != null) {
                            map.put(BaseParam.QIAN_MY_ACTIVITY_URL, Check.jsonGetStringAnalysis(activityPic, BaseParam.QIAN_MY_ACTIVITY_URL));
                            map.put(BaseParam.QIAN_MY_ACTIVITY_LOCATION_URL, Check.jsonGetStringAnalysis(activityPic, BaseParam.QIAN_MY_ACTIVITY_LOCATION_URL));
                        }
                    }
                }
            } else {
                map.put("resultCode", "0");
                String errorCode = Check.jsonGetStringAnalysis(oj, "errorCode");
                if (!TextUtils.isEmpty(errorCode)) {
                    map.put("errorCode", Check.checkReturn(errorCode));
                } else {
                    String resultMsg = Check.jsonGetStringAnalysis(oj, "resultMsg");
                    if (!TextUtils.isEmpty(resultMsg)) {
                        map.put("errorCode", resultMsg);
                    } else {
                        map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return map;
    }

    /**
     * 找回方式
     */
    public HashMap<String, String> getWebserviceResult_getQIANRetrieveGai(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_CHECKRESETLOGIN, param, value);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("找回方式 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put("userId", Check.jsonGetStringAnalysis(oj1, "userId"));
            } else {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 发布公告给手机客户端-（针对已登录用户）
     */
    public List<Map<String, String>> getWebserviceResult_getQIANPublishuserNotice(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_PUBLISHUSERNOTICE, param, value);
        List<Map<String, String>> ret_list = new ArrayList<Map<String, String>>();
        if (result.equals("unusual")) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            ret_list.add(map);
            return ret_list;
        }
        Log.i("发布公告给客户端(已登录) json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                JSONArray product_list = oj1.getJSONArray("noticelist");
                if (product_list.length() == 0) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "0");
                    map.put("errorCode", "no");
                    ret_list.add(map);
                    return ret_list;
                }
                for (int i = 0; i < product_list.length(); i++) {
                    JSONObject product = product_list.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_MY_NOTICEID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_NOTICEID));
                    map.put(BaseParam.QIAN_MY_CONTENT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_CONTENT));
                    map.put(BaseParam.qIAN_MY_SHOWADDR, Check.jsonGetStringAnalysis(product, BaseParam.qIAN_MY_SHOWADDR));
                    map.put(BaseParam.QIAN_MY_TITLE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_TITLE));
                    map.put(BaseParam.QIAN_MY_TYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_TYPE));
                    map.put(BaseParam.QIAN_MY_URL, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_URL));
                    ret_list.add(map);
                }
            } else {
                Map<String, String> map = new HashMap<String, String>();
                map.put("resultCode", "0");
                map.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
                ret_list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
            ret_list.add(map);
        }
        return ret_list;
    }

    /**
     * 红包  和  活动
     */
    public String getWebserviceResult_getQIANUpdateuserNoticeRead(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_UPDATEUSERNOTICEREAD, param, value);
        return result;
    }

    /**
     * 投资概况
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> getWebserviceResult_getQIANUseMoneyDetial(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_MYTENDERDETIAL, param, value);
        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("投资概况 json数据返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                JSONObject oj2 = new JSONObject(Check.jsonGetStringAnalysis(oj1, "tenderdetail"));
                ret_list.put(BaseParam.QIAN_USEMONEY_ACCOUNT, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_ACCOUNT));
                ret_list.put(BaseParam.QIAN_USEMONEY_ADDTIME, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_ADDTIME));
                ret_list.put(BaseParam.QIAN_USEMONEY_APR, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_APR));
                ret_list.put(BaseParam.QIAN_USEMONEY_BORROWID, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_BORROWID));
                ret_list.put(BaseParam.QIAN_USEMONEY_BORROWNAME, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_BORROWNAME));
                ret_list.put(BaseParam.QIAN_USEMONEY_BORROWSTATUS, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_BORROWSTATUS));
                ret_list.put(BaseParam.QIAN_USEMONEY_INTEREST, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_INTEREST));
                ret_list.put(BaseParam.QIAN_USEMONEY_BACKPLACE, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_BACKPLACE));
                ret_list.put(BaseParam.QIAN_USEMONEY_REPAYMENTTIME, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_REPAYMENTTIME));
                ret_list.put(BaseParam.QIAN_USEMONEY_TENDERID, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_TENDERID));
                ret_list.put(BaseParam.QIAN_USEMONEY_ISBIN, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_ISBIN));
                ret_list.put(BaseParam.QIAN_USEMONEY_ISNEED, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_ISNEED));
                ret_list.put(BaseParam.QIAN_USEMONEY_EXTRAAWARD, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_EXTRAAWARD));
                ret_list.put("status", Check.jsonGetStringAnalysis(oj2,"status"));
                if (Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_ISBIN).equals("1")) {
                    ret_list.put(BaseParam.QIAN_USEMONEY_BANKNAME, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_BANKNAME));
                    ret_list.put(BaseParam.QIAN_USEMONEY_HIDDENCARDNO, Check.jsonGetStringAnalysis(oj2, BaseParam.QIAN_USEMONEY_HIDDENCARDNO));
                }
                ret_list.put(BaseParam.QIAN_USEMONEY_DEFAULTBANKNAME, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_USEMONEY_DEFAULTBANKNAME));
                ret_list.put(BaseParam.QIAN_USEMONEY_DEFAULTBANKCARD, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_USEMONEY_DEFAULTBANKCARD));
            } else {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 我的红包
     */
    public String getWebserviceResult_getQIANMyRedpacketsGaiList(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_MY_REDPACKETSGAI, param, value);
        Log.i("我的红包 json数据返回", result);
        return result;
    }

    /**
     * 我的奖励
     */
    public String getWebserviceResult_getQIANMyAwardList(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_MY_AWARD, param, value);
        Log.i("我的奖励 json数据返回", result);
        return result;
    }


    /**
     * 申购  选择红包  和   银行卡
     *
     * @param param
     * @param value
     * @return
     */
    public String getWebserviceResult_getQIANGetpayInfo(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_GETPAYINFO, param, value);
        Log.i(" 申购选择红包和银行卡 json格式", result);
        Logger.i(result);
        return result;
    }

    /**
     * 申购绑定银行卡
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> getWebserviceResult_getQIANBuyAddBank(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_REALINFOBACK, param, value);
        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("申购绑定银行卡  json格式", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put("real_name", Check.jsonGetStringAnalysis(oj1, "real_name"));
                ret_list.put("card_no", Check.jsonGetStringAnalysis(oj1, "card_no"));
            } else {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 申购前添加银行卡
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> getWebserviceResult_getQIANBuyAddBankCard(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_ADDBANKCARD, param, value);
        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("申购前添加银行卡  json格式", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj2 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj2, "bankCard"));
                ret_list.put(BaseParam.QIAN_BANK_ID, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BANK_ID));
                ret_list.put(BaseParam.QIAN_BANK_ADDTIME, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BANK_ADDTIME));
                ret_list.put(BaseParam.QIAN_BANK_BANKCODE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BANK_BANKCODE));
                ret_list.put(BaseParam.QIAN_BANK_BANKSHORTNAME, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BANK_BANKSHORTNAME));
                ret_list.put(BaseParam.QIAN_BANK_HIDDENCARDNO, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BANK_HIDDENCARDNO));
                ret_list.put(BaseParam.QIAN_BANK_PERDAYLIMIT, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BANK_PERDAYLIMIT));
                ret_list.put(BaseParam.QIAN_BANK_PERDEALLIMIT, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BANK_PERDEALLIMIT));
                ret_list.put(BaseParam.QIAN_BANK_STATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BANK_STATUS));
                ret_list.put(BaseParam.QIAN_BANK_UNIQNO, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_BANK_UNIQNO));
            } else {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 注册
     *
     * @param param
     * @param value
     * @return
     */
    public HashMap<String, String> getWebserviceResult_getQIANNewReg(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_NEWREG, param, value);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("注册  json格式", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE));
                ret_list.put(BaseParam.QIAN_REDPACKETAMOUNT, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_REDPACKETAMOUNT));
                ret_list.put(BaseParam.QIAN_REDPACKETOPEN, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_REDPACKETOPEN));
                ret_list.put(BaseParam.QIAN_REDPACKETTYPE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_REDPACKETTYPE));
                ret_list.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_SHARE, Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_SHARE));
            } else {
                /**
                 *{"errorCode":"USER_REGISTER_MSG_VERIFICATION_CODE_ERROR","resultCode":0}
                 */
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 实名认证
     *
     * @param param
     * @param value
     * @return
     */
    public HashMap<String, String> getWebserviceResult_getQIANNewReal(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_SETTING_PAY_PASSWORD, param, value);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("实名认证  json格式", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject json = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put(BaseParam.QIAN_REDPACKETAMOUNT, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_REDPACKETAMOUNT));
                ret_list.put(BaseParam.QIAN_REDPACKETOPEN, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_REDPACKETOPEN));
                ret_list.put(BaseParam.QIAN_REDPACKETTYPE, Check.jsonGetStringAnalysis(json, BaseParam.QIAN_REDPACKETTYPE));
            } else {
                /**
                 *{"errorCode":"USER_REGISTER_MSG_VERIFICATION_CODE_ERROR","resultCode":0}
                 */
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }


    /**
     * 支付失败   取消订单
     */
    public Map<String, String> getWebserviceResult_getQIANChangTenderStatus(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_CHANGTENDERSTATUS, param, value);
        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("支付失败 取消订单 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put("success", Check.jsonGetStringAnalysis(oj1, "success"));
            } else {
                ret_list.put("resultMsg", Check.jsonGetStringAnalysis(oj, "resultMsg"));
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 我的银行卡
     *
     * @param param
     * @param value
     * @return
     */
    public List<Map<String, String>> getWebserviceResult_getQIANMyBankGai(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_MYBANKCARD, param, value);
        List<Map<String, String>> ret_list = new ArrayList<Map<String, String>>();
        if (result.equals("unusual")) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            ret_list.add(map);
            return ret_list;
        }
        Log.i("我的银行卡 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                JSONArray jArray = new JSONArray(Check.jsonGetStringAnalysis(oj1, "list"));
                if (jArray.length() == 0) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "0");
                    map.put("errorCode", "no_note");
                    ret_list.add(map);
                    return ret_list;
                }
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject bank = jArray.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_MY_BANK_ADDTIME, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_ADDTIME));
                    map.put(BaseParam.QIAN_MY_BANK_BANKCODE, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_BANKCODE));
                    map.put(BaseParam.QIAN_MY_BANK_BANKSHORTNAME, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_BANKSHORTNAME));
                    map.put(BaseParam.QIAN_MY_BANK_HIDDENCARDNO, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_HIDDENCARDNO));
                    map.put(BaseParam.QIAN_MY_BANK_ID, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_ID));
                    map.put(BaseParam.QIAN_MY_BANK_PERDAYLIMIT, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_PERDAYLIMIT));
                    map.put(BaseParam.QIAN_MY_BANK_PERDEALLIMIT, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_PERDEALLIMIT));
                    map.put(BaseParam.QIAN_MY_BANK_STATUS, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_STATUS));
                    map.put(BaseParam.QIAN_MY_BANK_UNIQNO, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_UNIQNO));
                    map.put(BaseParam.QIAN_MY_BANK_ISDEFAULT, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_ISDEFAULT));
                    map.put(BaseParam.QIAN_BANK_REAL_USER_NAEM, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_BANK_REAL_USER_NAEM));
                    map.put(BaseParam.QIAN_BANK_IS_NEED_ADD_INFORMATION, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_BANK_IS_NEED_ADD_INFORMATION));
                    ret_list.add(map);
                }
            } else {
                Map<String, String> map = new HashMap<String, String>();
                map.put("resultCode", "0");
                map.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
                ret_list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
            ret_list.add(map);
        }
        return ret_list;
    }

    /**
     * 我的投资记录
     *
     * @param param
     * @param value
     * @return
     */
    public List<Map<String, String>> getWebserviceResult_getQIANMyGethistoryTender(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_MY_GETHISTORYTENDER, param, value);
        List<Map<String, String>> ret_list = new ArrayList<Map<String, String>>();
        if (result.equals("unusual")) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            ret_list.add(map);
            return ret_list;
        }
        Log.i("我的投资记录 json格式 ", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                JSONArray product_list = oj1.getJSONArray("list");
                if (product_list.length() == 0) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "0");
                    map.put("errorCode", "no_note");
                    ret_list.add(map);
                    return ret_list;
                }
                for (int i = 0; i < product_list.length(); i++) {
                    JSONObject product = product_list.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_PRODUCT_ACCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ACCOUNT));
                    map.put(BaseParam.QIAN_PRODUCT_ADDTIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ADDTIME));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWID));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWNAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWNAME));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWSTATUS, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWSTATUS));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWTYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWTYPE));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWUSERID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWUSERID));
                    map.put(BaseParam.QIAN_PRODUCT_FRANCHISEENAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_FRANCHISEENAME));
                    map.put(BaseParam.QIAN_PRODUCT_HASCOLLECTEDPERIOD, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_HASCOLLECTEDPERIOD));
                    map.put(BaseParam.QIAN_PRODUCT_ISEXPERIENCE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_ISEXPERIENCE));
                    map.put(BaseParam.QIAN_PRODUCT_MONEY, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_MONEY));
                    map.put(BaseParam.QIAN_PRODUCT_TENDERID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TENDERID));
                    map.put(BaseParam.QIAN_PRODUCT_TENDERSTATUS, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TENDERSTATUS));
                    map.put(BaseParam.QIAN_PRODUCT_TENDERTYPE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TENDERTYPE));
                    map.put(BaseParam.QIAN_PRODUCT_TOTALPERIOD, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_TOTALPERIOD));
                    map.put(BaseParam.QIAN_PRODUCT_USERID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_USERID));
                    map.put(BaseParam.QIAN_PRODUCT_USERSHOWNAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_USERSHOWNAME));
                    map.put(BaseParam.QIAN_PRODUCT_INTEREST, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_INTEREST));
                    map.put(BaseParam.QIAN_PRODUCT_BORROWSTATUS, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_PRODUCT_BORROWSTATUS));
                    ret_list.add(map);
                }
            } else {
                Map<String, String> map = new HashMap<String, String>();
                map.put("resultCode", "0");
                map.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
                ret_list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
            ret_list.add(map);
        }
        return ret_list;
    }

    /**
     * 一分钱绑卡
     *
     * @param param
     * @param value
     * @return
     */
    public Map<String, String> getWebserviceResult_getQIANOneMoneyBinCard(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_ONEMONEYBINCARD, param, value);
        Map<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("一分钱绑卡 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                ret_list.put("orderNo", Check.jsonGetStringAnalysis(oj1, "orderNo"));
                ret_list.put("paymentInfo", Check.jsonGetStringAnalysis(oj1, "paymentInfo"));
            } else {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
        }
        return ret_list;
    }

    /**
     * 活动中心
     *
     * @param param 键
     * @param value 值
     * @return String
     */
    public String getWebserviceResult_getQIANMoreActiveList(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_MORE_ACTIVITY, param, value);
        Log.i("活动中心 json格式返回", result);
        return result;
    }

    /**
     * 设置  默认 银行卡
     *
     * @param param 键
     * @param value 值
     * @return String
     */
    public String getWebserviceResult_getQIANDefaultBank(ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_DEFAULTBANK, param, value);
        Log.i("设置 默认 银行卡 json格式返回", result);
        return result;

    }

    /**
     * 已回款 待回款
     *
     * @param param 键
     * @param value 值
     * @return List
     */
    public List<Map<String, String>> getWebserviceResult_getQIANMyRepaymentGaiList2(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_MY_RETURNRECORD2, param, value);
        List<Map<String, String>> ret_list = new ArrayList<Map<String, String>>();
        if (result.equals("unusual")) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", "连接服务器异常");
            ret_list.add(map);
            return ret_list;
        }
        Log.i("已回款 待回款 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                JSONArray product_list = oj1.getJSONArray("list");
                if (product_list.length() == 0) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "0");
                    map.put("errorCode", "no_note");
                    ret_list.add(map);
                    return ret_list;
                }
                for (int i = 0; i < product_list.length(); i++) {
                    JSONObject product = product_list.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_MY_REPAY_BORROWID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_BORROWID));
                    map.put(BaseParam.QIAN_MY_REPAY_BORROWNAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_BORROWNAME));
                    map.put(BaseParam.QIAN_MY_REPAY_CAPTIAL, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_CAPTIAL));
                    map.put(BaseParam.QIAN_MY_REPAY_INTEREST, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_INTEREST));
                    map.put(BaseParam.QIAN_MY_REPAY_REPAYMENTIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_REPAYMENTIME));
                    map.put(BaseParam.QIAN_MY_REPAY_REPAYMENTACCOUNT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_REPAYMENTACCOUNT));
                    map.put(BaseParam.QIAN_MY_REPAY_TENDERID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_TENDERID));
                    map.put(BaseParam.QIAN_MY_REPAY_TENDERTIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_TENDERTIME));
                    map.put(BaseParam.QIAN_MY_REPAY_ISBIN, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_ISBIN));
                    map.put(BaseParam.QIAN_MY_REPAY_BANKNAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_BANKNAME));
                    map.put(BaseParam.QIAN_MY_REPAY_HIDDENCARDNO, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_HIDDENCARDNO));
                    map.put(BaseParam.QIAN_MY_REPAY_BACKPLACE, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MY_REPAY_BACKPLACE));
                    ret_list.add(map);
                }
            } else {
                Map<String, String> map = new HashMap<String, String>();
                map.put("resultCode", "0");
                map.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
                ret_list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<String, String>();
            map.put("resultCode", "0");
            map.put("errorCode", BaseParam.ERRORCODE_CHECKFWQ);
            ret_list.add(map);
        }
        return ret_list;
    }

    /**
     * 找回交易密码
     *
     * @param param 键
     * @param value 值
     * @return HashMap
     */
    public HashMap<String, String> getWebserviceResult_getQIANFindPayPwd(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_FINDPAYPWD, param, value);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("找回交易密码 json格式返回", result);

        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("0")) {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
                ret_list.put("resultMsg", Check.jsonGetStringAnalysis(oj, "resultMsg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "json异常");
        }
        return ret_list;


    }

    /**
     * 找回交易密码  获取验证码
     *
     * @param param 键
     * @param value 值
     * @return HashMap
     */
    public HashMap<String, String> getWebserviceResult_getQIANForgetVerify(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_RETURN_FORGETVERIFY, param, value);
        HashMap<String, String> ret_list = new HashMap<String, String>();
        if (result.equals("unusual")) {
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "网络不稳定");
            return ret_list;
        }
        Log.i("找回交易密码时获取验证码 json格式返回", result);
        try {
            JSONObject oj = new JSONObject(result);
            ret_list.put("resultCode", Check.jsonGetStringAnalysis(oj, "resultCode"));
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("0")) {
                ret_list.put("errorCode", Check.jsonGetStringAnalysis(oj, "errorCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret_list.put("resultCode", "0");
            ret_list.put("errorCode", "json异常");
        }
        return ret_list;
    }

    /**
     * 获取启动页图片
     *
     * @param param 键
     * @param value 值
     * @return String
     */
    public String getWebserviceResult_getQIANGETStartPage(
            ArrayList<String> param, ArrayList<String> value) {
        String result = generalRequestPost1(BaseParam.URL_QIAN_GETSTARTPAGE, param, value);
        Log.i("获取启动页图片 json格式返回", result);
        return result;
    }
}
