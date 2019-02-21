package com.rd.qnz.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.rd.qnz.R;

/**
 * Created by Evonne on 2016.10.17.
 */
public class RedCursorView extends View {

    private int cursorColor = getResources().getColor(R.color.app_color); // 颜色
    private int counts = 2; // item个数
    private float posX = 0f; // 当前X坐标的位置
    private Paint paint;

    public RedCursorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RedCursorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedCursorView(Context context) {
        super(context);
        init();
    }

    private void init() {
        // 初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(cursorColor);
    }

    /**
     * 设置ViewPager有几块
     *
     * @param counts
     */
    public void setCounts(int counts) {
        this.counts = counts;
    }

    /**
     * 设置坐标
     *
     * @param pos  当前的item
     * @param rate 变化率
     */
    public void setXY(int pos, float rate) {
        int single = getMeasuredWidth() / counts;
        posX = pos * single + single * rate + 75;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(getMeasuredHeight());
        int width = getMeasuredWidth() / counts - 150;
        canvas.drawLine(posX, 0, posX + width, 0, paint);
    }

}
