package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * Created by luzx on 2018/5/8.
 * 海信空调睡眠模式
 */

public class OkSleepModeView extends FrameLayout implements IDeviceMore {

    private static final String SET_SLEEP_MODE_VALUE = "set_sleep_mode_value";
    private View rootView;
    private View layoutContent;
    private Context mContext;
    private WLDialog dialog;

    private String deviceID = "";
    private String gwID = "";
    private String onOff = null;
    private String workMode = null;
    private TextView item_sleep_mode_value;

    public OkSleepModeView(Context context, String deviceID, String gwID) {
        super(context);
        this.mContext = context;
        this.gwID = gwID;
        this.deviceID = deviceID;
        initView(context);
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
        rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_ok_sleep_mode, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        item_sleep_mode_value = (TextView) rootView.findViewById(R.id.item_sleep_mode_value);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OkSetSleepModeActivity.start(mContext, deviceID);
            }
        });
        layoutContent = rootView.findViewById(R.id.layoutContent);
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if(device.mode == 2){
            rootView.setEnabled(false);
            layoutContent.setAlpha(0.54f);
        }else if(device.mode == 1 || device.mode == 0){
            updateShow(device);
        }
//        sendCmd(0x0201);
    }

//    private void sendCmd(int commandId) {
//        ProgressDialogManager.getDialogManager().showDialog(SET_SLEEP_MODE_VALUE, mContext, null, null, 10000);
//        JSONObject object = new JSONObject();
//        object.put("cmd", "501");
//        object.put("gwID", gwID);
//        object.put("devID", deviceID);
//        object.put("clusterId", 0x0201);
//        object.put("commandType", 1);
//        object.put("commandId", commandId);
//        MainApplication.getApplication()
//                .getMqttManager()
//                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
//    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                    rootView.setEnabled(false);
                    layoutContent.setAlpha(0.54f);
                } else if (event.device.mode == 1) {
                    // 更新上线
                    updateShow(event.device);
                } else if (event.device.mode == 0) {
                    updateShow(event.device);
                }
            }
        }
    }

    private void updateShow(Device device){
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (cluster.clusterId == 0x0201) {
                    if (attribute.attributeId == 0x8104) {
                        onOff = attribute.attributeValue;
                    } else if(attribute.attributeId == 0x8103){
                        workMode = attribute.attributeValue;
                    } else if(attribute.attributeId == 0x8102){
                        try{
                            int value = Integer.valueOf(attribute.attributeValue);
                            switch (value){
                                case 1:
                                    item_sleep_mode_value.setText(R.string.Device_Ok_Details_Sleep1);
                                    break;
                                case 2:
                                    item_sleep_mode_value.setText(R.string.Device_Ok_Details_Sleep2);
                                    break;
                                case 3:
                                    item_sleep_mode_value.setText(R.string.Device_Ok_Details_Sleep3);
                                    break;
                                case 4:
                                    item_sleep_mode_value.setText(R.string.Device_Ok_Details_Sleep4);
                                    break;
                                case 0:
                                    item_sleep_mode_value.setText(R.string.Device_Ok_Details_Sleep5);
                                    break;
                                default:
                                    item_sleep_mode_value.setText(R.string.Device_Ok_Details_Sleep5);
                                    break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        if("1".equals(onOff) && ("01".equals(workMode) || "02".equals(workMode) || "03".equals(workMode))){
            rootView.setEnabled(true);
            layoutContent.setAlpha(1f);
        }else{
            rootView.setEnabled(false);
            layoutContent.setAlpha(0.54f);
        }
    }
}