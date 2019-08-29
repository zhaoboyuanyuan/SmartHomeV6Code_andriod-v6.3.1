package cc.wulian.smarthomev6.support.customview.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;

public class ChoosePanelPopupWindow {

    private LinearLayout rootView;
    private LayoutInflater inflater;
    private Context context;
    private PopupWindow popupWindow;

    private TextView mTextTrue, mTextFalse;

    private boolean isChoose = true;

    public interface OnPopupClickListener {
        void onChoose(boolean isChoose);
    }

    private OnPopupClickListener listener;

    public ChoosePanelPopupWindow(Context context, OnPopupClickListener listener) {
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        rootView = (LinearLayout) inflater.inflate(
                R.layout.popup_choose_panel, null);

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

            mTextTrue = (TextView) rootView.findViewById(R.id.choose_panel_text_true);
            mTextFalse = (TextView) rootView.findViewById(R.id.choose_panel_text_false);

            rootView.findViewById(R.id.choose_panel_blank).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            rootView.findViewById(R.id.choose_panel_text_sure).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    listener.onChoose(isChoose);
                }
            });

            rootView.findViewById(R.id.choose_panel_text_true).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 选择是
                    isChoose = true;
                    setChoose();
                }
            });

            rootView.findViewById(R.id.choose_panel_text_false).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 选择否
                    isChoose = false;
                    setChoose();
                }
            });

            rootView.findViewById(R.id.choose_panel_text_cancel).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    private void setChoose() {
        if (isChoose) {
            mTextFalse.setTextColor(context.getResources().getColor(R.color.newSecondaryText));
            mTextFalse.setBackgroundColor(context.getResources().getColor(R.color.white));
            mTextTrue.setTextColor(context.getResources().getColor(R.color.newPrimaryText));
            mTextTrue.setBackgroundColor(context.getResources().getColor(R.color.newPrimaryLight));
        } else {
            mTextFalse.setTextColor(context.getResources().getColor(R.color.newPrimaryText));
            mTextFalse.setBackgroundColor(context.getResources().getColor(R.color.newPrimaryLight));
            mTextTrue.setTextColor(context.getResources().getColor(R.color.newSecondaryText));
            mTextTrue.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    public boolean isShown() {
        if (popupWindow == null)
            return false;
        return popupWindow.isShowing();
    }

    public void showParent(View view, boolean is) {
        this.isChoose = is;
        setChoose();
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
