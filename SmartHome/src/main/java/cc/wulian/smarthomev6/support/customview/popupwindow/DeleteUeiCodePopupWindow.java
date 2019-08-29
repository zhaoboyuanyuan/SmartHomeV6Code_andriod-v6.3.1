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
 * Created by 上海滩小马哥 on 2017/11/16.
 */

public class DeleteUeiCodePopupWindow {
    private LinearLayout rootView;
    private LayoutInflater inflater;
    private Context context;
    private PopupWindow popupWindow;
    private String tag;

    public interface OnPopupClickListener {
        void onDelete(String tag);
    }

    private OnPopupClickListener listener;

    public void setPopupClickListener(OnPopupClickListener listener) {
        this.listener = listener;
    }

    public DeleteUeiCodePopupWindow(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        rootView = (LinearLayout) inflater.inflate(
                R.layout.pop_delete_uei_code, null);

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
//                    dismiss();
                }
            });

            rootView.findViewById(R.id.popup_delete_uei_code).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != listener) {
                        listener.onDelete(tag);
                    }
                }
            });

            rootView.findViewById(R.id.popup_delete_uei_code_cancel).setOnClickListener(new View.OnClickListener() {
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

    public void showParent(View view, String tag) {
        this.tag = tag;
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
