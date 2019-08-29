package cc.wulian.smarthomev6.support.customview;

import android.text.TextUtils;
import android.view.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.Arrays;

import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;

/**
 * Created by Veev on 2017/8/24
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    侧边栏
 */
public class SortBar extends View {

    // 26个字母
    public static String[] A_Z = {"#" , "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private int mTouchSlop;
    private Paint mPaint, mPaintFocus;
    private int mIndex = -1, mLastIndex = -1;// 选中
    private Animation mAnimChangeIndex;
    private float mProgressChangeIndex;

    private OnSortChangedListener mOnSortChangedListener;

    public interface OnSortChangedListener {
        void onSortChanged(int newIndex, int oldIndex, String newText, String oldText);
    }

    public SortBar(Context context) {
        this(context, null);
    }

    public SortBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SortBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.rgb(33, 65, 98));  //设置字体颜色
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);  //设置字体
        mPaint.setAntiAlias(true);  //设置抗锯齿
        mPaint.setTextSize(36);  //设置字母字体大小

        mPaintFocus = new Paint(mPaint);
        mPaintFocus.setColor(Color.parseColor("#3399ff"));

        mAnimChangeIndex = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                mProgressChangeIndex = interpolatedTime;
                postInvalidate();
            }
        };
        mAnimChangeIndex.setDuration(300);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = adjustSize(getHeight(), heightMeasureSpec, getHeight());
        int width = adjustSize(50, widthMeasureSpec, getWidth());
        setMeasuredDimension(width, height);
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
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        float singleHeight = height * 1.0f / A_Z.length;// 获取每一个字母的高度

        for (int i = 0; i < A_Z.length; i++) {
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - mPaint.measureText(A_Z[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            // 选中的状态
            if (i == mIndex) {
//                canvas.drawText(A_Z[i], xPos, yPos, mPaint);
                mPaintFocus.setAlpha((int) (200 * mProgressChangeIndex + 55));
                canvas.drawText(A_Z[i], xPos, yPos, mPaintFocus);
            } else {
                canvas.drawText(A_Z[i], xPos, yPos, mPaint);
            }
        }
    }

    private PointF mTouch = new PointF(0f, 0f);
    private PointF mTouchDown = new PointF(0f, 0f);

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
        int index = (int) (mTouch.y / (getHeight() / A_Z.length));
        index = index > A_Z.length - 1 ? A_Z.length - 1 : index;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDown.set(mTouch);
                performClick();
                break;
            case MotionEvent.ACTION_MOVE:
                setIndex(index);
                break;
            case MotionEvent.ACTION_UP:
                if (isClick()) {
                    setIndex(index);
                }
                break;
        }
        return true;
    }

    /**
     * 设置位置
     */
    public void setIndex(int i) {
        if (mIndex != i) {
            notifyIndex(i, mLastIndex);
            mLastIndex = mIndex;
            mIndex = i;
            startAnimation(mAnimChangeIndex);
        }
    }

    public void setIndex(String s) {
        int position = Arrays.binarySearch(A_Z, s);
        setIndex(position);
    }

    private void notifyIndex(int i, int lastIndex) {
        if (mOnSortChangedListener != null) {
            mOnSortChangedListener.onSortChanged(i, lastIndex,
                    i == -1 ? " " : A_Z[i],
                    lastIndex == -1 ? " " : A_Z[lastIndex]);
        }
    }

    public void setOnSortChangedListener(OnSortChangedListener onSortChangedListener) {
        mOnSortChangedListener = onSortChangedListener;
    }
}
