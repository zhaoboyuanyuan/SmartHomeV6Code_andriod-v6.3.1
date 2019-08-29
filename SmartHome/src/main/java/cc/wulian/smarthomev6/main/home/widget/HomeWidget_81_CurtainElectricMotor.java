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
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by luzx on 2017/10/10
 * Func:网络双轨双驱动自动窗帘首页widget
 */

public class HomeWidget_81_CurtainElectricMotor extends FrameLayout implements IWidgetLifeCycle, View.OnClickListener {

    private Context mContext;
    private TextView tv_name, tv_sate, tv_room;
    private ImageView iv_on_switch, iv_stop_switch, iv_off_switch;
    private Device mDevice;
    private HomeItemBean mHomeItemBean;
    private ViewGroup switch_layout;
    private int checkPosition = 0;

    public HomeWidget_81_CurtainElectricMotor(@NonNull Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_81_CurtainElectricMotor(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_81_curtain_electric_motor, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);

        iv_on_switch = (ImageView) rootView.findViewById(R.id.on_switch_image);
        iv_stop_switch = (ImageView) rootView.findViewById(R.id.stop_switch_image);
        iv_off_switch = (ImageView) rootView.findViewById(R.id.off_switch_image);

        switch_layout = (ViewGroup) rootView.findViewById(R.id.switch_layout);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);

        iv_on_switch.setOnClickListener(this);
        iv_stop_switch.setOnClickListener(this);
        iv_off_switch.setOnClickListener(this);
//        rootView.findViewById(R.id.root_view).setOnClickListener(this);
        initSwitchLayout(switch_layout, 0);
    }

    private void initSwitchLayout(final ViewGroup parent, int checkPosition){
        for(int i=0; i<parent.getChildCount(); i++){
            CheckedTextView text = (CheckedTextView) parent.getChildAt(i);
            text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int j=0; j<parent.getChildCount(); j++){
                        CheckedTextView checkedTextView = (CheckedTextView) parent.getChildAt(j);
                        if(checkedTextView.isChecked()){
                            checkedTextView.setChecked(false);
                            checkedTextView.setTextColor(getResources().getColor(R.color.home_widget_curtain));
                        }else{
                            HomeWidget_81_CurtainElectricMotor.this.checkPosition = j;
                            checkedTextView.setChecked(true);
                            checkedTextView.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                }
            });
            if(i == checkPosition){
                this.checkPosition = checkPosition;
                text.setChecked(true);
                text.setTextColor(getResources().getColor(R.color.white));
            }else{
                text.setChecked(false);
                text.setTextColor(getResources().getColor(R.color.home_widget_curtain));
            }
        }
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());

        // 设置标题
        updateMode();
        setName();
        setRoomName();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void dealDevice(String json) {
        updateMode();
        updateState();
    }

    private void setName() {
        if (mDevice == null) {
            tv_name.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            tv_name.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        tv_room.setText(areaName);
    }

    /**
     * 发出控制指令后, 更新状态
     */
    private void updateState() {

    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        switch (mDevice.mode) {
            case 0:
                break;
            case 4:
                break;
            case 1:
                // 上线
                tv_sate.setText(R.string.Device_Online);
                tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));
                iv_on_switch.setEnabled(true);
                iv_off_switch.setEnabled(true);
                iv_stop_switch.setEnabled(true);
                iv_on_switch.setImageResource(R.drawable.home_widget_open);
                iv_off_switch.setImageResource(R.drawable.home_widget_close);
                iv_stop_switch.setImageResource(R.drawable.home_widget_stop);
                for(int i=0; i<switch_layout.getChildCount(); i++){
                    CheckedTextView text = (CheckedTextView) switch_layout.getChildAt(i);
                    text.setEnabled(true);
                    if(i == checkPosition){
                        text.setChecked(true);
                        text.setTextColor(getResources().getColor(R.color.white));
                    }else{
                        text.setChecked(false);
                        text.setTextColor(getResources().getColor(R.color.home_widget_curtain));
                    }
                }
                break;
            case 2:
                // 离线
                tv_sate.setText(R.string.Device_Offline);
                tv_sate.setTextColor(getResources().getColor(R.color.newStateText));
                iv_on_switch.setEnabled(false);
                iv_off_switch.setEnabled(false);
                iv_stop_switch.setEnabled(false);
                iv_on_switch.setImageResource(R.drawable.icon_open_offline);
                iv_off_switch.setImageResource(R.drawable.icon_colse_offline);
                iv_stop_switch.setImageResource(R.drawable.icon_stop_offline);
                for(int i=0; i<switch_layout.getChildCount(); i++){
                    CheckedTextView text = (CheckedTextView) switch_layout.getChildAt(i);
                    text.setEnabled(false);
                    text.setTextColor(getResources().getColor(R.color.home_widget_curtain_offline));
                }
                break;
            case 3:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
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
                dealDevice(event.device.data);
                setName();
            }
        }else{
            mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
            if(mDevice != null){
                dealDevice(mDevice.data);
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
            case R.id.on_switch_image:
                sendCmd(0x00);
                break;
            case R.id.stop_switch_image:
                sendCmd(0x02);
                break;
            case R.id.off_switch_image:
                sendCmd(0x01);
                break;
            case R.id.root_view:
                DeviceInfoDictionary.showDetail(mContext, mDevice);
                break;
        }
    }

    private void sendCmd(int commandId) {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 0x0102);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            if(checkPosition == 0){
                object.put("endpointNumber", 1);
            }else if(checkPosition == 1){
                object.put("endpointNumber", 2);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
