package com.rd.qnz.dialog;

import android.app.Activity;

public class GetDialog {
    private CustomProgressDialog progressDialog = null;

    public GetDialog() {
    }

    public CustomProgressDialog getLoginDialog(Activity paramActivity, String paramMess) {
        if (progressDialog == null)
            progressDialog = CustomProgressDialog.createDialog(paramActivity);
        progressDialog.setMessage(paramMess);
        progressDialog.setCancelable(false);
        return progressDialog;
    }


}
