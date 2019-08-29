package cc.wulian.smarthomev6.main.device.device_cj;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
import cc.wulian.smarthomev6.support.customview.BottomMenu;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2018/4/8
 * Cj地暖，室内温度校准
 */

public class CjCalibrationIndoorView extends LinearLayout implements IDeviceMore {

    private static String TAG = CjCalibrationIndoorView.class.getSimpleName();

    private TextView mTextPanel, mTextName;
    private BottomMenu mBottomMenu;

    private String deviceID;
    private Device mDevice;

    private ArrayList<String> list;
    private int tempIndex = 10;

    public CjCalibrationIndoorView(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bm_return_setts, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextPanel = (TextView) rootView.findViewById(R.id.bm_return_setts_text_panel);
        mTextName = (TextView) rootView.findViewById(R.id.bm_return_setts_text_sys_name);
        mTextName.setText(R.string.Device_Indoortpcorrect);

        list = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            list.add(tempIndexToTempValue(i) + "℃");
        }

        mBottomMenu = new BottomMenu(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                WLog.i(TAG, "onSure: " + mBottomMenu.getCurrent());
                mTextPanel.setText(mBottomMenu.getCurrentItem());
                sendCmd(mBottomMenu.getCurrent());
            }

            @Override
            public void onCancel() {

            }
        });
        mBottomMenu.setData(list);
        mBottomMenu.setTitle(getResources().getString(R.string.Device_Indoortpcorrect));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                mBottomMenu.show(v);
            }
        });
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceID".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            }
        }

        dealData(mDevice);
    }


    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event != null && TextUtils.equals(event.device.devID, deviceID)) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            dealData(event.device);
        }
    }

    private void dealData(Device device) {
        if (device == null) {
            return;
        }

        if (device.isOnLine()) {
            mTextName.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
            mTextName.setTextColor(getResources().getColor(R.color.newStateText));
            setEnabled(false);
        }

        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (cluster.clusterId == 0x0201) {
                    if (attribute.attributeId == 0x800C) {
                        try {
//                            tempIndex = hexStringToTempIndex(attribute.attributeValue);
//                            mTextPanel.setText(tempIndexToTempValue(tempIndex) + "℃");
                            mTextPanel.setText(attribute.attributeValue + ".0℃");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

//        mTextPanel.setText(tempIndexToTempValue(tempIndex) + "℃");
    }

    private float tempIndexToTempValue(int index) {
        return index * 1.0f - 5;
    }

    private String valueToHexString(int value) {
        String hex = Integer.toHexString(value);
        if (hex.length() > 2) {
            hex = hex.substring(hex.length() - 2);
        } else if (hex.length() == 1) {
            hex = "0" + hex;
        }
        return hex;
    }

    private int hexStringToTempValue(String hexString) {
        int value = Integer.parseInt(hexString, 16);
        if ((value & 0x80) > 0) {
            value = value - 0x100;
        }
        return value;
    }

    private int hexStringToTempIndex(String hexString) {
        return hexStringToTempValue(hexString) / 5 + 10;
    }

    private void sendCmd(int index) {
        JSONObject object = new JSONObject();
        try {
            float value = tempIndexToTempValue(index);
            int commandId = value < 0 ? 0x801B : 0x801A;
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 0x0201);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);
            JSONArray array = new JSONArray();
            array.put(valueToHexString((int) Math.abs(value)));
            object.put("parameter", array);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
