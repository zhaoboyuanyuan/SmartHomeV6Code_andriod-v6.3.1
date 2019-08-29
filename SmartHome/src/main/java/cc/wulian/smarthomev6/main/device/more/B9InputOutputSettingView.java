package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

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
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

/**
 * created by huxc  on 2018/5/11.
 * func：一路输入输出翻译器输入端正常状态设置
 * email: hxc242313@qq.com
 */

public class B9InputOutputSettingView extends FrameLayout implements IDeviceMore, CompoundButton.OnCheckedChangeListener {
    private Context context;
    private CheckBox cbInput;
    private CheckBox cbOutput;
    private TextView tvInput;
    private TextView tvOutput;
    private TextView tvInputText;
    private TextView tvOutputText;
    private Device mDevice;
    private static final String INPUT_NORMAL_SET_OPEN = "1";
    private static final String INPUT_NORMAL_SET_CLOSE = "0";
    private static final String OUTPUT_INIT_ACTION_OPEN = "1";
    private static final String OUTPUT_INIT_ACTION_CLOSE = "0";
    private static final String SET = "SET";


    public B9InputOutputSettingView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    private String deviceID;

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_b9_normal_set_state, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cbInput = (CheckBox) rootView.findViewById(R.id.cb_input);
        cbOutput = (CheckBox) rootView.findViewById(R.id.cb_output);
        tvInput = (TextView) rootView.findViewById(R.id.tv_input);
        tvInputText = (TextView) rootView.findViewById(R.id.tv_input_text);
        tvOutput = (TextView) rootView.findViewById(R.id.tv_output);
        tvOutputText = (TextView) rootView.findViewById(R.id.tv_output_text);
        cbInput.setOnCheckedChangeListener(this);
        cbOutput.setOnCheckedChangeListener(this);
    }


    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceID".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(p.value);
                continue;
            }
        }
        sendCmd(0x0102, null);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void sendCmd(int commandId, Object args) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", deviceID);
            object.put("commandType", 1);
            object.put("clusterId", 0x0201);
            object.put("commandId", commandId);
            if (args != null) {
                JSONArray array = new JSONArray();
                array.put(args);
                object.put("parameter", array);
            }
            ProgressDialogManager.getDialogManager().showDialog(SET, context, null, null, 10000);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void dealData(Device device) {
        if (device == null) {
            return;
        }

        if (device.isOnLine()) {
            cbInput.setEnabled(true);
            cbOutput.setEnabled(true);
            tvInput.setTextColor(getResources().getColor(R.color.newPrimaryText));
            tvOutput.setTextColor(getResources().getColor(R.color.newPrimaryText));
            tvInputText.setTextColor(getResources().getColor(R.color.newPrimaryText));
            tvOutputText.setTextColor(getResources().getColor(R.color.newPrimaryText));
        } else {
            cbInput.setEnabled(false);
            cbOutput.setEnabled(false);
            tvInput.setTextColor(getResources().getColor(R.color.newStateText));
            tvOutput.setTextColor(getResources().getColor(R.color.newStateText));
            tvInputText.setTextColor(getResources().getColor(R.color.newStateText));
            tvOutputText.setTextColor(getResources().getColor(R.color.newStateText));
        }

        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x8101) {
                    if ("0".equals(attribute.attributeValue)) {
                        tvInput.setText(context.getResources().getString(R.string.device_A2_action_close));
                        cbInput.setChecked(true);
                    } else {
                        tvInput.setText(context.getResources().getString(R.string.device_A2_action_open));
                        cbInput.setChecked(false);
                    }
                }

                if (attribute.attributeId == 0x8105) {
                    if ("0".equals(attribute.attributeValue)) {
                        tvOutput.setText(context.getResources().getString(R.string.device_A2_action_open));
                        cbOutput.setChecked(false);
                    } else {
                        tvOutput.setText(context.getResources().getString(R.string.device_A2_action_close));
                        cbOutput.setChecked(true);
                    }
                }
            }
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        int id = compoundButton.getId();
        switch (id) {
            case R.id.cb_input:
                if (!compoundButton.isPressed()) {
                    return;
                }
                if (checked) {
                    sendCmd(0x0101, INPUT_NORMAL_SET_CLOSE);
                } else {
                    sendCmd(0x0101, INPUT_NORMAL_SET_OPEN);
                }
                break;
            case R.id.cb_output:
                if (!compoundButton.isPressed()) {
                    return;
                }
                if (checked) {
                    sendCmd(0x0104, OUTPUT_INIT_ACTION_OPEN);
                } else {
                    sendCmd(0x0104, OUTPUT_INIT_ACTION_CLOSE);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (event.device.devID.equals(deviceID)) {
                ProgressDialogManager.getDialogManager().dimissDialog(SET,0);
                mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
                dealData(mDevice);
            }
        }
    }
}
