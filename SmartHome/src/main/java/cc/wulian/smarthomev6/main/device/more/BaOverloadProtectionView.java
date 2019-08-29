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

/**
 * Created by zbl on 2017/7/20.
 * 过载保护设置
 */

public class BaOverloadProtectionView extends FrameLayout implements IDeviceMore {

    private static final String LOAD_OVERLOAD_PROTECTION_VALUE = "load_overload_protection_value";

    private Context mContext;
    String deviceID = "";
    String gwID = "";
    private Device mDevice;
    private int powerValue;
    private TextView tv_overload_protection_value;

    public BaOverloadProtectionView(Context context, String deviceID, String gwID) {
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_ai_overload_protection, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv_overload_protection_value = (TextView) rootView.findViewById(R.id.item_overload_protection_value);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                float convertValue = ((float) powerValue) / 1000;
                BaSetOverloadProtectionActivity.start(mContext, deviceID, String.format("%1$.0f", (float) convertValue));
            }
        });
        View layoutContent = rootView.findViewById(R.id.layoutContent);
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if(mDevice.mode == 2){
            rootView.setEnabled(false);
            layoutContent.setAlpha(0.54f);
        }else if(mDevice.mode == 1){
            rootView.setEnabled(true);
            layoutContent.setAlpha(1f);
        }
        sendCmd(2);
//        initShowData();
    }

//    private void initShowData() {
//        if(mDevice.mode == 0){
//            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
//                @Override
//                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
//                    if (cluster.clusterId == 0x0006) {
//                        if (attribute.attributeId == 0x8004) {
//                            powerValue = Integer.parseInt(attribute.attributeValue);
//                            float convertValue;
//                            if(powerValue > 1000){
//                                convertValue = ((float) powerValue) / 1000;
//                                tv_overload_protection_value.setText(String.format("%1$.0fkw", (float) convertValue));
//                            }else{
//                                convertValue = powerValue;
//                                tv_overload_protection_value.setText(String.format("%1$.0fw", (float) convertValue));
//                            }
//                        }
//                    }
//                }
//            });
//        }else if(mDevice.mode == 1){
//            // 更新上线
//            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
//                @Override
//                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
//                    if (cluster.clusterId == 0x0006) {
//                        if (attribute.attributeId == 0x8004) {
//                            powerValue = Integer.parseInt(attribute.attributeValue);
//                            float convertValue;
//                            if(powerValue > 1000){
//                                convertValue = ((float) powerValue) / 1000;
//                                tv_overload_protection_value.setText(String.format("%1$.0fkw", (float) convertValue));
//                            }else{
//                                convertValue = powerValue;
//                                tv_overload_protection_value.setText(String.format("%1$.0fw", (float) convertValue));
//                            }
//                        }
//                    }
//                }
//            });
//        }
//    }


    private void sendCmd(int commandId) {
        ProgressDialogManager.getDialogManager().showDialog(LOAD_OVERLOAD_PROTECTION_VALUE, mContext, null, null, 10000);
        JSONObject object = new JSONObject();
        object.put("cmd", "501");
        object.put("gwID", gwID);
        object.put("devID", deviceID);
        object.put("clusterId", 0x0006);
        object.put("commandType", 1);
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
                            if (cluster.clusterId == 0x0006) {
                                if (attribute.attributeId == 0x8004) {
                                    powerValue = Integer.parseInt(attribute.attributeValue);
                                    float convertValue;
                                    if(powerValue > 1000){
                                        convertValue = ((float) powerValue) / 1000;
                                        tv_overload_protection_value.setText(String.format("%1$.0fkw", (float) convertValue));
                                    }else{
                                        convertValue = powerValue;
                                        tv_overload_protection_value.setText(String.format("%1$.0fw", (float) convertValue));
                                    }
                                    ProgressDialogManager.getDialogManager().dimissDialog(LOAD_OVERLOAD_PROTECTION_VALUE, 0);
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}