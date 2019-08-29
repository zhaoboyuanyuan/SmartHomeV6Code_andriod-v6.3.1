package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
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

/**
 * Created by Veev on 2017/7/13
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    HomeWidget_An_TwoWaysSwitch
 */

public class HomeWidget_62_TwoWaysSwitch extends FrameLayout implements IWidgetLifeCycle, View.OnClickListener {

    private Context mContext;
    private TextView tv_name, tv_sate, tv_room;
    private TextView tv_one_switch_state, tv_two_switch_state;
    private ImageView iv_one_switch, iv_two_switch;
    private ProgressRing pr_one_switch, pr_two_switch;
    private Device mDevice;
    private TextView tv_toast;
    private boolean isOpenCommand_oneSwitch = false;
    private boolean isOpenCommand_twoSwitch = false;

    public HomeWidget_62_TwoWaysSwitch(@NonNull Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_62_TwoWaysSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_62_two_ways_switch, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);
        tv_toast = (TextView) rootView.findViewById(R.id.text_toast);
        iv_one_switch = (ImageView) rootView.findViewById(R.id.one_switch_image);
        iv_two_switch = (ImageView) rootView.findViewById(R.id.two_switch_image);

        pr_one_switch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);
        pr_two_switch = (ProgressRing) rootView.findViewById(R.id.two_switch_progress);

        tv_one_switch_state = (TextView) rootView.findViewById(R.id.one_switch_state);
        tv_two_switch_state = (TextView) rootView.findViewById(R.id.two_switch_state);
        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);

        iv_one_switch.setOnClickListener(this);
        iv_two_switch.setOnClickListener(this);
        iv_one_switch.setEnabled(false);
        iv_two_switch.setEnabled(false);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {

        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
//        Log.i("luzx", "onBindViewHolder 62:" + mDevice);
        // 设置标题
        setName();
        setRoomName();

        dealDevice(mDevice);
        sendCmd(2, 0);
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
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice.roomID);
        tv_room.setText(areaName);
    }

    /**
     * 发出控制指令后, 更新状态
     */
    private void updateState(int mode, int endpointNumber, String attributeValue) {
        ImageView iv_icon_temp = null;
        TextView tv_state_temp = null;
        Object switchState = null;
        switch (endpointNumber){
            case 1:
                iv_icon_temp = iv_one_switch;
                tv_state_temp = tv_one_switch_state;
                switchState = iv_icon_temp.getTag();
                if(attributeValue != null && !attributeValue.equals(switchState)){
                    pr_one_switch.setState(ProgressRing.FINISHED);
                }
                break;
            case 2:
                iv_icon_temp = iv_two_switch;
                tv_state_temp = tv_two_switch_state;
                switchState = iv_icon_temp.getTag();
                if(attributeValue != null && !attributeValue.equals(switchState)){
                    pr_two_switch.setState(ProgressRing.FINISHED);
                }
                break;
        }

        if(mode == 2){
            //设置离线状态
            tv_sate.setText(R.string.Device_Offline);
            tv_sate.setTextColor(getResources().getColor(R.color.newStateText));
            iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);
            iv_icon_temp.setImageResource(R.drawable.switch_off);
            iv_icon_temp.setEnabled(false);
            tv_state_temp.setVisibility(View.INVISIBLE);
        }else {
            //设置title上线状态
            tv_sate.setText(R.string.Device_Online);
            tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));
            tv_state_temp.setVisibility(View.VISIBLE);
            //设置按钮状态
            iv_icon_temp.setTag(attributeValue);
            switch (attributeValue) {
                case "0":
                    iv_icon_temp.setImageResource(R.drawable.switch_off);
                    iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);
                    tv_state_temp.setText(R.string.Device_Lock_Widget_Gatestatusa);
                    tv_state_temp.setTextColor(0xff535353);
                    break;
                case "1":
                    iv_icon_temp.setImageResource(R.drawable.switch_on);
                    iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_green);
                    tv_state_temp.setText(R.string.Home_Scene_IsOpen);
                    tv_state_temp.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
            }
            iv_icon_temp.setEnabled(true);
        }
    }

    private void setText(TextView textView, String text){
        if(text == null){
            return;
        }
        if(text.length() > 9){
            textView.setText(text.substring(0,9) + "...");
        }else{
            textView.setText(text);
        }
    }

    private void dealDevice(final Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                int endpointNumber = endpoint.endpointNumber;
                String attributeValue = attribute.attributeValue;
                updateState(device.mode, endpointNumber, attributeValue);
            }
        });
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
                if(event.device.mode == 0 || event.device.mode == 2){
                    dealDevice(event.device);
                }else if(event.device.mode == 1){
                    sendCmd(2, 0);
                }
            }
        }else{
//            Log.i("luzx", "onDeviceReport 62:" + mDevice);
//            mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
//            dealDevice(mDevice);
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
                Object one_type = iv_one_switch.getTag();
                if ("0".equals(one_type)) {
                    sendCmd(0x01, 1);//打开
                    isOpenCommand_oneSwitch = true;
                } else {
                    sendCmd(0x00, 1);//关闭
                    isOpenCommand_oneSwitch = false;
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
            case R.id.two_switch_image:
                Object two_type = iv_two_switch.getTag();
                iv_two_switch.setEnabled(false);
                if ("0".equals(two_type)) {
                    sendCmd(0x01, 2);//打开
                    isOpenCommand_twoSwitch = true;
                } else {
                    sendCmd(0x00, 2);//关闭
                    isOpenCommand_twoSwitch = false;
                }
                pr_two_switch.setTimeout(5000);
                pr_two_switch.setState(ProgressRing.WAITING);
                pr_two_switch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        iv_two_switch.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_two_switch.setEnabled(true);
                    }
                });
                break;
        }
    }

    private void sendCmd(int commandId, int endpointNumber) {
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
            object.put("endpointNumber", endpointNumber);
            object.put("clusterId", 0x0006);
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

    private void toast(@StringRes int sss) {
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
