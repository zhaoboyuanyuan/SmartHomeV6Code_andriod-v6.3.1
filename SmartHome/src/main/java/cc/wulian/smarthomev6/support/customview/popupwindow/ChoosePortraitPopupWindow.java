package cc.wulian.smarthomev6.support.customview.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cc.wulian.smarthomev6.R;

/**
 * 作者: mamengchao
 * 时间: 2017/3/24 0024
 * 描述:选择头像popupWindow
 * 联系方式: 805901025@qq.com
 */

public class ChoosePortraitPopupWindow {
    private LinearLayout rootView;
    private LayoutInflater inflater;
    private Context context;
    private PopupWindow popupWindow;

    public interface OnPopupClickListener {
        void onTakePhoto();
        void onAlbum();
    }

    private ChoosePortraitPopupWindow.OnPopupClickListener listener;

    public void setPopupClickListener(ChoosePortraitPopupWindow.OnPopupClickListener listener) {
        this.listener = listener;
    }

    public ChoosePortraitPopupWindow(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        rootView = (LinearLayout) inflater.inflate(
                R.layout.pop_choose_portrait, null);

        init();
    }

    private void init() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(context);
            popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popwindow_edit_scene_bg));
            // 指定popupWindow的宽和高
            popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            // 使popupWindow全屏显示
//            popupWindow.setClippingEnabled(false);
            popupWindow.setContentView(rootView);

            rootView.findViewById(R.id.popup_choose_portrait_blank).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            rootView.findViewById(R.id.popup_choose_portrait_take_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != listener) {
                        listener.onTakePhoto();
                    }
                }
            });

            rootView.findViewById(R.id.popup_choose_portrait_album).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != listener) {
                        listener.onAlbum();
                    }
                }
            });

            rootView.findViewById(R.id.popup_choose_portrait_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    public boolean isShown() {
        if (popupWindow == null)
            return false;
        return popupWindow.isShowing();
    }

    public void showParent(View view) {
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        popupWindow.setFocusable(true);
        popupWindow.update();
    }

    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        if (popupWindow != null)
            popupWindow.setOnDismissListener(listener);
    }
}
