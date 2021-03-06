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

/**
 * Created by luzx on 2017/10/10
 * Function: 移动插首页widget
 */

public class HomeWidget_16_Socket extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_16_Socket.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textState, textArea, state_text;
    private ImageView iv_one_switch, scoket_image;
    private ProgressRing pr_one_switch;
    private RelativeLayout title;
    private Context mContext;
    private String toastText;

    /**
     * 开关状态：0关闭 1打开
     */
    private int openState = 0;

    public HomeWidget_16_Socket(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_16_Socket(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_16_socket, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);

        iv_one_switch = (ImageView) rootView.findViewById(R.id.one_switch_image);
        pr_one_switch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);
        scoket_image = (ImageView) rootView.findViewById(R.id.scoket_image);
        state_text = (TextView) rootView.findViewById(R.id.state_text);
        rootView.findViewById(R.id.root_view).setOnClickListener(this);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        iv_one_switch.setOnClickListener(this);
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
//        sendCmd(2);//获取所有状态信息
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initShowData() {
        switchState = null;
        if(mDevice.mode == 2){
            // 设备离线
            updateMode();
        }else{
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 6) {
                        if (attribute.attributeId == 0) {
                            switchState = attribute.attributeValue;
                        }
                    }
                }
            });
            // 更新上线
            updateMode();
        }
        updateSwitchState(switchState);
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
                iv_one_switch.setImageResource(R.drawable.switch_off);
                iv_one_switch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                scoket_image.setImageResource(R.drawable.icon_device_16_off);
                state_text.setText(R.string.Device_Lock_Widget_Gatestatusa);
                break;
            case "1":
                openState = 1;
                iv_one_switch.setImageResource(R.drawable.switch_on);
                iv_one_switch.setBackgroundResource(R.drawable.home_widget_circle_green);
                scoket_image.setImageResource(R.drawable.icon_device_16_on);
                state_text.setText(R.string.Device_Lock_Widget_Gatestaopen);
                break;
        }
        pr_one_switch.setState(ProgressRing.FINISHED);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            textState.setText(R.string.Device_Online);
            textState.setTextColor(getResources().getColor(R.color.colorPrimary));
            iv_one_switch.setEnabled(true);
        } else {
            // 离线
            openState = 0;
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
            iv_one_switch.setImageResource(R.drawable.switch_off);
            iv_one_switch.setBackgroundResource(R.drawable.home_widget_circle_gray);
            iv_one_switch.setEnabled(false);
            scoket_image.setImageResource(R.drawable.icon_device_16_off);
        }
    }

    private String switchState = null;

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
//                    sendCmd(2);//获取所有状态信息
                    // 更新上线
                } else if (event.device.mode == 0) {
                    switchState = null;
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 6) {
                                if (attribute.attributeId == 0) {
                                    switchState = attribute.attributeValue;
                                }
                            }
                        }
                    });
                    //必须先更新开关状态，再更新数值，因为实时功率的显示依赖开关状态
                    updateSwitchState(switchState);
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
            case R.id.one_switch_image:
                iv_one_switch.setEnabled(false);
                switch (openState) {
                    case 0:
                        sendCmd(1);
                        break;
                    case 1:
                        sendCmd(0);
                        break;
                }
                pr_one_switch.setTimeout(5000);
                pr_one_switch.setState(ProgressRing.WAITING);
                pr_one_switch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        iv_one_switch.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_one_switch.setEnabled(true);
                    }
                });
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
            object.put("clusterId", 6);
            object.put("commandId", commandId);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

}
