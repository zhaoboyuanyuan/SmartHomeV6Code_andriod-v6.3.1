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
 * Created by 上海滩小马哥 on 2018/02/23.
 * 六路开关模组
 */

public class HomeWidget_Ax_Switch extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private Context mContext;
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textState, textArea;
    private TextView textSwitchState_one, textSwitchState_two, textSwitchState_three;
    private ImageView iv_switch_one, iv_switch_two, iv_switch_three;
    private ProgressRing pr_switch_one, pr_switch_two, pr_switch_three;
    private String toastText;
    /**
     * 开关状态：-1 不可用，0 关，1 开
     */
    private int openStateOne = -1;
    private int openStateTwo = -1;
    private int openStateThree = -1;

    public HomeWidget_Ax_Switch(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Ax_Switch(Context context, AttributeSet attrs) {
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
        initState();
        sendCmd(-1, 0x8008);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_ax_switch, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);
        textSwitchState_one = (TextView) rootView.findViewById(R.id.textSwitchState_one);
        textSwitchState_two = (TextView) rootView.findViewById(R.id.textSwitchState_two);
        textSwitchState_three = (TextView) rootView.findViewById(R.id.textSwitchState_three);
        iv_switch_one = (ImageView) rootView.findViewById(R.id.switch_image_one);
        iv_switch_two = (ImageView) rootView.findViewById(R.id.switch_image_two);
        iv_switch_three = (ImageView) rootView.findViewById(R.id.switch_image_three);
        pr_switch_one = (ProgressRing) rootView.findViewById(R.id.switch_progress_one);
        pr_switch_two = (ProgressRing) rootView.findViewById(R.id.switch_progress_two);
        pr_switch_three = (ProgressRing) rootView.findViewById(R.id.switch_progress_three);


        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        iv_switch_one.setOnClickListener(this);
        iv_switch_two.setOnClickListener(this);
        iv_switch_three.setOnClickListener(this);
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

            textState.setText(R.string.Device_Online);
            textState.setTextColor(getResources().getColor(R.color.colorPrimary));
            textSwitchState_one.setText(R.string.widget_door_open);
            textSwitchState_two.setText(R.string.widget_door_open);
            textSwitchState_three.setText(R.string.widget_door_open);
            iv_switch_one.setImageResource(R.drawable.switch_on);
            iv_switch_two.setImageResource(R.drawable.switch_on);
            iv_switch_three.setImageResource(R.drawable.switch_on);
            iv_switch_one.setBackgroundResource(R.drawable.home_widget_circle_green);
            iv_switch_two.setBackgroundResource(R.drawable.home_widget_circle_green);
            iv_switch_three.setBackgroundResource(R.drawable.home_widget_circle_green);
            iv_switch_one.setEnabled(true);
            iv_switch_two.setEnabled(true);
            iv_switch_three.setEnabled(true);
            textSwitchState_one.setVisibility(VISIBLE);
            textSwitchState_two.setVisibility(VISIBLE);
            textSwitchState_three.setVisibility(VISIBLE);
        } else {
            // 离线
            openStateOne = -1;
            openStateTwo = -1;
            openStateThree = -1;
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
            textSwitchState_one.setText(R.string.widget_door_closed);
            textSwitchState_two.setText(R.string.widget_door_closed);
            textSwitchState_three.setText(R.string.widget_door_closed);
            iv_switch_one.setImageResource(R.drawable.switch_off);
            iv_switch_two.setImageResource(R.drawable.switch_off);
            iv_switch_three.setImageResource(R.drawable.switch_off);
            iv_switch_one.setBackgroundResource(R.drawable.home_widget_circle_gray);
            iv_switch_two.setBackgroundResource(R.drawable.home_widget_circle_gray);
            iv_switch_three.setBackgroundResource(R.drawable.home_widget_circle_gray);
            iv_switch_one.setEnabled(false);
            iv_switch_two.setEnabled(false);
            iv_switch_three.setEnabled(false);
            textSwitchState_one.setVisibility(GONE);
            textSwitchState_two.setVisibility(GONE);
            textSwitchState_three.setVisibility(GONE);
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        textArea.setText(areaName);
    }

    private void initState() {
        EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (endpoint.endpointNumber == 1) {
//                    if (cluster.clusterId == 6) {
                    if (attribute.attributeId == 0) {
                        if (TextUtils.equals(attribute.attributeValue, "1")) {
                            openStateOne = 1;
                        } else {
                            openStateOne = 0;
                        }
                        updateSwitchState(1, openStateOne);
                    }
//                    }
                } else if (endpoint.endpointNumber == 2) {
//                    if (cluster.clusterId == 6) {
                    if (attribute.attributeId == 0) {
                        if (TextUtils.equals(attribute.attributeValue, "1")) {
                            openStateTwo = 1;
                        } else {
                            openStateTwo = 0;
                        }
                        updateSwitchState(2, openStateOne);
                    }
//                    }
                } else if (endpoint.endpointNumber == 3) {
//                    if (cluster.clusterId == 6) {
                    if (attribute.attributeId == 0) {
                        if (TextUtils.equals(attribute.attributeValue, "1")) {
                            openStateThree = 1;
                        } else {
                            openStateThree = 0;
                        }
                        updateSwitchState(3, openStateOne);
                    }
//                    }
                }
            }
        });
    }

    /**
     * 更新开关状态
     */
    private void updateSwitchState(int endpointNum, int state) {
        if (state == -1) {
            return;
        }
        switch (state) {
            case 0:
                if (endpointNum == 1) {
                    pr_switch_one.setState(ProgressRing.FINISHED);
                    openStateOne = 0;
                    textSwitchState_one.setText(R.string.widget_door_closed);
                    textSwitchState_one.setTextColor(mContext.getResources().getColor(R.color.gray));
                    iv_switch_one.setImageResource(R.drawable.switch_off);
                    iv_switch_one.setBackgroundResource(R.drawable.home_widget_circle_gray);
                } else if (endpointNum == 2) {
                    pr_switch_two.setState(ProgressRing.FINISHED);
                    openStateTwo = 0;
                    textSwitchState_two.setText(R.string.widget_door_closed);
                    textSwitchState_two.setTextColor(mContext.getResources().getColor(R.color.gray));
                    iv_switch_two.setImageResource(R.drawable.switch_off);
                    iv_switch_two.setBackgroundResource(R.drawable.home_widget_circle_gray);
                } else if (endpointNum == 3) {
                    pr_switch_three.setState(ProgressRing.FINISHED);
                    openStateThree = 0;
                    textSwitchState_three.setText(R.string.widget_door_closed);
                    textSwitchState_three.setTextColor(mContext.getResources().getColor(R.color.gray));
                    iv_switch_three.setImageResource(R.drawable.switch_off);
                    iv_switch_three.setBackgroundResource(R.drawable.home_widget_circle_gray);
                }
                break;
            case 1:
                if (endpointNum == 1) {
                    pr_switch_one.setState(ProgressRing.FINISHED);
                    openStateOne = 1;
                    textSwitchState_one.setText(R.string.widget_door_open);
                    textSwitchState_one.setTextColor(mContext.getResources().getColor(R.color.v6_green));
                    iv_switch_one.setImageResource(R.drawable.switch_on);
                    iv_switch_one.setBackgroundResource(R.drawable.home_widget_circle_green);
                } else if (endpointNum == 2) {
                    pr_switch_two.setState(ProgressRing.FINISHED);
                    openStateTwo = 1;
                    textSwitchState_two.setText(R.string.widget_door_open);
                    textSwitchState_two.setTextColor(mContext.getResources().getColor(R.color.v6_green));
                    iv_switch_two.setImageResource(R.drawable.switch_on);
                    iv_switch_two.setBackgroundResource(R.drawable.home_widget_circle_green);
                } else if (endpointNum == 3) {
                    pr_switch_three.setState(ProgressRing.FINISHED);
                    openStateThree = 1;
                    textSwitchState_three.setText(R.string.widget_door_open);
                    textSwitchState_three.setTextColor(mContext.getResources().getColor(R.color.v6_green));
                    iv_switch_three.setImageResource(R.drawable.switch_on);
                    iv_switch_three.setBackgroundResource(R.drawable.home_widget_circle_green);
                }
                break;
        }
    }

    /**
     * @param commandId 3:关闭；4：打开；0x8008:查询
     */
    private void sendCmd(int endpointNumber, int commandId) {
        if (mDevice.isOnLine()) {
            JSONObject object = new JSONObject();
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 6);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            if (endpointNumber != -1) {
                object.put("endpointNumber", endpointNumber);
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
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode();
                } else if (event.device.mode == 1) {
                    updateMode();
                    sendCmd(-1, 0x8008);//获取所有状态信息
                    // 上线
                } else if (event.device.mode == 0) {
                    // 更新
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (endpoint.endpointNumber == 1) {
//                                if (cluster.clusterId == 6) {
                                if (attribute.attributeId == 0) {
                                    if (TextUtils.equals(attribute.attributeValue, "1")) {
                                        openStateOne = 1;
                                    } else {
                                        openStateOne = 0;
                                    }
                                    updateSwitchState(1, openStateOne);
                                }
//                                }
                            } else if (endpoint.endpointNumber == 2) {
//                                if (cluster.clusterId == 6) {
                                if (attribute.attributeId == 0) {
                                    if (TextUtils.equals(attribute.attributeValue, "1")) {
                                        openStateTwo = 1;
                                    } else {
                                        openStateTwo = 0;
                                    }
                                    updateSwitchState(2, openStateTwo);
                                }
//                                }
                            } else if (endpoint.endpointNumber == 3) {
//                                if (cluster.clusterId == 6) {
                                if (attribute.attributeId == 0) {
                                    if (TextUtils.equals(attribute.attributeValue, "1")) {
                                        openStateThree = 1;
                                    } else {
                                        openStateThree = 0;
                                    }
                                    updateSwitchState(3, openStateThree);
                                }
//                                }
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
        switch (v.getId()) {
            case R.id.switch_image_one:
                iv_switch_one.setEnabled(false);
                switch (openStateOne) {
                    case 0:
                        sendCmd(1, 4);
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        break;
                    case 1:
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        sendCmd(1, 3);
                        break;
                }
                pr_switch_one.setTimeout(5000);
                pr_switch_one.setState(ProgressRing.WAITING);
                pr_switch_one.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
//                        ToastUtil.single(toastText);
                        iv_switch_one.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_switch_one.setEnabled(true);
                    }
                });
                break;
            case R.id.switch_image_two:
                iv_switch_two.setEnabled(false);
                switch (openStateTwo) {
                    case 0:
                        sendCmd(2, 4);
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        break;
                    case 1:
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        sendCmd(2, 3);
                        break;
                }
                pr_switch_two.setTimeout(5000);
                pr_switch_two.setState(ProgressRing.WAITING);
                pr_switch_two.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
//                        ToastUtil.single(toastText);
                        iv_switch_two.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_switch_two.setEnabled(true);
                    }
                });
                break;
            case R.id.switch_image_three:
                iv_switch_three.setEnabled(false);
                switch (openStateThree) {
                    case 0:
                        sendCmd(3, 4);
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        break;
                    case 1:
                        toastText = String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime), mContext.getString(R.string.Home_Widget_Switch));
                        sendCmd(3, 3);
                        break;
                }
                pr_switch_three.setTimeout(5000);
                pr_switch_three.setState(ProgressRing.WAITING);
                pr_switch_three.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
//                        ToastUtil.single(toastText);
                        iv_switch_three.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_switch_three.setEnabled(true);
                    }
                });
                break;
        }
    }
}
