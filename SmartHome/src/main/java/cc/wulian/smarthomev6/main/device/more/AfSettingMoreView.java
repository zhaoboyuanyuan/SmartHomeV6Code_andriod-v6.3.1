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
import java.util.HashMap;

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
 * created by huxc  on 2018/7/25.
 * func：Af设备更多界面
 * email: hxc242313@qq.com
 */

public class AfSettingMoreView extends FrameLayout implements IDeviceMore, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String AF_SETTING = "af_setting";
    private static final String AF_SETTING_CLOSE = "0";
    private static final String AF_SETTING_OPEN = "1";
    private static final String AF_SETTING_QUERY = "2";

    private ToggleButton tbVoice;
    private ToggleButton tbShake;
    private RelativeLayout rlFilterTip;
    private RelativeLayout rlBacklashSet;
    private RelativeLayout rlVoice;
    private RelativeLayout rlShake;
    private TextView tvFilterTip;
    private TextView tvBacklashSet;
    private Button btnReset;


    private Context mContext;
    private WLDialog resetDialog;
    private WLDialog.Builder builder;
    private BottomMenu filterBottomMenu;
    private BottomMenu backlashBottomMenu;

    private String deviceID;
    private String gwID;
    private Device device;
    private ArrayList filterTipData;
    private ArrayList backlashData;
    private static String[] backlashKeyArray = {"1.0℃", "1.5℃", "2.0℃", "2.5℃", "3.0℃", "3.5℃", "4.0℃", "4.5℃", "5.0℃", "5.5℃", "6.0℃", "6.5℃", "7.0℃", "7.5℃", "8.0℃", "8.5℃", "9.0℃", "9.5℃", "10.0℃"};
    private static String[] backlashValueArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"};
    private static int[] filterKeyArray = {R.string.Device_Widget_82_Speed5, R.string.Device_Af_Bulltebox_01, R.string.Device_Af_Bulltebox_02, R.string.Device_Af_Bulltebox_03, R.string.Device_Af_Bulltebox_04};
    private static String[] filterValueArray = {"0", "3", "6", "9", "12"};


    public AfSettingMoreView(Context context, String deviceID, String gwID) {
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_af, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tbShake = rootView.findViewById(R.id.tb_shake);
        tbVoice = rootView.findViewById(R.id.tb_voice);
        tvFilterTip = rootView.findViewById(R.id.tv_filter_tip);
        tvBacklashSet = rootView.findViewById(R.id.tv_backlash_set);
        rlFilterTip = rootView.findViewById(R.id.rl_filter_tip);
        rlBacklashSet = rootView.findViewById(R.id.rl_backlash_set);
        rlShake = rootView.findViewById(R.id.rl_key_shake);
        rlVoice = rootView.findViewById(R.id.rl_key_voice);
        btnReset = rootView.findViewById(R.id.btn_reset);
        initFilterTipView();
        initBacklashView();

    }

    private void initListener() {
        rlFilterTip.setOnClickListener(this);
        rlBacklashSet.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        tbVoice.setOnCheckedChangeListener(this);
        tbShake.setOnCheckedChangeListener(this);
    }


    private void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        }

        updateMode();
        sendCmd(0x0100, AF_SETTING_QUERY);
    }


    private void updateMode() {
        rlFilterTip.setEnabled(device.isOnLine() ? true : false);
        rlBacklashSet.setEnabled(device.isOnLine() ? true : false);
        btnReset.setEnabled(device.isOnLine() ? true : false);
        tbShake.setEnabled(device.isOnLine() ? true : false);
        tbVoice.setEnabled(device.isOnLine() ? true : false);
        rlFilterTip.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        rlBacklashSet.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        rlShake.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        rlVoice.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        btnReset.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
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
            case R.id.btn_reset:
                showResetDialog();
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.tb_voice:
                sendCmd(0x0107, isChecked ? AF_SETTING_OPEN : AF_SETTING_CLOSE);
                break;
            case R.id.tb_shake:
                sendCmd(0x0108, isChecked ? AF_SETTING_OPEN : AF_SETTING_CLOSE);
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
        if (device.isOnLine()) {
            ProgressDialogManager.getDialogManager().showDialog(AF_SETTING, mContext, null, null, getResources().getInteger(R.integer.http_timeout));
        }
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
            //音效
            case 0x810D:
                if (TextUtils.equals(attributeValue, AF_SETTING_CLOSE)) {
                    tbVoice.setChecked(false);
                } else if (TextUtils.equals(attributeValue, AF_SETTING_OPEN)) {
                    tbVoice.setChecked(true);
                }
                break;
            //震动设置
            case 0x810F:
                if (TextUtils.equals(attributeValue, AF_SETTING_CLOSE)) {
                    tbShake.setChecked(false);
                } else if (TextUtils.equals(attributeValue, AF_SETTING_OPEN)) {
                    tbShake.setChecked(true);
                }
                break;
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

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        ProgressDialogManager.getDialogManager().dimissDialog(AF_SETTING, 0);
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
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
