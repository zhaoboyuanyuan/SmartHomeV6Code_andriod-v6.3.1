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
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

/**
 * Created by zbl on 2017/7/20
 * 二路输出型翻译器02型，背光灯状态
 */

public class TranslatorBacklightStateView extends FrameLayout implements IDeviceMore {
    private static String TAG = TranslatorBacklightStateView.class.getSimpleName();
    private static final String CMD_SEND = "cmd_send_backlight";

    private Context context;
    private CheckBox cb_recover;

    private int backlightStatus = -1;

    public TranslatorBacklightStateView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    private String deviceID;
    private String gwID;

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bo_backlight_state, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cb_recover = (CheckBox) rootView.findViewById(R.id.cb_recover);
        cb_recover.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (backlightStatus != -1) {
                        sendSetValueCmd(backlightStatus == 0 ? 1 : 0);
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
        sendCmd();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    //查询背景灯状态
    private void sendCmd() {
        JSONObject object = new JSONObject();
        object.put("cmd", "501");
        object.put("gwID", gwID);
        object.put("devID", deviceID);
        object.put("commandType", 1);
        object.put("commandId", 0x02);

        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }

    private void sendSetValueCmd(int backlightStatus) {
        ProgressDialogManager.getDialogManager().showDialog(CMD_SEND, context, null, null, getResources().getInteger(R.integer.http_timeout));
        JSONObject object = new JSONObject();
        object.put("cmd", "501");
        object.put("gwID", gwID);
        object.put("devID", deviceID);
        object.put("commandType", 1);
        int commandId = backlightStatus == 1 ? 0x01 : 0x00;
        object.put("commandId", commandId);

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
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 0x0201 && attribute.attributeId == 0x8001) {
                                try {
                                    backlightStatus = Integer.parseInt(attribute.attributeValue);
                                    cb_recover.setChecked(!(backlightStatus == 0));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                ProgressDialogManager.getDialogManager().dimissDialog(CMD_SEND, 0);
                            }
                        }
                    });
                }
            }
        }
    }

}
