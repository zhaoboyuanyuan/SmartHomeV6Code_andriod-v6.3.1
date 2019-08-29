package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cc.wulian.smarthomev6.R;

/**
 * Created by Veev on 2017/6/13
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    输入密码显示圆点
 */

public class PinCodeView extends View {

    private static String TAG = PinCodeView.class.getSimpleName();

    private Context mContext;

    @ColorInt
    private int mCircleColor;
    private int mPinCount = 6;
    private float mPinRadius;

    private int mInputCount = 0;

    private Rect mRect = new Rect();

    public PinCodeView(Context context) {
        super(context);

        mContext = context;
        init();
    }

    private Paint mPaint;

    public PinCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PinCodeView, 0, 0);

        try {
            mCircleColor = ta.getColor(R.styleable.PinCodeView_pinCircleColor, context.getResources().getColor(R.color.newPrimary));
            mPinRadius = ta.getDimension(R.styleable.PinCodeView_pinRadius, dp2px(5));
            mPinCount = ta.getInteger(R.styleable.PinCodeView_pinCount, 6);
        } finally {
            ta.recycle();
        }
        init();
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    private float dp2px(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return pxValue * scale;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mCircleColor);
        mPaint.setStrokeWidth(dp2px(2));
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = adjustSize(getDefaultWidth(), widthMeasureSpec, getWidth());
        int height = adjustSize(getDefaultHeight(), heightMeasureSpec, getHeight());
        setMeasuredDimension(width, height);
    }

    private int getDefaultWidth() {
        return getWidth();
    }

    private int getDefaultHeight() {
        return 50;
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
        drawPin(canvas);
    }

    private void drawPin(Canvas canvas) {
        int startX = getPaddingLeft();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int centerH = getPaddingTop() + height / 2;

        int unitX = width / (mPinCount + 1);

        for (int i = 1; i <= mPinCount; i ++) {
            int pinX = startX + unitX * i;
            if (i <= mInputCount) {
                mPaint.setStyle(Paint.Style.FILL);
            } else {
                mPaint.setStyle(Paint.Style.STROKE);
            }
            canvas.drawCircle(pinX, centerH, mPinRadius, mPaint);
        }
    }

    /**
     * 设置输入的密码的 长度
     */
    public void setInputCount(int in) {
        mInputCount = in;
        invalidate();
    }
}
