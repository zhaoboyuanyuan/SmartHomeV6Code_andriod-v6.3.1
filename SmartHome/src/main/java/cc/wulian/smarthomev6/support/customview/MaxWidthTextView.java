package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;

/**
 * 作者：luzx on 2017/7/27 11:52
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class MaxWidthTextView extends android.support.v7.widget.AppCompatTextView {

    private TextPaint mPaint;

    private String suffix = "... ";

    public MaxWidthTextView(Context context) {
        super(context);
    }

    public MaxWidthTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxWidthTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = getPaint();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if(text == null){
            return;
        }
        for(int i=0; i<text.length(); i++){
            float textWidth = mPaint.measureText(text.subSequence(0, i) + suffix);
            if(textWidth == getMaxWidth()){
                text = text.subSequence(0, i) + suffix;
                break;
            }else if(textWidth > getMaxWidth()){
                text = text.subSequence(0, i -1) + suffix;
                break;
            }
        }
        super.setText(text, type);
    }
}
