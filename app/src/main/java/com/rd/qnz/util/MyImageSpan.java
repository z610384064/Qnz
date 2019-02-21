package com.rd.qnz.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by Administrator on 2017/9/19 0019.
 */

public class MyImageSpan extends ImageSpan {
    public MyImageSpan(Bitmap b) {
        super(b);
    }

    public MyImageSpan(Bitmap b, int verticalAlignment) {
        super(b, verticalAlignment);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();

        int transY = 0;
        if (mVerticalAlignment == ALIGN_BASELINE) {
               transY -= paint.getFontMetricsInt().descent;
        } else if (mVerticalAlignment == ALIGN_BOTTOM) {
            transY = bottom - b.getBounds().bottom;
        } else {
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            transY = (y + fm.descent + y + fm.ascent) / 2
                    - b.getBounds().bottom / 2;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
