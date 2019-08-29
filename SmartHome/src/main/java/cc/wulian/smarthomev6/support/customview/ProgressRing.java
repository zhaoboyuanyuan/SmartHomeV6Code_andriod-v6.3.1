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
import android.view.View;
import android.view.animation.LinearInterpolator;
import cc.wulian.smarthomev6.R;

/**
 * 作者：luzx on 2017/6/27 11:52
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class ProgressRing extends View {

    public static final int WAITING = 0x001;

    public static final int FINISHED = 0x002;

    private static final int ROTATION_ANIMATION_DURATION = 1000;

    private Paint mPaint;

    private int strokeStartColor;

    private int strokeEndColor;

    private float strokeWidth;

    private int width;

    private int height;

    private ObjectAnimator mObjectAnimator;

    private AnimatorListener mAnimatorListener;

    private int timeout;
    private long mStartTime;
    private SweepGradient mSweepGradient;

    public ProgressRing(Context context) {
        super(context);
    }

    public ProgressRing(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressRing(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressRing, defStyleAttr, 0);
        strokeStartColor = a.getColor(R.styleable.ProgressRing_ringStartColor, Color.BLUE);
        strokeEndColor = a.getColor(R.styleable.ProgressRing_ringEndColor, Color.BLUE);
        strokeWidth = a.getDimension(R.styleable.ProgressRing_ringWidth, 2);
        a.recycle();

        mPaint = new Paint();
        //防止边缘的锯齿
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        if(mSweepGradient == null){
            mSweepGradient = new SweepGradient(width / 2f, height / 2f, strokeStartColor, strokeEndColor);
        }
        mPaint.setShader(mSweepGradient);
        //画圆环
        canvas.drawCircle(width / 2f, height / 2f, (width - strokeWidth) / 2f, mPaint);
    }

    public interface AnimatorListener {
        void onTimeout();

        void onEnd();
    }

    public void setAnimatorListener(AnimatorListener listener) {
        mAnimatorListener = listener;
    }

    public void setTimeout(int time) {
        timeout = time;
    }

    public void setState(int state) {
        switch (state){
            case WAITING:
                setVisibility(View.VISIBLE);
                if(mObjectAnimator == null){
                    mObjectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
                    mObjectAnimator.setDuration(ROTATION_ANIMATION_DURATION);
                    mObjectAnimator.setRepeatCount(timeout / ROTATION_ANIMATION_DURATION - 1);
                    mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
                    mObjectAnimator.setInterpolator(new LinearInterpolator());
                    mObjectAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setVisibility(View.INVISIBLE);
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
                mStartTime = SystemClock.uptimeMillis();
                mObjectAnimator.start();
                break;
            case FINISHED:
                setVisibility(View.INVISIBLE);
                if(mObjectAnimator != null){
                    mObjectAnimator.cancel();
                }
                break;
        }
    }
}
