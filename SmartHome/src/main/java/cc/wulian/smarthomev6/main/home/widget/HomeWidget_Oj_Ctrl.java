package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * 新风控制器 widget
 */
public class HomeWidget_Oj_Ctrl extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView mTextName, mTextRoom, mTextState;
    private ImageView mImagePower;
    private TextView mTextInside, mTextOutside, mTextInsideData, mTextOutsideData, mTextPower;
    private View mRootView;
    /**
     * 状态
     * 1 开
     * 0 停
     */
    private int mState = -1;
    /**
     * 是否为控制器
     * true     是 作为控制器
     * false    否 作为翻译器
     */
    private boolean isCtrl = false;

    // 室内外 温度湿度
    private String mTempInside, mTempOutside, mHumiInside, mHumiOutside;
    // 室内外温度湿度 是否上报
    private boolean mTempInsideState = false, mTempOutsideState = false,
            mHumiInsideState = false, mHumiOutsideState = false;

    public HomeWidget_Oj_Ctrl(Context context) {
        super(context);
        initView(context);
        initListener();
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());

        dealData(mDevice);
        // 设置在线离线的状态
        updateMode();

        setName();
        setRoomName();

        if (!HomeWidgetManager.hasInCache(mDevice)) {
            sendCmd(0x0105, "00");
        }
        HomeWidgetManager.add2Cache(mDevice);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_home_oj_ctrl, null);
        addView(mRootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextName = (TextView) mRootView.findViewById(R.id.widget_title_name);
        mTextRoom = (TextView) mRootView.findViewById(R.id.widget_title_room);
        mTextState = (TextView) mRootView.findViewById(R.id.widget_title_state);

        mImagePower = (ImageView) mRootView.findViewById(R.id.widget_oj_image_power);

        mTextInside = (TextView) mRootView.findViewById(R.id.widget_oj_text_inside);
        mTextOutside = (TextView) mRootView.findViewById(R.id.widget_oj_text_outside);
        mTextInsideData = (TextView) mRootView.findViewById(R.id.widget_oj_text_inside_data);
        mTextOutsideData = (TextView) mRootView.findViewById(R.id.widget_oj_text_outside_data);
        mTextPower = (TextView) mRootView.findViewById(R.id.widget_oj_text_power);

        mImagePower.setOnClickListener(this);
    }

    private void initListener() {
    }

    private void setName() {
        if (mDevice == null) {
            mTextName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            mTextName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        mTextRoom.setText(areaName);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        switch (mDevice.mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                mTextState.setText(R.string.Device_Online);
                mTextState.setTextColor(getResources().getColor(R.color.colorPrimary));

                if (mState == 0) {
                    mImagePower.setImageResource(R.drawable.icon_widget_power_off);
                } else {
                    mImagePower.setImageResource(R.drawable.icon_widget_power_on);
                }

                mTextInside.setTextColor(getResources().getColor(R.color.newPrimaryText));
                mTextOutside.setTextColor(getResources().getColor(R.color.newPrimaryText));
                break;
            case 2:
                // 离线
                mTextState.setText(R.string.Device_Offline);
                mTextState.setTextColor(getResources().getColor(R.color.newStateText));

                mImagePower.setImageResource(R.drawable.icon_widget_power_off);

                mTextInside.setTextColor(getResources().getColor(R.color.newStateText));
                mTextOutside.setTextColor(getResources().getColor(R.color.newStateText));
                break;
            case 3:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.widget_oj_image_power:
                if (mState == 0) {
                    sendCmd(0x0102, "1");
                } else {
                    sendCmd(0x0102, "0");
                }
                break;
            default:
                break;
        }

    }

    private void dealData(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x8101) {
                    try {
                        mState = Integer.parseInt(attribute.attributeValue);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (attribute.attributeId == 0x8104) {
                    // 0x8104   室外温度上报
                    mTempOutside = attribute.attributeValue;
                } else if (attribute.attributeId == 0x8105) {
                    // 0x8105   室外湿度上报
                    mHumiOutside = attribute.attributeValue;
                } else if (attribute.attributeId == 0x8106) {
                    // 0x8106   室内温度上报
                    mTempInside = attribute.attributeValue;
                } else if (attribute.attributeId == 0x8107) {
                    // 0x8107   室内湿度上报
                    mHumiInside = attribute.attributeValue;
                } else if (attribute.attributeId == 0x810B) {
                    // 性能信息——模式
                    // （1） 室外温度显示  （2）  温度单位（0：摄氏度 1：华氏度） （3） 室外温度显示
                    // （4）风量设置（5） 模式设置  （6）室内温度显示   （7） 室内湿度显示
                    if (TextUtils.equals(attribute.attributeValue.substring(0, 1), "1")) {
                        mTempOutsideState = true;
                    } else {
                        mTempOutsideState = false;
                    }

                    if (TextUtils.equals(attribute.attributeValue.substring(2, 3), "1")) {
                        mHumiOutsideState = true;
                    } else {
                        mHumiOutsideState = false;
                    }

                    if (TextUtils.equals(attribute.attributeValue.substring(5, 6), "1")) {
                        // FIXME: 2018/2/23 协议有误 与1 相同
                        mTempInsideState = true;
                    } else {
                        mTempInsideState = false;
                    }

                    if (TextUtils.equals(attribute.attributeValue.substring(6, 7), "1")) {
                        mHumiInsideState = true;
                    } else {
                        mHumiInsideState = false;
                    }

                    if (mTempInsideState && mHumiInsideState) {
                        // 说明有室内温度 作为控制器
                        isCtrl = true;
                    } else {
                        // 说明没有室内温度 作为翻译器
                        isCtrl = false;
                    }
                }
            }
        });

        updateData();
    }

    private void updateData() {
        switch (mState) {
            case 0:
                mTextPower.setText(R.string.widget_door_closed);
                mImagePower.setImageResource(R.drawable.icon_widget_power_off);
                break;
            case 1:
                mTextPower.setText(R.string.Device_Lock_Widget_Gatestaopen);
                mImagePower.setImageResource(R.drawable.icon_widget_power_on);
                break;
        }

        // 没有数值, 数值为"--", 没有上报
        // 都显示为: "--"
        if (TextUtils.isEmpty(mHumiOutside) || TextUtils.equals(mHumiOutside, "--") || !mHumiOutsideState) {
            mHumiOutside = "--";
        }

        if (TextUtils.isEmpty(mTempOutside) || TextUtils.equals(mTempOutside, "--") || !mTempOutsideState) {
            mTempOutside = "--";
        }

        String strOut = mTempOutside + "℃ / " + mHumiOutside + "%RH";

        // 是控制器
        if (isCtrl) {
            if (TextUtils.isEmpty(mHumiInside) || TextUtils.equals(mHumiInside, "--") || !mHumiInsideState) {
                mHumiInside = "--";
            }
            if (TextUtils.isEmpty(mTempInside) || TextUtils.equals(mTempInside, "--") || !mTempInsideState) {
                mTempInside = "--";
            }
            String strIn = mTempInside + "℃ / " + mHumiInside + "%RH";

            mTextOutsideData.setVisibility(View.VISIBLE);
            mTextInside.setVisibility(View.VISIBLE);

            mTextOutsideData.setText(strOut);
            mTextInsideData.setText(strIn);
        } else {
            // 是翻译器
            mTextOutsideData.setVisibility(View.GONE);
            mTextInside.setVisibility(View.GONE);

            mTextInsideData.setText(strOut);
        }
    }

    /**
     * 发送命令
     */
    private void sendCmd(int commandId, Object args){
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 0x0203);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);
            if (args != null) {
                JSONArray array = new JSONArray();
                array.put(args);
                object.put("parameter", array);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    setName();
                    setRoomName();
                    // 设置在线离线的状态
                    updateMode();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                dealData(event.device);
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                setName();
                setRoomName();
                // 设置在线离线的状态
                updateMode();
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
