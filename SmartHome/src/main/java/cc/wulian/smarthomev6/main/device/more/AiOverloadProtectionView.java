package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/7/20.
 * 过载保护设置
 */

public class AiOverloadProtectionView extends FrameLayout implements IDeviceMore {

    private static final String LOAD_OVERLOAD_PROTECTION_VALUE = "load_overload_protection_value";

    private Context mContext;
    private WLDialog dialog;

    String deviceID = "";
    String gwID = "";
    private TextView tv_overload_protection_value;

    public AiOverloadProtectionView(Context context, String deviceID, String gwID) {
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
                CharSequence value = tv_overload_protection_value.getText();
                if(value != null && value.length() > 0){
                    value = value.subSequence(0, value.length() - 2);
                }
                AiSetOverloadProtectionActivity.start(mContext, deviceID, value.toString());
            }
        });
        View layoutContent = rootView.findViewById(R.id.layoutContent);
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if(device.mode == 2){
            rootView.setEnabled(false);
            layoutContent.setAlpha(0.54f);
        }else if(device.mode == 1){
            rootView.setEnabled(true);
            layoutContent.setAlpha(1f);
        }
        sendCmd(0x8018);
    }

    private void sendCmd(int commandId) {
        ProgressDialogManager.getDialogManager().showDialog(LOAD_OVERLOAD_PROTECTION_VALUE, mContext, null, null, 10000);
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        object.put("cmd", "501");
        object.put("gwID", gwID);
        object.put("devID", deviceID);
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
                            if (cluster.clusterId == 0x0b04) {
                                if (attribute.attributeId == 0x8003) {
                                    int quantityValue = Integer.parseInt(attribute.attributeValue);
                                    float convertValue = ((float) quantityValue) / 1000;
                                    tv_overload_protection_value.setText(String.format("%1$.0fkw", (float) convertValue));
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