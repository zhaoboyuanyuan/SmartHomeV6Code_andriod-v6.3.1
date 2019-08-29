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
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
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

//多功能红外人体探测器首页widget
public class HomeWidget_C0_Ad_Sensor extends FrameLayout implements IWidgetLifeCycle, View.OnClickListener {
    private static final String DEFENSE = "1";
    private static final String UNDEFENSE = "0";
    private Context mContext;
    private TextView tv_name, tv_sate, tv_room;
    private TextView tvInfo;
    private TextView tvTime;
    private TextView tvHumidity;
    private TextView tvLightIntensity;
    private ImageView ivDefense;
    private Device mDevice;
    private DataApiUnit dataApiUnit;

    private String temp;
    private String humidity;
    private String lightIntensity;
    private String endpointStatus;

    public HomeWidget_C0_Ad_Sensor(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public HomeWidget_C0_Ad_Sensor(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_c0_ad_sensor, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);
        tvTime = (TextView) rootView.findViewById(R.id.tv_time);
        tvInfo = (TextView) rootView.findViewById(R.id.tv_info);
        tvHumidity = (TextView) rootView.findViewById(R.id.tv_humiture);
        tvLightIntensity = (TextView) rootView.findViewById(R.id.tv_light_intensity);
        ivDefense = (ImageView) rootView.findViewById(R.id.iv_defense);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);

        ivDefense.setOnClickListener(this);
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
        sendCmd();
        getLastAlarmTime();
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


    private void getLastAlarmTime() {
        dataApiUnit = new DataApiUnit(mContext);
        String startTime = "1";
        String endTime = "" + (System.currentTimeMillis() + 2 * 24 * 3600);
        dataApiUnit.doGetDeviceDataHistory(
                mDevice.gwID,
                "1",
                mDevice.devID,
                "0102011",
                startTime,
                endTime,
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        try {
                            MessageBean messageBean = (MessageBean) bean;
                            if (messageBean.recordList != null && messageBean.recordList.size() > 0) {
                                MessageBean.RecordListBean recordListBean = messageBean.recordList.get(0);
                                tvTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(recordListBean.time));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                    }
                });
    }

    /**
     * 发出控制指令后, 更新状态
     */
    private void updateState() {
        tvHumidity.setText(temp + "℃/" + humidity + "%RH");
        tvLightIntensity.setText(lightIntensity + "LUX");
        if (mDevice != null) {
            if (mDevice.isOnLine()) {
                //设置title上线状态
                tv_sate.setText(R.string.Device_Online);
                tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTime.setTextColor(getResources().getColor(R.color.black));
                tvInfo.setTextColor(getResources().getColor(R.color.black));
                tvHumidity.setTextColor(getResources().getColor(R.color.black));
                tvLightIntensity.setTextColor(getResources().getColor(R.color.black));
                ivDefense.setEnabled(true);
                if (mDevice.endpoints.get(0).endpointNumber == 1)
                    for (Endpoint endpoint :
                            mDevice.endpoints) {
                        if (endpoint.endpointNumber == 1) {
                            endpointStatus = endpoint.endpointStatus;
                            if (UNDEFENSE.equals(endpointStatus)) {
                                ivDefense.setImageResource(R.drawable.icon_not_defence);
                            } else if (DEFENSE.equals(endpointStatus)) {
                                ivDefense.setImageResource(R.drawable.icon_defence);
                            }
                        }
                    }
                return;
            }
        }
        tv_sate.setText(R.string.Device_Offline);
        tv_sate.setTextColor(getResources().getColor(R.color.newStateText));
        tvTime.setTextColor(getResources().getColor(R.color.newStateText));
        tvInfo.setTextColor(getResources().getColor(R.color.newStateText));
        tvHumidity.setTextColor(getResources().getColor(R.color.newStateText));
        tvLightIntensity.setTextColor(getResources().getColor(R.color.newStateText));
        ivDefense.setEnabled(false);
        ivDefense.setImageResource(R.drawable.icon_not_defence_offline);
    }

    private void dealDevice(final Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (endpoint.endpointNumber == 2) {
                    if (cluster.clusterId == 0x0402) {
                        if (attribute.attributeId == 0x0000) {
                            temp = attribute.attributeValue;
                        }
                    }
                } else if (endpoint.endpointNumber == 3) {
                    if (cluster.clusterId == 0x0405) {
                        if (attribute.attributeId == 0x0000) {
                            humidity = attribute.attributeValue;
                        }
                    }
                } else if (endpoint.endpointNumber == 4) {
                    if (cluster.clusterId == 0x0800) {
                        if (attribute.attributeId == 0x8005) {
                            lightIntensity = attribute.attributeValue;
                        }
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
                } else if (event.deviceInfoBean.mode == 0) {
                    updateState();
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
        if (v == ivDefense) {
            if (UNDEFENSE.equals(endpointStatus)) {
                sendCmd(DEFENSE);
            } else if (DEFENSE.equals(endpointStatus)) {
                sendCmd(UNDEFENSE);
            }
        }
    }

    private void sendCmd() {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("commandType", 1);
            object.put("commandId", 0x02);
            object.put("endpointNumber", 1);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCmd(String endpointStatus) {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "502");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("mode", 0);
            object.put("endpointNumber", 1);
            object.put("endpointStatus", endpointStatus);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
