package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

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
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/7/18
 * Function: 一位DIN开关计量版02型 首页widget
 */

public class HomeWidget_Ai_MeteringSwitch extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_Ai_MeteringSwitch.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textState, textArea;
    private TextView tv_power, tv_power_unit, tv_quantity, tv_quantity_unit;
    private ImageView iv_switch_btn;
    private ProgressRing pr_one_switch;
    private RelativeLayout title;
    private Context mContext;
    private String toastText;

    /**
     * 开关状态：-1 不可用，0 关，1 开
     */
    private int openState = -1;

    public HomeWidget_Ai_MeteringSwitch(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Ai_MeteringSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_ai_metering_switch, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);

        tv_power = (TextView) rootView.findViewById(R.id.tv_power);
        tv_power_unit = (TextView) rootView.findViewById(R.id.tv_power_unit);
        tv_quantity = (TextView) rootView.findViewById(R.id.tv_quantity);
        tv_quantity_unit = (TextView) rootView.findViewById(R.id.tv_quantity_unit);

        iv_switch_btn = (ImageView) rootView.findViewById(R.id.switch_btn);
        pr_one_switch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);

        rootView.findViewById(R.id.root_view).setOnClickListener(this);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        iv_switch_btn.setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        // 设置在线离线的状态
        updateMode();

        setName();
        setRoomName();
        initShowData();
        sendCmd(2);//获取所有状态信息
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initShowData() {
        switchState = null;
        powerInfo = null;
        if (mDevice.mode == 0) {
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 6) {
                        if (attribute.attributeId == 0) {
                            switchState = attribute.attributeValue;
                        }
                    } else if (cluster.clusterId == 0x0b04) {
                        if (attribute.attributeId == 0x8001) {
                            powerInfo = attribute.attributeValue;
                        } else if (attribute.attributeId == 0x8005) {
                            //电量清零
                            tv_quantity.setText("0.0");
                            tv_quantity_unit.setText("w.h");
                        }
                    }
                }
            });
        } else if (mDevice.mode == 2) {
            // 设备离线
            updateMode();
        } else if (mDevice.mode == 1) {
            // 更新上线
            updateMode();
        }
        updateSwitchState(switchState);
        updatePowerInfo(powerInfo);
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
     * 更新开关状态
     */
    private void updateSwitchState(String state) {
        if (state == null) {
            return;
        }
        switch (state) {
            case "0":
                openState = 0;
                iv_switch_btn.setImageResource(R.drawable.switch_off);
                iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_gray);
                break;
            case "1":
                openState = 1;
                iv_switch_btn.setImageResource(R.drawable.switch_on);
                iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_green);
                break;
        }
        pr_one_switch.setState(ProgressRing.FINISHED);
    }

    /**
     * 更新电量信息
     */
    private void updatePowerInfo(String value) {
        if (value != null && value.length() >= 18) {
            if (openState == 1) {//开的状态才显示数值
                try {
                    String powerValueText = value.substring(4, 10);
                    int powerValue = Integer.parseInt(powerValueText, 16);
                    if (powerValue > 1000) {
                        float convertValue = ((float) powerValue) / 10000;
                        tv_power.setText(String.format("%-2.1f", (float) convertValue));
                        tv_power_unit.setText("kw");
                    } else {
                        float convertValue = ((float) powerValue) / 10;
                        tv_power.setText(String.format("%-2.1f", (float) convertValue));
                        tv_power_unit.setText("w");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                tv_power.setText("---");
                tv_power_unit.setText("");
            }
            try {
                String quantityValueText = value.substring(10, 16);
                int quantityValue = Integer.parseInt(quantityValueText, 16);
                if (quantityValue > 1000) {
                    float convertValue = ((float) quantityValue) / 1000;
                    tv_quantity.setText(String.format("%-2.1f", (float) convertValue));
                    tv_quantity_unit.setText("kw.h");
                } else {
                    tv_quantity.setText(String.format("%-2.1f", (float) quantityValue));
                    tv_quantity_unit.setText("w.h");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            textState.setText(R.string.Device_Online);
            textState.setTextColor(getResources().getColor(R.color.colorPrimary));
            iv_switch_btn.setEnabled(true);
        } else {
            // 离线
            openState = -1;
            tv_power.setText("---");
            tv_power_unit.setText("");
            tv_quantity.setText("---");
            tv_quantity_unit.setText("");
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
            iv_switch_btn.setImageResource(R.drawable.switch_off);
            iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_gray);
            iv_switch_btn.setEnabled(false);
        }
    }

    private String switchState = null, powerInfo = null;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode();
                } else if (event.device.mode == 1) {
                    updateMode();
                    sendCmd(2);//获取所有状态信息
                    // 更新上线
                } else if (event.device.mode == 0) {
                    switchState = null;
                    powerInfo = null;
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 6) {
                                if (attribute.attributeId == 0) {
                                    switchState = attribute.attributeValue;
                                }
                            } else if (cluster.clusterId == 0x0b04) {
                                if (attribute.attributeId == 0x8001) {
                                    powerInfo = attribute.attributeValue;
                                } else if (attribute.attributeId == 0x8005) {
                                    //电量清零
                                    tv_quantity.setText("0.0");
                                    tv_quantity_unit.setText("w.h");
                                }
                            }
                        }
                    });
                    //必须先更新开关状态，再更新数值，因为实时功率的显示依赖开关状态
                    updateSwitchState(switchState);
                    updatePowerInfo(powerInfo);
                }
            }
        } else if (event.device == null) {
            updateMode();
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
            case R.id.switch_btn:
                iv_switch_btn.setEnabled(false);
                switch (openState) {
                    case 0:
                        sendCmd(1);
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        break;
                    case 1:
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        sendCmd(0);
                        break;
                }
                pr_one_switch.setTimeout(5000);
                pr_one_switch.setState(ProgressRing.WAITING);
                pr_one_switch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        ToastUtil.single(toastText);
                        iv_switch_btn.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_switch_btn.setEnabled(true);
                    }
                });
                break;
            case R.id.root_view:
                DeviceInfoDictionary.showDetail(mContext, mDevice);
                break;
        }
    }

    private void sendCmd(int commandId) {
        if (mDevice.isOnLine()) {
            JSONObject object = new JSONObject();
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

}
