package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;


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
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

/**
 * created by huxc  on 2018/11/5.
 * func：Ai恢复断电后状态
 * email: hxc242313@qq.com
 */

public class AiRecoverStatusView extends FrameLayout implements IDeviceMore, CompoundButton.OnCheckedChangeListener {
    private static final String AI_RECOVER_CLOSE = "0";
    private static final String AI_RECOVER_KEEP = "1";
    private static final String SET = "SET";
    private Context mContext;
    private Device device;

    String deviceID = "";
    String gwID = "";

    private ToggleButton tbRecover;
    private View itemView;
    private RelativeLayout rlItemRecoverView;

    public AiRecoverStatusView(Context context, String deviceID, String gwID) {
        super(context);
        this.mContext = context;
        this.gwID = gwID;
        this.deviceID = deviceID;
        initView(context);
        initData();
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_ai_recover_status, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tbRecover = (ToggleButton) rootView.findViewById(R.id.tb_recover_status);
        itemView = (RelativeLayout) rootView.findViewById(R.id.rl_recover_status);
        rlItemRecoverView = (RelativeLayout) rootView.findViewById(R.id.item_ai_recover_status);
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        updateMode();
    }


    private void initData() {
        tbRecover.setOnCheckedChangeListener(this);
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        }

        updateMode();
        sendCmd(2, null);
    }

    private void updateMode() {
        itemView.setEnabled(device.isOnLine());
        itemView.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
    }

    private void sendCmd(int commandId, String parameter) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", gwID);
            object.put("devID", deviceID);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            if (parameter != null) {
                JSONArray array = new JSONArray();
                array.put(parameter);
                object.put("parameter", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }

    private void dealDevice(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (cluster.clusterId == 0x0b04) {
                    if (attribute.attributeId == 0x8006) {
                        updateViews(attribute.attributeValue);
                    }
                }
            }
        });
    }

    private void updateViews(String attributeValue) {
        ProgressDialogManager.getDialogManager().dimissDialog(SET,0);
        rlItemRecoverView.setVisibility(VISIBLE);
        if (TextUtils.equals(attributeValue, AI_RECOVER_CLOSE)) {
            tbRecover.setChecked(false);
        } else if (TextUtils.equals(attributeValue, AI_RECOVER_KEEP)) {
            tbRecover.setChecked(true);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode();
                } else if (event.device.mode == 1) {
                    // 更新上线
                    updateMode();
                } else if (event.device.mode == 0) {
                    updateMode();
                    dealDevice(event.device);
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.tb_recover_status:
                if (!compoundButton.isPressed()) {
                    return;
                }
                sendCmd(0x8015, isChecked ? AI_RECOVER_KEEP : AI_RECOVER_CLOSE);
                ProgressDialogManager.getDialogManager().showDialog(SET, mContext, null, null, 10000);
                break;
        }
    }
}
