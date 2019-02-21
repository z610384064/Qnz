package com.rd.qnz.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by Evonne on 2016/10/12.
 */

public class MoveImageView extends ImageView {

    private int lastX = 0;
    private int lastY = 0;

    private static int screenWidth;//屏幕宽度
    private static int screenHeight;//屏幕高度

    private boolean keepPosition;

    public MoveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        if (!keepPosition) {
            super.layout(l, t, r, b);
            keepPosition = true;
        }
    }

    public void moveTiger(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
    }

    public void startKeepPosition() {
        keepPosition = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("", "This is ACTION_DOWN");
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("", "This is ACTION_MOVE");
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                int left = getLeft() + dx;
                int top = getTop() + dy;
                int right = getRight() + dx;
                int bottom = getBottom() + dy;
                // 设置不能出界
                if (left < 0) {
                    left = 0;
                    right = left + getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - getHeight();
                }
                startKeepPosition();
                layout(left, top, right, bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                Log.i("", "This is ACTION_UP");
                break;
            default:
                break;
        }
        return true;
    }

}
