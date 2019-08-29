package cc.wulian.smarthomev6.support.customview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cc.wulian.smarthomev6.R;

/**
 * 作者：luzx on 2017/6/27 11:52
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class ColorBrightSwitch extends View {
    public static final int TOUCH_DOWN = 0x101;
    public static final int TOUCH_MOVE = 0x102;
    private Paint mPaint;

    private RectF mRectF;

    private int width;

    private int height;

    private float cirqueWidth;

    private float textSize;

    private float percent;

    private float lastPercent;

    private float waitingPercent;

    private float animationPercent;
    private float oPenClosePercent;
    private boolean isWaiting;

    private int textColor = 0xff54BF00;
    private int cirqueGroundColor = 0xffDDDFE1;
    private int onffColor = 0xff9e9e9e;

    private int switchDuration = 400;
    private int waitingDuration = 5000;
    private int touchAction;
    private String unit = "%";
    private String off = "OFF";
    private String on = "ON";
    private DashPathEffect pathEffect;
    private float dp1;
    private float dp2;
    private SweepGradient mSweepGradient;
    private OnSwitchListener mOnSwitchListener;
    private ValueAnimator oPenCloseAnimation;
    private ValueAnimator waitingAnimation;
    private int angle = 40;
    private float padding = 0;
    private boolean canTouchMove = false;

    private float boundaryX1;//圆弧起始点x
    private float boundaryY1;//圆弧起始点y
    private float boundaryX2;//圆弧180度点x
    private float boundaryY2;//圆弧180度点x
    private float radius;//圆弧半径
    private Handler mHandler;

    public ColorBrightSwitch(Context context) {
        super(context);
    }

    public ColorBrightSwitch(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorBrightSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        //防止边缘的锯齿
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        cirqueWidth = width / 10f;
        textSize = width / 4.5f;
        padding = cirqueWidth;
        dp1 = width / 120f;
        dp2 = width / 38.8f;
        if (pathEffect == null) {
            pathEffect = new DashPathEffect(new float[]{dp1, dp2}, 0);
        }
        radius = (width - cirqueWidth) / 2f - padding;
        boundaryX1 = (float) (width / 2f - (width / 2f - padding) * Math.sin(Math.PI * angle / 180));
        boundaryY1 = (float) (height / 2f + (width / 2f - padding) * Math.cos(Math.PI * angle / 180));

        boundaryX2 = (float) ((width - cirqueWidth * 2) * Math.cos(Math.PI * (90 - angle) / 180)) + boundaryX1;
        boundaryY2 = boundaryY1 - (float) ((width - cirqueWidth * 2) * Math.sin(Math.PI * (90 - angle) / 180));
    }

    /**
     * @param x
     * @param y
     * @return >0点在直线右边<0点做直线左边
     */
    private float getPointPosition(float x, float y) {
        return (y - boundaryY1) / (boundaryY1 - boundaryY2) - (x - boundaryX1) / (boundaryX1 - boundaryX2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(cirqueGroundColor);
        float startX1 = (float) (width / 2f - (radius + cirqueWidth / 2f) * Math.sin(Math.PI * (angle + 0.6) / 180));
        float startY1 = (float) (width / 2f + (radius + cirqueWidth / 2f) * Math.cos(Math.PI * (angle + 0.6) / 180));
        float stopX1 = (float) (width / 2f - (radius - cirqueWidth / 2f) * Math.sin(Math.PI * (angle + 0.6) / 180));
        float stopY1 = (float) (width / 2f + (radius - cirqueWidth / 2f) * Math.cos(Math.PI * (angle + 0.6) / 180));

        float startX2 = (float) (width / 2f + (radius + cirqueWidth / 2f) * Math.sin(Math.PI * angle / 180));
        float stopX2 = (float) (width / 2f + (radius - cirqueWidth / 2f) * Math.sin(Math.PI * angle / 180));
        mPaint.setStrokeWidth(dp1);
        canvas.drawLine(startX1, startY1, stopX1, stopY1, mPaint);// 画线
        canvas.drawLine(startX2, startY1, stopX2, stopY1, mPaint);// 画线
        mPaint.setStrokeWidth(cirqueWidth * 0.6f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(pathEffect);
        float w = cirqueWidth / 2f + padding;
        fillRect(w, w, width - w, height - w);
        //画底色圆环
        canvas.drawArc(mRectF, 130, 280, false, mPaint);
        Paint.FontMetrics fontMetrics;
        if (isWaiting) {
            float percentTemp = percent * waitingPercent;
            int sweepAngle = (int) (percentTemp * 280);
            if (mSweepGradient == null) {
                mSweepGradient = new SweepGradient(width / 2, height / 2, new int[]{0XFFFFF600, 0XFFFF6E02, 0XFFFFE203}, new float[]{0.14f, 0.36f, 1f});
            }
            mPaint.setShader(mSweepGradient);
            //画进度环
//            WLog.i("luzx", "sweepAngle:" + sweepAngle);
            canvas.drawArc(mRectF, 130, sweepAngle, false, mPaint);
            mPaint.setShader(null);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(0);
            mPaint.setColor(textColor);
            float x = 0;
            float y = 0;
            int a;
            int a1;
            int a2;
            float x2 = 0;
            float y2 = 0;
            float x3 = 0;
            float y3 = 0;
            float cc = cirqueWidth * 0.7f;//小三角等边长
            int aa = 50;//小三角顶角
            float r = (radius + cirqueWidth * 0.6f);//外圈小三角的半径
            if (sweepAngle <= 90 - angle) {
                a = 90 - angle - sweepAngle;
                a1 = a - aa / 2;
                a2 = 90 - aa - a1;
                x = width / 2f - (float) Math.cos(Math.PI * a / 180) * r;
                y = height / 2f + (float) Math.sin(Math.PI * a / 180) * r;
                x2 = (float) (x - Math.cos(Math.PI * a1 / 180) * cc);
                y2 = (float) (Math.sin(Math.PI * a1 / 180) * cc + y);
                x3 = (float) (x - Math.sin(Math.PI * a2 / 180) * cc);
                y3 = (float) (Math.cos(Math.PI * a2 / 180) * cc + y);
            } else if (sweepAngle > 90 - angle && sweepAngle <= 180 - angle) {
                a = 180 - angle - sweepAngle;
                a1 = a - aa / 2;
                a2 = 90 - aa - a1;
                x = width / 2f - (float) Math.sin(Math.PI * a / 180) * r;
                y = height / 2f - (float) Math.cos(Math.PI * a / 180) * r;
                x2 = (float) (x - Math.sin(Math.PI * a1 / 180) * cc);
                y2 = (float) (y - Math.cos(Math.PI * a1 / 180) * cc);
                x3 = (float) (x - Math.cos(Math.PI * a2 / 180) * cc);
                y3 = (float) (y - Math.sin(Math.PI * a2 / 180) * cc);
            } else if (sweepAngle > 180 - angle && sweepAngle <= 270 - angle) {
                a = 270 - angle - sweepAngle;
                a1 = 90 - a - aa / 2;
                a2 = 90 - aa - a1;
                x = width / 2f + (float) Math.cos(Math.PI * a / 180) * r;
                y = height / 2f - (float) Math.sin(Math.PI * a / 180) * r;
                x2 = (float) (Math.sin(Math.PI * a1 / 180) * cc + x);
                y2 = (float) (y - Math.cos(Math.PI * a1 / 180) * cc);
                x3 = (float) (Math.cos(Math.PI * a2 / 180) * cc + x);
                y3 = (float) (y - Math.sin(Math.PI * a2 / 180) * cc);
            } else if (sweepAngle > 270 - angle && sweepAngle <= 280) {
                a = 360 - angle - sweepAngle;
                a1 = sweepAngle - 270 + angle - aa / 2;
                a2 = 90 - aa - a1;
                x = width / 2f + (float) Math.sin(Math.PI * a / 180) * r;
                y = height / 2f + (float) Math.cos(Math.PI * a / 180) * r;
                x2 = (float) (Math.cos(Math.PI * a1 / 180) * cc + x);
                y2 = (float) (y + Math.sin(Math.PI * a1 / 180) * cc);
                x3 = (float) (Math.sin(Math.PI * a2 / 180) * cc + x);
                y3 = (float) (y + Math.cos(Math.PI * a2 / 180) * cc);
            }
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(x2, y2);
            path.lineTo(x3, y3);
            path.close();
            canvas.drawPath(path, mPaint);

            mPaint.setStrokeWidth(dp1);
            mPaint.setColor(0XFFFF6E02);
            canvas.drawLine(startX1, startY1, stopX1, stopY1, mPaint);// 画线
            if (percentTemp > 0.995) {
                mPaint.setColor(0XFFFFF600);
                canvas.drawLine(startX2, startY1, stopX2, stopY1, mPaint);// 画线
            } else {
                mPaint.setColor(cirqueGroundColor);
                if (percentTemp == 0) {
                    canvas.drawLine(startX1, startY1, stopX1, stopY1, mPaint);// 画线
                }
                canvas.drawLine(startX2, startY1, stopX2, stopY1, mPaint);// 画线
            }
            float textWidth;
            float textHeight;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(0);
            if (percentTemp == 0) {
                mPaint.setColor(onffColor);
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                mPaint.setTextSize(textSize * 0.5f);
                textWidth = mPaint.measureText(off);
                fontMetrics = mPaint.getFontMetrics();
                textHeight = fontMetrics.ascent + fontMetrics.descent;
                canvas.drawText(off, (width - textWidth) / 2f, (height - textHeight) / 2f, mPaint);
            } else {
                //画百分比
                mPaint.setTextSize(textSize * 0.5f);
                float unitTextWidth = mPaint.measureText(unit);
                String text = String.format("%.0f", percent * waitingPercent * 100);
                mPaint.setTextSize(textSize);
                textWidth = mPaint.measureText(text);
                fontMetrics = mPaint.getFontMetrics();
                textHeight = fontMetrics.ascent + fontMetrics.descent;
                mPaint.setTypeface(Typeface.DEFAULT);
                mPaint.setColor(textColor);
                canvas.drawText(text, (width - textWidth - unitTextWidth) / 2f, (height - textHeight) / 2f, mPaint);
                mPaint.setTextSize(textSize * 0.5f);
                canvas.drawText(unit, (width - textWidth - unitTextWidth) / 2f + textWidth, (height - textHeight) / 2f, mPaint);
            }
        } else {
            if (percent != 0) {
                percent = percent < 0.01f ? 0.01f : percent;
                int sweepAngle = (int) (animationPercent * 280);
                if (mSweepGradient == null) {
                    mSweepGradient = new SweepGradient(width / 2, height / 2, new int[]{0XFFFFF600, 0XFFFF6E02, 0XFFFFE203}, new float[]{0.14f, 0.36f, 1f});
                }
                mPaint.setShader(mSweepGradient);
                //画进度环
                canvas.drawArc(mRectF, 130, sweepAngle, false, mPaint);
                mPaint.setStrokeWidth(dp1);
                mPaint.setColor(0XFFFF6E02);
                canvas.drawLine(startX1, startY1, stopX1, stopY1, mPaint);// 画线
                if (animationPercent > 0.995) {
                    mPaint.setColor(0XFFFFF600);
                    canvas.drawLine(startX2, startY1, stopX2, stopY1, mPaint);// 画线
                }
                mPaint.setShader(null);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(0);
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                mPaint.setTypeface(Typeface.DEFAULT);
                mPaint.setColor(onffColor);
                mPaint.setTextSize(textSize * 0.5f);
                float onTextWidth = mPaint.measureText(on);
                fontMetrics = mPaint.getFontMetrics();
                float onTextHeight = fontMetrics.ascent + fontMetrics.descent;
                float offTextWidth = mPaint.measureText(off);
                fontMetrics = mPaint.getFontMetrics();
                float offTextHeight = fontMetrics.ascent + fontMetrics.descent;
                mPaint.setColor(textColor);

                float x = 0;
                float y = 0;
                int a;
                int a1;
                int a2;
                float x2 = 0;
                float y2 = 0;
                float x3 = 0;
                float y3 = 0;
                float cc = cirqueWidth * 0.7f;//小三角等边长
                int aa = 50;//小三角顶角
                float r = (radius + cirqueWidth * 0.6f);//外圈小三角的半径
                if (sweepAngle <= 90 - angle) {
                    a = 90 - angle - sweepAngle;
                    a1 = a - aa / 2;
                    a2 = 90 - aa - a1;
                    x = width / 2f - (float) Math.cos(Math.PI * a / 180) * r;
                    y = height / 2f + (float) Math.sin(Math.PI * a / 180) * r;
                    x2 = (float) (x - Math.cos(Math.PI * a1 / 180) * cc);
                    y2 = (float) (Math.sin(Math.PI * a1 / 180) * cc + y);
                    x3 = (float) (x - Math.sin(Math.PI * a2 / 180) * cc);
                    y3 = (float) (Math.cos(Math.PI * a2 / 180) * cc + y);
                } else if (sweepAngle > 90 - angle && sweepAngle <= 180 - angle) {
                    a = 180 - angle - sweepAngle;
                    a1 = a - aa / 2;
                    a2 = 90 - aa - a1;
                    x = width / 2f - (float) Math.sin(Math.PI * a / 180) * r;
                    y = height / 2f - (float) Math.cos(Math.PI * a / 180) * r;
                    x2 = (float) (x - Math.sin(Math.PI * a1 / 180) * cc);
                    y2 = (float) (y - Math.cos(Math.PI * a1 / 180) * cc);
                    x3 = (float) (x - Math.cos(Math.PI * a2 / 180) * cc);
                    y3 = (float) (y - Math.sin(Math.PI * a2 / 180) * cc);
                } else if (sweepAngle > 180 - angle && sweepAngle <= 270 - angle) {
                    a = 270 - angle - sweepAngle;
                    a1 = 90 - a - aa / 2;
                    a2 = 90 - aa - a1;
                    x = width / 2f + (float) Math.cos(Math.PI * a / 180) * r;
                    y = height / 2f - (float) Math.sin(Math.PI * a / 180) * r;
                    x2 = (float) (Math.sin(Math.PI * a1 / 180) * cc + x);
                    y2 = (float) (y - Math.cos(Math.PI * a1 / 180) * cc);
                    x3 = (float) (Math.cos(Math.PI * a2 / 180) * cc + x);
                    y3 = (float) (y - Math.sin(Math.PI * a2 / 180) * cc);
                } else if (sweepAngle > 270 - angle && sweepAngle <= 280) {
                    a = 360 - angle - sweepAngle;
                    a1 = sweepAngle - 270 + angle - aa / 2;
                    a2 = 90 - aa - a1;
                    x = width / 2f + (float) Math.sin(Math.PI * a / 180) * r;
                    y = height / 2f + (float) Math.cos(Math.PI * a / 180) * r;
                    x2 = (float) (Math.cos(Math.PI * a1 / 180) * cc + x);
                    y2 = (float) (y + Math.sin(Math.PI * a1 / 180) * cc);
                    x3 = (float) (Math.sin(Math.PI * a2 / 180) * cc + x);
                    y3 = (float) (y + Math.cos(Math.PI * a2 / 180) * cc);
                }
                Path path = new Path();
                path.moveTo(x, y);
                path.lineTo(x2, y2);
                path.lineTo(x3, y3);
                path.close();
                canvas.drawPath(path, mPaint);
                if (oPenClosePercent == 1 || lastPercent > 0) {
                    mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    mPaint.setColor(onffColor);
                    canvas.drawText(on, (width - onTextWidth) / 2f, height - padding + (onTextHeight / 2f), mPaint);
                    mPaint.setColor(textColor);
                    fontMetrics = mPaint.getFontMetrics();
                    mPaint.setTextSize(textSize * 0.5f);
                    float unitTextWidth = mPaint.measureText(unit);
                    mPaint.setTextSize(textSize * 0.3f);
//                Log.i("luzx", "percent:" + percent);
                    String text = getResources().getString(R.string.Device_91_Brightness) + String.format("%.0f", animationPercent * 100);
//                Log.i("luzx", "percentText:" + text);
                    float textWidth = mPaint.measureText(text);
                    fontMetrics = mPaint.getFontMetrics();
                    float textHeight = fontMetrics.ascent + fontMetrics.descent;
                    //画百分比
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTypeface(Typeface.DEFAULT);
                    canvas.drawText(text, (width - textWidth - unitTextWidth) / 2f, (height - textHeight) / 2f, mPaint);
                    mPaint.setTextSize(textSize * 0.2f);
                    canvas.drawText(unit, (width - textWidth - unitTextWidth) / 2f + textWidth, (height - textHeight) / 2f, mPaint);
                } else {
                    mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    mPaint.setColor(onffColor);
                    canvas.drawText(off, (width - offTextWidth) / 2f, (height - offTextHeight) / 2f + (((height - cirqueWidth) / 2f - padding) * oPenClosePercent), mPaint);
                }
            } else {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(0);
                mPaint.setColor(onffColor);
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                mPaint.setTextSize(textSize * 0.5f);
                float textWidth = mPaint.measureText(off);
                fontMetrics = mPaint.getFontMetrics();
                float textHeight = fontMetrics.ascent + fontMetrics.descent;
                float onTextWidth = mPaint.measureText(on);
                fontMetrics = mPaint.getFontMetrics();
                float onTextHeight = fontMetrics.ascent + fontMetrics.descent;
                //画状态
                if (oPenClosePercent == 1 || lastPercent == 0) {
                    canvas.drawText(off, (width - textWidth) / 2f, (height - textHeight) / 2f, mPaint);
                } else {
                    canvas.drawText(on, (width - onTextWidth) / 2f, height - padding + (onTextHeight / 2f) - (((height - cirqueWidth) / 2f - padding) * oPenClosePercent), mPaint);
                }
            }
        }

//        canvas.drawLine(boundaryX1, boundaryY1, boundaryX2, boundaryY2, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || isWaiting) {
            return false;
        }
        double result;
        float xd;
        float yd;
        float insideDistance = width / 2f - cirqueWidth * 2.5f;
        float outsideDistance = width / 2f;
        float radius = width / 2f - padding;
        float startX = (float) (width / 2f - (radius - cirqueWidth) * Math.sin(Math.PI * angle / 180));
        float endX = (float) (width / 2f + (radius - cirqueWidth) * Math.sin(Math.PI * angle / 180));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xd = Math.abs(event.getX() - width / 2);
                yd = Math.abs(event.getY() - height / 2);
                result = (int) Math.sqrt(xd * xd + yd * yd);
                if (result <= insideDistance) {
                    touchAction = TOUCH_DOWN;
                }
                break;
            case MotionEvent.ACTION_UP:
                xd = Math.abs(event.getX() - width / 2);
                yd = Math.abs(event.getY() - height / 2);
                result = (int) Math.sqrt(xd * xd + yd * yd);
                if (result <= insideDistance && touchAction == TOUCH_DOWN) {
                    touchAction = 0;
                    if (mOnSwitchListener != null) {
                        mOnSwitchListener.onSwitch();
                    }
                } else if (touchAction == TOUCH_MOVE) {
                    touchAction = 0;
                    if (mOnSwitchListener != null) {
                        mOnSwitchListener.onRegulate(percent);
                    }
                }
                setEnabled(false);
                if (mHandler == null) {
                    mHandler = new Handler();
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setEnabled(true);
                    }
                }, 1000);//点击间隔
                break;
            case MotionEvent.ACTION_MOVE:
                if (percent == 0 || !canTouchMove) {
                    return true;
                }
                xd = Math.abs(event.getX() - width / 2);
                yd = Math.abs(event.getY() - height / 2);
                result = Math.sqrt(xd * xd + yd * yd);
                if (result > insideDistance && result < outsideDistance
                        && !(event.getX() > startX && event.getX() < endX && event.getY() > radius)) {
                    touchAction = TOUCH_MOVE;
                    float xd1 = Math.abs(event.getX() - boundaryX1);
                    float yd1 = Math.abs(event.getY() - boundaryY1);
                    double c = Math.sqrt(xd1 * xd1 + yd1 * yd1);
                    double d = ((result * result) + (radius * radius) - (c * c)) / (2 * result * radius);
                    if (d > 0) {
                        d = d > 1 ? 1 : d;
                    } else {
                        d = d < -1 ? -1 : d;
                    }
                    float percent = 0;
                    if (getPointPosition(event.getX(), event.getY()) > 0) {
                        if (event.getX() > startX) {
                            Log.i("luzx", "d1:" + Math.acos(d));
                            Log.i("luzx", "s1:" + (360 - Math.acos(d) * 180 / Math.PI));
                            percent = (float) ((360 - Math.acos(d) * 180 / Math.PI) / 280);
                            percent = percent > 1 ? 1 : percent;
                        }
                    } else {
                        Log.i("luzx", "d2:" + Math.acos(d));
                        Log.i("luzx", "s2:" + (Math.acos(d) * 180 / Math.PI));
                        percent = (float) (Math.acos(d) * 180 / Math.PI) / 280;
                    }
                    BigDecimal bdp = new BigDecimal(percent);
                    percent = bdp.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    if (percent > 0) {
                        this.percent = percent;
                        animationPercent = percent;
                        Log.i("luzx", "percent:" + this.percent);
                        postInvalidate();
                    }
                }
                break;
        }
        return true;
    }

    public interface OnSwitchListener {
        void onSwitch();

        void onRegulate(float percent);
    }

    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        mOnSwitchListener = onSwitchListener;
    }

    public float getPercent() {
        return percent;
    }

    public void setTouchMove(boolean canTouchMove) {
        this.canTouchMove = canTouchMove;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setPercent(float percentF) {
        lastPercent = percent;
        percent = percentF;
        if (lastPercent == percent) {
            return;
        }
        if (oPenCloseAnimation == null) {
            oPenCloseAnimation = new ValueAnimator();
            oPenCloseAnimation.setDuration(switchDuration);
            oPenCloseAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            oPenCloseAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    BigDecimal value = new BigDecimal((float) animation.getAnimatedValue()).setScale(2, RoundingMode.HALF_UP);
                    animationPercent = value.floatValue();
                    //当前进度
                    BigDecimal a = new BigDecimal((Math.abs(animationPercent - lastPercent))).setScale(2, RoundingMode.HALF_UP);
                    //总进度
                    BigDecimal b = new BigDecimal((Math.abs(percent - lastPercent))).setScale(2, RoundingMode.HALF_UP);
                    oPenClosePercent = a.floatValue() / b.floatValue();
                    postInvalidate();
                }
            });
        }
        oPenCloseAnimation.setFloatValues(lastPercent, percent);
        oPenCloseAnimation.start();
    }

    public void waiting(boolean flag) {
        isWaiting = flag;
        if (flag) {
            percent = 1;
        }
        if (waitingAnimation == null) {
            waitingAnimation = new ValueAnimator();
            waitingAnimation.setFloatValues(0, 1, 0);
            waitingAnimation.setDuration(waitingDuration);
            waitingAnimation.setInterpolator(new LinearInterpolator());
            waitingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    waitingPercent = (float) animation.getAnimatedValue();
                    postInvalidate();
                }


            });
            waitingAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isWaiting) {
                        isWaiting = false;
                        percent = 0;
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
        if (flag) {
            waitingAnimation.start();
        } else {
            waitingAnimation.cancel();
        }
    }

    private void fillRect(float left, float top, float right, float bottom) {
        if (mRectF == null) {
            mRectF = new RectF();
        }
        mRectF.set(left, top, right, bottom);
    }
}
