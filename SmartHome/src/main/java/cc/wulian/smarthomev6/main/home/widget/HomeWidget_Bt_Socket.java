package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.StringRes;
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

import com.alibaba.fastjson.util.Base64;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_Ao.DevAoForChooseBindActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;
import cc.wulian.smarthomev6.support.customview.ProgressRing;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;

/**
 * Created by huxc on 2017/7/13
 * Function: 新连排插座 首页widget
 */

public class HomeWidget_Bt_Socket extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_Bt_Socket.class.getSimpleName();
    private Context mContext;
    private TextView tvName, tvState, tvRoom;
    private TextView tvSocketState;
    private ImageView ivOneSocket;
    private ProgressRing prOneSocket;
    private Device mDevice;
    private TextView tv_toast;
    private String socketState;
    /**
     * 开关状态：0关闭 1打开
     */
    private int openState = 0;

    public HomeWidget_Bt_Socket(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Bt_Socket(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_bt_socket, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tvName = (TextView) rootView.findViewById(R.id.widget_title_name);
        tvRoom = (TextView) rootView.findViewById(R.id.widget_title_room);
        tvState = (TextView) rootView.findViewById(R.id.widget_title_state);

        tv_toast = (TextView) rootView.findViewById(R.id.text_toast);

        ivOneSocket = (ImageView) rootView.findViewById(R.id.one_socket_image);
        prOneSocket = (ProgressRing) rootView.findViewById(R.id.one_socket_progress);
        tvSocketState = (TextView) rootView.findViewById(R.id.socket_state);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tvName.setMaxWidth(titleH / 2);
        tvRoom.setMaxWidth(titleH / 4);

        ivOneSocket.setOnClickListener(this);
        ivOneSocket.setEnabled(true);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        setName();
        setRoomName();
        updateMode();
        //查询
        sendCmd(0x02);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void setName() {
        String name = DeviceInfoDictionary.getNameByDevice(mDevice);
        tvName.setText(name);
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice.roomID);
        tvRoom.setText(areaName);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            tvState.setText(R.string.Device_Online);
            tvState.setTextColor(getResources().getColor(R.color.colorPrimary));
            ivOneSocket.setEnabled(true);
        } else {
            // 离线
            tvState.setText(R.string.Device_Offline);
            tvState.setTextColor(getResources().getColor(R.color.newStateText));
            ivOneSocket.setImageResource(R.drawable.switch_off);
            ivOneSocket.setBackgroundResource(R.drawable.home_widget_circle_gray);
            ivOneSocket.setEnabled(false);
        }
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
                ivOneSocket.setImageResource(R.drawable.switch_off);
                ivOneSocket.setBackgroundResource(R.drawable.home_widget_circle_gray);
                tvSocketState.setText(R.string.Device_switch_Widget_Off);
                break;
            case "1":
                openState = 1;
                ivOneSocket.setImageResource(R.drawable.switch_on);
                ivOneSocket.setBackgroundResource(R.drawable.home_widget_circle_green);
                tvSocketState.setText(R.string.Device_switch_Widget_On);
                break;
            case "F":
                toast("命令异常");
                break;
        }
        prOneSocket.setState(ProgressRing.FINISHED);
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
                    // 更新上线
                } else if (event.device.mode == 0) {
                    socketState = null;
                    updateMode();
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 6) {
                                if (attribute.attributeId == 0) {
                                    socketState = attribute.attributeValue;
                                    updateSwitchState(socketState);
                                }
                            }
                        }
                    });
                }
            }
        }
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
            case R.id.one_socket_image:
                ivOneSocket.setEnabled(false);
                sendCmd(openState == 0 ? 0x01 : 0x00);

                prOneSocket.setTimeout(5000);
                prOneSocket.setState(ProgressRing.WAITING);
                prOneSocket.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        ivOneSocket.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        ivOneSocket.setEnabled(true);
                    }
                });
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
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);
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
