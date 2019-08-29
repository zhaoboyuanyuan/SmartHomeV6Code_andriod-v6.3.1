package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.support.annotation.NonNull;
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
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.ProgressRing;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by zbl on 2018/6/22
 * 海信新风有线无线翻译器首页widget
 */

public class HomeWidget_On_Controller extends FrameLayout implements IWidgetLifeCycle, View.OnClickListener {

    private Context mContext;
    private TextView tv_name, tv_sate, tv_room;
    private ImageView btn_switch;
    private Device mDevice;

    /**
     * -1 离线，0 关，1 开
     */
    private int switch_status = -1;

    public HomeWidget_On_Controller(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public HomeWidget_On_Controller(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_on_controller, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);
        btn_switch = (ImageView) rootView.findViewById(R.id.btn_switch);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);

        btn_switch.setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
//        Log.i("luzx", "onBindViewHolder 61:" + mDevice);
        // 设置标题
        setName();
        setRoomName();

        dealDevice(mDevice);
        sendCmd(0x0105, "01");
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void setName() {
        tv_name.setText(DeviceInfoDictionary.getNameByDevice(mDevice));
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = MainApplication.getApplication().getRoomCache().getRoomName(mDevice.roomID);
        tv_room.setText(areaName);
    }

    /**
     * 发出控制指令后, 更新状态
     */
    private void updateState() {
        if (mDevice != null) {
            if (mDevice.isOnLine()) {
                //设置title上线状态
                tv_sate.setText(R.string.Device_Online);
                tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));
                btn_switch.setEnabled(true);
                if (switch_status == 0) {
                    btn_switch.setImageResource(R.drawable.icon_widget_on_switch_off);
                } else if (switch_status == 1) {
                    btn_switch.setImageResource(R.drawable.icon_widget_on_switch_on);
                }
                return;
            }
        }
        tv_sate.setText(R.string.Device_Offline);
        tv_sate.setTextColor(getResources().getColor(R.color.newStateText));
        btn_switch.setImageResource(R.drawable.icon_widget_on_switch_offline);
        btn_switch.setEnabled(false);
    }

    private void dealDevice(final Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x8101) {
                    try {
                        switch_status = Integer.parseInt(attribute.attributeValue);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateState();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.deviceInfoBean.mode == 2) {
                    setRoomName();
                    setName();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                dealDevice(event.device);
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
        if(v == btn_switch){
            if (switch_status == 0) {
                sendCmd(0x0102, "1");//打开
            } else if (switch_status == 1) {
                sendCmd(0x0102, "0");//关闭
            }
        }
    }

    private void sendCmd(int commandId, String parameterValue) {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);
            object.put("clusterId", 0x0203);
            if (parameterValue != null) {
                JSONArray parameter = new JSONArray();
                parameter.put(parameterValue);
                object.put("parameter", parameter);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
