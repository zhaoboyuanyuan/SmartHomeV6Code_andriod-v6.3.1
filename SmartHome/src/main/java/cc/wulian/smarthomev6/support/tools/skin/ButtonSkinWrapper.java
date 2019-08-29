package cc.wulian.smarthomev6.support.tools.skin;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;

/**
 * Created by zbl on 2017/10/26.
 * 实现对通用按钮切换可用和不可用状态的皮肤切换封装
 */

public class ButtonSkinWrapper {

    private TextView buttonView;

    private String btnBgActiveDrawableKey = SkinResouceKey.BITMAP_BUTTON_BG_S;
    private String btnBgNegativeDrawableKey = SkinResouceKey.BITMAP_BUTTON_BG_N;
    private String btnTextColorKey = SkinResouceKey.COLOR_BUTTON_TEXT;
    private String btnTextColorActiveKey = SkinResouceKey.COLOR_BUTTON_TEXT_ACTIVE;
    private int defaultActiveBgResId = R.drawable.shape_btn_active_bg;
    private int defaultNegativeBgResId = R.drawable.shape_btn_negative_bg;


    private Drawable btn_bg_active, btn_bg_negative;
    private Integer btnTextColor, btnTextColorActive;

    public ButtonSkinWrapper(TextView textView) {
        this.buttonView = textView;
        init();
    }

    public void ButtonSkinWrapper(TextView textView, @NonNull int defaultActiveBgResId, @NonNull int defaultNegativeBgResId) {
        this.buttonView = textView;
        this.defaultActiveBgResId = defaultActiveBgResId;
        this.defaultNegativeBgResId = defaultNegativeBgResId;
        init();
    }

    public void ButtonSkinWrapper(
            TextView textView,
            @NonNull int defaultActiveBgResId, @NonNull int defaultNegativeBgResId,
            String btnBgActiveDrawableKey, String btnBgNegativeDrawableKey,
            String btnTextColorKey, String btnTextColorActiveKey) {
        this.buttonView = textView;
        this.defaultActiveBgResId = defaultActiveBgResId;
        this.defaultNegativeBgResId = defaultNegativeBgResId;
        this.btnBgActiveDrawableKey = btnBgActiveDrawableKey;
        this.btnBgNegativeDrawableKey = btnBgNegativeDrawableKey;
        this.btnTextColorKey = btnTextColorKey;
        this.btnTextColorActiveKey = btnTextColorActiveKey;
        init();
    }

    private void init() {
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        //按钮
        btnTextColor = skinManager.getColor(btnTextColorKey);
        btnTextColorActive = skinManager.getColor(btnTextColorActiveKey);
        btn_bg_active = skinManager.getDrawable(btnBgActiveDrawableKey);
        btn_bg_negative = skinManager.getDrawable(btnBgNegativeDrawableKey);
    }

    public void setActive(boolean isActive) {
        if (isActive) {
            if (btn_bg_active == null) {
                buttonView.setBackgroundResource(defaultActiveBgResId);
            } else {
                buttonView.setBackground(btn_bg_active);
            }
            if (btnTextColorActive != null) {
                buttonView.setTextColor(btnTextColorActive);
            }
            buttonView.setEnabled(true);
        } else {
            if (btn_bg_negative == null) {
                buttonView.setBackgroundResource(defaultNegativeBgResId);
            } else {
                buttonView.setBackground(btn_bg_negative);
            }
            if (btnTextColor != null) {
                buttonView.setTextColor(btnTextColor);
            }
            buttonView.setEnabled(false);
        }
    }
}
