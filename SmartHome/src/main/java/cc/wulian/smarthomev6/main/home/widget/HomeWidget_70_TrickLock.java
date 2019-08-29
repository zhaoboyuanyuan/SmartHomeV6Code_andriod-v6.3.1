package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/5/8
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 密码锁 首页widget
 */

public class HomeWidget_70_TrickLock extends RelativeLayout implements IWidgetLifeCycle {

    private static final String TAG = HomeWidget_70_TrickLock.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textArea, textState, toast;
    private EditText editInput;
    private ImageView imageOpen;
    private RelativeLayout title;

    private int mode;

    public HomeWidget_70_TrickLock(Context context) {
        super(context);
        initView(context);
    }

    public HomeWidget_70_TrickLock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        mode = mDevice.mode;
        // 设置在线离线的状态
        updateMode();

        dealDevice(mDevice);

        setName();
        setRoomName();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void setName() {
        if (mDevice == null) {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        textArea.setText(areaName);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_70_trick_lock, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));//DisplayUtil.dip2Pix(context, 300)));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        toast = (TextView) rootView.findViewById(R.id.trick_lock_text_toast);
        editInput = (EditText) rootView.findViewById(R.id.trick_lock_edit_input);
        imageOpen = (ImageView) rootView.findViewById(R.id.trick_lock_image_open);

        imageOpen.setEnabled(false);

        editInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = editInput.getText().toString().trim().length();
                if (length < 4 || length > 9) {
                    imageOpen.setImageResource(R.drawable.ic_lock_open_nor);
//                    imageOpen.setBackgroundResource(R.drawable.shape_home_widget_lock_nor);
                    imageOpen.setEnabled(false);
                } else {
                    imageOpen.setImageResource(R.drawable.ic_lock_open);
                    if (mode == 1) {
//                        imageOpen.setBackgroundResource(R.drawable.shape_home_widget_lock);
                    }
                    imageOpen.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = editInput.getText().toString().trim().length();
                if (length >=4 && length <= 9) {
                    sendCmd(editInput.getText().toString().trim());
                }
            }
        });

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        switch (mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                textState.setText(R.string.Device_Online);
                textState.setTextColor(getResources().getColor(R.color.colorPrimary));
                editInput.setEnabled(true);
                editInput.setBackgroundResource(R.drawable.shape_home_widget_lock);
                break;
            case 2:
                // 离线
                textState.setText(R.string.Device_Offline);
                textState.setTextColor(getResources().getColor(R.color.newStateText));
//                imageOpen.setBackgroundResource(R.drawable.shape_home_widget_lock_nor);
                editInput.setBackgroundResource(R.drawable.shape_home_widget_lock_nor);
                editInput.setText("");
                editInput.setEnabled(false);
                imageOpen.setEnabled(false);
                break;
            case 3:
                break;
        }
    }

    /**
     * 解析设备数据
     */
    private void dealDevice(Device device) {
        mode = device.mode;
        updateMode();
        if (device.mode == 3) {
            // 设备删除
        } else if (device.mode == 2) {
            // 设备离线
        } else if (device.mode == 1) {
            // 更新上线
            dealData(device.data);
        } else if (device.mode == 0) {
            dealData(device.data);
        }
    }

    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray endpoints = object.getJSONArray("endpoints");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).getString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).getInt("attributeId");
            // 更新状态
            updateState(attributeId, attributeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toast(@StringRes int sss) {
        toast.setText(sss);
        toast.setVisibility(VISIBLE);
        toast.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.setVisibility(INVISIBLE);
            }
        }, 2000);
    }

    private void updateState(int attributeId, String attributeValue) {
        WLog.i(TAG, "updateState: attributeId-" + attributeId + ", attributeValue-"+attributeValue);
        if (attributeValue.isEmpty()) {
            return;
        }
        int value = 0;
        try {
            value = Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        WLog.i(TAG, attributeId + " " + attributeValue);

        switch (attributeId) {
            case 0x0000:
                handleAttribute_0(value);
                break;
            case 0x8001:
                handleAttribute_8001(value);
                break;
            case 0x8002:
                handleAttribute_8002(value);
                break;
            case 0x8003:
                handleAttribute_8003(value);
                break;
            case 0x8004:
                handleAttribute_8004(value);
                break;
        }
    }

    private void handleAttribute_0(int value) {
        switch (value) {
            case 1:
                // 上锁
                break;
            case 2:
                // 解锁
                break;
            case 3:
                // 上保险
                break;
            case 4:
                // 接触保险
                break;
            case 5:
                // 接触反锁
                break;
            case 6:
                // 反锁
                break;
        }
    }

    private void handleAttribute_8001(int value) {
        switch (value) {
            case 1:
                // 入侵报警
                break;
            case 2:
                // 报警解除
                break;
            case 3:
                // 破坏报警
                break;
            case 4:
                // 密码连续出错
                break;
            case 5:
                // 欠压报警
                break;
        }
    }

    private void handleAttribute_8002(int value) {
        toast(R.string.Home_Widget_Lock_Opened);
        // 清空输入框
        editInput.setText("");
    }

    private void handleAttribute_8003(int value) {
        switch (value) {
            case 1:
                // 密码校验成功
                break;
            case 2:
                // 密码校验失败
                toast(R.string.Home_Widget_Password_Error);
                // 清空输入框
                editInput.setText("");
                break;
            case 3:
                // 强制上锁
                break;
            case 4:
                // 自动上锁
                break;
            case 5:
                // 登记密码
                break;
        }
    }

    private void handleAttribute_8004(int value) {
        switch (value) {
            case 1:
                // 开锁
                break;
        }
    }

    private long lastSendTime = -1;

    private void sendCmd(String pwd) {
        // 消抖
        // 300ms
        long sendTime = System.currentTimeMillis();
        if (sendTime - lastSendTime < 300) {
            return;
        }
        lastSendTime = sendTime;

        if (mode != 0 && mode != 1) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 257);
            object.put("commandType", 1);
            object.put("commandId", 32772);
            object.put("endpointNumber", 1);
            JSONArray array = new JSONArray();
            array.put(pwd);
            object.put("parameter", array);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);

            MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                    MQTTCmdHelper.createGatewayConfig(
                            Preference.getPreferences().getCurrentGatewayID(),
                            3,
                            MainApplication.getApplication().getLocalInfo().appID,
                            mDevice.devID,
                            null,
                            null,
                            null
                    ), MQTTManager.MODE_GATEWAY_FIRST
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                WLog.i(TAG, "开锁有回复了: " + event.device.data);
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                dealDevice(event.device);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, event.deviceInfoBean.name));
                    setRoomName();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomInfoEvent(RoomInfoEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRoomListEvent(GetRoomListEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }
}
