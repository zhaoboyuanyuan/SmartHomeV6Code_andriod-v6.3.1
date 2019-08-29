package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
 * Created by 上海滩小马哥 on 2018/02/23.
 * 1/2开关模组
 */

public class HomeWidget_Aw_Switch extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private Context mContext;
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private View layout_mode_bell, layout_mode_switch;

    private ImageView iv_bell;
    private TextView tv_bell_short, tv_bell_alert, tv_bell_stop;

    private TextView textName, textState, textArea, textSwitchState;
    private ImageView iv_switch;
    private ProgressRing pr_switch;
    private String toastText;
    /**
     * 开关状态：-1 不可用，0 关，1 开
     */
    private int openState = -1;
    /**
     * 电铃状态：0 关，1 开
     */
    private int bellState = 0;
    /**
     * 工作模式：0 开关，1 电铃
     */
    private int awMode = 0;

    public HomeWidget_Aw_Switch(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Aw_Switch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
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

        updateMode();
        setName();
        setRoomName();
        updateView(mDevice);
        sendCmd(0x8008, null);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_aw_switch, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //title
        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);

        //开关模式
        layout_mode_switch = rootView.findViewById(R.id.layout_mode_switch);
        textSwitchState = (TextView) rootView.findViewById(R.id.textSwitchState);
        iv_switch = (ImageView) rootView.findViewById(R.id.one_switch_image);
        pr_switch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);
        iv_switch.setOnClickListener(this);

        //电铃模式
        layout_mode_bell = rootView.findViewById(R.id.layout_mode_bell);
        iv_bell = (ImageView) rootView.findViewById(R.id.iv_bell);
        tv_bell_short = (TextView) rootView.findViewById(R.id.tv_bell_short);
        tv_bell_alert = (TextView) rootView.findViewById(R.id.tv_bell_alert);
        tv_bell_stop = (TextView) rootView.findViewById(R.id.tv_bell_stop);

        tv_bell_short.setOnClickListener(this);
        tv_bell_alert.setOnClickListener(this);
        tv_bell_stop.setOnClickListener(this);
    }

    private void setName() {
        if (mDevice == null) {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            textSwitchState.setVisibility(VISIBLE);
            textSwitchState.setText(R.string.widget_door_open);
            textState.setText(R.string.Device_Online);
            textState.setTextColor(getResources().getColor(R.color.colorPrimary));
            iv_switch.setImageResource(R.drawable.switch_on);
            iv_switch.setBackgroundResource(R.drawable.home_widget_circle_green);
            iv_switch.setEnabled(true);

            tv_bell_short.setEnabled(true);
            tv_bell_alert.setEnabled(true);
            tv_bell_stop.setEnabled(true);
            tv_bell_short.setAlpha(1);
            tv_bell_alert.setAlpha(1);
            tv_bell_stop.setAlpha(1);
        } else {
            // 离线
            openState = -1;
            textSwitchState.setVisibility(GONE);
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
            iv_switch.setImageResource(R.drawable.switch_off);
            iv_switch.setBackgroundResource(R.drawable.home_widget_circle_gray);
            iv_switch.setEnabled(false);

            tv_bell_short.setEnabled(false);
            tv_bell_alert.setEnabled(false);
            tv_bell_stop.setEnabled(false);
            tv_bell_short.setAlpha(0.45f);
            tv_bell_alert.setAlpha(0.45f);
            tv_bell_stop.setAlpha(0.45f);
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        textArea.setText(areaName);
    }

    private void updateView(Device device) {
        if (device == null) {
            return;
        }
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (endpoint.endpointNumber == 1) {
                    if (cluster.clusterId == 0x0006) {
                        if (attribute.attributeId == 0x0002) {
                            updateSwitchBellMode(TextUtils.equals("1", attribute.attributeValue));
                        } else if (attribute.attributeId == 0x0000) {
                            if (TextUtils.equals(attribute.attributeValue, "1")) {
                                openState = 1;
                            } else {
                                openState = 0;
                            }
                        } else if (attribute.attributeId == 0x0003) {
                            if (TextUtils.equals(attribute.attributeValue, "1")) {
                                bellState = 1;
                            } else {
                                bellState = 0;
                            }
                        }
                    }
                }
            }
        });
        updateSwitchState(openState);
        updateBellState(bellState);
    }

    private void updateSwitchBellMode(boolean isBellMode) {
        if (isBellMode) {
            awMode = 1;
            layout_mode_switch.setVisibility(GONE);
            layout_mode_bell.setVisibility(VISIBLE);
        } else {
            awMode = 0;
            layout_mode_switch.setVisibility(VISIBLE);
            layout_mode_bell.setVisibility(GONE);
        }
    }

    /**
     * 更新开关状态
     */
    private void updateSwitchState(int state) {
        if (state == -1) {
            return;
        }
        switch (state) {
            case 0:
                openState = 0;
                textSwitchState.setText(R.string.widget_door_closed);
                textSwitchState.setTextColor(mContext.getResources().getColor(R.color.gray));
                iv_switch.setImageResource(R.drawable.switch_off);
                iv_switch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                break;
            case 1:
                openState = 1;
                textSwitchState.setText(R.string.widget_door_open);
                textSwitchState.setTextColor(mContext.getResources().getColor(R.color.v6_green));
                iv_switch.setImageResource(R.drawable.switch_on);
                iv_switch.setBackgroundResource(R.drawable.home_widget_circle_green);
                break;
        }
        pr_switch.setState(ProgressRing.FINISHED);
    }

    /**
     * 更新电铃状态
     */
    private void updateBellState(int state) {
        if (mDevice != null && mDevice.isOnLine()) {
            if (state == 1) {
                iv_bell.setImageResource(R.drawable.icon_aw_bell_on);
                iv_bell.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_aw_widget_vibrate));
            } else {
                iv_bell.clearAnimation();
                iv_bell.setImageResource(R.drawable.icon_aw_bell_normal);
            }
        } else {
            iv_bell.clearAnimation();
            iv_bell.setImageResource(R.drawable.icon_aw_bell_off);
        }
    }

    /**
     * @param commandId 3:关闭；4：打开；0x8008:查询
     */
    private void sendCmd(int commandId, String parameterValue) {
        if (mDevice!=null && mDevice.isOnLine()) {
            JSONObject object = new JSONObject();
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 0x0006);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);
            if (parameterValue != null) {
                JSONArray parameter = new JSONArray();
                parameter.add(parameterValue);
                object.put("parameter", parameter);
            }

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                updateMode();
                updateView(mDevice);
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
            case R.id.one_switch_image: {
                iv_switch.setEnabled(false);
                switch (openState) {
                    case 0:
                        sendCmd(0x04, null);
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        break;
                    case 1:
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        sendCmd(0x03, null);
                        break;
                }
                pr_switch.setTimeout(5000);
                pr_switch.setState(ProgressRing.WAITING);
                pr_switch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
//                        ToastUtil.single(toastText);
                        iv_switch.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_switch.setEnabled(true);
                    }
                });
            }
            break;
            case R.id.tv_bell_short: {
                sendCmd(0x08, null);
            }
            break;
            case R.id.tv_bell_alert: {
                sendCmd(0x09, "1");
            }
            break;
            case R.id.tv_bell_stop: {
                sendCmd(0x09, "0");
            }
            break;
        }
    }
}
