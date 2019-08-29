package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.BottomMenu;
import cc.wulian.smarthomev6.support.customview.BottomMenu02;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.WLog;

import static java.lang.Thread.sleep;

/**
 * Created by Wulian on 2018/9/5.
 */

public class BsSettingMoreView extends FrameLayout implements IDeviceMore, View.OnClickListener, CompoundButton.OnCheckedChangeListener  {
    private static final String BS_SETTING = "bs_setting";

    private ToggleButton tbAvoidCold;
    private RelativeLayout rlAvoidTip;
    private RelativeLayout rlAvoidCold;
    private RelativeLayout rlLeaveMode;
    private RelativeLayout rlTempGround;
    private TextView tvAvoidTip;
    private TextView tvLeaveMode;
    private TextView tvTempGround;
    private TextView tvTempGroundMode;

    private Context mContext;
    private BottomMenu avoidBottomMenu;
    private BottomMenu leaveBottomMenu;
    private BottomMenu02 tempGroundBottomMenu;

    private String deviceID;
    private String gwID;
    private Device device;
    private ArrayList avoidTipData;
    private ArrayList leaveData;
    private ArrayList tempGroundData;
    private ArrayList tempGroundModesData;
    private int groundTempMode = 0;


    public BsSettingMoreView(Context context, String deviceID, String gwID) {
        super(context);
        this.mContext = context;
        this.gwID = gwID;
        this.deviceID = deviceID;
        initView(context);
        initListener();
        initData();
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bs, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tbAvoidCold = rootView.findViewById(R.id.tb_avoid_cold);
        tvAvoidTip = rootView.findViewById(R.id.tv_avoid_tip);
        tvLeaveMode = rootView.findViewById(R.id.tv_leave_mode);
        tvTempGround = rootView.findViewById(R.id.tv_temp_ground);
        tvTempGroundMode = rootView.findViewById(R.id.tv_temp_ground_mode);
        rlAvoidTip = rootView.findViewById(R.id.rl_avoid_tip);
        rlAvoidCold = rootView.findViewById(R.id.rl_avoid_cold);
        rlLeaveMode = rootView.findViewById(R.id.rl_mode_leave);
        rlTempGround = rootView.findViewById(R.id.rl_temp_ground);
        initAvoidTipView();
        initLeaveView();
        initTempGroundView();

    }

    private void initListener() {
        rlAvoidTip.setOnClickListener(this);
        rlLeaveMode.setOnClickListener(this);
        rlTempGround.setOnClickListener(this);
        tbAvoidCold.setOnCheckedChangeListener(this);
    }

    private void updateMode() {
        rlAvoidTip.setEnabled(device.isOnLine() ? true : false);
        rlLeaveMode.setEnabled(device.isOnLine() ? true : false);
        rlTempGround.setEnabled(device.isOnLine() ? true : false);
        tbAvoidCold.setEnabled(device.isOnLine() ? true : false);
        rlAvoidTip.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        rlLeaveMode.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        rlTempGround.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        rlAvoidCold.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        tbAvoidCold.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
    }

    private void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        }

        updateMode();
        //查询当前状态
        sendCmd(0x0101, null);
    }

    private String formatTemperature(Float temperature){
        String tempFormat = "";
        if (temperature<10){
            tempFormat = "00"+(int)(temperature*10);
        }else{
            tempFormat = "0"+(int)(temperature*10);
        }
        return tempFormat;
    }

    private void initAvoidTipView() {
        avoidBottomMenu = new BottomMenu(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                tvAvoidTip.setText(avoidBottomMenu.getCurrentItem());
                Float temperature = avoidBottomMenu.getCurrent()*0.5f+5;
                sendCmd(0x0202, formatTemperature(temperature));
            }

            @Override
            public void onCancel() {

            }
        });
        avoidTipData = new ArrayList();
        for (int i = 0; i <= 24; i++) {
            avoidTipData.add((i*0.5f+5) + "℃");
        }
        avoidBottomMenu.setData(avoidTipData);
    }

    private void initLeaveView() {
        leaveBottomMenu = new BottomMenu(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                tvLeaveMode.setText(leaveBottomMenu.getCurrentItem());
                Float temperature = leaveBottomMenu.getCurrent()*0.5f+5;
                sendCmd(0x0105, formatTemperature(temperature));
            }

            @Override
            public void onCancel() {

            }
        });
        leaveData = new ArrayList();
        for (int i = 0; i <= 50; i++) {
            leaveData.add((i*0.5f+5) + "℃");
        }
        leaveBottomMenu.setData(leaveData);
    }

    private void initTempGroundView() {
        tempGroundBottomMenu = new BottomMenu02(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                tvTempGroundMode.setText(tempGroundBottomMenu.getLeftCurrentItem());
                tvTempGround.setText(tempGroundBottomMenu.getRightCurrentItem());

                Float temperature = tempGroundBottomMenu.getRightCurrent()*0.5f+20;
                //设置地面温度限制模式
                sendCmd(0x0108, String.valueOf(tempGroundBottomMenu.getLeftCurrent()+1));
//                synchronized (Thread.currentThread()){
                    try {
                        sleep(20);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
//                }
                //设置限定值
                sendCmd(0x0109, formatTemperature(temperature));
            }

            @Override
            public void onCancel() {

            }
        });

        tempGroundData = new ArrayList();
        tempGroundModesData = new ArrayList();
        for (int i = 0; i <= 50; i++) {
            tempGroundData.add((i*0.5f+20) + "℃");
        }
        String[] tempGroundModes = {mContext.getString(R.string.Bs_More_Limit_02), mContext.getString(R.string.Bs_More_Limit_03)};
        for (int i = 0; i < tempGroundModes.length; i++) {
            tempGroundModesData.add(tempGroundModes[i]);
        }

        tempGroundBottomMenu.setData(tempGroundModesData, tempGroundData);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_avoid_tip:
                avoidBottomMenu.setTitle(mContext.getString(R.string.Bs_More_Antifreeze_01));
                avoidBottomMenu.show(view);
                break;
            case R.id.rl_mode_leave:
                leaveBottomMenu.setTitle(mContext.getString(R.string.Device_Widget_Bm_Temperture1));
                leaveBottomMenu.show(view);
                break;
            case R.id.rl_temp_ground:
                tempGroundBottomMenu.setTitle(mContext.getString(R.string.Bs_More_Limit_01));
                tempGroundBottomMenu.show(view);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.tb_avoid_cold:
                if (isChecked){
                    sendCmd(0x0201, "1");
                    rlAvoidTip.setVisibility(View.VISIBLE);
                }else{
                    sendCmd(0x0201, "2");
                    rlAvoidTip.setVisibility(View.GONE);
                }
                break;
        }

    }

    private void sendCmd(int commandId, String parameter) {
        ProgressDialogManager.getDialogManager().showDialog(BS_SETTING, mContext, null, null, getResources().getInteger(R.integer.http_timeout));
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", gwID);
            object.put("devID", deviceID);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("clusterId", 0x0201);
            if (parameter != null) {
                JSONArray array = new JSONArray();
                array.put(parameter);
                object.put("parameter", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }

    private void dealDevice(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (cluster.clusterId == 0x0201) {
                    updateViews(attribute.attributeId, attribute.attributeValue);
                }
            }
        });
    }

    private void updateViews(int attributeId, String attributeValue) {
        switch (attributeId) {
            case 0x810A:
                float avoidCold = Float.parseFloat(attributeValue);
                tvAvoidTip.setText(avoidCold + "℃");
                avoidBottomMenu.setCurrent((int)((avoidCold-5)*2));
                break;
            case 0x8109:
                int avoidColdOpened = Integer.parseInt(attributeValue);
                if (avoidColdOpened == 1){
                    tbAvoidCold.setChecked(true);
                    rlAvoidTip.setVisibility(View.VISIBLE);
                }else{
                    tbAvoidCold.setChecked(false);
                    rlAvoidTip.setVisibility(View.GONE);
                }
                break;
            case 0x8104:
                float leaveTip = Float.parseFloat(attributeValue);
                tvLeaveMode.setText(leaveTip + "℃");
                leaveBottomMenu.setCurrent((int)((leaveTip-5)*2));
                break;
            case 0x8108:
                float groundTempTip = Float.parseFloat(attributeValue);
                tvTempGround.setText(groundTempTip + "℃");
                tempGroundBottomMenu.setRightCurrent((int)((groundTempTip-20)*2));
                break;
            case 0x8107:
                groundTempMode = Integer.parseInt(attributeValue);
                if (groundTempMode == 1){
                    tvTempGroundMode.setText(R.string.Bs_More_Limit_02);
                    tempGroundBottomMenu.setLeftCurrent(0);
                }else if(groundTempMode == 2){
                    tvTempGroundMode.setText(R.string.Bs_More_Limit_03);
                    tempGroundBottomMenu.setLeftCurrent(1);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        ProgressDialogManager.getDialogManager().dimissDialog(BS_SETTING, 0);
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                device = MainApplication.getApplication().getDeviceCache().get(deviceID);
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode();
                } else if (event.device.mode == 1) {
                    // 更新上线
                    updateMode();
                } else if (event.device.mode == 0) {
                    dealDevice(event.device);
                    updateMode();
                }
            }
        }
    }
}