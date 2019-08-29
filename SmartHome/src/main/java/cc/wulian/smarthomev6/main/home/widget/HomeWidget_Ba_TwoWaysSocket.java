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
 * Function: 两位插座 首页widget
 */

public class HomeWidget_Ba_TwoWaysSocket extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_Ba_TwoWaysSocket.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textState, textArea;
    private TextView tv_power, tv_power_unit, tv_quantity, tv_quantity_unit;
    private ImageView iv_switch_one_btn, iv_switch_two_btn;
    private Context mContext;
    private String toastText;

    public HomeWidget_Ba_TwoWaysSocket(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Ba_TwoWaysSocket(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_ba_socket, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);

        tv_power = (TextView) rootView.findViewById(R.id.tv_power);
        tv_power_unit = (TextView) rootView.findViewById(R.id.tv_power_unit);
        tv_quantity = (TextView) rootView.findViewById(R.id.tv_quantity);
        tv_quantity_unit = (TextView) rootView.findViewById(R.id.tv_quantity_unit);

        iv_switch_one_btn = (ImageView) rootView.findViewById(R.id.switch_btn_one);
        iv_switch_two_btn = (ImageView) rootView.findViewById(R.id.switch_btn_two);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        iv_switch_one_btn.setOnClickListener(this);
        iv_switch_two_btn.setOnClickListener(this);
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
        if (!HomeWidgetManager.hasInCache(mDevice)) {
            sendCmd(2, 0);//获取所有状态信息
        }
        HomeWidgetManager.add2Cache(mDevice);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initShowData() {
        if(mDevice.mode == 0){
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 6) {
                        if (attribute.attributeId == 0) {
                            updateSwitchState(endpoint.endpointNumber, attribute.attributeValue);
                        } else if (attribute.attributeId == 0x8008) {
                            updatePowerInfo(attribute.attributeValue);
                        } else if (attribute.attributeId == 0x8009) {
                            updateQuantityInfo(attribute.attributeValue);
                        }else if (attribute.attributeId == 0x8005) {
                            //电量清零
                            tv_quantity.setText("0.0");
                            tv_quantity_unit.setText("w.h");
                        }
                    }
                }
            });
        }else if(mDevice.mode == 2){
            // 设备离线
            updateMode();
        }else if(mDevice.mode == 1){
            // 更新上线
            updateMode();

            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 6) {
                        if (attribute.attributeId == 0) {
                            updateSwitchState(endpoint.endpointNumber, attribute.attributeValue);
                        } else if (attribute.attributeId == 0x8008) {
                            updatePowerInfo(attribute.attributeValue);
                        } else if (attribute.attributeId == 0x8009) {
                            updateQuantityInfo(attribute.attributeValue);
                        }else if (attribute.attributeId == 0x8005) {
                            //电量清零
                            tv_quantity.setText("0.0");
                            tv_quantity_unit.setText("w.h");
                        }
                    }
                }
            });
        }
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
    private void updateSwitchState(int endpointNum, String switchState) {
        if (switchState == null) {
            return;
        }
        ImageView iv_switch_btn = null;
        if(endpointNum == 1){
            iv_switch_btn = iv_switch_one_btn;
        }else if(endpointNum == 2){
            iv_switch_btn = iv_switch_two_btn;
        }
        iv_switch_btn.setTag(switchState);
        switch (switchState) {
            case "0":
                iv_switch_btn.setTag(switchState);
                iv_switch_btn.setImageResource(R.drawable.icon_ba_off);
                break;
            case "1":
                iv_switch_btn.setImageResource(R.drawable.icon_ba_on);
                break;
        }
        if("0".equals(iv_switch_one_btn.getTag()) && "0".equals(iv_switch_two_btn.getTag())){
            tv_power.setText("---");
            tv_power_unit.setText("");
        }
    }

    /**
     * 更新电量信息
     */
    private void updatePowerInfo(String power) {
        try {
            int powerValue = Integer.parseInt(power);
            if (powerValue > 1000) {
                float convertValue = ((float) powerValue) / 1000;
                tv_power.setText(String.format("%-2.1f", (float) convertValue));
                tv_power_unit.setText("kw");
            } else {
                tv_power.setText(powerValue + "");
                tv_power_unit.setText("w");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新电量信息
     */
    private void updateQuantityInfo(String quantity) {
        try {
            int quantityValue = Integer.parseInt(quantity);
            if (quantityValue > 1000) {
                float convertValue = ((float) quantityValue) / 1000;
                tv_quantity.setText(String.format("%-2.1f", (float) convertValue));
                tv_quantity_unit.setText("kw.h");
            } else {
                tv_quantity.setText(quantityValue + "");
                tv_quantity_unit.setText("w.h");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
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
            iv_switch_one_btn.setEnabled(true);
            iv_switch_two_btn.setEnabled(true);
        } else {
            // 离线
            tv_power.setText("---");
            tv_power_unit.setText("");
            tv_quantity.setText("---");
            tv_quantity_unit.setText("");
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
            iv_switch_one_btn.setImageResource(R.drawable.icon_ba_off);
            iv_switch_two_btn.setImageResource(R.drawable.icon_ba_off);
            iv_switch_one_btn.setEnabled(false);
            iv_switch_two_btn.setEnabled(false);
        }
    }

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
                    sendCmd(2, 0);//获取所有状态信息
                    // 更新上线
                } else if (event.device.mode == 0) {
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 6) {
                                if (attribute.attributeId == 0) {
                                    updateSwitchState(endpoint.endpointNumber, attribute.attributeValue);
                                } else if (attribute.attributeId == 0x8008) {
                                    updatePowerInfo(attribute.attributeValue);
                                } else if (attribute.attributeId == 0x8009) {
                                    updateQuantityInfo(attribute.attributeValue);
                                }else if (attribute.attributeId == 0x8005) {
                                    //电量清零
                                    tv_quantity.setText("0.0");
                                    tv_quantity_unit.setText("w.h");
                                }
                            }
                        }
                    });
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
        String state = v.getTag() == null ? "-1" : v.getTag().toString();
        switch (v.getId()) {
            case R.id.switch_btn_one:
                switch (state) {
                    case "0":
                        sendCmd(1, 1);
                        break;
                    case "1":
                        sendCmd(0, 1);
                        break;
                }
                break;
            case R.id.switch_btn_two:
                switch (state) {
                    case "0":
                        sendCmd(1, 2);
                        break;
                    case "1":
                        sendCmd(0, 2);
                        break;
                }
                break;
//            case R.id.root_view:
//                DeviceInfoDictionary.showDetail(mContext, mDevice);
//                break;
        }
    }

    private void sendCmd(int commandId, int endpointNumber) {
        if (mDevice.isOnLine()) {
            JSONObject object = new JSONObject();
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            if(endpointNumber != 0){
                object.put("endpointNumber", endpointNumber);
            }
            object.put("clusterId", 0x0006);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

}
