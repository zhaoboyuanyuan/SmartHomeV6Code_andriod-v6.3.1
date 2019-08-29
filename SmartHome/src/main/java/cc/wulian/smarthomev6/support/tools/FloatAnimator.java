package cc.wulian.smarthomev6.support.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by Veev on 2017/8/23
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    FloatAnimator
 */

public class FloatAnimator {

    private long mDuration = 1000;
    private View mTarget;
    private int mRangeVertical = 10, mRangeHorizontal = 10;
    private ValueAnimator mAnimator;
    private long mStartDelay = 0;

    private int x, y, lastX, lastY;
    private float mProgress;

    private FloatAnimator(View target) {
        mTarget = target;
    }

    public static FloatAnimator with(View target) {
        FloatAnimator anim = new FloatAnimator(target);
        return anim;
    }

    public FloatAnimator setDuration(long duration) {
        mDuration = duration;
        return this;
    }

    public FloatAnimator setRange(int rangeAll) {
        mRangeVertical = rangeAll;
        mRangeHorizontal = rangeAll;
        return this;
    }

    public FloatAnimator setStartDelay(long s) {
        mStartDelay = s;
        return this;
    }

    public FloatAnimator setRange(int rangeVertical, int rangeHorizontal) {
        mRangeVertical = rangeVertical;
        mRangeHorizontal = rangeHorizontal;
        return this;
    }

    private void buildAnimator() {
        mAnimator = ValueAnimator.ofFloat(0, 1f);
        mAnimator.setDuration(mDuration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

            }
        });
        lastY = mRangeVertical;
        lastX = mRangeHorizontal;
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                ObjectAnimator.ofFloat(mTarget, View.TRANSLATION_Y, 0, lastY).setDuration(mDuration / 2).start();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                x = 0 - lastX;
                y = 0 - lastY;
                ObjectAnimator.ofFloat(mTarget, View.TRANSLATION_Y, lastY, y).setDuration(mDuration).start();
                lastX = x;
                lastY = y;
            }
        });
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        if (mStartDelay > 0) {
            mAnimator.setStartDelay(mStartDelay);
        }
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
    }

    public void start() {
        buildAnimator();
        mAnimator.start();
    }

    public void cancel() {
        mAnimator.cancel();
    }
}
