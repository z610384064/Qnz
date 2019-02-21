package com.rd.qnz.mine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.rd.qnz.entity.CityModel;
import com.rd.qnz.entity.DistrictModel;
import com.rd.qnz.entity.ProvinceModel;

import com.rd.qnz.util.XmlParserHandler;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.util.PostJsonObjectRequest;
import com.wheel.widget.OnWheelChangedListener;
import com.wheel.widget.WheelView;
import com.wheel.widget.adapters.ArrayWheelAdapter;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 *   添加收货地址,删除收货地址 页面
 * Created by Evonne on 2016/10/20.
 */
public class ReceiptAddress extends BaseActivity implements OnWheelChangedListener, View.OnClickListener {

    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private Button mBtnConfirm;
    private TextView select_address;
    private EditText address_more, address_phone, address_name;
    private LinearLayout linearLayout_address;
    private CustomProgressDialog progressDialog = null;
    private GetDialog dia;
    private String phone, name, select, more;
    private String name1, select1, more1;
    private int type;

    protected String[] mProvinceDatas;
    /**
     * key - 省
     * value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市
     * values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 区
     * values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 省份
     */
    protected String mCurrentProviceName;
    /**
     * 市名
     */
    protected String mCurrentCityName;
    /**
     * 区名
     */
    protected String mCurrentDistrictName = "";

    /**
     * 邮编
     */
    protected String mCurrentZipCode = "";

    /**
     * 解析xml文件
     */
    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            /* 获取解析出来的数据 */
            provinceList = handler.getDataList();
            /* 初始化默认选中的省、市、区 */
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_address);
        setSwipeBackEnable(false); //禁止滑动删除
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt("type");
        select = bundle.getString("area");
        more = bundle.getString("address");
        phone = bundle.getString("phone");
        name = bundle.getString("realName");
        initBar();
        inview();
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

        linearLayout_address = (LinearLayout) findViewById(R.id.linearLayout_address);
        linearLayout_address.setVisibility(View.VISIBLE);
        linearLayout_address.setOnClickListener(this);

        if (type == 2) {
            actionbar_side_name.setText("编辑收货地址");
        } else if (type == 1) {
            actionbar_side_name.setText("添加收货地址");
        }


    }

    private void inview() {
        this.dia = new GetDialog();
        address_name = (EditText) findViewById(R.id.address_name);
        address_more = (EditText) findViewById(R.id.address_more);
        address_phone = (EditText) findViewById(R.id.address_phone);
        select_address = (TextView) findViewById(R.id.select_address);

        if (type == 2) {
            address_name.setText(name);
            address_more.setText(more);
            address_phone.setText(phone);
            select_address.setText(select);
            address_name.setSelection(name.length());
        }
//        address_name.setInputType(InputType.TYPE_CLASS_TEXT);//光标定位到该控件
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        select_address.setOnClickListener(new View.OnClickListener() { //所在地区
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(select_address.getWindowToken(), 0);  //
                LayoutInflater inflaterDl = LayoutInflater.from(ReceiptAddress.this);
                LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_select_address, null);
                final Dialog dialog = new AlertDialog.Builder(ReceiptAddress.this).create();
                dialog.show();
                dialog.getWindow().setContentView(layout);
                mViewProvince = (WheelView) layout.findViewById(R.id.id_province); //省
                mViewCity = (WheelView) layout.findViewById(R.id.id_city);   //市
                mViewDistrict = (WheelView) layout.findViewById(R.id.id_district); //区
                mBtnConfirm = (Button) layout.findViewById(R.id.btn_confirm);
                setUpListener();
                setUpData();
                mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showSelectedResult();
                    }
                });
            }
        });
        handleWatcher();
    }

    private void handleWatcher() {
        address_more.addTextChangedListener(watcher);
        address_name.addTextChangedListener(watcher);
        select_address.addTextChangedListener(watcher);
        address_phone.addTextChangedListener(watcher);
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            phone = address_phone.getText().toString().trim();
            name = address_name.getText().toString().trim();
            select = select_address.getText().toString().trim();
            more = address_more.getText().toString().trim();

            /*if (phone.equals("") || name.equals("") || select.equals("请选择") || more.equals("")) {
                linearLayout_address.setClickable(false);
            } else {
                linearLayout_address.setClickable(true);
            }*/

            if (phone.length() > 11) {
                address_phone.setText(phone.substring(0, phone.length() - 1));
                address_phone.setSelection(address_phone.length());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub
        }
    };

    private void showSelectedResult() {
        //  select_address.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName + mCurrentZipCode);
        select_address.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);
    }

    private void setUpListener() {
        mViewProvince.addChangingListener(this);
        mViewCity.addChangingListener(this);
        mViewDistrict.addChangingListener(this);
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(ReceiptAddress.this, mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        mViewDistrict.setCurrentItem(0);

        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
        mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayout_address:
                if (name == null || name.equals("")) {
                    showToast("请输入收货人姓名");
                    return;
                }
                if (phone == null || phone.equals("")) {
                    showToast("请输入联系电话");
                    return;
                }
                if (select == null || select.equals("请选择")) {
                    showToast("请选择所在地区");
                    return;
                }
                if (more == null || more.equals("")) {
                    showToast("请输入详细地址");
                    return;
                }

                String regex = "[\u4E00-\u9FA5]+";
                if (!name.matches(regex)) {
                    showToast(R.string.toast_name_regex);
                    break;
                }
                if (!phone.matches("^\\d{11}$")) {
                    showToast(R.string.toast_phone_regex);
                    return;
                }
                if (select.equals("请选择")) {
                    showToast("请选择所在地区");
                    break;
                }
                if (more.length() < 5) {
                    showToast(R.string.toast_more_regex);
                    break;
                }
                try {
                    name = URLEncoder.encode(address_name.getText().toString().trim(), "UTF-8");
                    select = URLEncoder.encode(select_address.getText().toString().trim(), "UTF-8");
                    more = URLEncoder.encode(address_more.getText().toString().trim(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                modifyAddress();
                break;
            default:
                break;
        }
    }

    private void modifyAddress() {

        try {
            more1 = URLEncoder.encode(more, "UTF-8");
            select1 = URLEncoder.encode(select, "UTF-8");
            name1 = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JSONObject param = new JSONObject();
        MyApplication myApp = MyApplication.getInstance();
        String sign = null;
        try {
            param.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, Profile.getoAutoToken());
            param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
            param.put(BaseParam.URL_QIAN_API_SERVICE, "receiptAddress");
            param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);
            param.put("area", select1);
            param.put("address", more1);
            param.put("phone", phone);
            param.put("realName", name1);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + Profile.getoAutoToken(),
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=" + "receiptAddress",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                    "area" + "=" + select,
                    "address" + "=" + more,
                    "phone" + "=" + phone,
                    "realName" + "=" + name,
            };
            APIModel apiModel = new APIModel();
            sign = apiModel.sortStringArray(array);
            param.put(BaseParam.URL_QIAN_API_SIGN, sign);

            progressDialog = dia.getLoginDialog(ReceiptAddress.this, "正在获取数据..");
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
                + "&" + "area=" + select1
                + "&" + "address=" + more1
                + "&" + "phone=" + phone
                + "&" + "realName=" + name1;

        PostJsonObjectRequest request = new PostJsonObjectRequest(Request.Method.POST, url, param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("modifyAddress", response.toString());
                        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                            progressDialog.dismiss();
                        }
                        try {
                            int errorcode = response.getInt("resultCode");
                            if (errorcode == 1) {
                                finish();
                                showToast("保存成功");
                            } else if (errorcode == 0) {
                                String resultMsg = response.getString("resultMsg");
                                showToast(resultMsg);
                                showToast("保存失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("modifyAddress error", error.toString());
                        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                            progressDialog.dismiss();
                        }
                    }
                });
        requestQueue.add(request);
    }
}
