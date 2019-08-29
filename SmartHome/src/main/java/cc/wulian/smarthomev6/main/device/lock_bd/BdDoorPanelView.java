package cc.wulian.smarthomev6.main.device.lock_bd;

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
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.popupwindow.ChoosePanelPopupWindow;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/7/6
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    BdDoorPannelView
 */

public class BdDoorPanelView extends LinearLayout implements IDeviceMore {

    private static String TAG = BdDoorPanelView.class.getSimpleName();

    private TextView mTextPanel, mTextName;
    private ChoosePanelPopupWindow mPopupWindow;

    private String deviceID;
    private Device mDevice;
    private boolean isChoose = true;
    private boolean curChooseState=true;
    public BdDoorPanelView(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bd_door_pannel, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextPanel = (TextView) rootView.findViewById(R.id.item_device_more_panel);
        mTextName = (TextView) rootView.findViewById(R.id.item_device_more_name);

        mPopupWindow = new ChoosePanelPopupWindow(context, new ChoosePanelPopupWindow.OnPopupClickListener() {
            @Override
            public void onChoose(boolean isChoose) {
                curChooseState=isChoose;
                sendCmd_8016(isChoose ? "1" : "0");
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.showParent(v, isChoose);
            }
        });
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceId".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
                sendCmd_800A();
            }
        }

//        mTextName.setText(bean.name);
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        updateState();
    }

    private void updateState() {
        if (mDevice.isOnLine()) {
            mTextName.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
            mTextName.setTextColor(getResources().getColor(R.color.newStateText));
            setEnabled(false);
        }
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event == null || event.device == null) {
            return;
        }

        if (TextUtils.equals(event.device.devID, deviceID)) {
            dealData(event.device.data);
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            updateState();
        }
    }

    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray endpoints = object.optJSONArray("endpoints");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).optInt("attributeId");
            // 更新状态
            updateState(attributeId, attributeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void updateState(int attributeId, String attributeValue) {
        if (attributeValue.isEmpty()) {
            return;
        }

        if (attributeId == 0x8005 && attributeValue.startsWith("1")) {
            String ff = attributeValue.substring(11, 13);
            if (TextUtils.equals(ff, "01")) {
                // 安装了门扣板
                isChoose = true;
                mTextPanel.setText(R.string.Band_Firmware_Version_Update_Permit);
            } else {
                // 没安装
                isChoose = false;
                mTextPanel.setText(R.string.Band_Firmware_Version_Update_Refuse);
            }
        }else if(attributeId==0x8007){
            if(TextUtils.equals(attributeValue,"09")){/*设置是否安装门扣板：成功*/
                WLog.d(TAG,"设置是否安装门扣板：成功");
                isChoose=curChooseState;
                if(isChoose){
                    mTextPanel.setText(R.string.Band_Firmware_Version_Update_Permit);
                }else {
                    mTextPanel.setText(R.string.Band_Firmware_Version_Update_Refuse);
                }
//                sendCmd_800A();
            }else if(TextUtils.equals(attributeValue,"10")){/*设置是否安装门扣板：失败*/
                WLog.d(TAG,"设置是否安装门扣板：失败");
            }
        }
    }

    private void sendCmd_8016(String panel) {
        if (mDevice == null) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 257);
            object.put("commandType", 1);
            object.put("commandId", 0x8016);
            object.put("endpointNumber", 1);
            JSONArray array = new JSONArray();
            array.put(panel);
            object.put("parameter", array);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCmd_800A() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 257);
            object.put("commandType", 1);
            object.put("commandId", 0x800A);
            object.put("endpointNumber", 1);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
