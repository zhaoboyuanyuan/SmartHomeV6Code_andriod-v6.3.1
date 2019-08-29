package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class RecorderSeekBar extends AppCompatSeekBar {

    private boolean canTouchAble = true;

    public RecorderSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RecorderSeekBar(Context context,
                           AttributeSet attrs) {
        super(context, attrs);
    }

    public RecorderSeekBar(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canTouchAble) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setCanTouchAble(boolean canTouchAble) {
        this.canTouchAble = canTouchAble;
    }
}
