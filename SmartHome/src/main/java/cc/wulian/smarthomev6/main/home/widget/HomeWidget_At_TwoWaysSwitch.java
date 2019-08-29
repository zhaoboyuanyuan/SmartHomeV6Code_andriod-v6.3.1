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
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.ProgressRing;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * 作者: chao
 * 时间: 2017/7/20
 * 描述: 内嵌式零火二路开关首页widget
 * 联系方式: 805901025@qq.com
 */

public class HomeWidget_At_TwoWaysSwitch extends FrameLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final int SWITCH_TYPE = 0x0006;
    private Context mContext;
    private TextView tv_name, tv_sate, tv_room;
    private TextView tv_one_switch_name, tv_two_switch_name;
    private TextView tv_toast;
    private ImageView iv_one_switch, iv_two_switch;
    private ProgressRing pr_one_switch, pr_two_switch;
    private RelativeLayout rl_title;
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private boolean isOpenCommand_oneSwitch = false;
    private boolean isOpenCommand_twoSwitch = false;

    public HomeWidget_At_TwoWaysSwitch(@NonNull Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_At_TwoWaysSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_an_two_ways_switch, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);
        tv_toast = (TextView) rootView.findViewById(R.id.text_toast);
        rl_title = (RelativeLayout) rootView.findViewById(R.id.widget_relative_title);

        iv_one_switch = (ImageView) rootView.findViewById(R.id.one_switch_image);
        iv_two_switch = (ImageView) rootView.findViewById(R.id.two_switch_image);
        tv_one_switch_name = (TextView) rootView.findViewById(R.id.one_switch_name);
        tv_two_switch_name = (TextView) rootView.findViewById(R.id.two_switch_name);

        pr_one_switch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);
        pr_two_switch = (ProgressRing) rootView.findViewById(R.id.two_switch_progress);

        tv_one_switch_name.setText(context.getString(R.string.Home_Widget_Switch) + "1");
        tv_two_switch_name.setText(context.getString(R.string.Home_Widget_Switch) + "2");

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);

        iv_one_switch.setOnClickListener(this);
        iv_two_switch.setOnClickListener(this);
        rootView.findViewById(R.id.root_view).setOnClickListener(this);
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
        setName();
        setRoomName();
        sendCmd(0, 2);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
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
     * 数据解析
     */
    private void dealDevice(String json) {
        try {
            JSONObject object = new JSONObject(json);
            Log.i("mamc", json);
            JSONArray endpoints = object.getJSONArray("endpoints");
            for(int i=0; i < endpoints.length(); i++){
                JSONArray clusters = ((JSONObject) endpoints.get(i)).getJSONArray("clusters");
                String switchName = ((JSONObject) endpoints.get(i)).optString("endpointName");
                int endpointNumber = ((JSONObject) endpoints.get(i)).optInt("endpointNumber");

                JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
                int attributeId = ((JSONObject) attributes.get(0)).getInt("attributeId");
                int attributeValue = ((JSONObject) attributes.get(0)).getInt("attributeValue");
                updateState(endpointNumber, attributeValue, switchName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新状态
     */
    private void updateState(int index, int state, String name) {
        ImageView iv_icon_temp = null;
        TextView tv_name_temp = null;
        switch (index){
            case 1:
                iv_icon_temp = iv_one_switch;
                tv_name_temp = tv_one_switch_name;
                pr_one_switch.setState(ProgressRing.FINISHED);
                break;
            case 2:
                iv_icon_temp = iv_two_switch;
                tv_name_temp = tv_two_switch_name;
                pr_two_switch.setState(ProgressRing.FINISHED);
                break;
        }

        if(iv_icon_temp == null || tv_name_temp == null){
            return;
        }

        if (!TextUtils.isEmpty(name)){
            tv_name_temp.setText(name);
        }
        if(mDevice.isOnLine()){
            // 上线
            tv_sate.setText(R.string.Device_Online);
            tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));
            iv_icon_temp.setEnabled(true);
            iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_green);
            switch (state){
                case 0:
                    iv_icon_temp.setImageResource(R.drawable.switch_off);
                    iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);
                    if (iv_icon_temp == iv_one_switch){
                        isOpenCommand_oneSwitch = true;
                    }else {
                        isOpenCommand_twoSwitch = true;
                    }
                    break;
                case 1:
                    iv_icon_temp.setImageResource(R.drawable.switch_on);
                    iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_green);
                    if (iv_icon_temp == iv_one_switch){
                        isOpenCommand_oneSwitch = false;
                    }else {
                        isOpenCommand_twoSwitch = false;
                    }
                    break;
            }
        }else {
            // 离线
            tv_sate.setText(R.string.Device_Offline);
            tv_sate.setTextColor(getResources().getColor(R.color.newStateText));
            iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);

            iv_icon_temp.setEnabled(false);
            iv_icon_temp.setImageResource(R.drawable.switch_off);
            iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    dealDevice(mDevice.data);
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
                sendCmd(1,3);
                pr_one_switch.setTimeout(5000);
                pr_one_switch.setState(ProgressRing.WAITING);
                pr_one_switch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        if (isOpenCommand_oneSwitch) {
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime),
                                    tv_one_switch_name.getText()));
                        } else {
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime),
                                    tv_one_switch_name.getText()));
                        }
                        iv_one_switch.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_one_switch.setEnabled(true);
                    }
                });
                break;
            case R.id.two_switch_image:
                sendCmd(2,3);
                pr_two_switch.setTimeout(5000);
                pr_two_switch.setState(ProgressRing.WAITING);
                pr_two_switch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        if (isOpenCommand_twoSwitch) {
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime),
                                    tv_two_switch_name.getText()));
                        } else {
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime),
                                    tv_two_switch_name.getText()));
                        }
                        iv_two_switch.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_one_switch.setEnabled(true);
                    }
                });
                break;
            case R.id.root_view:
                DeviceInfoDictionary.showDetail(mContext, mDevice);
                break;
        }
    }

    private void sendCmd(int id, int commandId) {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", SWITCH_TYPE);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            if (id != 0){
                object.put("endpointNumber", id);
            }

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toast(String sss) {
        tv_toast.setText(sss);
        tv_toast.setVisibility(VISIBLE);
        tv_toast.removeCallbacks(toastRun);
        tv_toast.postDelayed(toastRun, 2000);
    }

    private Runnable toastRun = new Runnable() {
        @Override
        public void run() {
            tv_toast.setVisibility(INVISIBLE);
        }
    };
}