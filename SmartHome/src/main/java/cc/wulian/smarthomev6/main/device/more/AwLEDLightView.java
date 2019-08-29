package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

/**
 * Created by 上海滩小马哥 on 2018/02/23.
 */

public class AwLEDLightView extends FrameLayout implements IDeviceMore {

    private static final String CHANGE_MODE = "change_mode";
    private Context context;
    private AppCompatCheckBox checkBox;
    private Device device;
    String deviceID = "";
    String gwID = "";
    String state = "1";

    public AwLEDLightView(Context context, String deviceID, String gwID) {
        super(context);
        this.context = context;
        this.gwID = gwID;
        this.deviceID = deviceID;
        initView(context);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        if (device != null && device.isOnLine()) {
            getStateCmd();
        }
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_aw_led, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View layoutContent = rootView.findViewById(R.id.layoutContent);
        checkBox = (AppCompatCheckBox) rootView.findViewById(R.id.right_image);
        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(state, "1")) {
                    setStateCmd(false);
                } else {
                    setStateCmd(true);
                }
            }
        });
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device.mode == 2) {
            rootView.setEnabled(false);
            checkBox.setEnabled(false);
            layoutContent.setAlpha(0.54f);
        } else if (device.mode == 1) {
            rootView.setEnabled(true);
            checkBox.setEnabled(true);
            layoutContent.setAlpha(1f);
        }
    }

    private void getStateCmd() {
//        ProgressDialogManager.getDialogManager().showDialog(CHANGE_MODE, context, null, null, getResources().getInteger(R.integer.http_timeout));
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

    private void setStateCmd(boolean state) {
        ProgressDialogManager.getDialogManager().showDialog(CHANGE_MODE, context, null, null, getResources().getInteger(R.integer.http_timeout));
        JSONObject object = new JSONObject();
        object.put("cmd", "501");
        object.put("gwID", gwID);
        object.put("devID", deviceID);
        object.put("commandType", 1);
        if (state) {
            object.put("commandId", 0x01);
        } else {
            object.put("commandId", 0x00);
        }


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
                            if (attribute.attributeId == 0x8001) {
                                ProgressDialogManager.getDialogManager().dimissDialog(CHANGE_MODE, 0);
                                state = attribute.attributeValue;
                                if (TextUtils.equals(state, "1")) {
                                    checkBox.setChecked(true);
                                } else {
                                    checkBox.setChecked(false);
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
