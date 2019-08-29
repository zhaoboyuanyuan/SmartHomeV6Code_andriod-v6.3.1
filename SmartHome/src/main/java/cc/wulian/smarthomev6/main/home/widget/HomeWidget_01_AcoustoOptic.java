package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import cc.wulian.smarthomev6.entity.DeviceInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by Veev on 2017/5/8
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 声光 首页widget
 */

public class HomeWidget_01_AcoustoOptic extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_01_AcoustoOptic.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textState, textArea;

    private ImageView imageUniversal, imageFire, imageStop;
    private RelativeLayout title;

    private int mode;

    public HomeWidget_01_AcoustoOptic(Context context) {
        super(context);

        initView(context);
    }

    public HomeWidget_01_AcoustoOptic(Context context, AttributeSet attrs) {
        super(context, attrs);initView(context);

        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_01_acousto_optic, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);

        imageUniversal = (ImageView) rootView.findViewById(R.id.acousto_optic_image_universal);
        imageFire = (ImageView) rootView.findViewById(R.id.acousto_optic_image_fire);
        imageStop = (ImageView) rootView.findViewById(R.id.acousto_optic_image_stop);

        rootView.findViewById(R.id.acousto_optic_linear_universal).setOnClickListener(this);
        rootView.findViewById(R.id.acousto_optic_linear_fire).setOnClickListener(this);
        rootView.findViewById(R.id.acousto_optic_linear_stop).setOnClickListener(this);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);
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

    /**
     * 发出控制指令后, 更新状态
     */
    private void updateState(String state) {
        switch (state) {
            case "0":
                imageUniversal.setImageResource(R.drawable.icon_bell_nor);
                imageFire.setImageResource(R.drawable.icon_fire_nor);
                imageStop.setImageResource(R.drawable.icon_stop_pre);
                break;
            case "1":
                imageUniversal.setImageResource(R.drawable.icon_bell_pre);
                imageFire.setImageResource(R.drawable.icon_fire_nor);
                imageStop.setImageResource(R.drawable.icon_stop_nor);
                break;
            case "2":
                imageUniversal.setImageResource(R.drawable.icon_bell_nor);
                imageFire.setImageResource(R.drawable.icon_fire_pre);
                imageStop.setImageResource(R.drawable.icon_stop_nor);
                break;
        }
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
//                textState.setText(R.string.Device_Online);
//                textState.setTextColor(getResources().getColor(R.color.colorPrimary));
                imageUniversal.setImageResource(R.drawable.icon_bell_nor);
                imageFire.setImageResource(R.drawable.icon_fire_nor);
                imageStop.setImageResource(R.drawable.icon_stop_nor);
                break;
            case 2:
                // 离线
                textState.setText(R.string.Device_Offline);
                textState.setTextColor(getResources().getColor(R.color.newStateText));
//                textState.setText(R.string.Home_Widget_OffLine);
//                textState.setTextColor(getResources().getColor(R.color.home_widget_state_offline));
                imageUniversal.setImageResource(R.drawable.icon_bell);
                imageFire.setImageResource(R.drawable.icon_fire);
                imageStop.setImageResource(R.drawable.icon_stop);
                break;
            case 3:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                // do something...
                mode = event.device.mode;
                updateMode();
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                } else if (event.device.mode == 1) {
                    // 更新上线
                } else if (event.device.mode == 0) {
                    try {
                        JSONObject object = new JSONObject(event.device.data);
                        // TODO: 2017/5/16 更新 endpointNumber
//                        endpointNumber = object.getString("endpointNumber");
                        JSONArray endpoints = object.getJSONArray("endpoints");
                        JSONArray clusters = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
                        JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
                        int clusterId = ((JSONObject) clusters.get(0)).getInt("clusterId");
                        if (clusterId == 1280) {
                            String attributeValue = ((JSONObject) attributes.get(0)).getString("attributeValue");
                            // 更新状态
                            updateState(attributeValue);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.acousto_optic_linear_universal:
                sendCmd(1);
                break;
            case R.id.acousto_optic_linear_fire:
                sendCmd(2);
                break;
            case R.id.acousto_optic_linear_stop:
                sendCmd(0);
                break;

        }
    }

    private void sendCmd(int commandId) {
        if (mode != 0 && mode != 1) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 1280);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
