package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import cc.wulian.smarthomev6.R;

/**
 * 作者：luzx on 2017/6/27 11:52
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class PercentProgressRing extends View {

    private Paint mPaint;

    private RectF mRectF;

    private int cirqueGroundColor;

    private float cirqueGroundWidth;

    private int cirqueColor;

    private float cirqueWidth;

    private float textSize;

    private float percent;

    private int textColor;

    private int width;

    private int height;

    private float sweepAngle = 0;

    private float currentPercent = 0;

    private String percentText = "0";

    private String text;

    private String unit = "%";
    private float  point;
    private boolean showText = true;
    private boolean showAnimation = true;
    private boolean showGround = true;

    public PercentProgressRing(Context context) {
        super(context);
    }

    public PercentProgressRing(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentProgressRing(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PercentProgressRing, defStyleAttr, 0);
        cirqueGroundColor = a.getColor(R.styleable.PercentProgressRing_cirqueGroundColor, Color.BLUE);
        cirqueGroundWidth = a.getDimension(R.styleable.PercentProgressRing_cirqueGroundWidth, 5);
        cirqueColor = a.getColor(R.styleable.PercentProgressRing_cirqueColor, Color.BLUE);
        cirqueWidth = a.getDimension(R.styleable.PercentProgressRing_cirqueWidth, 20);
        textSize = a.getDimension(R.styleable.PercentProgressRing_textSize, 20);
        percent = a.getFloat(R.styleable.PercentProgressRing_percent, 0);
        text = a.getString(R.styleable.PercentProgressRing_text);
        textColor = a.getColor(R.styleable.PercentProgressRing_textColor, Color.BLUE);
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
        mPaint.setColor(cirqueGroundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        if(showGround){
            //画底色圆环
            mPaint.setStrokeWidth(cirqueGroundWidth);
            canvas.drawCircle(width / 2f, height / 2f, (width - cirqueWidth) / 2f  - getPaddingBottom(), mPaint);
        }
        if (currentPercent != 0) {
            mPaint.setStrokeWidth(cirqueWidth);
            float w = cirqueWidth / 2f;
            fillRect(w + getPaddingBottom(), w + getPaddingBottom(), width - w- getPaddingBottom(), height - w- getPaddingBottom());
            mPaint.setColor(cirqueColor);
            //画进度环
            canvas.drawArc(mRectF, 269, sweepAngle, false, mPaint);
        }
        if(showText){
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(0);
            mPaint.setColor(textColor);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextSize(textSize);
            float percentTextWidth = mPaint.measureText(percentText);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float percentTextHeight = fontMetrics.ascent;
            float textWidth = 0;
            float textHeight = 0;

            //画文字
            if(text != null && !"".equals(text)){
                mPaint.setTextSize(textSize / 5);
                mPaint.setTypeface(Typeface.DEFAULT);
                textWidth = mPaint.measureText(text);
                fontMetrics = mPaint.getFontMetrics();
                textHeight = fontMetrics.top;
                canvas.drawText(text, (width - textWidth) / 2f, (height - percentTextHeight) / 2f, mPaint);
            }

            mPaint.setTextSize(textSize / 3);
            float unitTextWidth = mPaint.measureText(unit);

            //画百分比
            mPaint.setTextSize(textSize);
            canvas.drawText(percentText, (width - percentTextWidth - unitTextWidth) / 2f, (height - percentTextHeight) / 2f + textHeight, mPaint);

            mPaint.setTextSize(textSize / 3);
            mPaint.setTypeface(Typeface.DEFAULT);
            canvas.drawText(unit, (width + unitTextWidth + percentTextWidth) / 2 - unitTextWidth, (height - percentTextHeight) / 2f + textHeight, mPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && currentPercent < percent) {
            drawArc();
        }
    }

    public void setShowGround(boolean showGround){
        this.showGround = showGround;
    }

    public void setTextVisibility(boolean showText){
       this.showText = showText;
    }

    public void setShowAnimation(boolean showAnimation){
        this.showAnimation = showAnimation;
    }

    public void setPercent(float percent) {
        this.percent = percent;
        if(showAnimation && percent > 0){
            drawArc();
        }else{
            currentPercent = percent;
            sweepAngle = currentPercent * 360;
            percentText = String.format("%d", Math.round(currentPercent * 100));
            invalidate();
        }
    }

    private void drawArc() {
        new Thread() {
            @Override
            public void run() {
                int count = 0;
                float input = 0;
                float totalCount = 20f;
                while (count < totalCount) {
                    count++;
                    input += 1 / totalCount;
                    float s = (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
                    currentPercent = percent * s;
                    if (currentPercent > percent) {
                        currentPercent = percent;
                    }
                    sweepAngle = currentPercent * 360;
                    percentText = String.format("%d", Math.round(currentPercent * 100));
                    postInvalidate();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void fillRect(float left, float top, float right, float bottom) {
        if (mRectF == null) {
            mRectF = new RectF();
        }
        mRectF.set(left, top, right, bottom);
    }
}
