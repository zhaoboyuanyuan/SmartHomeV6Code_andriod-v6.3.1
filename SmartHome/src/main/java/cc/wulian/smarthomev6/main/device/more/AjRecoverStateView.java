package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

/**
 * Created by zbl on 2017/7/20
 * 内嵌一路开关，恢复断电前状态
 */

public class AjRecoverStateView extends FrameLayout implements IDeviceMore {
    private static String TAG = AjRecoverStateView.class.getSimpleName();
    private static final String CMD_RECOVER_STATE = "cmd_recover_state";

    private Context context;
    private CheckBox cb_recover;

    private int recoverState = -1;

    public AjRecoverStateView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    private String deviceID;
    private String gwID;

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_aj_recover_state, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cb_recover = (CheckBox) rootView.findViewById(R.id.cb_recover);
        cb_recover.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if (recoverState != -1) {
                        sendSetValueCmd(recoverState == 0 ? 1 : 0);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        gwID = bean.getValueByKey("gwID");
        deviceID = bean.getValueByKey("deviceID");
        sendCmd(2);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void sendCmd(int cmd) {
        JSONObject object = new JSONObject();
        object.put("cmd", "501");
        object.put("gwID", gwID);
        object.put("devID", deviceID);
        object.put("commandType", 1);
        object.put("commandId", cmd);

        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }

    private void sendSetValueCmd(int recoverState) {
        ProgressDialogManager.getDialogManager().showDialog(CMD_RECOVER_STATE, context, null, null, getResources().getInteger(R.integer.http_timeout));
        JSONObject object = new JSONObject();
        object.put("cmd", "501");
        object.put("gwID", gwID);
        object.put("devID", deviceID);
        object.put("commandType", 1);
        object.put("commandId", 0x8015);

        JSONArray parameter = new JSONArray();
        parameter.add(recoverState + "");
        object.put("parameter", parameter);

        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                } else if (event.device.mode == 1) {
                    // 更新上线
                } else if (event.device.mode == 0) {
                    String powerInfo = null;
                    JSONObject object = JSON.parseObject(event.device.data);
                    JSONArray endpoints = object.getJSONArray("endpoints");
                    JSONArray clusters = endpoints.getJSONObject(0).getJSONArray("clusters");
                    for (int i = 0; i < clusters.size(); i++) {
                        JSONObject cluster = clusters.getJSONObject(i);
                        if (cluster.getInteger("clusterId") == 0x0b04) {//电量信息
                            JSONArray attributes = cluster.getJSONArray("attributes");
                            for (int j = 0; j < attributes.size(); j++) {
                                JSONObject attribute = attributes.getJSONObject(j);
                                if (attribute.getInteger("attributeId") == 0x8001) {
                                    powerInfo = attribute.getString("attributeValue");
                                    if (powerInfo != null) {
                                        updatePowerInfo(powerInfo);
                                        break;
                                    }
                                } else if (attribute.getInteger("attributeId") == 0x8006) {
                                    try {
                                        String valueText = attribute.getString("attributeValue");
                                        recoverState = Integer.parseInt(valueText, 16);
                                        cb_recover.setChecked(!(recoverState == 0));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    ProgressDialogManager.getDialogManager().dimissDialog(CMD_RECOVER_STATE, 0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新断电恢复开关状态
     */
    private void updatePowerInfo(String value) {
        if (value != null && value.length() >= 20) {
            try {
                String valueText = value.substring(18, 20);
                recoverState = Integer.parseInt(valueText, 16);
                cb_recover.setChecked(!(recoverState == 0));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

}
