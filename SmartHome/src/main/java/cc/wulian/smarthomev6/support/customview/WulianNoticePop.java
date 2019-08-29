package cc.wulian.smarthomev6.support.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;

/**
 * created by huxc  on 2019/1/7.
 * func：wulian通知栏pop
 * email: hxc242313@qq.com
 */

public class WulianNoticePop extends PopupWindow {
    private Context context;
    private ImageView ivCancel;
    private TextView tvNoticeContent;
    private WulianNoticePop.onPopClickListener listener;
    private View parentView;
    private int popupWidth;
    private int popupHeight;

    public interface onPopClickListener {
        void cancel();
    }

    public WulianNoticePop(Context context, String msg) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = inflater.inflate(R.layout.popwindow_wulian_notice, null);
        initView(msg);
        initPopWindow();
    }


    public void setOnPopClickListener(onPopClickListener listener) {
        this.listener = listener;
    }

    private void initView(String msg) {
        ivCancel = parentView.findViewById(R.id.iv_cancel);
        tvNoticeContent = parentView.findViewById(R.id.tv_notice_content);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (listener != null) {
                    listener.cancel();
                }
            }
        });
        tvNoticeContent.setText(msg);


    }


    private void initPopWindow() {
        this.setContentView(parentView);
        // 设置弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击()
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.pop_animation);
        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable();
        //设置弹出窗体的背景
        backgroundAlpha((Activity) context, 1.0f);//0.0-1.0
        this.setBackgroundDrawable(new BitmapDrawable());
        //获取自身的长宽高
        parentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = parentView.getMeasuredHeight();
        popupWidth = parentView.getMeasuredWidth();
    }

    public void showUp2(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        showAtLocation(v, Gravity.START | Gravity.TOP, location[0], location[1]);
//        showAsDropDown(v);
    }


    /**
     * 设置添加屏幕的背景透明度(值越大,透明度越高)
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
}
