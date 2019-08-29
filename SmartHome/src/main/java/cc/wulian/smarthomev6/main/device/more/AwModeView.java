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

/**
 * Created by zbl on 2018/5/31.
 * 电铃工作模式
 */

public class AwModeView extends FrameLayout implements IDeviceMore {

    private static final String GET_DATA = "get_aw_mode_data";
    private View rootView;
    private View layoutContent;
    private Context mContext;

    private String deviceID = "";
    private String gwID = "";
    private TextView tv_mode_value;

    public AwModeView(Context context, String deviceID, String gwID) {
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
        tv_mode_value = (TextView) rootView.findViewById(R.id.item_sleep_mode_value);
        ((TextView)rootView.findViewById(R.id.left_overload_protection)).setText(R.string.device_Aw_mode);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AwSetModeActivity.start(mContext, deviceID);
            }
        });
        layoutContent = rootView.findViewById(R.id.layoutContent);
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if(device!=null){
            updateShow(device);
        }
        sendCmd(0x07);
    }

    private void sendCmd(int commandId) {
//        ProgressDialogManager.getDialogManager().showDialog(GET_DATA, mContext, null, null, 10000);
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
                updateShow(event.device);
            }
        }
    }

    private void updateShow(Device device) {
        if(device.isOnLine()){
            rootView.setEnabled(true);
            layoutContent.setAlpha(1f);
        }else {
            rootView.setEnabled(false);
            layoutContent.setAlpha(0.54f);
        }
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (cluster.clusterId == 0x0006) {
                    if (attribute.attributeId == 0x0002) {
                        if (TextUtils.equals("1", attribute.attributeValue)) {
                            tv_mode_value.setText(R.string.device_Aw_mode_electric_bell);
                        } else {
                            tv_mode_value.setText(R.string.device_Aw_mode_Switch);
                        }
                    }
                }
            }
        });
    }
}