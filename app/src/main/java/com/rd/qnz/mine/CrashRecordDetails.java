package com.rd.qnz.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.bean.CrashRecordBean;
import com.rd.qnz.tools.AppTool;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提现订单详情页 2017/10/11 0011.
 */

public class CrashRecordDetails extends BaseActivity {
    @BindView(R.id.textView_crash_money)
    TextView textViewCrashMoney;
    @BindView(R.id.textView_crash_status)
    TextView textViewCrashStatus;
    @BindView(R.id.textView_crash_sxmoney)
    TextView textViewCrashSxmoney;
    @BindView(R.id.textView_crash_realmoney)
    TextView textViewCrashRealmoney;
    @BindView(R.id.textView_crash_order_type)
    TextView textViewCrashOrderType;
    @BindView(R.id.textView_crash_bankname)
    TextView textViewCrashBankname;
    @BindView(R.id.textView_crash_addtime)
    TextView textViewCrashAddtime;
    @BindView(R.id.textView_crash_sno)
    TextView textViewCrashSno;
    @BindView(R.id.textView_crash_remark)
    TextView textViewCrashRemark;
    @BindView(R.id.actionbar_side_left_iconfont)
    ImageView actionbarSideLeftIconfont;
    @BindView(R.id.actionbar_side_name)
    TextView actionbarSideName;
    private CrashRecordBean.CashDetailBean cashDetailBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crashdetails);
        ButterKnife.bind(this);
        actionbarSideLeftIconfont.setVisibility(View.VISIBLE);
        actionbarSideLeftIconfont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionbarSideName.setText("订单详情");
        cashDetailBean = (CrashRecordBean.CashDetailBean) getIntent().getSerializableExtra("CashDetailBean");
        String bankName = cashDetailBean.getBankName();
        String bankNo = cashDetailBean.getBankNo();
        String realMoney = cashDetailBean.getMoney();
        String addTime = cashDetailBean.getAddtime() + "";
        String statusDesc = cashDetailBean.getStatusDesc();
        if (!TextUtils.isEmpty(bankName) && !TextUtils.isEmpty(bankNo)) {
            textViewCrashBankname.setText(bankName + "(尾号" + bankNo + ")");
        }
        if (!TextUtils.isEmpty(realMoney)) {
            textViewCrashRealmoney.setText(realMoney + "元");
        }
        if (!TextUtils.isEmpty(addTime)) {
            textViewCrashAddtime.setText(AppTool.getMsgTwoDateDistance_miao(addTime));
        }
        if (!TextUtils.isEmpty(statusDesc)) {
            textViewCrashStatus.setText(statusDesc);
        }


    }
}
