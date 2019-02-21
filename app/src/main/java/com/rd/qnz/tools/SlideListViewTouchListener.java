package com.rd.qnz.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.mine.MyBankCardAct;
import com.rd.qnz.mine.MyBankListAdapter;
import com.rd.qnz.tools.webservice.JsonRequeatThreadDefaultBank;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;

public class SlideListViewTouchListener implements View.OnTouchListener {

    /**
     * 屏幕宽度
     */
    private float screenWidth = 1;

    /**
     * 最后一次触发的坐标
     */
    private float lastMotionX = 0;
    private float lastMotionY = 0;

    /**
     * 横向移动的路径
     */
    private float moveX = 0;
    /**
     * 自动滑行的速度
     */
    private long animationTime = 200;

    /**
     * 用来判定Y轴的滑动
     */
    private int touchSlop = 0;

    /**
     * 垂直滑动
     */
    private boolean isScrollInY = false;
    private boolean haveItemSwipe = false;
    private boolean haveItemSelected = false;

    private SlideListView slideListView = null;
    private MyBankListAdapter myBankListAdapter = null;
//	Handler myHandler;

    private View itemView = null;
    private View showView = null;
    private View hideView = null;

    /**
     * 现在按下的位置
     */
    private int downPosition = 0;

    private Rect rect = new Rect();

    private TextView btnDelete = null;
    private TextView btnWanshan = null;
    private TextView btnSet = null;
    private List<Map<String, String>> list;
    Context context;
    private MyApplication myApp;

    public SlideListViewTouchListener(Context context, SlideListView slideListView, int touchSlop) {
        this.slideListView = slideListView;
        this.touchSlop = touchSlop;
        this.context = context;

        myApp = (MyApplication) context.getApplicationContext();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (screenWidth < 2) {
            screenWidth = slideListView.getWidth();
        }
        myBankListAdapter = (MyBankListAdapter) slideListView.getAdapter();

        list = myBankListAdapter.getInfoList();

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {

                haveItemSwipe = false;
                lastMotionX = motionEvent.getRawX();
                lastMotionY = motionEvent.getRawY();
                final int position = getPosition();


                final int firstVisiblePosition = slideListView.getFirstVisiblePosition();
                final int lastVisiblePosition = slideListView.getLastVisiblePosition();
                if ((downPosition != position) && (downPosition >= firstVisiblePosition)
                        && (downPosition <= lastVisiblePosition)) {
                    itemView = slideListView.getChildAt(downPosition - firstVisiblePosition);
                    showView = itemView.findViewById(R.id.show_item);
                    hideView = itemView.findViewById(R.id.hide_item);
                    ViewPropertyAnimator.animate(showView).translationX(0).setDuration(100);
                    ViewPropertyAnimator.animate(hideView).translationX(0).setDuration(100);
                    moveX = 0;
                    haveItemSelected = false;
                }
                downPosition = position;
                itemView = slideListView.getChildAt(downPosition - slideListView.getFirstVisiblePosition());
                btnDelete = (TextView) itemView.findViewById(R.id.bank_delete);
                btnWanshan = (TextView) itemView.findViewById(R.id.bank_wanshan);
                // btnSet = (TextView) itemView.findViewById(R.id.bank_set);
                btnDelete.setClickable(false);
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (hideView == null) {

                } else {
                    final int hideViewWidth = hideView.getWidth();
//				System.out.println("hideViewWidth = " + hideViewWidth);
                    final float translationX = ViewHelper.getTranslationX(showView);
//				System.out.println("translationX = " + translationX);
                    float deltaX = 0;
//				System.out.println("haveItemSelected = " + haveItemSelected);

                    if (translationX == -hideViewWidth) {
                        animationTime = 0;
                    } else {
                        animationTime = 200;
                    }
                    System.out.println("translationX = " + translationX);
                    if (translationX > -hideViewWidth / 2 || haveItemSelected) {
                        deltaX = 0;
                        haveItemSelected = false;
                    } else if (translationX <= -hideViewWidth / 2) {
                        deltaX = -hideViewWidth;
                        haveItemSelected = true;
                    }

                    // Toast.makeText(context,
                    // "moveX="+moveX+";downPosition = "+downPosition, 1/20).show();
                    // System.out.println("moveX="+moveX+";downPosition = "+downPosition);
                    moveX += deltaX;
                    if ((moveX >= 0) || (deltaX == 0)) {

                        moveX = 0;
                    } else if (moveX <= -hideViewWidth) {
                        moveX = -hideViewWidth;
                    }
                    /** 自动滑行 */
                    ViewPropertyAnimator.animate(showView).translationX(deltaX).setDuration(animationTime);
                    ViewPropertyAnimator.animate(hideView).translationX(deltaX).setDuration(animationTime)
                            .setListener(new AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    // TODO Auto-generated method stub
                                }

                                /** 动画结束时才能点击删除按钮 */
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    // TODO Auto-generated method stub
                                    setBtnOnClickListener();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    // TODO Auto-generated method stub
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                    // TODO Auto-generated method stub

                                }
                            });
                }


                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (downPosition == -1) {
                    break;
                }
                float deltaX = motionEvent.getRawX() - lastMotionX;
                isScrollInY = IsMovingInY(motionEvent.getRawX(), motionEvent.getRawY());
                if ((isScrollInY)) {
                    return false;
                }
                itemView = slideListView.getChildAt(downPosition - slideListView.getFirstVisiblePosition());
                showView = itemView.findViewById(R.id.show_item);
                hideView = itemView.findViewById(R.id.hide_item);
                final int hideViewWidth = hideView.getWidth();
                deltaX += moveX;
                if (deltaX >= -hideViewWidth / 5) {
                    deltaX = 0;
                }
                if (deltaX <= -hideViewWidth) {
                    deltaX = -hideViewWidth;
                }
                ViewHelper.setTranslationX(showView, deltaX);
                ViewHelper.setTranslationX(hideView, deltaX);
                float translationX = ViewHelper.getTranslationX(showView);
                if (translationX != -hideViewWidth && translationX != 0)
                    haveItemSwipe = true;
                return true;
            }
            default: {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否是横向滑动
     *
     * @param x Position X
     * @param y Position Y
     */
    private boolean IsMovingInY(float x, float y) {
        final int xDiff = (int) Math.abs(x - lastMotionX);
        final int yDiff = (int) Math.abs(y - lastMotionY);
        final int touchSlop = this.touchSlop;
        if (haveItemSelected || haveItemSwipe) {
            return false;
        }
        if (xDiff == 0 || x > lastMotionX) {
            return true;
        }
        if ((yDiff / (xDiff / (float) 5) >= 1) && (yDiff > touchSlop)) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前手指所在的position
     */
    private int getPosition() {
        final int childCount = slideListView.getChildCount();
        int[] listViewCoords = new int[2];
        slideListView.getLocationOnScreen(listViewCoords);
        final int x = (int) lastMotionX - listViewCoords[0];
        final int y = (int) lastMotionY - listViewCoords[1];
        View child;
        int childPosition = 0;
        for (int i = 0; i < childCount; i++) {
            child = slideListView.getChildAt(i);
            child.getHitRect(rect);
            childPosition = slideListView.getPositionForView(child);
            if (rect.contains(x, y)) {
                return childPosition;
            }
        }
        return childPosition;
    }

    /**
     * 设置删除按钮的监听
     *
     * @param
     */
    private void setBtnOnClickListener() {
        itemView = slideListView.getChildAt(downPosition - slideListView.getFirstVisiblePosition());
        btnDelete.findViewById(R.id.bank_delete);
        btnWanshan.findViewById(R.id.bank_wanshan);
        // btnSet.findViewById(R.id.bank_set);

        hideView.findViewById(R.id.hide_item);
        /* 修改信息 */
        btnWanshan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // 删除
                moveX = 0;
                Intent intent = new Intent(context, WebViewAct.class);
                intent.putExtra("web_url", Wanshan());
                intent.putExtra("title", "完善银行卡信息");
                context.startActivity(intent);
            }
        });
        /* 点击删除按钮 */
        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除
                moveX = 0;
                list = myBankListAdapter.getInfoList();
                SharedPreferences preferences = context.getSharedPreferences(
                        BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);

                String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");

                ArrayList<String> param = new ArrayList();
                ArrayList<String> value = new ArrayList();
                param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
                value.add(oauthToken);
                param.add("id");
                value.add(list.get(downPosition).get("id"));
                param.add(BaseParam.URL_QIAN_API_APPID);
                value.add(myApp.appId);
                param.add(BaseParam.URL_QIAN_API_SERVICE);
                value.add("setDefaultCard");
                param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
                value.add(myApp.signType);

                String[] array = new String[]{
                        BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                        "id=" + list.get(downPosition).get("id"),
                        BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                        BaseParam.URL_QIAN_API_SERVICE + "=setDefaultCard",
                        BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
                APIModel apiModel = new APIModel();
                String sign = apiModel.sortStringArray(array);
                param.add(BaseParam.URL_QIAN_API_SIGN);
                value.add(sign);

                new Thread(new JsonRequeatThreadDefaultBank(
                        context,
                        myApp,
                        MyBankCardAct.myHandler,
                        param,
                        value)
                ).start();
//				list.remove(downPosition);
//				myBankListAdapter.setInfoList(list);
//				myBankListAdapter.notifyDataSetChanged();

                ListAdapter listAdapter = slideListView.getAdapter();
                if (listAdapter == null) {
                    return;
                }
                int totalHeight = 0;
                System.out.println("  listAdapter.getCount(); =  " + listAdapter.getCount());
                ViewGroup.LayoutParams params = slideListView.getLayoutParams();
                params.height = listAdapter.getCount() * DensityUtil.dip2px(context, 61);
                System.out.println(" params.height = " + params.height);
                slideListView.setLayoutParams(params);
            }
        });

    }

    private String Wanshan() {
        list = myBankListAdapter.getInfoList();
        SharedPreferences preferences = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER,
                Context.MODE_PRIVATE);

        String oauthToken = preferences.getString(
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        String url = BaseParam.URL_QIAN_EDITBANKINFO + "?" + "id=" + list.get(downPosition).get("id") + "&"
                + "oauthToken=" + oauthToken;
        return url;
    }

}
