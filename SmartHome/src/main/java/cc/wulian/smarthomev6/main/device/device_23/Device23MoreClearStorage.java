package cc.wulian.smarthomev6.main.device.device_23;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/7/20.
 * 内嵌一路开关，开启绑定/解绑模式
 */

public class Device23MoreClearStorage extends FrameLayout implements IDeviceMore {

    private View mRootView;
    private TextView mTextName;
    private Device mDevice;
    private String deviceID;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    public Device23MoreClearStorage(Context context) {
        super(context);
        initView(context);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        deviceID = bean.getValueByKey("deviceID");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        mTextName = (TextView) mRootView.findViewById(R.id.op_name);

        mRootView.setEnabled(mDevice.isOnLine());
        mTextName.setText(bean.name);
        mTextName.setTextColor(mDevice.isOnLine()
                ? getResources().getColor(R.color.newPrimaryText)
                : getResources().getColor(R.color.newStateText));
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GatewayConfigEvent event) {
        if (!TextUtils.equals(event.bean.d, deviceID)) {
            return;
        }

        if (event.bean.m == 4) {
            ToastUtil.single(R.string.Infraredtransponder_Clearcode_Success);
        }
    }

    private void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_device_23_clear_storage, null);
        addView(mRootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void sendCmd() {
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
    }

    private void deleteUeiCode() {
        StringBuilder epData = new StringBuilder("0A00020903");
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("devID", deviceID);
            object.put("gwID", mDevice.gwID);
            object.put("endpointNumber", 1);
            object.put("clusterId", 0x0F01);
            object.put("commandType", 1);
            object.put("commandId", 0x8001);
            JSONArray array = new JSONArray();
            array.put(epData.toString().toUpperCase());
            object.put("parameter", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }

    private void showDialog() {
        builder = new WLDialog.Builder(getContext());
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(R.string.Infraredrelay_More_Emptylibrary_Confirm)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        sendCmd();
                        deleteUeiCode();
                        dialog.dismiss();
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
}