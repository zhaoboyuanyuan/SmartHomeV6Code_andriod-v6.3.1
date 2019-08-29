package cc.wulian.smarthomev6.support.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.math.BigDecimal;

import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者：Administrator on 2017/9/27 09:13
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class LoadingView extends View {

    private Paint mPaint;

    private float radius;

    private float precent1;

    private float precent2;

    private float precent3;

    private float cirqueWidth;

    private int height;
    private int width;
    private float cy;
    private ValueAnimator mValueAnimator1;
    private ValueAnimator mValueAnimator2;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        //防止边缘的锯齿
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xff8DD652);
        mPaint.setStyle(Paint.Style.STROKE);
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, getResources().getDisplayMetrics());
        cirqueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(cirqueWidth);
        radius = (height - cirqueWidth) / 2f;
        cy = height / 2f;
        mValueAnimator2 = ValueAnimator.ofFloat(0f, 1f, 0f);
        mValueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                precent2 = value;
                postInvalidate();

            }
        });
        mValueAnimator2.setDuration(1050);
        mValueAnimator2.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator2.setRepeatCount(-1);
        mValueAnimator2.setInterpolator(new LinearInterpolator());

        mValueAnimator1 = ValueAnimator.ofFloat(0f, 1f, 0f);
        mValueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                BigDecimal b = new BigDecimal(value);
                if(b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() == 0.5f && !mValueAnimator2.isRunning()){
                    mValueAnimator2.start();
                }
                precent3 = 1 - value;
                precent1 = value;
                postInvalidate();

            }
        });
        mValueAnimator1.setDuration(1050);
        mValueAnimator1.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator1.setRepeatCount(-1);
        mValueAnimator1.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = height * 4;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.i("luzx", "precent1:" + precent1 + " precent2:" + precent2 + " precent3:" + precent3);
        //画圆环
        canvas.drawCircle(cy, cy, radius * precent1, mPaint);
        canvas.drawCircle(cy * 4, cy, radius * precent2, mPaint);
        canvas.drawCircle(cy * 7, cy, radius * precent3, mPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        mValueAnimator1.start();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mValueAnimator1.cancel();
        mValueAnimator2.cancel();
        super.onDetachedFromWindow();
    }
}
