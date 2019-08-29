package cc.wulian.smarthomev6.main.device.eques;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.eques.icvss.core.module.user.BuddyType;

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
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * 作者: chao
 * 时间: 2017/6/5
 * 描述: 生成二维码界面
 * 联系方式: 805901025@qq.com
 */

public class EquesAddQRActivity extends BaseTitleActivity implements View.OnClickListener{
    private ImageView imQRCode;
    private Button btnNextStep;

    private WLDialog.Builder builder;
    private WLDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_qr, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Config_Barcode));
    }

    @Override
    protected void initData() {
        String wifiSsid = getIntent().getStringExtra("wifiSsid");
        String wifiPwd = getIntent().getStringExtra("wifiPwd");
        Bitmap bitmap = MainApplication.getApplication().getEquesApiUnit().getIcvss().equesCreateQrcode(wifiSsid, wifiPwd, ApiConstant.EQUES_KEYID, ApiConstant.getUserID(),
                BuddyType.TYPE_WIFI_DOOR_R22, 500);

        imQRCode.setImageBitmap(bitmap);
    }

    @Override
    protected void initView() {
        super.initView();
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        imQRCode = (ImageView) findViewById(R.id.iv_qr_code);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initListeners() {
        btnNextStep.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_next_step:
                startActivityForResult(new Intent(this, EquesAddBindActivity.class), 1);
                break;
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
                        startActivity(new Intent(EquesAddQRActivity.this, AddDeviceActivity.class));
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

}
