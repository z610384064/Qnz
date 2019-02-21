package com.rd.qnz.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rd.qnz.R;

import java.util.List;

/**
 * Created by Evonne on 2016/12/15.
 */
public class ADCircleCalendarView extends LinearLayout implements View.OnClickListener {
    private WeekView weekView;
    private ADCircleMonthView circleMonthView;
    private TextView textViewYear, textViewMonth;
    private TextView last_year, last_month;
    private TextView next_year, next_month;
    OnCouponChangeListener onCouponChange;


    public ADCircleCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        LayoutParams llParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(context).inflate(R.layout.display_grid_date, null);
        weekView = new WeekView(context, null);
        circleMonthView = new ADCircleMonthView(context, null);
        addView(view, llParams);
        addView(weekView, llParams);
        addView(circleMonthView, llParams);

        onCouponChange = (OnCouponChangeListener) context;

        view.findViewById(R.id.left).setOnClickListener(this);
        view.findViewById(R.id.right).setOnClickListener(this);
        textViewYear = (TextView) view.findViewById(R.id.year);
        textViewMonth = (TextView) view.findViewById(R.id.month);

        last_year = (TextView) view.findViewById(R.id.last_year);
        last_month = (TextView) view.findViewById(R.id.last_month);
        next_year = (TextView) view.findViewById(R.id.next_year);
        next_month = (TextView) view.findViewById(R.id.next_month);


        circleMonthView.setMonthLisener(new MonthView.IMonthLisener() {
            @Override
            public void setTextMonth() {
                textViewYear.setText(circleMonthView.getSelYear() + "年");
                textViewMonth.setText((circleMonthView.getSelMonth() + 1) + "月");

              //  Log.e("12345", circleMonthView.getSelYear() + "----" + (circleMonthView.getSelMonth() + 1));

                if ((circleMonthView.getSelMonth() + 1) == 12) {
                    last_year.setText(circleMonthView.getSelYear() + "年");
                    last_month.setText(circleMonthView.getSelMonth() + "月");

                    next_year.setText(circleMonthView.getSelYear() + 1 + "年");
                    next_month.setText("1月");

                } else if ((circleMonthView.getSelMonth() + 1) == 1) {
                    last_year.setText(circleMonthView.getSelYear() - 1 + "年");
                    last_month.setText("12月");

                    next_year.setText(circleMonthView.getSelYear() + "年");
                    next_month.setText((circleMonthView.getSelMonth() + 2) + "月");
                } else {
                    last_year.setText(circleMonthView.getSelYear() + "年");
                    last_month.setText(circleMonthView.getSelMonth() + "月");

                    next_year.setText(circleMonthView.getSelYear() + "年");
                    next_month.setText((circleMonthView.getSelMonth() + 2) + "月");
                }

                onCouponChange.OnCouponChange(circleMonthView.getSelYear() + "-" + (circleMonthView.getSelMonth() + 1) + "-01");

            }
        });
    }

    public interface OnCouponChangeListener {
        public void OnCouponChange(String date);

    }


    /**
     * 设置日历点击事件
     *
     * @param dateClick
     */
    public void setDateClick(MonthView.IDateClick dateClick) {
        circleMonthView.setDateClick(dateClick);
    }

    /**
     * 设置星期的形式
     *
     * @param weekString 默认值	"日","一","二","三","四","五","六"
     */
    public void setWeekString(String[] weekString) {
        weekView.setWeekString(weekString);
    }

    public void setCalendarInfos(List<CalendarInfo> calendarInfos) {
        circleMonthView.setCalendarInfos(calendarInfos);
        textViewYear.setText(circleMonthView.getSelYear() + "年");
        textViewMonth.setText((circleMonthView.getSelMonth() + 1) + "月");

        if ((circleMonthView.getSelMonth() + 1) == 12) {
            last_year.setText(circleMonthView.getSelYear() + "年");
            last_month.setText(circleMonthView.getSelMonth() + "月");

            next_year.setText(circleMonthView.getSelYear() + 1 + "年");
            next_month.setText("1月");

        } else if ((circleMonthView.getSelMonth() + 1) == 1) {
            last_year.setText(circleMonthView.getSelYear() - 1 + "年");
            last_month.setText("12月");

            next_year.setText(circleMonthView.getSelYear() + "年");
            next_month.setText((circleMonthView.getSelMonth() + 2) + "月");
        } else {
            last_year.setText(circleMonthView.getSelYear() + "年");
            last_month.setText(circleMonthView.getSelMonth() + "月");

            next_year.setText(circleMonthView.getSelYear() + "年");
            next_month.setText((circleMonthView.getSelMonth() + 2) + "月");
        }
    }

    public void setDayTheme(IDayTheme theme) {
        circleMonthView.setTheme(theme);
    }

    public void setWeekTheme(IWeekTheme weekTheme) {
        weekView.setWeekTheme(weekTheme);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.left) {
            circleMonthView.onLeftClick();
        } else {
            circleMonthView.onRightClick();
        }
    }
}
