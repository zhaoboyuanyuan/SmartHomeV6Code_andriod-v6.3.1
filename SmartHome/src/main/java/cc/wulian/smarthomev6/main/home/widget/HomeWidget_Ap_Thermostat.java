package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import cc.wulian.smarthomev6.main.device.DeviceDetailActivity;
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
 * Created by Veev on 2017/5/8
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: wulian 地暖 首页widget
 */

public class HomeWidget_Ap_Thermostat extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_Ap_Thermostat.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView mTextName, mTextRoom, mTextState;

    private TextView mTextRoomTemp, mTextSurfaceTemp, mTextPreTemp;
    private ImageView mImageLeft, mImageRight, mImageState, mImageRoom, mImageSurface;
    private FrameLayout mFrameSwitch;

    private View mRootView;

    private int mode;

    private float mTempRoom = 25.0f, mTempFloor = 27.0f, mTempMain = 26f, mTempPre = 26f;
    // 温标 0 c  1 f
    // 电源 0 关 1 开
    // 节能 0 关 1 开
    private int mTempScale = 0, mPower = 0, mGreen = 0;

    public HomeWidget_Ap_Thermostat(Context context) {
        super(context);

        initView(context);
    }

    public HomeWidget_Ap_Thermostat(Context context, AttributeSet attrs) {
        super(context, attrs);initView(context);

        initView(context);
    }

    private void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_home_bm, null);
        addView(mRootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextName = (TextView) mRootView.findViewById(R.id.widget_title_name);
        mTextRoom = (TextView) mRootView.findViewById(R.id.widget_title_room);
        mTextState = (TextView) mRootView.findViewById(R.id.widget_title_state);

        mTextRoomTemp = (TextView) mRootView.findViewById(R.id.widget_bm_text_room);
        mTextSurfaceTemp = (TextView) mRootView.findViewById(R.id.widget_bm_text_surface);
        mTextPreTemp = (TextView) mRootView.findViewById(R.id.widget_bm_text_pre);
        mImageLeft = (ImageView) mRootView.findViewById(R.id.widget_bm_image_left);
        mImageRight = (ImageView) mRootView.findViewById(R.id.widget_bm_image_right);
        mImageState = (ImageView) mRootView.findViewById(R.id.widget_bm_image_switch);
        mImageRoom = (ImageView) mRootView.findViewById(R.id.widget_bm_image_room);
        mImageSurface = (ImageView) mRootView.findViewById(R.id.widget_bm_image_surface);
        mFrameSwitch = (FrameLayout) mRootView.findViewById(R.id.widget_bm_frame_switch);

        mImageLeft.setOnClickListener(this);
        mImageRight.setOnClickListener(this);
        mFrameSwitch.setOnClickListener(this);

        final DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        mTextName.setMaxWidth(titleH / 2);
        mTextRoom.setMaxWidth(titleH / 4);

        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice == null) {
                    DeviceDetailActivity.start(getContext(), mDevice.devID);
                }
            }
        });
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

        if (!HomeWidgetManager.hasInCache(mDevice)) {
            // do smoething... here
            sendCmd(0x8010, "1");
        }
        HomeWidgetManager.add2Cache(mDevice);

        dealData(mDevice);
        // 设置在线离线的状态
        updateMode();

        setName();
        setRoomName();
    }

    private void updateData() {
        if (mTempRoom == 100f && mDevice.isOnLine()) {
            mTextRoomTemp.setVisibility(View.INVISIBLE);
            mImageRoom.setVisibility(VISIBLE);
        } else {
            mImageRoom.setVisibility(View.INVISIBLE);
            mTextRoomTemp.setVisibility(View.VISIBLE);
            mTextRoomTemp.setText(getShownTemp(mTempRoom));
        }
        if (mTempFloor == 100f && mDevice.isOnLine()) {
            mTextSurfaceTemp.setVisibility(View.INVISIBLE);
            mImageSurface.setVisibility(VISIBLE);
        } else {
            mImageSurface.setVisibility(View.INVISIBLE);
            mTextSurfaceTemp.setVisibility(View.VISIBLE);
            mTextSurfaceTemp.setText(getShownTemp(mTempFloor));
        }
        mTextPreTemp.setText(getShownTemp(mTempMain));

        mImageLeft.setEnabled(mPower == 1);
        mImageRight.setEnabled(mPower == 1);
        mImageState.setEnabled(mPower == 1);
    }

    private String getShownTemp(float temp) {
        if (mTempScale == 0) {
            return String.format("%.1f ℃", temp);
        }
        return String.format("%d ℉", Math.round(temp * 1.8 + 32));
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
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
        switch (mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                mTextState.setText(R.string.Device_Online);
                mTextState.setTextColor(getResources().getColor(R.color.colorPrimary));

                mTextPreTemp.setTextColor(getResources().getColor(R.color.newPrimaryText));
                mTextSurfaceTemp.setTextColor(getResources().getColor(R.color.newPrimaryText));
                mTextRoomTemp.setTextColor(getResources().getColor(R.color.newPrimaryText));

                mImageLeft.setEnabled(true);
                mImageRight.setEnabled(true);
                mFrameSwitch.setEnabled(true);
                break;
            case 2:
                // 离线
                mTextState.setText(R.string.Device_Offline);
                mTextState.setTextColor(getResources().getColor(R.color.newStateText));

                mTextPreTemp.setTextColor(getResources().getColor(R.color.newStateText));
                mTextSurfaceTemp.setTextColor(getResources().getColor(R.color.newStateText));
                mTextRoomTemp.setTextColor(getResources().getColor(R.color.newStateText));
                mTextPreTemp.setText("- -");
                mTextSurfaceTemp.setText("- -");
                mTextRoomTemp.setText("- -");

                mImageLeft.setEnabled(false);
                mImageRight.setEnabled(false);
                mFrameSwitch.setEnabled(false);
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
                dealData(mDevice);
                mode = event.device.mode;
                updateMode();

                setName();
                setRoomName();
            }
        }
    }

    private void dealData(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x8001) {
                    mPower = TextUtils.equals(attribute.attributeValue.substring(0, 2), "00") ? 0 : 1;
                    mGreen = TextUtils.equals(attribute.attributeValue.substring(34, 36), "00") ? 0 : 1;

                    try {
                        mTempRoom = Integer.parseInt(attribute.attributeValue.substring(20, 24), 16) / 10f;
                        if (mTempRoom > 3276.7f) {
                            mTempRoom = mTempRoom - 6553.6f;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        mTempFloor = Integer.parseInt(attribute.attributeValue.substring(40, 44), 16) / 10f;
                        if (mTempFloor > 3276.7f) {
                            mTempFloor = mTempFloor - 6553.6f;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        mTempMain = Integer.parseInt(attribute.attributeValue.substring(16, 20), 16) / 100f;
                        mTempPre = mTempMain;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        mTempScale = Integer.parseInt(attribute.attributeValue.substring(14, 16), 16);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (attribute.attributeId == 0x8006) {
                    try {
                        mTempScale = Integer.parseInt(attribute.attributeValue.substring(0, 2), 16);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (attribute.attributeId == 0x8007) {
                    try {
                        mTempMain = Integer.parseInt(attribute.attributeValue.substring(0, 4), 16) / 100f;
                        mTempPre = mTempMain;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (attribute.attributeId == 0x800F) {
                    mGreen = TextUtils.equals(attribute.attributeValue.substring(0, 2), "00") ? 0 : 1;
                }
            }
        });

        updateData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    setName();
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
            case R.id.widget_bm_image_left:
                if (mPower == 1 && mGreen == 0) {
                    subTemp();
                }
                break;
            case R.id.widget_bm_image_right:
                if (mPower == 1 && mGreen == 0) {
                    addTemp();
                }
                break;
            case R.id.widget_bm_frame_switch:
                sendCmd(mPower == 0 ? 1 : 0, null);
                break;
        }
    }

    private Runnable changeTempRunnable = new Runnable() {
        @Override
        public void run() {
            sendCmd(0x8013, "" + (int)(mTempPre * 100));
        }
    };

    private void addTemp() {
        removeCallbacks(changeTempRunnable);

        if (mTempPre >= 32) {
            return;
        }
        if (mTempScale == 0) {
            mTempPre = mTempPre + 0.5f;
        } else {
            // 转正 华氏度 再 +1
            float f = Math.round(mTempPre * 1.8 + 32) + 1;
            // 再转回摄氏度
            float tt = (f - 32) / 1.8f;
            // 整数部分
            int ttInt = (int)(tt);
            // 小数部分
            float ttF = tt - ttInt;
            if (ttF < 0.25f) {
                mTempPre = ttInt;
            } else if (ttF < 0.75) {
                mTempPre = ttInt + 0.5f;
            } else {
                mTempPre = ttInt + 1f;
            }
        }

        mTextPreTemp.setText(getShownTemp(mTempPre));

        postDelayed(changeTempRunnable, 700);
    }

    private void subTemp() {
        removeCallbacks(changeTempRunnable);

        if (mTempPre <= 10) {
            return;
        }
        if (mTempScale == 0) {
            mTempPre = mTempPre - 0.5f;
        } else {
            // 转成 华氏度 再 +1
            float f = Math.round(mTempPre * 1.8 + 32) - 1;
            // 再转回摄氏度
            float tt = (f - 32) / 1.8f;
            // 整数部分
            int ttInt = (int)(tt);
            // 小数部分
            float ttF = tt - ttInt;
            if (ttF < 0.25f) {
                mTempPre = ttInt;
            } else if (ttF < 0.75) {
                mTempPre = ttInt + 0.5f;
            } else {
                mTempPre = ttInt + 1f;
            }
        }

        mTextPreTemp.setText(getShownTemp(mTempPre));

        postDelayed(changeTempRunnable, 700);
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
            object.put("clusterId", 0x0201);
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
}
