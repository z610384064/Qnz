package com.rd.qnz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.rd.qnz.R;
import com.rd.qnz.view.RoundCornerNetworkImageView;

/**
 * 首页弹窗
 */
public class PopupAdDialog extends Dialog {

    private Context context;
    private ImageLoader imageLoader;
    private String imgurl;

    public PopupAdDialog(Context context, ImageLoader imageLoader, String url) {
        super(context, R.style.CustomDialog2);
        this.context = context;
        this.imageLoader = imageLoader;
        this.imgurl = url;
    }

    public interface OnImgClickListener {
        void onImgClick();
    }

    private OnImgClickListener onImgClickListener;

    public void setOnImgClickListener(OnImgClickListener onImgClickListener) {
        this.onImgClickListener = onImgClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_window, null);
        setContentView(view);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount=0.8f;
        getWindow().setAttributes(lp);

        final RoundCornerNetworkImageView networkImageView = (RoundCornerNetworkImageView) findViewById(R.id.pop_window_iv);
        ViewGroup.LayoutParams layoutParams = networkImageView.getLayoutParams();
        layoutParams.width = (int) (dm.widthPixels * 0.9);
        layoutParams.height = (int) (dm.widthPixels * 0.9 *7);
        networkImageView.setLayoutParams(layoutParams);
        networkImageView.setImageUrl(imgurl, imageLoader);


        networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImgClickListener.onImgClick();
            }
        });

        ImageView iv_cancel = (ImageView) findViewById(R.id.pop_window_iv_close);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupAdDialog.this.dismiss();
            }
        });
    }

}
