package cc.wulian.smarthomev6.main.home.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by luzx on 2018/2/27
 * Function: 1/2场景调光 首页widget
 */

public class HomeWidget_Be_RegulateSwitch extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_Be_RegulateSwitch.class.getSimpleName();
    private Context mContext;
    private TextView tv_name, tv_sate, tv_room, tv_light;
    private View btn_min, btn_max;
    private SeekBar regulateSeekBar;
    private Device mDevice;
    private Handler mHandler = new Handler();

    public HomeWidget_Be_RegulateSwitch(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Be_RegulateSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_be_regulate_switch, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);
        regulateSeekBar = (SeekBar) rootView.findViewById(R.id.regulate_seek_bar);
        tv_light = (TextView) rootView.findViewById(R.id.light_text);
        btn_min = rootView.findViewById(R.id.icon_sun_samll);
        btn_max = rootView.findViewById(R.id.icon_sun);
        btn_min.setOnClickListener(this);
        btn_max.setOnClickListener(this);
        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);
        regulateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
                    tv_light.setText(progress + "%");
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int percent = seekBar.getProgress();
                if (percent == 0) {
                    sendCmd(0x03, 0x0008, 1, null);
                } else {
                    JSONArray ja = new JSONArray();
                    ja.put(percent + "");
                    sendCmd(0x08, 0x0008, 1, ja);
                }
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv_light.setText("");
//                    }
//                }, 1000);

            }
        });
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        // 设置在线离线的状态
        updateMode();
        setName();
        setRoomName();
        updateMode();
        dealData(mDevice);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void setName() {
        String name = DeviceInfoDictionary.getNameByDevice(mDevice);
        tv_name.setText(name);
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice.roomID);
        tv_room.setText(areaName);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            tv_sate.setText(R.string.Device_Online);
            regulateSeekBar.setEnabled(true);
            tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            // 离线
            tv_sate.setText(R.string.Device_Offline);
            tv_sate.setTextColor(getResources().getColor(R.color.newStateText));
            regulateSeekBar.setEnabled(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                updateMode();
                dealData(mDevice);
            }
        }
    }

    private void dealData(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (cluster.clusterId == 0x0008) {
                    if (attribute.attributeId == 0x0011) {
                        if (!TextUtils.isEmpty(attribute.attributeValue)) {
                            int percent = Integer.valueOf(attribute.attributeValue);
                            updateSeekbarProgress(percent);
                        }
                    }
                }
            }
        });
    }

    private ValueAnimator animator;
    private void updateSeekbarProgress(int percent) {
        int currentProgress = regulateSeekBar.getProgress();
        if (percent != currentProgress) {
            if(animator!=null && animator.isRunning()){
                animator.cancel();
            }
            animator = ValueAnimator.ofInt(currentProgress, percent);
            animator.setDuration(400);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    regulateSeekBar.setProgress((Integer) animation.getAnimatedValue());
                }
            });
            animator.start();
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

    private void sendCmd(int commandId, int clusterId, int endpointNumber, JSONArray parameter) {
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
            if (parameter != null) {
                object.put("parameter", parameter);
            }
            object.put("clusterId", clusterId);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_max) {
            JSONArray ja = new JSONArray();
            ja.put("100");
            sendCmd(0x08, 0x0008, 1, ja);
        } else if (v == btn_min) {
            sendCmd(0x03, 0x0008, 1, null);
        }
    }
}
