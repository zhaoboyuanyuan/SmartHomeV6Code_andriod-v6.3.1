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
import cc.wulian.smarthomev6.support.tools.NFCTool;

public class EditScenePopupWindow {

    private LinearLayout rootView;
    private LayoutInflater inflater;
    private Context context;
    private PopupWindow popupWindow;

    public interface OnPopupClickListener {
        void onDelete();

        void onEdit();

        void onEditNewScene();

        void onWriteNFC();
    }

    private OnPopupClickListener listener;

    public void setPopupClickListener(OnPopupClickListener listener) {
        this.listener = listener;
    }

    public EditScenePopupWindow(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        rootView = (LinearLayout) inflater.inflate(
                R.layout.popup_edit_scene, null);

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
            popupWindow.setClippingEnabled(true);
            popupWindow.setContentView(rootView);

            rootView.findViewById(R.id.popup_edit_scene_blank).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            View view_write_nfc = rootView.findViewById(R.id.popup_edit_scene_write_nfc);
            if (NFCTool.isSupport(context)) {
                view_write_nfc.setVisibility(View.VISIBLE);
            } else {
                view_write_nfc.setVisibility(View.GONE);
            }
            view_write_nfc.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != listener) {
                        listener.onWriteNFC();
                    }
                }
            });

            rootView.findViewById(R.id.popup_edit_scene_text_edit).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != listener) {
                        listener.onEdit();
                    }
                }
            });
            rootView.findViewById(R.id.popup_edit_scene_text_edit_new).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != listener) {
                        listener.onEditNewScene();
                    }
                }
            });

            rootView.findViewById(R.id.popup_edit_scene_text_delete).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != listener) {
                        listener.onDelete();
                    }
                }
            });

            rootView.findViewById(R.id.popup_edit_scene_text_cancel).setOnClickListener(new OnClickListener() {
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
        popupWindow.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
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
