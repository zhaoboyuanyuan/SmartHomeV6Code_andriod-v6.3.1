package cc.wulian.smarthomev6.main.device.eques;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.main.device.config.DeviceConfigFailActivity;
import cc.wulian.smarthomev6.main.device.config.DeviceConfigSuccessActivity;
import cc.wulian.smarthomev6.main.device.eques.bean.OnAddbdyReqBean;
import cc.wulian.smarthomev6.main.device.eques.bean.OnAddbdyResultBean;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * 作者: chao
 * 时间: 2017/6/5
 * 描述: 绑定等待界面
 * 联系方式: 805901025@qq.com
 */

public class EquesAddBindActivity extends BaseTitleActivity {
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private TimeCount timeCount;
    private ImageView ivWaitting;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_bind, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        ivWaitting = (ImageView) findViewById(R.id.iv_config_wifi_step_state);
    }

    @Override
    protected void initData() {
        timeCount = new TimeCount(120000, 1000);
        timeCount.start();

        animationDrawable = (AnimationDrawable) ivWaitting.getDrawable();
        animationDrawable.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeCount.cancel();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Connect_Device));
    }

    private void showConfirmDialog(final OnAddbdyReqBean bean){
        builder = new WLDialog.Builder(this);
        builder.setMessage(getString(R.string.Cateyemini_Adddevice_Alreadybound))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setPositiveButton(getString(R.string.Cateyemini_Adddevice_Continue))
                .setNegativeButton(getString(R.string.Cateyemini_Adddevice_cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesAckAddResponse(bean.reqId, 1);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                        startActivity(new Intent(EquesAddBindActivity.this, AddDeviceActivity.class));
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OnAddbdyReqBean bean) {
        if (bean.extra != null){
            showConfirmDialog(bean);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnAddbdyResult(OnAddbdyResultBean bean) {
        if (TextUtils.equals(bean.code, "4000")||
                TextUtils.equals(bean.code, "4407")){
            startActivity(new Intent(this, DeviceConfigSuccessActivity.class).putExtra("type", "camera_3"));
        }else if (TextUtils.equals(bean.code, "4002")||
                TextUtils.equals(bean.code, "4412")){
            startActivity(new Intent(this, DeviceConfigFailActivity.class).putExtra("type", "camera_3"));
        }
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            startActivity(new Intent(EquesAddBindActivity.this, DeviceConfigFailActivity.class).putExtra("type", "camera_3"));
        }
    }
}
