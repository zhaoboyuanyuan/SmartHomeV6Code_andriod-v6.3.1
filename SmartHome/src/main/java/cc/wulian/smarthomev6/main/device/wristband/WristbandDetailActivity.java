package cc.wulian.smarthomev6.main.device.wristband;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.home.scene.HouseKeeperActivity;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.support.customview.AnimationRing;
import cc.wulian.smarthomev6.support.customview.PercentProgressRing;

public class WristbandDetailActivity extends BaseActivity implements View.OnClickListener {

    private AnimationRing connectingBar;
    private LinearLayout ll_top;
    private LinearLayout ll_bottom;
    private View text_layout;
    private ImageView ring_icon;
    private View ring_layout;
    private View v_update_warn;
    private View rl_version_num;
    private View rl_smart_control;
    private ToggleButton tb_alarm_notice_switch;
    private PercentProgressRing percent_connecting_bar;
    private TextView top_state_tip_text;
    private TextView top_tip_text;
    private int height;
    private int width;
    private Handler mHandler = new Handler();
    private ValueAnimator mValueAnimator;
    private int currentState;//0详情1升级

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wristband_detail);
        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;
        initView();
    }

    public void initView(){
        connectingBar = (AnimationRing) findViewById(R.id.connecting_bar);
        ring_icon = (ImageView) findViewById(R.id.ring_icon);
        ring_layout = findViewById(R.id.ring_layout);
        ll_top = (LinearLayout) findViewById(R.id.top_layout);
        ll_bottom = (LinearLayout) findViewById(R.id.bottom_layout);
        v_update_warn = findViewById(R.id.update_warn_icon);
        rl_version_num = findViewById(R.id.version_num_rl);
        tb_alarm_notice_switch = (ToggleButton) findViewById(R.id.alarm_notice_switch);
        rl_smart_control = findViewById(R.id.smart_control_rl);
        text_layout = findViewById(R.id.text_layout);
        percent_connecting_bar = (PercentProgressRing) findViewById(R.id.percent_connecting_bar);
        top_state_tip_text = (TextView) findViewById(R.id.top_state_tip_text);
        top_tip_text = (TextView) findViewById(R.id.top_tip_text);
        connectingBar.setTimeout(4000);
        connectingBar.setState(AnimationRing.WAITING);
        connectingBar.setAnimatorListener(new AnimationRing.AnimatorListener() {
            @Override
            public void onTimeout() {
//                setToolBarTitleAndRightImg(R.string.Band_Details, R.drawable.icon_more);
                showInfoLayout();
            }

            @Override
            public void onEnd() {

            }
        });

        rl_version_num.setOnClickListener(this);
        rl_smart_control.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.version_num_rl:
                showUpdateLayout();
                break;
            case R.id.alarm_notice_switch:
                break;
            case R.id.smart_control_rl:
                intent = new Intent(this, HouseKeeperActivity.class);
                startActivity(intent);
                break;
            case R.id.img_right:
                intent = new Intent(this, WristbandMoreActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(currentState == 0){
            super.onBackPressed();
        }else{
//            setToolBarTitleAndRightImg(R.string.Band_Details, R.drawable.icon_more);
            showInfoLayout();
        }
    }

    @Override
    protected void onDestroy() {
        if(mValueAnimator != null){
            mValueAnimator.cancel();
        }
        super.onDestroy();
    }

    private void showInfoLayout(){
        currentState =0;
        if(mValueAnimator != null){
            return;
        }
        final int h = height * 7 / 13;
        mValueAnimator = ValueAnimator.ofInt(height, h);
        mValueAnimator.setDuration(800);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams mLayoutParams1 = ll_top.getLayoutParams();
                mLayoutParams1.height = (int)animation.getAnimatedValue();
                ll_top.setLayoutParams(mLayoutParams1);

                ViewGroup.LayoutParams mLayoutParams2 = ll_bottom.getLayoutParams();
                mLayoutParams2.height = height - (int)animation.getAnimatedValue();
                ll_bottom.setLayoutParams(mLayoutParams2);

            }
        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ll_bottom.setVisibility(View.VISIBLE);
                percent_connecting_bar.setVisibility(View.VISIBLE);
                percent_connecting_bar.setTextVisibility(true);
                percent_connecting_bar.setShowAnimation(true);
                percent_connecting_bar.setShowGround(true);
                percent_connecting_bar.setPercent(0);
                ring_icon.setVisibility(View.GONE);
                text_layout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                percent_connecting_bar.setPercent(0.732f);
                text_layout.setVisibility(View.GONE);
                FrameLayout.LayoutParams fLayoutParams = (FrameLayout.LayoutParams)ring_layout.getLayoutParams();
                fLayoutParams.gravity = Gravity.CENTER;
                ring_layout.setLayoutParams(fLayoutParams);
                mValueAnimator.cancel();
                mValueAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mValueAnimator.start();
    }

    private void showUpdateLayout(){
        currentState = 1;
        if(mValueAnimator != null){
            return;
        }
        mValueAnimator = ValueAnimator.ofInt(ll_bottom.getHeight(), 0);
        mValueAnimator.setDuration(800);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams mLayoutParams1 = ll_top.getLayoutParams();
                mLayoutParams1.height = height - (int)animation.getAnimatedValue();
                ll_top.setLayoutParams(mLayoutParams1);

                ViewGroup.LayoutParams mLayoutParams2 = ll_bottom.getLayoutParams();
                mLayoutParams2.height = (int)animation.getAnimatedValue();
                ll_bottom.setLayoutParams(mLayoutParams2);

            }
        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ll_bottom.setVisibility(View.VISIBLE);
                percent_connecting_bar.setVisibility(View.VISIBLE);
                percent_connecting_bar.setTextVisibility(false);
                percent_connecting_bar.setShowAnimation(false);
                percent_connecting_bar.setShowGround(false);
                percent_connecting_bar.setPercent(0);
                ring_icon.setVisibility(View.VISIBLE);
                ring_icon.setImageResource(R.drawable.icon_wristband_update);
                text_layout.setVisibility(View.VISIBLE);
                top_state_tip_text.setText(R.string.Band_Firmware_Version_Updating);
                top_tip_text.setText(R.string.Band_Firmware_Version_Updating_tips);
//                setToolBarTitle("");
                FrameLayout.LayoutParams fLayoutParams = (FrameLayout.LayoutParams)ring_layout.getLayoutParams();
                fLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                ring_layout.setLayoutParams(fLayoutParams);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                percent_connecting_bar.setPercent(0.53f);
                mValueAnimator.cancel();
                mValueAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mValueAnimator.start();
    }
}
