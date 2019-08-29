package cc.wulian.smarthomev6.main.device.device_23;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UeiConfig;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.device.device_23.custom.CustomActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.AudioRemoteMainActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.ProjectorRemoteMainActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.TvRemoteMainActivity;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayConfigBean;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.SINGLE_CODE_PROJECTOR;
import static cc.wulian.smarthomev6.main.device.device_23.tv.TvRemoteMainActivity.MODE_LEARN;

public class ControllerMoreActivity extends BaseTitleActivity {

    private TextView mTextName;
    private String deviceID, mCreatedTime, mName, mType, singleCode;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private GatewayConfigBean mConfigBean;

    public static void start(Context context, String deviceID, String time, String name, String type) {
        Intent intent = new Intent(context, ControllerMoreActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("time", time);
        intent.putExtra("name", name);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static void start(Context context, String deviceID, String time, String name, String type, String singleCode) {
        Intent intent = new Intent(context, ControllerMoreActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("time", time);
        intent.putExtra("name", name);
        intent.putExtra("type", type);
        intent.putExtra("singleCode", singleCode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_more, true);
        EventBus.getDefault().register(this);

        deviceID = getIntent().getStringExtra("deviceID");
        mCreatedTime = getIntent().getStringExtra("time");
        mName = getIntent().getStringExtra("name");
        mType = getIntent().getStringExtra("type");
        singleCode = getIntent().getStringExtra("singleCode");
        mTextName = (TextView) findViewById(R.id.item_device_more_rename_name);

        if (!TextUtils.isEmpty(mName)) {
            mTextName.setText(mName);
        }

        mConfigBean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");
        List<UeiConfig> configs = JSON.parseArray(mConfigBean.v, UeiConfig.class);
        for (UeiConfig ueiConfig : configs) {
            if (TextUtils.equals(ueiConfig.time, mCreatedTime)) {
                if (TextUtils.isEmpty(ueiConfig.pick)) {
                    findViewById(R.id.item_device_more_edit).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.item_device_more_edit).setVisibility(View.GONE);
                }
                break;
            }
        }

        findViewById(R.id.item_device_more_delete).setOnClickListener(this);
        findViewById(R.id.item_device_more_rename).setOnClickListener(this);
        findViewById(R.id.item_device_more_edit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.item_device_more_rename:
                showChangeNameDialog();
                break;
            case R.id.item_device_more_delete:
                showDeleteDialog();
                break;
            case R.id.item_device_more_edit:
                Intent intent = new Intent();
                if (TextUtils.equals(mType, "CUSTOM")){
                    intent.setClass(ControllerMoreActivity.this, CustomActivity.class);
                }else if(TextUtils.equals(mType, "R,M")){
                    intent.setClass(ControllerMoreActivity.this, AudioRemoteMainActivity.class);
                }else if(TextUtils.equals(mType, "T")){
                    if(TextUtils.equals(singleCode, SINGLE_CODE_PROJECTOR)){
                        intent.setClass(ControllerMoreActivity.this, ProjectorRemoteMainActivity.class);
                    }else{
                        intent.setClass(ControllerMoreActivity.this, TvRemoteMainActivity.class);
                    }
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//刷新
                intent.putExtra("deviceID", deviceID);
                intent.putExtra("mode", "MODE_LEARN");
                intent.putExtra("type", mType);
                intent.putExtra("name", mName);
                intent.putExtra("time", mCreatedTime);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private void showChangeNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextText(mName)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (TextUtils.isEmpty(msg.trim())) {
                            ToastUtil.show(R.string.Mine_Rename_Empty);
                            return;
                        }

                        dialog.dismiss();
                        rename(msg.trim());
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void rename(String name) {
        GatewayConfigBean bean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.v)) {
                List<UeiConfig> configs = JSON.parseArray(bean.v, UeiConfig.class);
                for (UeiConfig config : configs) {
                    if (TextUtils.equals(mCreatedTime, config.time)) {
                        config.name = name;
                    }
                }

                sendCmd(configs);
            }
        }
    }

    private void showDeleteDialog() {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(R.string.Infraredtransponder_Airconditioner_Reconfirm)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        dialog.dismiss();
                        delete();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void delete() {
        GatewayConfigBean bean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.v)) {
                List<UeiConfig> newConfig = new ArrayList<>();
                List<UeiConfig> configs = JSON.parseArray(bean.v, UeiConfig.class);
                for (UeiConfig config : configs) {
                    if (!TextUtils.equals(mCreatedTime, config.time)) {
                        newConfig.add(config);
                    }
                }
                if (newConfig.size() == 0){
                    MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                            MQTTCmdHelper.createGatewayConfig(
                                    Preference.getPreferences().getCurrentGatewayID(),
                                    4,
                                    MainApplication.getApplication().getLocalInfo().appID,
                                    deviceID,
                                    "list",
                                    null,
                                    System.currentTimeMillis() + ""
                            ), MQTTManager.MODE_GATEWAY_FIRST
                    );
                }else {
                    sendCmd(newConfig);
                }
            }
        }
    }

    private void sendCmd(List<UeiConfig> newConfig) {
        String v = Base64.encodeToString(JSON.toJSONString(newConfig).getBytes(), Base64.NO_WRAP);
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                MQTTCmdHelper.createGatewayConfig(
                        Preference.getPreferences().getCurrentGatewayID(),
                        2,
                        MainApplication.getApplication().getLocalInfo().appID,
                        deviceID,
                        "list",
                        v,
                        System.currentTimeMillis() + ""
                ), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GatewayConfigEvent event) {
        WLog.i(TAG, "收到红外config了: " + event.bean);

        if (!TextUtils.equals(event.bean.d, deviceID)) {
            return;
        }

        GatewayConfigBean bean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.v)) {
                boolean hasMe = false;
                List<UeiConfig> configs = JSON.parseArray(bean.v, UeiConfig.class);
                for (UeiConfig config : configs) {
                    if (TextUtils.equals(mCreatedTime, config.time)) {
                        hasMe = true;
                        mName = config.getName();
                        mTextName.setText(mName);
                        break;
                    }
                }

                if (!hasMe) {
                    finish();
                }
            }
        }else {
            finish();
        }

    }

    @Override
    protected void initTitle() {
        super.initTitle();

        setToolBarTitle(R.string.Mine_Setts);
    }
}
