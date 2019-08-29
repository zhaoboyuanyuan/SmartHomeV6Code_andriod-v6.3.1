package cc.wulian.smarthomev6.support.customview.popupwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.customview.WLDatePicker;
import cc.wulian.smarthomev6.support.customview.calendar.WLCalendarDateContent;
import cc.wulian.smarthomev6.support.customview.calendar.WLCalendarTitle;
import cc.wulian.smarthomev6.support.customview.calendar.WLCalendarWeeks;
import cc.wulian.smarthomev6.support.utils.FastBlur;

public class DatePickerPopupWindow {

    private LinearLayout rootView;
    private LinearLayout shadowView;
    private LayoutInflater inflater;
    private Context context;
    private PopupWindow popupWindow;

    public DatePickerPopupWindow(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        rootView = (LinearLayout) inflater.inflate(
                R.layout.popupwindos_date_picker, null);
        shadowView = (LinearLayout) inflater.inflate(
                R.layout.account_set_icon_content, null);
        shadowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        init();
    }

    private void init() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(context);
            popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popwindow_bg));
            // 指定popupWindow的宽和高
            popupWindow.setWidth(LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(rootView);
        }
    }

    public boolean isShown() {
        if (popupWindow == null)
            return false;
        return popupWindow.isShowing();
    }

    // 选取时间的监听
    private WLDatePicker.DatePickListener listener;
    public void setDatePickListener(WLDatePicker.DatePickListener listener) {
        this.listener = listener;
    }

    public void showParent(View view, int year, int month, int day) {
        rootView.removeAllViews();

        WLDatePicker datePicker = new WLDatePicker(context, year, month, day);
        if (listener != null) {
            datePicker.setDatePickListener(listener);
        }
        rootView.addView(datePicker);
//        rootView.addView(new WLCalendarTitle(context));
//        rootView.addView(new WLCalendarWeeks(context));
//        rootView.addView(new WLCalendarDateContent(context));

        popupWindow.showAsDropDown(view, 30, 2);
        popupWindow.setFocusable(true);
        popupWindow.update();
    }

    public void setBlurResId(int resId) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);//从资源文件中得到图片，并生成Bitmap图片
        rootView.setBackground(new BitmapDrawable(FastBlur.fastBlur(context, bmp, 0.1f, 5)));
    }

    /**
     * <h1>隐藏Pop</h1>
     */
    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void setOnDismissListener(OnDismissListener listener) {
        if (popupWindow != null)
            popupWindow.setOnDismissListener(listener);
    }
}
