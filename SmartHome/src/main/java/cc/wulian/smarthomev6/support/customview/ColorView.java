package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.tools.singlechoice.CheckedListener;
import cc.wulian.smarthomev6.support.tools.singlechoice.SingleChoice;

/**
 * Created by Veev on 2017/8/22
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    ColorView
 */

public class ColorView extends View implements SingleChoice {

    private static final String TAG = "ColorView";

    private int mTouchSlop;

    private int mColor = Color.RED;
    private float mRadiusSmall = 20, mRadiusLarge = 25;
    private int mSpeed = 700;

    private Paint mPaint, mPaintS;

    private boolean mChecked = false;
    private Animation mAnimChecked;
    private float mCheckedProgress;
    private RectF mBound = new RectF();
    private float centerX, centerY;

    public ColorView(Context context) {
        this(context, null);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorView, 0, 0);

        try {
            mColor = ta.getColor(R.styleable.ColorView_colorColor, mColor);
            mRadiusSmall = ta.getDimension(R.styleable.ColorView_colorRadiusSmall, mRadiusSmall);
            mRadiusLarge = ta.getDimension(R.styleable.ColorView_colorRadiusLarge, mRadiusLarge);
            mSpeed = ta.getInt(R.styleable.ColorView_colorSpeed, mSpeed);
        } finally {
            ta.recycle();
        }

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);

        mPaintS = new Paint(mPaint);
//        mPaintS.setAlpha(100);

        mAnimChecked = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);

                mCheckedProgress = interpolatedTime;
                postInvalidate();
            }
        };
        mAnimChecked.setDuration(mSpeed);
//        mAnimChecked.setRepeatCount(-1);
        mAnimChecked.setRepeatCount(1);
        mAnimChecked.setRepeatMode(Animation.REVERSE);
        mAnimChecked.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mChecked) {
                    startAnimation(mAnimChecked);
                } else {
                    clearAnimation();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = adjustSize(50, heightMeasureSpec, getHeight());
        int width = adjustSize(50, widthMeasureSpec, getWidth());
        setMeasuredDimension(width, height);

        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;
        mBound.set(centerX - mRadiusLarge,
                centerY - mRadiusLarge ,
                centerX + mRadiusLarge,
                centerY + mRadiusLarge);
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

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = maxSize;
                break;
            case MeasureSpec.AT_MOST:
                result = desiredSize;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getAnimation() != null && getAnimation().hasStarted()) {
            mPaintS.setAlpha((int) (50 * mCheckedProgress) + 50);
            float r = mRadiusSmall + (mRadiusLarge - mRadiusSmall) * mCheckedProgress;
            canvas.drawCircle(centerX, centerY, r, mPaintS);
        }
        canvas.drawCircle(centerX, centerY, mRadiusSmall, mPaint);
    }

    private PointF mTouch = new PointF(0f, 0f);
    private PointF mTouchDown = new PointF(0f, 0f);

    /**
     * 是否点击我了
     */
    private boolean touchMe() {
        if (mTouch.x >= mBound.left
                && mTouch.x <= mBound.right
                && mTouch.y >= mBound.top
                && mTouch.y <= mBound.bottom) {
            return true;
        }
        return false;
    }

    /**
     * 是否 是一次点击事件
     */
    private boolean isClick() {
        if (Math.abs(mTouch.x - mTouchDown.x) < mTouchSlop
                && Math.abs(mTouch.y - mTouchDown.y) < mTouchSlop) {
            return true;
        }
        return false;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouch.set(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDown.set(mTouch);
                performClick();
                break;
            case MotionEvent.ACTION_UP:
                if (touchMe() && isClick()) {
                    toggle();
                }
                break;
        }
        return true;
    }

    private void checkAnimation() {
        if (getAnimation() == null) {
            startAnimation(mAnimChecked);
        } else if (!getAnimation().hasStarted()) {
            startAnimation(mAnimChecked);
        }
    }

    @Override
    public void setChecked(boolean b) {
        mChecked = b;
        if (mChecked) {
            checkAnimation();
        }

        if (!mCheckedListenerList.isEmpty()) {
            for (CheckedListener c : mCheckedListenerList) {
                c.onChecked(mChecked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    private List<CheckedListener> mCheckedListenerList = new ArrayList<>();

    @Override
    public void addCheckedListener(CheckedListener listener) {
        mCheckedListenerList.add(listener);
    }

    @Override
    public void removeCheckedListener(CheckedListener listener) {
        mCheckedListenerList.remove(listener);
    }

    @Override
    public void clearCheckedListener(CheckedListener listener) {
        mCheckedListenerList.clear();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
        invalidate();
    }
}
