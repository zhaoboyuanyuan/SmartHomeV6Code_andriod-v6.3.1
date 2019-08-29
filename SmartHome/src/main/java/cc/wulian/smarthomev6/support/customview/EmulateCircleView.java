package cc.wulian.smarthomev6.support.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import cc.wulian.smarthomev6.R;

/**
 * 作者：luzx on 2017/8/25 11:52
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class EmulateCircleView extends View {

    public static final int OPEN = 0x001;

    public static final int CLOSE = 0x002;

    public static final int WAITING = 0x003;

    public static final int BIG_CIRCLE_TOUCH_DOWN = 0x101;

    public static final int SMALL_CIRCLE1_TOUCH_DOWN = 0x102;

    public static final int SMALL_CIRCLE2_TOUCH_DOWN = 0x103;

    public static final int SMALL_CIRCLE3_TOUCH_DOWN = 0x104;

    public static final int SMALL_CIRCLE4_TOUCH_DOWN = 0x105;

    private Paint mPaint;

    private int bigCircleColor = 0xff8DD652;

    private int bigCirclePressColor = 0XFFA9EB73;

    private int smallCircleColor = 0xffffffff;

    private int smallCirclePressColor = 0XFFF3FBED;

    private int outerRingColor = 0XFFE2F3D5;

    private int outerRingPressColor = 0xffE6F4DC;

    private int switchDuration = 400;

    private int width;

    private int height;

    private int touchAction;

    private ValueAnimator oPenCloseAnimation;
    private ValueAnimator mAnimator;
    private int bigRadius;
    private int smallRadius;
    private float distance;
    private int state;
    private float percent = 1;
    private String percentText = "ON";
    private DashPathEffect pathEffect;
    private OnCircleClickListener mOnCircleClickListener;
    private Point bigCircleCentrePoint;
    private Point smallCircleCentrePoint1;
    private Point smallCircleCentrePoint2;
    private Point smallCircleCentrePoint3;
    private Point smallCircleCentrePoint4;
    private float animatedValue;
    private float moveVertical;
    private int bigCircleTextSize;
    private int smallCircleTextSize;
    private int smallCircleTextColor = 0xff626262;
    private int smallCircleStrokeWidth;
    private int outerRingWidth;
    private boolean isEffective;//是否是有效按下
    private String[] smallCircleTexts = {getResources().getString(R.string.Minigateway_Lighting_Always),
            getResources().getString(R.string.Minigateway_Lighting_Flowingwater),
            getResources().getString(R.string.Minigateway_Lighting_Flash),
            getResources().getString(R.string.Minigateway_Lighting_Slowflash)};

    public EmulateCircleView(Context context) {
        super(context);
    }

    public EmulateCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public EmulateCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        //防止边缘的锯齿
        mPaint.setAntiAlias(true);
        DisplayMetrics displayMetrics= getResources().getDisplayMetrics();
        bigRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 76, displayMetrics);
        smallRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
        distance = bigRadius + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, displayMetrics) + smallRadius;
        moveVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics);
        bigCircleTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, displayMetrics);
        smallCircleTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, displayMetrics);
        smallCircleStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, displayMetrics);
        outerRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, displayMetrics);
        int dp2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics);
        int dp1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics);
        pathEffect = new DashPathEffect(new float[] { dp2, dp1}, 0);
        buildAnimator();
        bigCircleCentrePoint = new Point();
        smallCircleCentrePoint1 = new Point();
        smallCircleCentrePoint2 = new Point();
        smallCircleCentrePoint3 = new Point();
        smallCircleCentrePoint4 = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = (int) ((distance * Math.cos(Math.PI * 30 / 180)) + bigRadius + smallRadius + moveVertical);
        int expandSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, expandSpec);
        width = getMeasuredWidth();
    }

    class Point {
        float x;
        float y;
    }

    private void buildAnimator() {
        mAnimator = ValueAnimator.ofFloat(-1, 1f);
        mAnimator.setDuration(3000);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animatedValue = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if(state == CLOSE){
            bigCircleCentrePoint.x = width / 2f;
            bigCircleCentrePoint.y = bigRadius;
//        }else if(state == OPEN){
//            bigCircleCentrePoint.x = width / 2f;
//            bigCircleCentrePoint.y = height / 2f - (height / 2f - bigRadius) * percent;
//        }
        float d = percent * distance;
        smallCircleCentrePoint1.x = (float) (width / 2f - (d * Math.sin(Math.PI * 75 / 180)));
        smallCircleCentrePoint1.y = (float) (bigRadius + (d * Math.cos(Math.PI * 75 / 180))) + moveVertical * animatedValue;
        smallCircleCentrePoint2.x = (float) (width / 2f - (d * Math.sin(Math.PI * 30 / 180)));
        smallCircleCentrePoint2.y = (float) (bigRadius + (d * Math.cos(Math.PI * 30 / 180))) + moveVertical * animatedValue;
        smallCircleCentrePoint3.x = (float) (width / 2f + (d * Math.sin(Math.PI * 30 / 180)));
        smallCircleCentrePoint3.y = (float) (bigRadius + (d * Math.cos(Math.PI * 30 / 180))) + moveVertical * animatedValue;
        smallCircleCentrePoint4.x = (float) (width / 2f + (d * Math.sin(Math.PI * 75 / 180)));
        smallCircleCentrePoint4.y = (float) (bigRadius + (d * Math.cos(Math.PI * 75 / 180))) + moveVertical * animatedValue;
        if(state != 0){
            if(d > bigRadius){
                //画第一个小圆
                mPaint.setColor(touchAction == SMALL_CIRCLE1_TOUCH_DOWN ? smallCirclePressColor : smallCircleColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setPathEffect(null);
                canvas.drawCircle(smallCircleCentrePoint1.x, smallCircleCentrePoint1.y, smallRadius, mPaint);
                mPaint.setStrokeWidth(smallCircleStrokeWidth);
                mPaint.setColor(bigCircleColor);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setPathEffect(pathEffect);
                canvas.drawCircle(smallCircleCentrePoint1.x, smallCircleCentrePoint1.y, smallRadius - smallCircleStrokeWidth, mPaint);

                mPaint.setColor(smallCircleTextColor);
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextSize(smallCircleTextSize);
                float percentTextWidth = mPaint.measureText(smallCircleTexts[0]);
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                float percentTextHeight = -fontMetrics.ascent;
                canvas.drawText(smallCircleTexts[0], smallCircleCentrePoint1.x - percentTextWidth / 2f, percentTextHeight / 2f + smallCircleCentrePoint1.y, mPaint);

                //画第二个小圆
                mPaint.setColor(touchAction == SMALL_CIRCLE2_TOUCH_DOWN ? smallCirclePressColor : smallCircleColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setPathEffect(null);
                canvas.drawCircle(smallCircleCentrePoint2.x, smallCircleCentrePoint2.y, smallRadius, mPaint);
                mPaint.setStrokeWidth(smallCircleStrokeWidth);
                mPaint.setColor(bigCircleColor);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setPathEffect(pathEffect);
                canvas.drawCircle(smallCircleCentrePoint2.x, smallCircleCentrePoint2.y, smallRadius - smallCircleStrokeWidth, mPaint);

                mPaint.setColor(smallCircleTextColor);
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextSize(smallCircleTextSize);
                percentTextWidth = mPaint.measureText(smallCircleTexts[1]);
                canvas.drawText(smallCircleTexts[1], smallCircleCentrePoint2.x - percentTextWidth / 2f, percentTextHeight / 2f + smallCircleCentrePoint2.y, mPaint);

                //画第三个小圆
                mPaint.setColor(touchAction == SMALL_CIRCLE3_TOUCH_DOWN ? smallCirclePressColor : smallCircleColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setPathEffect(null);
                canvas.drawCircle(smallCircleCentrePoint3.x, smallCircleCentrePoint3.y, smallRadius, mPaint);
                mPaint.setStrokeWidth(smallCircleStrokeWidth);
                mPaint.setColor(bigCircleColor);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setPathEffect(pathEffect);
                canvas.drawCircle(smallCircleCentrePoint3.x, smallCircleCentrePoint3.y, smallRadius - smallCircleStrokeWidth, mPaint);

                mPaint.setColor(smallCircleTextColor);
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextSize(smallCircleTextSize);
                percentTextWidth = mPaint.measureText(smallCircleTexts[2]);
                canvas.drawText(smallCircleTexts[2], smallCircleCentrePoint3.x - percentTextWidth / 2f, percentTextHeight / 2f + smallCircleCentrePoint3.y, mPaint);

                //画第四个小圆
                mPaint.setColor(touchAction == SMALL_CIRCLE4_TOUCH_DOWN ? smallCirclePressColor : smallCircleColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setPathEffect(null);
                canvas.drawCircle(smallCircleCentrePoint4.x, smallCircleCentrePoint4.y, smallRadius, mPaint);
                mPaint.setStrokeWidth(smallCircleStrokeWidth);
                mPaint.setColor(bigCircleColor);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setPathEffect(pathEffect);
                canvas.drawCircle(smallCircleCentrePoint4.x, smallCircleCentrePoint4.y, smallRadius - smallCircleStrokeWidth, mPaint);

                mPaint.setColor(smallCircleTextColor);
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextSize(smallCircleTextSize);
                percentTextWidth = mPaint.measureText(smallCircleTexts[3]);
                canvas.drawText(smallCircleTexts[3], smallCircleCentrePoint4.x - percentTextWidth / 2f, percentTextHeight / 2f + smallCircleCentrePoint4.y, mPaint);
            }

            //画外大圆
            mPaint.setColor(touchAction == BIG_CIRCLE_TOUCH_DOWN ? outerRingPressColor : outerRingColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setPathEffect(null);
            canvas.drawCircle(bigCircleCentrePoint.x, bigCircleCentrePoint.y, bigRadius, mPaint);

            //画大圆
            mPaint.setColor(touchAction == BIG_CIRCLE_TOUCH_DOWN ? bigCirclePressColor : bigCircleColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setPathEffect(null);
            canvas.drawCircle(bigCircleCentrePoint.x, bigCircleCentrePoint.y, bigRadius - outerRingWidth, mPaint);

            mPaint.setColor(Color.WHITE);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextSize(bigCircleTextSize);
            float percentTextWidth = mPaint.measureText(percentText);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float percentTextHeight = - fontMetrics.ascent;
            if((state == CLOSE && percent == 0) || (state == OPEN && percent == 1)){
                percentText = state == OPEN ? "OFF" : "ON";
                canvas.drawText(percentText, (width - percentTextWidth) / 2f, percentTextHeight / 2f + bigCircleCentrePoint.y, mPaint);
            }else{
                canvas.drawText(percentText, (width - percentTextWidth) / 2f, percentTextHeight / 2f + bigCircleCentrePoint.y, mPaint);
            }
        }else{
            //画外大圆
//            bigCircleCentrePoint.x = width / 2f;
//            bigCircleCentrePoint.y = height / 2f;
            mPaint.setColor(touchAction == BIG_CIRCLE_TOUCH_DOWN ? outerRingColor : outerRingPressColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setPathEffect(null);
            canvas.drawCircle(bigCircleCentrePoint.x, bigCircleCentrePoint.y, bigRadius, mPaint);

            //画大圆
            mPaint.setColor(touchAction == BIG_CIRCLE_TOUCH_DOWN ? bigCirclePressColor : bigCircleColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setPathEffect(null);
            canvas.drawCircle(bigCircleCentrePoint.x, bigCircleCentrePoint.y, bigRadius - outerRingWidth, mPaint);

            mPaint.setColor(Color.WHITE);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextSize(bigCircleTextSize);
            float percentTextWidth = mPaint.measureText(percentText);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float percentTextHeight = - fontMetrics.ascent;
            canvas.drawText(percentText, (width - percentTextWidth) / 2f, percentTextHeight / 2f + bigCircleCentrePoint.y, mPaint);
        }
    }

    public interface OnCircleClickListener {
        void onBigCircleClick();

        void onSmallCircle1Click();

        void onSmallCircle2Click();

        void onSmallCircle3Click();

        void onSmallCircle4Click();
    }

    public void setOnCircleClickListener(OnCircleClickListener onCircleClickListener) {
        mOnCircleClickListener = onCircleClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int result = 0;
        float xd = 0;
        float yd = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isEffective = false;
                // 两点间距离公式
                xd = Math.abs(event.getX() - bigCircleCentrePoint.x);
                yd = Math.abs(event.getY() - bigCircleCentrePoint.y);
                result = (int)Math.sqrt(xd * xd + yd * yd);
                if (result <= bigRadius) {
                    touchAction = BIG_CIRCLE_TOUCH_DOWN;
                    postInvalidate();
                    break;
                }

                if(state == OPEN){
                    xd = Math.abs(event.getX() - smallCircleCentrePoint1.x);
                    yd = Math.abs(event.getY() - smallCircleCentrePoint1.y);
                    result = (int)Math.sqrt(xd * xd + yd * yd);
                    if (result <= smallRadius) {
                        if(touchAction != SMALL_CIRCLE1_TOUCH_DOWN){
                            isEffective = true;
                            touchAction = SMALL_CIRCLE1_TOUCH_DOWN;
                            postInvalidate();
                        }
                        break;
                    }
                    xd = Math.abs(event.getX() - smallCircleCentrePoint2.x);
                    yd = Math.abs(event.getY() - smallCircleCentrePoint2.y);
                    result = (int)Math.sqrt(xd * xd + yd * yd);
                    if (result <= smallRadius) {
                        if(touchAction != SMALL_CIRCLE2_TOUCH_DOWN){
                            isEffective = true;
                            touchAction = SMALL_CIRCLE2_TOUCH_DOWN;
                            postInvalidate();
                        }
                        break;
                    }
                    xd = Math.abs(event.getX() - smallCircleCentrePoint3.x);
                    yd = Math.abs(event.getY() - smallCircleCentrePoint3.y);
                    result = (int)Math.sqrt(xd * xd + yd * yd);
                    if (result <= smallRadius) {
                        if(touchAction != SMALL_CIRCLE3_TOUCH_DOWN){
                            isEffective = true;
                            touchAction = SMALL_CIRCLE3_TOUCH_DOWN;
                            postInvalidate();
                        }
                        break;
                    }
                    xd = Math.abs(event.getX() - smallCircleCentrePoint4.x);
                    yd = Math.abs(event.getY() - smallCircleCentrePoint4.y);
                    result = (int)Math.sqrt(xd * xd + yd * yd);
                    if (result <= smallRadius) {
                        if(touchAction != SMALL_CIRCLE4_TOUCH_DOWN){
                            isEffective = true;
                            touchAction = SMALL_CIRCLE4_TOUCH_DOWN;
                            postInvalidate();
                        }
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                xd = Math.abs(event.getX() - bigCircleCentrePoint.x);
                yd = Math.abs(event.getY() - bigCircleCentrePoint.y);
                result = (int)Math.sqrt(xd * xd + yd * yd);
                if(touchAction == BIG_CIRCLE_TOUCH_DOWN){
                    touchAction = 0;
                    if (result <= bigRadius) {
//                        if (state == OPEN) {
//                            setSwitchState(CLOSE);
//                        } else if (state == CLOSE || state == 0) {
//                            setSwitchState(OPEN);
//                        }
                        if (mOnCircleClickListener != null) {
                            mOnCircleClickListener.onBigCircleClick();
                        }
                        break;
                    }
                }

                if(state == OPEN) {
                    xd = Math.abs(event.getX() - smallCircleCentrePoint1.x);
                    yd = Math.abs(event.getY() - smallCircleCentrePoint1.y);
                    result = (int)Math.sqrt(xd * xd + yd * yd);
                    if (result <= smallRadius && touchAction == SMALL_CIRCLE1_TOUCH_DOWN) {
                        if (mOnCircleClickListener != null) {
                            mOnCircleClickListener.onSmallCircle1Click();
                        }
                        break;
                    }
                    xd = Math.abs(event.getX() - smallCircleCentrePoint2.x);
                    yd = Math.abs(event.getY() - smallCircleCentrePoint2.y);
                    result = (int)Math.sqrt(xd * xd + yd * yd);
                    if (result <= smallRadius && touchAction == SMALL_CIRCLE2_TOUCH_DOWN) {
                        if (mOnCircleClickListener != null) {
                            mOnCircleClickListener.onSmallCircle2Click();
                        }
                        break;
                    }
                    xd = Math.abs(event.getX() - smallCircleCentrePoint3.x);
                    yd = Math.abs(event.getY() - smallCircleCentrePoint3.y);
                    result = (int)Math.sqrt(xd * xd + yd * yd);
                    if (result <= smallRadius && touchAction == SMALL_CIRCLE3_TOUCH_DOWN) {
                        if (mOnCircleClickListener != null) {
                            mOnCircleClickListener.onSmallCircle3Click();
                        }
                        break;
                    }
                    xd = Math.abs(event.getX() - smallCircleCentrePoint4.x);
                    yd = Math.abs(event.getY() - smallCircleCentrePoint4.y);
                    result = (int)Math.sqrt(xd * xd + yd * yd);
                    if (result <= smallRadius && touchAction == SMALL_CIRCLE4_TOUCH_DOWN) {
                        if (mOnCircleClickListener != null) {
                            mOnCircleClickListener.onSmallCircle4Click();
                        }
                        break;
                    }
                }
                if(isEffective){
                    touchAction = 0;
                }
                break;
//            case MotionEvent.ACTION_UP:
//                if(downType != 0){
//                    touchAction = 0;
//                    postInvalidate();
//                }
//                xd = Math.abs(event.getX() - bigCircleCentrePoint.x);
//                yd = Math.abs(event.getY() - bigCircleCentrePoint.y);
//                result = (int)Math.sqrt(xd * xd + yd * yd);
//                if (result <= bigRadius && downType == 1) {
//                    if (state == OPEN) {
//                        setSwitchState(CLOSE);
//                    } else if (state == CLOSE || state == 0) {
//                        setSwitchState(OPEN);
//                    }
//                    if (mOnCircleClickListener != null) {
//                        mOnCircleClickListener.onBigCircleClick();
//                    }
//                    break;
//                }
//                if(state == OPEN) {
//                    xd = Math.abs(event.getX() - smallCircleCentrePoint1.x);
//                    yd = Math.abs(event.getY() - smallCircleCentrePoint1.y);
//                    result = (int)Math.sqrt(xd * xd + yd * yd);
//                    if (result <= smallRadius && downType == 2) {
//                        if (mOnCircleClickListener != null) {
//                            mOnCircleClickListener.onSmallCircle1Click();
//                        }
//                        break;
//                    }
//                    xd = Math.abs(event.getX() - smallCircleCentrePoint2.x);
//                    yd = Math.abs(event.getY() - smallCircleCentrePoint2.y);
//                    result = (int)Math.sqrt(xd * xd + yd * yd);
//                    if (result <= smallRadius && downType == 3) {
//                        if (mOnCircleClickListener != null) {
//                            mOnCircleClickListener.onSmallCircle2Click();
//                        }
//                        break;
//                    }
//                    xd = Math.abs(event.getX() - smallCircleCentrePoint3.x);
//                    yd = Math.abs(event.getY() - smallCircleCentrePoint3.y);
//                    result = (int)Math.sqrt(xd * xd + yd * yd);
//                    if (result <= smallRadius && downType == 4) {
//                        if (mOnCircleClickListener != null) {
//                            mOnCircleClickListener.onSmallCircle3Click();
//                        }
//                        break;
//                    }
//                    xd = Math.abs(event.getX() - smallCircleCentrePoint4.x);
//                    yd = Math.abs(event.getY() - smallCircleCentrePoint4.y);
//                    result = (int)Math.sqrt(xd * xd + yd * yd);
//                    if (result <= smallRadius && downType == 5) {
//                        if (mOnCircleClickListener != null) {
//                            mOnCircleClickListener.onSmallCircle4Click();
//                        }
//                        break;
//                    }
//                }
//                break;
            case MotionEvent.ACTION_CANCEL:
                if(touchAction != 0){
                    touchAction = 0;
                    postInvalidate();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mAnimator != null){
            mAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (oPenCloseAnimation != null) {
            oPenCloseAnimation.cancel();
        }
        if(mAnimator != null){
            mAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }

    public void setSwitchDuration(int duration){
        this.switchDuration = duration;
    }

    public void setSwitchState(int state) {
        this.state = state;
        if (oPenCloseAnimation == null) {
            oPenCloseAnimation = new ValueAnimator();
            oPenCloseAnimation.setFloatValues(0, 1);
            oPenCloseAnimation.setDuration(switchDuration);
            oPenCloseAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            oPenCloseAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    percent = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
        }
        switch (state) {
            case OPEN:
                oPenCloseAnimation.start();
                if(mAnimator != null){
                    mAnimator.start();
                }
                break;
            case CLOSE:
                oPenCloseAnimation.reverse();
                if(mAnimator != null){
                    mAnimator.cancel();
                }
                animatedValue = 0;
                break;
        }
    }

    /**
     * 是否为打开状态
     */
    public boolean isOpen() {
        return state == OPEN;
    }

    /**
     * 选中哪一个 小圆
     */
    public int getCheckedItem() {
        return touchAction;
    }

    public void check(int checkType) {
        this.touchAction = checkType;
        postInvalidate();
//        switch (checkType) {
//            case CHECK_SMALL_CIRCLE1:
//                break;
//            case CHECK_SMALL_CIRCLE2:
//                break;
//            case CHECK_SMALL_CIRCLE3:
//                break;
//            case CHECK_SMALL_CIRCLE4:
//                break;
//        }
    }
}
