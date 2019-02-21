package com.rd.qnz.tools;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.widget.ListView;

public class SlideListView extends ListView {


    private SlideListViewTouchListener touchListener;

    public SlideListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        final int touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        touchListener = new SlideListViewTouchListener(context, this, touchSlop);
        setOnTouchListener(touchListener);
    }


}
