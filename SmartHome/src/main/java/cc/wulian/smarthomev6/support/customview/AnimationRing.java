package cc.wulian.smarthomev6.support.customview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import cc.wulian.smarthomev6.R;

/**
 * 作者：luzx on 2017/6/27 11:52
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class AnimationRing extends View {

    public static final int WAITING = 0x001;

    public static final int FINISHED = 0x002;

    private static final int ROTATION_ANIMATION_DURATION = 2000;

    private Paint mPaint;

    private int cirqueColor;

    private float cirqueWidth;

    private int width;

    private int height;

    private ValueAnimator mValueAnimator;

    private AnimatorListener mAnimatorListener;

    private int timeout;
    private long mStartTime;
    private float percent = 0;
    private int state;
    public AnimationRing(Context context) {
        super(context);
    }

    public AnimationRing(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationRing(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PercentProgressRing, defStyleAttr, 0);
        cirqueColor = a.getColor(R.styleable.PercentProgressRing_cirqueColor, Color.BLUE);
        cirqueWidth = a.getDimension(R.styleable.PercentProgressRing_cirqueWidth, 20);
        a.recycle();
        mPaint = new Paint();
        //防止边缘的锯齿
        mPaint.setAntiAlias(true);
        if(mValueAnimator == null){
            mValueAnimator = ValueAnimator.ofFloat(0, 1);
            mValueAnimator.setDuration(ROTATION_ANIMATION_DURATION);
            mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    percent = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    AnimationRing.this.state = FINISHED;
                    postInvalidate();
                    if(SystemClock.uptimeMillis() - mStartTime > timeout){
                        mStartTime = 0;
                        if(mAnimatorListener != null){
                            mAnimatorListener.onTimeout();
                        }
                    }else{
                        if(mAnimatorListener != null) {
                            mAnimatorListener.onEnd();
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(cirqueWidth);
        mPaint.setColor(cirqueColor);
        mPaint.setStyle(Paint.Style.STROKE);
        //画圆环
        float bigRadius = (width - cirqueWidth * 6) / 2f;
        canvas.drawCircle(width / 2f, height / 2f, bigRadius, mPaint);
        if(state != FINISHED){
            //画圆
            mPaint.setStyle(Paint.Style.FILL);
            float angle = 45 + 360 * percent;
            float x = (float) (width / 2f - (bigRadius * Math.cos(Math.PI * angle / 180)));
            float y = (float) (width / 2f - (bigRadius * Math.sin(Math.PI * angle / 180)));
            canvas.drawCircle(x, y, cirqueWidth * 3, mPaint);
        }
    }

    public interface AnimatorListener {
        void onTimeout();

        void onEnd();
    }

    @Override
    protected void onDetachedFromWindow() {
        if(mValueAnimator != null){
            mValueAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }

    public void setAnimatorListener(AnimatorListener listener) {
        mAnimatorListener = listener;
    }

    public void setTimeout(int time) {
        timeout = time;
    }

    public void setState(int state) {
        this.state = state;
        switch (state){
            case WAITING:
                mStartTime = SystemClock.uptimeMillis();
                if(mValueAnimator != null){
                    mValueAnimator.setRepeatCount(timeout / ROTATION_ANIMATION_DURATION - 1);
                    mValueAnimator.start();
                }
                break;
            case FINISHED:
                if(mValueAnimator != null){
                    mValueAnimator.cancel();
                }
                break;
        }
    }
}
