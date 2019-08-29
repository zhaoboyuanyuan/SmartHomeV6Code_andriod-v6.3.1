package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * Created by Veev on 2017/4/10
 * WebView 的进度条
 */
public class WLProgressBar extends View {

    private int mHeight = 8;
    private float mProgress = 0f;
    private int oldProgress = 0;
    private int aimProgress = 0;
    // 最大进度，默认100
    private int mMaxProgress = 100;
    private int colors[];

    // 进度加载完成，进度条消失的动画
    private MyAnimation myAnimation;
    private float animProgress = 0f;

    // 进度条改变时，动态改变的动画
    private ProgressAnimation progressAnimation;
    private float increaseProgress = 1f;

    // 画笔
    private Paint mPaint;

    public WLProgressBar(Context context) {
        this(context, null);
    }

    public WLProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WLProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        // 设置颜色梯度
        colors = new int[] {
                Color.CYAN, 0xff00b8d4, 0xff0091ea, 0xffaa00ff
        };

        myAnimation = new MyAnimation();
        myAnimation.setDuration(700);

        progressAnimation = new ProgressAnimation();
        progressAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mProgress >= mMaxProgress) {
                    loadDone();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = adjustSize(mHeight, heightMeasureSpec, getHeight());
        int width = adjustSize(getWidth(), widthMeasureSpec, getWidth());
        setMeasuredDimension(width, height);

        LinearGradient lg = new LinearGradient(0, 0, getWidth(), mHeight,
                colors,
                null,
                Shader.TileMode.CLAMP);
        mPaint.setShader(lg);
    }

    /**
     * 调整长宽
     * @param desiredSize   wrap_content
     * @param measureSpec   measure
     * @param maxSize       match_parent
     * @return 调整后的大小
     */
    private int adjustSize(int desiredSize, int measureSpec, int maxSize) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        // wrap_content 或者 match_parent 为0时，说明还没有完成测量
        if (desiredSize * maxSize == 0) {
            return specSize;
        }
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = desiredSize;
                break;
            case MeasureSpec.EXACTLY:
                result = maxSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = (int) (getHeight() * (1f - animProgress));
        int width = getWidth();

        canvas.drawRect(0, 0, width * mProgress / mMaxProgress, height, mPaint);
    }

    /**
     * set the progress
     * @param progress in [0, 100]
     */
    public void setProgress(int progress) {
        oldProgress = aimProgress;
        aimProgress = progress;
        int dProgress = aimProgress - oldProgress;
        if (dProgress > 1) {
            // 加载完成时，添加一个插值器
            if (aimProgress >= getMaxProgress()) {
                progressAnimation.setInterpolator(new WLInterpolator());
            }
            progressAnimation.setDuration(dProgress * 10);
            clearAnimation();
            startAnimation(progressAnimation);
        } else {
            mProgress = progress;
            invalidate();
        }
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    /**
     * 设置进度的最大值
     * @param maxProgress 进度最大值
     */
    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    /**
     * load done  then GONE
     */
    public void loadDone() {
        startAnimation(myAnimation);
    }

    private class WLInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return (float) -(Math.sqrt(1f - input * input) - 1);
        }
    }

    private class MyAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            animProgress = interpolatedTime;

            if (interpolatedTime >= 1.0f) {
                setVisibility(GONE);
            }
            postInvalidate();
        }
    }

    private class ProgressAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            mProgress = oldProgress + ((aimProgress - oldProgress) * interpolatedTime);
            postInvalidate();
        }
    }
}
