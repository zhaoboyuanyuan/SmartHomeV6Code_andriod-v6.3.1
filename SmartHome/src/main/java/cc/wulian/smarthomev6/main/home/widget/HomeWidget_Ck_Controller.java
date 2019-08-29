package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2018/6/22
 * PL02新风控制器
 */

public class HomeWidget_Ck_Controller extends FrameLayout implements IWidgetLifeCycle, View.OnClickListener {

    private Context mContext;
    private TextView tv_name, tv_sate, tv_room;
    private TextView tvTmp;
    private TextView tvSpeed;
    private ImageView ivSwitch;
    private ProgressRing prSwitch;
    private Device mDevice;
    private String toastText;
    /**
     * -1 离线，0 关，1 开
     */
    private int switch_status = -1;
    /**
     * 1: 低 2: 中 3: 高
     */
    private String speed = "--";
    private String temp = "--";

    public HomeWidget_Ck_Controller(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public HomeWidget_Ck_Controller(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_ck_controller, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);
        tvSpeed = (TextView) rootView.findViewById(R.id.tv_speed);
        tvTmp = (TextView) rootView.findViewById(R.id.tv_tmp);
        ivSwitch = (ImageView) rootView.findViewById(R.id.iv_switch);
        prSwitch = rootView.findViewById(R.id.switch_progress);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);

        ivSwitch.setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        // 设置标题
        setName();
        setRoomName();

        dealDevice(mDevice);
        sendCmd(0x8010, null);
        Log.i("HomeWidget_Ck", "查询基础数据: ");
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
        prSwitch.setState(ProgressRing.FINISHED);
        if (mDevice != null) {
            switch (speed) {
                case "1":
                    tvSpeed.setText(R.string.Device_Widget_Oi_Speed3);
                    break;
                case "2":
                    tvSpeed.setText(R.string.Device_Widget_Oi_Speed2);
                    break;
                case "3":
                    tvSpeed.setText(R.string.Device_Widget_Oi_Speed1);
                    break;
                default:
                    tvSpeed.setText("--");
                    break;
            }
            tvTmp.setText(temp);
            if (mDevice.isOnLine()) {
                //设置title上线状态
                tv_sate.setText(R.string.Device_Online);
                tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));
                ivSwitch.setEnabled(true);
                if (switch_status == 0) {
                    ivSwitch.setImageResource(R.drawable.icon_switch_mode_off);
                } else if (switch_status == 1) {
                    ivSwitch.setImageResource(R.drawable.icon_switch_mode_on);
                }
                return;
            }
        }
        tv_sate.setText(R.string.Device_Offline);
        tv_sate.setTextColor(getResources().getColor(R.color.newStateText));
        ivSwitch.setImageResource(R.drawable.icon_widget_on_switch_offline);
        ivSwitch.setEnabled(false);
    }

    private void dealDevice(final Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x8001) {
                    try {
                        switch_status = Integer.parseInt(attribute.attributeValue);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (attribute.attributeId == 0x8002) {
                    speed = attribute.attributeValue;
                } else if (attribute.attributeId == 0x8003) {
                    if (TextUtils.equals(attribute.attributeValue, "03E8")) {
                        temp = "--";
                        return;
                    }
                    temp = attribute.attributeValue;
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
        if (v == ivSwitch) {
            prSwitch.setTimeout(5000);
            prSwitch.setState(ProgressRing.WAITING);
            prSwitch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                @Override
                public void onTimeout() {
                    ToastUtil.single(toastText);
                    ivSwitch.setEnabled(true);
                }

                @Override
                public void onEnd() {
                    ivSwitch.setEnabled(true);
                }
            });
            if (switch_status == 0) {
                sendCmd(1, null);//打开
                toastText = String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime), mDevice.name);
            } else if (switch_status == 1) {
                sendCmd(0, null);//关闭
                toastText = String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime), mDevice.name);
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
            object.put("clusterId", 0x0201);
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
