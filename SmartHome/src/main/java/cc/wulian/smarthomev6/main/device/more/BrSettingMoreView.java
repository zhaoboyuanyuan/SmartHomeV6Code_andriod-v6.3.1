package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * Created by Wulian on 2018/8/15.
 */

public class BrSettingMoreView extends FrameLayout implements IDeviceMore, View.OnClickListener {
    private static final String BR_SETTING = "br_setting";

    private RelativeLayout rlFilterTip;
    private RelativeLayout rlBacklashSet;
    private RelativeLayout rlTempCheck;
    private RelativeLayout rlReset;
    private TextView tvFilterTip;
    private TextView tvBacklashSet;
    private TextView tvTempCheck;
//    private Button btnReset;

    private Context mContext;
    private WLDialog resetDialog;
    private WLDialog.Builder builder;
    private BottomMenu filterBottomMenu;
    private BottomMenu backlashBottomMenu;
    private BottomMenu tempCheckBottomMenu;

    private String deviceID;
    private String gwID;
    private Device device;
    private ArrayList filterTipData;
    private ArrayList backlashData;
    private ArrayList tempCheckData;
    private static String[] backlashKeyArray = {"1.0℃", "2.0℃", "3.0℃", "4.0℃", "5.0℃", "6.0℃", "7.0℃", "8.0℃", "9.0℃", "10.0℃"};
    private static String[] backlashValueArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10"};
    private static int[] filterKeyArray = {R.string.Device_Widget_82_Speed5, R.string.Device_Af_Bulltebox_01, R.string.Device_Af_Bulltebox_02, R.string.Device_Af_Bulltebox_03, R.string.Device_Af_Bulltebox_04};
    private static String[] filterValueArray = {"0", "3", "6", "9", "12"};


    public BrSettingMoreView(Context context, String deviceID, String gwID) {
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_br, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tvFilterTip = rootView.findViewById(R.id.tv_filter_tip);
        tvBacklashSet = rootView.findViewById(R.id.tv_backlash_set);
        tvTempCheck = rootView.findViewById(R.id.tv_temperature_check);
        rlFilterTip = rootView.findViewById(R.id.rl_filter_tip);
        rlBacklashSet = rootView.findViewById(R.id.rl_backlash_set);
        rlTempCheck = rootView.findViewById(R.id.rl_temp_check);
        rlReset = rootView.findViewById(R.id.rl_reset);
//        btnReset = rootView.findViewById(R.id.btn_reset);
        initFilterTipView();
        initBacklashView();
        initTempCheckView();

    }

    private float tempIndexToTempValue(int index) {
        return index * 0.5f - 5;
    }

    private int hexStringToTempValue(String hexString) {
        int value = Integer.parseInt(hexString, 10);
        if ((value & 0x80) > 0) {
            value = value - 0x100;
        }
        return value;
    }

    private int hexStringToTempIndex(String hexString) {
        return hexStringToTempValue(hexString) / 5 + 10;
    }

    private void initListener() {
        rlFilterTip.setOnClickListener(this);
        rlBacklashSet.setOnClickListener(this);
        rlTempCheck.setOnClickListener(this);
        rlReset.setOnClickListener(this);
//        btnReset.setOnClickListener(this);
    }


    private void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        }
        updateMode();
        //查询当前状态
        sendCmd(0x0100, "");
    }


    private void updateMode() {
        rlFilterTip.setEnabled(device.isOnLine() ? true : false);
        rlBacklashSet.setEnabled(device.isOnLine() ? true : false);
        rlTempCheck.setEnabled(device.isOnLine() ? true : false);
        rlReset.setEnabled(device.isOnLine() ? true : false);
        rlFilterTip.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        rlBacklashSet.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        rlTempCheck.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        rlReset.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
    }

    private void initFilterTipView() {
        filterBottomMenu = new BottomMenu(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                tvFilterTip.setText(filterBottomMenu.getCurrentItem());
                sendCmd(0x0206, filterConvertKeyValue(filterBottomMenu.getCurrentItem(), filterKeyArray, filterValueArray));
            }

            @Override
            public void onCancel() {

            }
        });
        filterTipData = new ArrayList();
        for (int i = 0; i < filterKeyArray.length; i++) {
            filterTipData.add(getResources().getString(filterKeyArray[i]));
        }
        filterBottomMenu.setData(filterTipData);
    }

    private void initBacklashView() {
        backlashBottomMenu = new BottomMenu(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                tvBacklashSet.setText(backlashBottomMenu.getCurrentItem());
                sendCmd(0x0106, backlashConvertKeyValue(backlashBottomMenu.getCurrentItem(), backlashKeyArray, backlashValueArray));
            }

            @Override
            public void onCancel() {

            }
        });
        backlashData = new ArrayList();
        for (int i = 0; i < backlashKeyArray.length; i++) {
            backlashData.add(backlashKeyArray[i]);
        }
        backlashBottomMenu.setData(backlashData);
    }

    private void initTempCheckView() {
        tempCheckBottomMenu = new BottomMenu(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                tvTempCheck.setText(tempCheckBottomMenu.getCurrentItem());
                if (tempCheckBottomMenu.getCurrent() >= 10) {
                    sendCmd(0x0204, String.valueOf(tempIndexToTempValue(tempCheckBottomMenu.getCurrent())));
                } else {
                    sendCmd(0x0205, String.valueOf(Math.abs(tempIndexToTempValue(tempCheckBottomMenu.getCurrent()))));
                }
            }

            @Override
            public void onCancel() {

            }
        });

        tempCheckData = new ArrayList();
        for (int i = 0; i <= 20; i++) {
            tempCheckData.add(tempIndexToTempValue(i) + "℃");
        }

        tempCheckBottomMenu.setData(tempCheckData);
    }

    /**
     * 回差时间发送数据和显示数据转化
     *
     * @param data
     * @param keyArray
     * @param valueArray
     * @return
     */
    private String backlashConvertKeyValue(String data, String[] keyArray, String[] valueArray) {
        int index = 0;
        for (int i = 0; i < keyArray.length; i++) {
            if (TextUtils.equals(keyArray[i], data)) {
                index = i;
            }
        }
        backlashBottomMenu.setCurrent(index);
        return valueArray[index];
    }

    /**
     * 回差时间发送数据和显示数据转化
     *
     * @param data
     * @param keyArray
     * @param valueArray
     * @return
     */
    private String tempCheckConvertKeyValue(String data, String[] keyArray, String[] valueArray) {
        int index = 0;
        for (int i = 0; i < keyArray.length; i++) {
            if (TextUtils.equals(keyArray[i], data)) {
                index = i;
            }
        }
        tempCheckBottomMenu.setCurrent(index);
        return valueArray[index];
    }

    /**
     * 过滤器提醒发送数据和显示数据转化
     *
     * @param data
     * @param keyArray
     * @param valueArray
     * @return
     */
    private String filterConvertKeyValue(String data, int[] keyArray, String[] valueArray) {
        int index = 0;
        for (int i = 0; i < keyArray.length; i++) {
            if (TextUtils.equals(getResources().getString(keyArray[i]), data)) {
                index = i;
            }
        }
        return valueArray[index];
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_filter_tip:
                filterBottomMenu.setTitle(getResources().getString(R.string.Device_Af_Remind));
                filterBottomMenu.show(view);
                break;
            case R.id.rl_backlash_set:
                backlashBottomMenu.setTitle(getResources().getString(R.string.Device_Bm_Details_Return));
                backlashBottomMenu.show(view);
                break;
            case R.id.rl_temp_check:
                tempCheckBottomMenu.setTitle(getResources().getString(R.string.Device_Br_Calibration));
                tempCheckBottomMenu.show(view);
                break;
            case R.id.rl_reset:
                showResetDialog();
                break;
        }

    }

    private void showResetDialog() {
        builder = new WLDialog.Builder(mContext);
        builder.setMessage(R.string.Device_Bm_Details_Restore_tips)
                .setPositiveButton(getResources().getString(R.string.Sure))
                .setNegativeButton(getResources().getString(R.string.Cancel))
                .setTitle(getResources().getString(R.string.Hint))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        sendCmd(0x0203, null);
                        resetDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        resetDialog.dismiss();
                    }
                });
        resetDialog = builder.create();
        if (!resetDialog.isShowing()) {
            resetDialog.show();
        }
    }

    private void sendCmd(int commandId, String parameter) {
        ProgressDialogManager.getDialogManager().showDialog(BR_SETTING, mContext, null, null, getResources().getInteger(R.integer.http_timeout));
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
            //过滤器提醒时间
            case 0x8113:
                for (int i = 0; i < filterValueArray.length; i++) {
                    if (TextUtils.equals(filterValueArray[i], attributeValue)) {
                        filterBottomMenu.setCurrent(i);
                        tvFilterTip.setText(getResources().getString(filterKeyArray[i]));
                    }
                }
                break;
            //回差温度
            case 0x8110:
                tvBacklashSet.setText(backlashConvertKeyValue(attributeValue, backlashValueArray, backlashKeyArray));
                break;
            case 0x8111:
                int tempIndex = hexStringToTempIndex(attributeValue);
                tempCheckBottomMenu.setCurrent(tempIndex);
                tvTempCheck.setText(tempIndexToTempValue(tempIndex) + "℃");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        ProgressDialogManager.getDialogManager().dimissDialog(BR_SETTING, 0);
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                if (event.device.mode == 3) {
                    // 设备删除
                    updateMode();
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