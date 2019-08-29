package cc.wulian.smarthomev6.support.customview.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cc.wulian.smarthomev6.R;

public class EditAreaPopupWindow {

    private LinearLayout rootView;
    private LayoutInflater inflater;
    private Context context;
    private PopupWindow popupWindow;

    public interface OnPopupClickListener {
        void onDelete();
        void onEdit();
    }

    private OnPopupClickListener listener;

    public void setPopupClickListener(OnPopupClickListener listener) {
        this.listener = listener;
    }

    public EditAreaPopupWindow(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        rootView = (LinearLayout) inflater.inflate(
                R.layout.popup_edit_area, null);

        init();
    }

    private void init() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(context);
            popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popwindow_edit_scene_bg));
            // 指定popupWindow的宽和高
            popupWindow.setWidth(LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(LayoutParams.MATCH_PARENT);
            // 使popupWindow全屏显示
//            popupWindow.setClippingEnabled(false);
            popupWindow.setContentView(rootView);

            rootView.findViewById(R.id.popup_edit_area_blank).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            rootView.findViewById(R.id.popup_edit_area_text_edit).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != listener) {
                        listener.onEdit();
                    }
                }
            });

            rootView.findViewById(R.id.popup_edit_area_text_delete).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != listener) {
                        listener.onDelete();
                    }
                }
            });

            rootView.findViewById(R.id.popup_edit_area_text_cancel).setOnClickListener(new OnClickListener() {
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

    /**
     * <h1>隐藏Pop</h1>
     */
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
