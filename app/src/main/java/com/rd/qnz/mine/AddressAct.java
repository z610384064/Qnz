package com.rd.qnz.mine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.util.PostJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 收货地址
 * Created by Evonne on 2016/10/24.
 */
public class AddressAct extends BaseActivity implements View.OnClickListener {

    private String oauthToken;
    private CustomProgressDialog progressDialog = null;
    private GetDialog dia;
    private TextView delete_address, revise_address;
    private LinearLayout address_list;
    private String area, address, phone, realName;
    private TextView address_name, address_phone, address_area;
    private ImageView del_iv, revise_iv;

    /**
     *
     * 1.6.0 版本 新的增加地址的布局
     */
    private RelativeLayout rl_add_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_list);
        this.dia = new GetDialog();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        initBar();
        initView();
        queryAddress("query");
    }

    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("我的收货地址");
    }

    private void initView() {
        rl_add_address= (RelativeLayout) findViewById(R.id.rl_add_address);
        rl_add_address.setOnClickListener(this);
//        noaddress= (LinearLayout) findViewById(R.id.noaddress);
        address_list = (LinearLayout) findViewById(R.id.address_list);
        delete_address = (TextView) findViewById(R.id.delete_address);
        revise_address = (TextView) findViewById(R.id.revise_address);
        address_name = (TextView) findViewById(R.id.address_name);
        address_phone = (TextView) findViewById(R.id.address_phone);
        address_area = (TextView) findViewById(R.id.address_area);
        revise_iv = (ImageView) findViewById(R.id.revise_iv);
        del_iv = (ImageView) findViewById(R.id.del_iv);

        delete_address.setOnClickListener(this);
        revise_address.setOnClickListener(this);
        revise_iv.setOnClickListener(this);
        del_iv.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        queryAddress("query");
        Log.e("添加收货地址","onRestart");
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.rl_add_address:  //增加
                Intent intent1 = new Intent(getApplicationContext(), ReceiptAddress.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("type", 1);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.revise_iv:
            case R.id.revise_address:   //编辑收货地址
                Intent intent2 = new Intent(getApplicationContext(), ReceiptAddress.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putString("area", area);
                bundle.putString("address", address);
                bundle.putString("phone", phone);
                bundle.putString("realName", realName);
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.del_iv:
            case R.id.delete_address:  //删除地址
                LayoutInflater inflaterDl = LayoutInflater.from(AddressAct.this);
                LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_logout_layout, null);
                final Dialog dialog = new AlertDialog.Builder(AddressAct.this).create();
                dialog.show();
                dialog.getWindow().setContentView(layout);
                TextView message = (TextView) layout.findViewById(R.id.message);
                message.setText("确定删除该地址吗？");
                Button cancel = (Button) layout.findViewById(R.id.negativeButton);
                Button sure = (Button) layout.findViewById(R.id.positiveButton);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        queryAddress("del");
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     *  先查询一下该账号当前所拥有的地址,根据地址显示不同的布局
     * @param type
     */
    private void queryAddress(final String type) {
        JSONObject param = new JSONObject();
        MyApplication myApp = MyApplication.getInstance();
        String sign = null;
        try {
            param.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, Profile.getoAutoToken());
            param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
            param.put(BaseParam.URL_QIAN_API_SERVICE, "receiptAddress");
            param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);
            param.put("type", type);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + Profile.getoAutoToken(),
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=" + "receiptAddress",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                    "type" + "=" + type,
            };
            APIModel apiModel = new APIModel();
            sign = apiModel.sortStringArray(array);
            param.put(BaseParam.URL_QIAN_API_SIGN, sign);

            progressDialog = dia.getLoginDialog(AddressAct.this, "正在获取数据..");
            progressDialog.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = BaseParam.URL_QIAN_ADDRESS
                + "?oauthToken=" + Profile.getoAutoToken()
                + "&" + "appId=" + myApp.appId
                + "&" + "service=" + "receiptAddress"
                + "&" + "signType=" + myApp.signType
                + "&" + "sign=" + sign
                + "&" + "type=" + type;
         //使用volley访问的网络
        PostJsonObjectRequest request = new PostJsonObjectRequest(Request.Method.POST, url, param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("queryAddress", response.toString());
                        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                            progressDialog.dismiss();
                        }
                        try {
                            int errorcode = response.getInt("resultCode");
                            if (errorcode == 1) {
                                if (type.equals("query")) {
                                    JSONObject jsonObject = response.getJSONObject("resultData");
                                    if (jsonObject.length() > 0) {
                                        rl_add_address.setVisibility(View.GONE);
                                        address_list.setVisibility(View.VISIBLE);
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("ra");
                                        area = jsonObject1.getString("area");
                                        address = jsonObject1.getString("address");
                                        phone = jsonObject1.getString("phone");
                                        realName = jsonObject1.getString("realName");
                                        address_name.setText(realName);
                                        address_phone.setText(phone);
                                        address_area.setText(area + address);
                                    } else { //还没有添加地址
                                        rl_add_address.setVisibility(View.VISIBLE);
                                        address_list.setVisibility(View.GONE);
                                    }
                                } else if (type.equals("del")) {
                                    rl_add_address.setVisibility(View.VISIBLE);
                                    address_list.setVisibility(View.GONE);
                                }
                            } else if (errorcode == 0) {
                                String resultMsg = response.getString("resultMsg");
                                showToast(resultMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("queryAddress error", error.toString());
                        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                            progressDialog.dismiss();
                        }
                    }
                });
        requestQueue.add(request);
    }

}
