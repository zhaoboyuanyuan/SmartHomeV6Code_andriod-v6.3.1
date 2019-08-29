package cc.wulian.smarthomev6.support.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cc.wulian.smarthomev6.R;

/**
 * 自定义的输入框
 *
 * @author xiaozhi
 * @创作日期 2014-7-24
 */
public class ClearEditText extends AppCompatEditText {
    private boolean isDelImgVis = false;
    private Drawable delDrawableImg = null;
    private DelTextWatcher watcher = null;

    /**
     * 输入监听回调函数
     */
    private WLInputTextWatcher wlInputTextWatcher;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint("NewApi")
    private void init(Context c) {
        this.setFocusable(true);
        this.setEnabled(true);
        this.setClickable(true);
        this.setCursorVisible(true);
        this.setFocusableInTouchMode(true);
        delDrawableImg = c.getResources().getDrawable(R.drawable.icon_delete);
        if (watcher == null) {
            watcher = new DelTextWatcher();
            this.addTextChangedListener(watcher);
        }
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            if (!TextUtils.isEmpty(this.getText()))
                setDelDrawable(true);
        } else {
            setDelDrawable(false);
        }
    }

    private class DelTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            if (s.length() > 0) {
                setDelDrawable(true);
            }
            if (wlInputTextWatcher != null) {
                wlInputTextWatcher.beforeTextChanged(s, start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (s.length() > 0) {
                setDelDrawable(true);
            }

            if (wlInputTextWatcher != null) {
                wlInputTextWatcher.onTextChanged(s, start, before, count);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                setDelDrawable(true);
            } else {
                setDelDrawable(false);
            }

            if (wlInputTextWatcher != null) {
                wlInputTextWatcher.afterTextChanged(s);
            }
        }
    }

    /**
     * 设置删除按钮图片
     *
     * @param delDrawableImg
     */
    public void setDeleteImageDrawable(Drawable delDrawableImg) {
        this.delDrawableImg = delDrawableImg;
        if (isDelImgVis) {
            setCompoundDrawablesWithIntrinsicBounds(null, null,
                    delDrawableImg, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null,
                    null, null);
        }
    }

    /**
     * @param isShow show drawable true / false
     */
    private void setDelDrawable(boolean isShow) {

        if (isShow) {
            if (!isDelImgVis) {
                setCompoundDrawablesWithIntrinsicBounds(null, null,
                        delDrawableImg, null);
                isDelImgVis = true;
            }
        } else {
            if (isDelImgVis) {
                setCompoundDrawablesWithIntrinsicBounds(null, null,
                        null, null);
                isDelImgVis = false;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setFocusableInTouchMode(true);
        if (isDelImgVis && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN)) {
            boolean isClean = (event.getX() > (getWidth() - getTotalPaddingRight())) &&
                    (event.getX() < (getWidth() - getPaddingRight()));
            if (isClean) {
                setText("");
                setDelDrawable(false);
            }
        }
        return super.onTouchEvent(event);
    }

    public void addTextChangedListener(WLInputTextWatcher inputTextWatcher) {
        this.wlInputTextWatcher = inputTextWatcher;
    }

    public static interface WLInputTextWatcher {
        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(Editable s);
    }

    @Override
    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        super.setFocusableInTouchMode(focusableInTouchMode);
        if (focusableInTouchMode) {

        } else {
            setDelDrawable(false);
        }
    }
}
