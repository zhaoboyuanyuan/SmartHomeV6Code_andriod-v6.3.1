package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 正弦曲线
 */
public class SineCurveView extends View {

    private TextPaint mPaint;
    private Path mPath;
    private int mStartX = 0;

    public SineCurveView(Context context) {
        super(context);
        init(null, 0);
    }

    public SineCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SineCurveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mPaint = new TextPaint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xff868DE4);
        mPaint.setStrokeWidth(2);
        mPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));

        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int contentWidth = getWidth();
        int contentHeight = getHeight();
        float widthIn8 = (contentWidth / 8f);
        float widthIn4 = (contentWidth / 4f);
        float widthIn2 = (contentWidth / 2f);
        float heightIn2 = (contentHeight / 2f);

        mPath.reset();
        mPath.moveTo(mStartX, heightIn2);
        for (int i = 0; i < 4; i ++) {
            mPath.rQuadTo(widthIn8, widthIn8, widthIn4, 0);
            mPath.rQuadTo(widthIn8, -widthIn8, widthIn4, 0);
        }

        canvas.drawPath(mPath, mPaint);
        // 速度, 越大越快
        mStartX -= 7;
        if (mStartX < -widthIn2) {
            mStartX = 0;
        }
        invalidate();
    }
}
