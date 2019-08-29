package cc.wulian.smarthomev6.main.device.device_Co;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.device.device_dd.DDChoiceSourceView;
import cc.wulian.smarthomev6.main.h5.H5BridgeCommonActivity;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class DeviceCoMoreView extends LinearLayout implements IDeviceMore, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String CLOSE = "0";
    private static final String OPEN = "1";
    private LinearLayout llRouteSetting;
    private LinearLayout llRecovery;
    private TextView tvReverse;
    private TextView tvManual;
    private TextView tvRecovery;
    private TextView tvRouteSetting;
    private ToggleButton tbReverse;
    private ToggleButton tbManual;


    private String deviceID, url;
    private String routeSetting;
    private Device mDevice;
    private WLDialog.Builder builder;
    private WLDialog wlDialog;

    public DeviceCoMoreView(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_co_setting, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llRouteSetting = findViewById(R.id.layout_route_setting);
        llRecovery = findViewById(R.id.layout_recovery);
        tbManual = findViewById(R.id.tb_manual);
        tbReverse = findViewById(R.id.tb_reverse);
        tvReverse = findViewById(R.id.tv_reverse);
        tvManual = findViewById(R.id.tv_manual);
        tvRouteSetting = findViewById(R.id.tv_route_setting);
        tvRecovery = findViewById(R.id.tv_recovery);

        llRouteSetting.setOnClickListener(this);
        llRecovery.setOnClickListener(this);
        tbManual.setOnCheckedChangeListener(this);
        tbReverse.setOnCheckedChangeListener(this);

    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceID".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
                updateMode();
            } else if ("url".equals(p.key)) {
                url = p.value;
            }
        }

        sendCmd(0x0103, null);//读取方向状态
        sendCmd(0x0104, null);//读取手拉状态

    }


    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
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
                    updateMode();
                    dealDevice(event.device);
                }
            }
        }
    }

    private void dealDevice(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (cluster.clusterId == 0x0102) {
                    updateViews(attribute.attributeId, attribute.attributeValue);
                }
            }
        });
    }

    private void updateViews(int attributeId, String attributeValue) {
        switch (attributeId) {
            case 0x8004:
                if("8".equals(attributeValue)){
                    ToastUtil.show(R.string.Device_Co_Details_Restore_in);
                }
                break;
            case 0x8006:
                tbReverse.setChecked(TextUtils.equals(attributeValue, OPEN));
                break;
            case 0x8007:
                tbManual.setChecked(TextUtils.equals(attributeValue, CLOSE));
                break;
            case 0x8009:
                routeSetting = attributeValue;
                break;
        }
    }

    private void updateMode() {
        if (mDevice == null) {
            return;
        }
        tbReverse.setEnabled(mDevice.isOnLine());
        tbManual.setEnabled(mDevice.isOnLine());
        llRecovery.setEnabled(mDevice.isOnLine());
        llRouteSetting.setEnabled(mDevice.isOnLine());
        tvReverse.setTextColor(mDevice.isOnLine() ? getResources().getColor(R.color.newPrimaryText) : getResources().getColor(R.color.newStateText));
        tvManual.setTextColor(mDevice.isOnLine() ? getResources().getColor(R.color.newPrimaryText) : getResources().getColor(R.color.newStateText));
        tvRouteSetting.setTextColor(mDevice.isOnLine() ? getResources().getColor(R.color.newPrimaryText) : getResources().getColor(R.color.newStateText));
        tvRecovery.setTextColor(mDevice.isOnLine() ? getResources().getColor(R.color.newPrimaryText) : getResources().getColor(R.color.newStateText));

    }

    private void showRecoveryDialog() {
        builder = new WLDialog.Builder(getContext());
        builder.setCancelOnTouchOutSide(false)
                .setTitle(R.string.Hint)
                .setMessage(R.string.Device_Bm_Details_Restore_tips)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        sendCmd(0x0308, null);
                        wlDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        wlDialog.dismiss();
                    }
                });
        wlDialog = builder.create();
        if (!wlDialog.isShowing()) {
            wlDialog.show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tb_reverse:
                if (!buttonView.isPressed()) {
                    return;
                }
                if (isChecked) {
                    sendCmd(0x0203, OPEN);
                } else {
                    sendCmd(0x0203, CLOSE);
                }
                break;
            case R.id.tb_manual:
                if (!buttonView.isPressed()) {
                    return;
                }
                if (isChecked) {
                    sendCmd(0x0204, CLOSE);
                } else {
                    sendCmd(0x0204, OPEN);
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_route_setting:
                if (TextUtils.equals("1", routeSetting)) {
                    Intent intent = new Intent(getContext(), H5BridgeCommonActivity.class);
                    intent.putExtra("url", "device/curtain_Co/strokeSetting.html");
                    intent.putExtra("deviceID", deviceID);
                    getContext().startActivity(intent);
                } else {
                    showMoreSolutionDialog();
                }
                break;
            case R.id.layout_recovery:
                showRecoveryDialog();
                break;
        }

    }

    private void showMoreSolutionDialog() {
        builder = new WLDialog.Builder(getContext());
        builder.setCancelOnTouchOutSide(false)
                .setTitle(R.string.Hint)
                .setMessage(R.string.device_Co_no_place_hint)
                .setPositiveButton(this.getResources().getString(R.string.Tip_I_Known))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        wlDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        wlDialog.dismiss();
                    }
                });
        wlDialog = builder.create();
        if (!wlDialog.isShowing()) {
            wlDialog.show();
        }
    }

    private void sendCmd(int commandId, String parameter) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("commandType", 1);
            object.put("clusterId", 0X0102);
            object.put("commandId", commandId);
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

}
