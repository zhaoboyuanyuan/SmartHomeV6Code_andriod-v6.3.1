package cc.wulian.smarthomev6.support.customview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by 上海滩小马哥 on 2018/01/23.
 * 安全狗扫描View
 */

public class SafeDogScanView extends View {

    private static final String TAG = "SafeDogScanView";
    private Paint scaneCirclePaint;
    private Animation animation;
    private Bitmap bitmap;
    private int width;
    private float cetX;
    private float cetY;
    private float initialRadius;
    private long duration = 3000;
    private long creatDuration = 1000;
    private long mLastCreateTime;
    private List<Circle> circles = new ArrayList<>();

    public SafeDogScanView(Context context) {
        super(context);
        init();
    }

    public SafeDogScanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SafeDogScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        scaneCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        scaneCirclePaint.setStyle(Paint.Style.FILL);
        scaneCirclePaint.setColor(Color.WHITE);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.safedog_home_scanning);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        cetX = w / 2;
        cetY = w / 2;
        initialRadius = width / 3;
        bitmap = Bitmap.createScaledBitmap(bitmap, width * 2 / 3, width * 2 / 3, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Iterator<Circle> iterator = circles.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            if (System.currentTimeMillis() - circle.creatTime < duration) {
                scaneCirclePaint.setAlpha(circle.getAlpha());
                canvas.drawCircle(cetX, cetY, circle.getCurrentRadius(), scaneCirclePaint);
            } else {
                iterator.remove();
            }
        }
        if (circles.size() > 0) {
            postInvalidateDelayed(10);
        }
        canvas.drawBitmap(bitmap, null, new Rect((int) (cetX - initialRadius), (int) (cetY - initialRadius), (int) (cetX + initialRadius), (int) (cetY + initialRadius)), null);

        startAnima();
    }

    public void startAnima() {
        if (!mIsRunning) {
            mIsRunning = true;
            mCreateCircle.run();
        }
        if (animation == null) {
            animation = new RotateAnimation(0f, -360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(duration/2);
            animation.setRepeatMode(Animation.RESTART);
            animation.setRepeatCount(-1);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            this.startAnimation(animation);
        }
    }

    public void stop() {
        this.clearAnimation();
        if (mIsRunning) {
            mIsRunning = false;
        }
    }

    private boolean mIsRunning = false;
    private Runnable mCreateCircle = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                newCircle();
                postDelayed(mCreateCircle, creatDuration);
            }
        }
    };

    private void newCircle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastCreateTime < creatDuration) {
            return;
        }
        Circle circle = new Circle();
        circles.add(circle);
        invalidate();
        mLastCreateTime = currentTime;
    }

    private class Circle {
        private long creatTime;

        public Circle() {
            this.creatTime = System.currentTimeMillis();
        }

        public int getAlpha() {
            float percent = (System.currentTimeMillis() - creatTime) * 1.0f / duration;
            return (int) ((1.0f - percent) * 255 * 0.15);
        }

        public float getCurrentRadius() {
            float percent = (System.currentTimeMillis() - creatTime) * 1.0f / duration;
//            WLog.d(TAG, "" + (initialRadius + percent * (cetX - initialRadius)));
            return initialRadius + percent * (cetX - initialRadius);
        }
    }
}
