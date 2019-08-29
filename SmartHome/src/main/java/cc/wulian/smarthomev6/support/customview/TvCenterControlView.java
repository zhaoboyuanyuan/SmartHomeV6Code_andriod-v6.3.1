package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cc.wulian.smarthomev6.R;

/**
 * Created by 上海滩小马哥 on 2017/11/07.
 */

public class TvCenterControlView extends View {
    public static final String MODE_LEARN = "MODE_LEARN";
    public static final String MODE_CONTROL = "MODE_CONTROL";
    public static final String AREA_LEFT = "40";
    public static final String AREA_RIGHT = "41";
    public static final String AREA_TOP = "38";
    public static final String AREA_BOTTOM = "39";
    public static final String AREA_CENTER = "42";
    public boolean isRelease = false;
    private Paint mPaint;
    private Paint textPaint;

    private String mode = MODE_CONTROL;
    //边框的宽度
    private float circleStrokeWidth = 3;

    //圆心的X坐标
    private float centerX;

    //圆心的Y坐标
    private float centerY;

    // 文本的Y轴偏移值
    private float textOffsetY;

    //线条的颜色
    private int circleRadiusColor = 0xFF5a5d74;

    //线条的颜色(不可点击)
    private int circleRadiusColor_unenable = 0xFFd8dbe1;

    //点击背景颜色
    private int circlebackground = 0xFFd8dbe1;

    //45°正弦值
    private float mSin45 = (float) Math.sin(45 * Math.PI / 180);

    private float outerCircleRadius, innerCircleRadius;

    private AreaItem area_left = new AreaItem(AREA_LEFT);
    private AreaItem area_right = new AreaItem(AREA_RIGHT);
    private AreaItem area_top = new AreaItem(AREA_TOP);
    private AreaItem area_bottom = new AreaItem(AREA_BOTTOM);
    private AreaItem area_center = new AreaItem(AREA_CENTER);
    private AreaItem current_area;
    private AreaItem current_area_c;

    private Runnable cilckRunnable = new Runnable(){
        @Override
        public void run() {
            if (isRelease){
                removeCallbacks(longRunnable);
                if (current_area_c == null){
                    return;
                }
                onClickListener.onClick(current_area_c);
            }
        }
    };

    private Runnable longRunnable = new Runnable() {
        @Override
        public void run() {
            if (current_area_c == null){
                return;
            }
            if (TextUtils.equals(mode, MODE_LEARN)){
                if (current_area_c.isEnable()){
                    onLongClickListener.onLongClick(current_area_c.getTag());
                }
            }
        }
    };

    public interface OnClickListener {
        void onClick(AreaItem item);
    }

    OnClickListener onClickListener = null;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnLongClickListener {
        void onLongClick(String tag);
    }

    OnLongClickListener onLongClickListener = null;

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public TvCenterControlView(Context context) {
        super(context);
        inintPaint();
    }

    public TvCenterControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inintPaint();
    }

    public TvCenterControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inintPaint();
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
        invalidate();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        int w = getWidth();
//        int h = getHeight();
//        centerX = w / 2;
//        centerY = h / 2;
//        outerCircleRadius = centerX - circleStrokeWidth;
//        innerCircleRadius = centerX / 3;
//
//        textOffsetY = (textPaint.descent() + textPaint.ascent()) / 2;
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = w / 2;
        centerY = h / 2;
        outerCircleRadius = centerX - circleStrokeWidth;
        innerCircleRadius = centerX / 3;

        textOffsetY = (textPaint.descent() + textPaint.ascent()) / 2;
    }

    private void inintPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        textPaint.setColor(circlebackground);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void setPaintDefault() {
        mPaint.reset();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
    }

    private void setPaintDash() {
        mPaint.reset();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        mPaint.setPathEffect(effects);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画圆
        drawCircle(canvas);
        //画直线
//        drawLine(canvas);
        //判断是否可用,画边框
        drawEnable(canvas);
        //画按下后背景
        drawOnClikColor(canvas, current_area);
        //画图案
        drawImage(canvas);
    }

    //画圆
    private void drawCircle(Canvas canvas) {
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, outerCircleRadius, mPaint);
        canvas.drawCircle(centerX, centerY, innerCircleRadius, mPaint);

        if (TextUtils.equals(mode, MODE_CONTROL)) {
            setPaintDefault();
        } else if (TextUtils.equals(mode, MODE_LEARN)) {
            setPaintDash();
        }
        mPaint.setColor(circleRadiusColor_unenable);
        mPaint.setStrokeWidth(circleStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX, centerY, outerCircleRadius, mPaint);
        canvas.drawCircle(centerX, centerY, innerCircleRadius, mPaint);
    }

    //画直线
    private void drawLine(Canvas canvas) {
        if (TextUtils.equals(mode, MODE_CONTROL)) {
            setPaintDefault();
        } else if (TextUtils.equals(mode, MODE_LEARN)) {
            setPaintDash();
        }
        mPaint.setColor(circleRadiusColor);
        mPaint.setStrokeWidth(circleStrokeWidth);

        canvas.drawLine(centerX - (mSin45 * outerCircleRadius), centerY - (mSin45 * outerCircleRadius),
                centerX - (mSin45 * innerCircleRadius), centerY - (mSin45 * innerCircleRadius), mPaint);
        canvas.drawLine(centerX - (mSin45 * outerCircleRadius), centerY + (mSin45 * outerCircleRadius),
                centerX - (mSin45 * innerCircleRadius), centerY + (mSin45 * innerCircleRadius), mPaint);
        canvas.drawLine(centerX + (mSin45 * outerCircleRadius), centerY + (mSin45 * outerCircleRadius),
                centerX + (mSin45 * innerCircleRadius), centerY + (mSin45 * innerCircleRadius), mPaint);
        canvas.drawLine(centerX + (mSin45 * outerCircleRadius), centerY - (mSin45 * outerCircleRadius),
                centerX + (mSin45 * innerCircleRadius), centerY - (mSin45 * innerCircleRadius), mPaint);
    }

    //画图案
    private void drawImage(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tv_remote_dict);
        Bitmap bitmap_unEnable = BitmapFactory.decodeResource(getResources(), R.drawable.tv_remote_dict_unenable);
        Matrix matrix = new Matrix();
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();

        if (area_left.isEnable()) {
            matrix.postTranslate((outerCircleRadius - innerCircleRadius) / 2 - imageWidth / 2, centerY - imageHeight / 2);
            canvas.drawBitmap(bitmap, matrix, null);
        } else {
            matrix.postTranslate((outerCircleRadius - innerCircleRadius) / 2 - imageWidth / 2, centerY - imageHeight / 2);
            canvas.drawBitmap(bitmap_unEnable, matrix, null);
        }
        if (area_top.isEnable()) {
            matrix.postRotate(90, centerX, centerY);
            canvas.drawBitmap(bitmap, matrix, null);
        } else {
            matrix.postRotate(90, centerX, centerY);
            canvas.drawBitmap(bitmap_unEnable, matrix, null);
        }
        if (area_right.isEnable()) {
            matrix.postRotate(90, centerX, centerY);
            canvas.drawBitmap(bitmap, matrix, null);
        } else {
            matrix.postRotate(90, centerX, centerY);
            canvas.drawBitmap(bitmap_unEnable, matrix, null);
        }
        if (area_bottom.isEnable()) {
            matrix.postRotate(90, centerX, centerY);
            canvas.drawBitmap(bitmap, matrix, null);
        } else {
            matrix.postRotate(90, centerX, centerY);
            canvas.drawBitmap(bitmap_unEnable, matrix, null);
        }
        if (area_center.isEnable()) {
            textPaint.setColor(circleRadiusColor);
            canvas.drawText("OK", centerX, centerY - textOffsetY, textPaint);
        } else {
            textPaint.setColor(circleRadiusColor_unenable);
            canvas.drawText("OK", centerX, centerY - textOffsetY, textPaint);
        }
    }

    //判断是否可用
    private void drawEnable(Canvas canvas) {
        setPaintDefault();
        mPaint.setColor(circleRadiusColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(circleStrokeWidth);

        if (area_top.isEnable()) {
            canvas.drawArc(new RectF(centerX - outerCircleRadius, centerY - outerCircleRadius,
                    centerX + outerCircleRadius, centerY
                    + outerCircleRadius), 225, 90, false, mPaint);
        }
        if (area_right.isEnable()) {
            canvas.drawArc(new RectF(centerX - outerCircleRadius, centerY - outerCircleRadius,
                    centerX + outerCircleRadius, centerY
                    + outerCircleRadius), 315, 90, false, mPaint);
        }
        if (area_bottom.isEnable()) {
            canvas.drawArc(new RectF(centerX - outerCircleRadius, centerY - outerCircleRadius,
                    centerX + outerCircleRadius, centerY
                    + outerCircleRadius), 45, 90, false, mPaint);
        }
        if (area_left.isEnable()) {
            canvas.drawArc(new RectF(centerX - outerCircleRadius, centerY - outerCircleRadius,
                    centerX + outerCircleRadius, centerY
                    + outerCircleRadius), 135, 90, false, mPaint);
        }
        if (area_center.isEnable()) {
            canvas.drawCircle(centerX, centerY, innerCircleRadius, mPaint);
        }
    }

    //画点击区域
    private void drawOnClikColor(Canvas canvas, AreaItem area) {
        if (area == null || (TextUtils.equals(mode, MODE_CONTROL) && !area.isEnable())) {
            return;
        }
        //设置点击之后的颜色
        setPaintDefault();

        RadialGradient radialGradient = new RadialGradient(centerX, centerY, outerCircleRadius, Color.WHITE, circlebackground, Shader.TileMode.CLAMP);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShader(radialGradient);
        mPaint.setStrokeWidth(outerCircleRadius - innerCircleRadius - circleStrokeWidth * 2);

        switch (area.getArea()) {
            case AREA_TOP:
                canvas.drawArc(new RectF(centerX - (outerCircleRadius - innerCircleRadius) - circleStrokeWidth, centerY - (outerCircleRadius - innerCircleRadius) - circleStrokeWidth,
                        centerX + (outerCircleRadius - innerCircleRadius) + circleStrokeWidth, centerY
                        + (outerCircleRadius - innerCircleRadius) + circleStrokeWidth), 225, 90, false, mPaint);
                break;
            case AREA_RIGHT:
                canvas.drawArc(new RectF(centerX - (outerCircleRadius - innerCircleRadius) - circleStrokeWidth, centerY - (outerCircleRadius - innerCircleRadius) - circleStrokeWidth,
                        centerX + (outerCircleRadius - innerCircleRadius) + circleStrokeWidth, centerY
                        + (outerCircleRadius - innerCircleRadius) + circleStrokeWidth), 315, 90, false, mPaint);
                break;
            case AREA_BOTTOM:
                canvas.drawArc(new RectF(centerX - (outerCircleRadius - innerCircleRadius) - circleStrokeWidth, centerY - (outerCircleRadius - innerCircleRadius) - circleStrokeWidth,
                        centerX + (outerCircleRadius - innerCircleRadius) + circleStrokeWidth, centerY
                        + (outerCircleRadius - innerCircleRadius) + circleStrokeWidth), 45, 90, false, mPaint);
                break;
            case AREA_LEFT:
                canvas.drawArc(new RectF(centerX - (outerCircleRadius - innerCircleRadius) - circleStrokeWidth, centerY - (outerCircleRadius - innerCircleRadius) - circleStrokeWidth,
                        centerX + (outerCircleRadius - innerCircleRadius) + circleStrokeWidth, centerY
                        + (outerCircleRadius - innerCircleRadius) + circleStrokeWidth), 135, 90, false, mPaint);
                break;
            case AREA_CENTER:
                mPaint.setShader(null);
                mPaint.setColor(circlebackground);
                mPaint.setStrokeWidth(0);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(centerX, centerY, innerCircleRadius, mPaint);
                break;
            default:
                break;
        }

    }

    //按下时的X，Y坐标
    float mDownX, mDownY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                current_area = judgeArea(mDownX, mDownY);
                current_area_c = current_area;
                if (current_area == null) {
                    break;
                }
                if (TextUtils.equals(mode, MODE_CONTROL)) {
                    isRelease = true;
                    invalidate();
                    post(cilckRunnable);
                } else if (TextUtils.equals(mode, MODE_LEARN)) {
                    isRelease = false;
                    invalidate();
                    postDelayed(cilckRunnable, 300);
                    postDelayed(longRunnable, 1000);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (TextUtils.equals(mode, MODE_LEARN)) {
                    removeCallbacks(longRunnable);
                }
                isRelease = true;
                current_area = null;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    //判断区域
    public AreaItem judgeArea(float x, float y) {
        //判断是是否在大圆内
        if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) <= outerCircleRadius * outerCircleRadius) {
            //判断是否在小圆内
            if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) > innerCircleRadius * innerCircleRadius) {
                x = x - centerX;
                y = y - centerY;
                float tan = y / x;
                if ((tan > Math.tan(45 * Math.PI / 180) && tan < Integer.MAX_VALUE && x < 0 && y < 0) ||
                        (tan > Integer.MIN_VALUE && tan < -Math.tan(45 * Math.PI / 180) && x > 0 && y < 0)) {
                    return area_top;
                } else if ((tan > 0 && tan < Math.tan(45 * Math.PI / 180) && x > 0 && y > 0) ||
                        (tan > -Math.tan(45 * Math.PI / 180) && tan < 0 && x > 0 && y < 0)) {
                    return area_right;
                } else if ((tan < -Math.tan(45 * Math.PI / 180) && tan > Integer.MIN_VALUE && x < 0 && y > 0) ||
                        (tan > Math.tan(45 * Math.PI / 180) && tan < Integer.MAX_VALUE && x > 0 && y > 0)) {
                    return area_bottom;
                } else if ((tan > 0 && tan < Math.tan(45 * Math.PI / 180) && x < 0 && y < 0) ||
                        (tan < 0 && tan > -Math.tan(45 * Math.PI / 180) && x < 0 && y > 0)) {
                    return area_left;
                }
            } else {
                return area_center;
            }
        } else {
            return null;
        }
        return null;
    }

    public void updateView(String tag, boolean enable) {
        switch (tag) {
            case AREA_LEFT:
                area_left.setEnable(enable);
                break;
            case AREA_RIGHT:
                area_right.setEnable(enable);
                break;
            case AREA_TOP:
                area_top.setEnable(enable);
                break;
            case AREA_BOTTOM:
                area_bottom.setEnable(enable);
                break;
            case AREA_CENTER:
                area_center.setEnable(enable);
                break;
            default:
                break;
        }
        invalidate();
    }

    public void updateViews(@Nullable boolean left, @Nullable boolean top, @Nullable boolean right, @Nullable boolean bottom, @Nullable boolean center) {
        area_left.setEnable(left);
        area_right.setEnable(right);
        area_top.setEnable(top);
        area_bottom.setEnable(bottom);
        area_center.setEnable(center);
        invalidate();
    }

    public class AreaItem {
        private boolean enable;
        private String tag;

        public AreaItem(String tag) {
            this.tag = tag;
            enable = false;
        }

        public AreaItem(String tag, boolean enable) {
            this.tag = tag;
            this.enable = enable;
        }

        public String getArea() {
            return tag;
        }

        public void setArea(String tag) {
            this.tag = tag;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

}
