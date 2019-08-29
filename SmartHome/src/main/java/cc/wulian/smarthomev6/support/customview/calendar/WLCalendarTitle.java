package cc.wulian.smarthomev6.support.customview.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import cc.wulian.smarthomev6.R;

/**
 * Created by Veev on 2017/5/5
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */

public class WLCalendarTitle extends RelativeLayout {

    private Context mContext;

    private TextView lastMonth, nextMonth, title;

    // 显示的年月日， 当前的年月日
    private int showYear, showMonth, showDay, currentYear, currentMonth, currentDay;

    private MonthChangeListener listener;

    public interface MonthChangeListener {
        void last();
        void next();
    }

    public void setMonthChangerListener(MonthChangeListener listener) {
        this.listener = listener;
    }

    public WLCalendarTitle(Context context) {
        super(context);

        initView(context);
    }

    public WLCalendarTitle(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    private void initView(Context context) {
        mContext = context;

        View rootView = LayoutInflater.from(context).inflate(R.layout.view_calendar_title, null);
        addView(rootView,
                new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

        lastMonth = (TextView) rootView.findViewById(R.id.calendar_title_last_month);
        nextMonth = (TextView) rootView.findViewById(R.id.calendar_title_next_month);
        title = (TextView) rootView.findViewById(R.id.calendar_title_month);

        lastMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                monthToLast();
            }
        });

        nextMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                monthToNext();
            }
        });

        Calendar mCalendar = Calendar.getInstance();
        showYear = mCalendar.get(Calendar.YEAR);
        showMonth = mCalendar.get(Calendar.MONTH);
        showDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        currentYear = mCalendar.get(Calendar.YEAR);
        currentMonth = mCalendar.get(Calendar.MONTH);
        currentDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        setMonthString();
    }

    private void setMonthString() {
        if (!isCurrentMonth()) {
            nextMonth.setTextColor(mContext.getResources().getColor(R.color.v6_text_green_light));
        } else {
            nextMonth.setTextColor(mContext.getResources().getColor(R.color.v6_text_gray));
        }

        title.setText(getYearMonthText());
    }

    private void monthToLast() {
        if (listener != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, showYear);
            calendar.set(Calendar.MONTH, showMonth);
            calendar.add(Calendar.MONTH, -1);
            showYear = calendar.get(Calendar.YEAR);
            showMonth = calendar.get(Calendar.MONTH);

            setMonthString();
            listener.last();
        }
    }

    private void monthToNext() {
        if (listener != null) {
            // 必须不是当前月份
            if (!isCurrentMonth()) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, showYear);
                calendar.set(Calendar.MONTH, showMonth);
                calendar.add(Calendar.MONTH, 1);
                showYear = calendar.get(Calendar.YEAR);
                showMonth = calendar.get(Calendar.MONTH);

                setMonthString();
                listener.next();
            }
        }
    }

    /**
     * 获取 “2017年2月”
     */
    private String getYearMonthText() {
        return showYear + mContext.getString(R.string.Message_Center_Year) + (showMonth + 1) + mContext.getString(R.string.Message_Center_Month);
    }

    /**
     * 判断日期是否是这个月
     */
    private boolean isCurrentMonth() {
        return showYear == currentYear
                && showMonth == currentMonth;
    }
}
