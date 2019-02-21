package com.rd.qnz.product;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 确认申购的时候：
 * 银行卡限额
 *
 * @author Evonne
 */
public class ProductContentQuotaBankAct extends BaseActivity {

    private ListView allbank_list;
    private ProductContentQuotaAllBankAdapter allListAdapter;
    private String allBankCardList = "";
    private List<Map<String, String>> all_list = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_quota_bank);
        allBankCardList = getIntent().getStringExtra("allBankCardList");
        initJsonAllBankCardList();
        initBar();
        initView();
    }

    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("支持银行及限额");
    }

    private void initView() {
        allbank_list = (ListView) findViewById(R.id.allbank_list);
        allListAdapter = new ProductContentQuotaAllBankAdapter(ProductContentQuotaBankAct.this, all_list);
        allbank_list.setAdapter(allListAdapter);

    }

    private void initJsonAllBankCardList() {
        try {
            JSONArray jArray = new JSONArray(allBankCardList);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject bank = jArray.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put(BaseParam.QIAN_ALL_BANK_BANKCODE, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_BANKCODE));
                map.put(BaseParam.QIAN_ALL_BANK_BANKNAME, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_BANKNAME));
                map.put(BaseParam.QIAN_ALL_BANK_BANKSHORTNAME, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_BANKSHORTNAME));
                map.put(BaseParam.QIAN_ALL_BANK_CHANNELCODE, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_CHANNELCODE));
                map.put(BaseParam.QIAN_ALL_BANK_ID, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_ID));
                map.put(BaseParam.QIAN_ALL_BANK_LOGOCSS, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_LOGOCSS));
                map.put(BaseParam.QIAN_ALL_BANK_PERDAYLIMIT, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_PERDAYLIMIT));
                map.put(BaseParam.QIAN_ALL_BANK_PERDEALLIMIT, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_PERDEALLIMIT));
                map.put(BaseParam.QIAN_ALL_BANK_STATUS, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_STATUS));
                all_list.add(map);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
