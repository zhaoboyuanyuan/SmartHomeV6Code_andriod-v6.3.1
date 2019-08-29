package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/7/20.
 * 内嵌一路开关，累计电量清零
 */

public class AjClearQuantityView extends FrameLayout implements IDeviceMore {

    private static final String CMD_CLEAR = "cmd_clear";

    private Context context;
    private WLDialog dialog;

    String deviceID = "";
    String gwID = "";

    public AjClearQuantityView(Context context, String deviceID, String gwID) {
        super(context);
        this.context = context;
        this.gwID = gwID;
        this.deviceID = deviceID;
        initView(context);
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_aj_clear_quantity, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        View layoutContent = rootView.findViewById(R.id.layoutContent);

        Device device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if(device.mode == 2){
            rootView.setEnabled(false);
            layoutContent.setAlpha(0.54f);
        }else if(device.mode == 1){
            rootView.setEnabled(true);
            layoutContent.setAlpha(1f);
        }
    }

    private void showDialog() {

        if (dialog == null) {
            WLDialog.Builder builder = new WLDialog.Builder(context);
            Device device = MainApplication.getApplication().getDeviceCache().get(deviceID);
            int contentRes;
            int positiveRes;
            int negativeRes;
            if(device != null && "Ai".equals(device.type)){
                contentRes = R.string.DINswitch_Cleared_Popup;
                positiveRes = R.string.Sure;
                negativeRes = R.string.Cancel;
            }else{
                contentRes = R.string.Device_More_Powerclear_Alarm;
                positiveRes = R.string.Device_More_Powerclear_Alarm_Clear;
                negativeRes = R.string.Cancel;
            }
            builder.setCancelOnTouchOutSide(false)
                    .setMessage(contentRes)
                    .setPositiveButton(positiveRes)
                    .setNegativeButton(negativeRes)
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                            dialog.dismiss();
                            sendClearCmd();
                        }

                        @Override
                        public void onClickNegative(View view) {

                        }

                    });
            dialog = builder.create();
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void sendClearCmd() {
        ProgressDialogManager.getDialogManager().showDialog(CMD_CLEAR, context, getResources().getString(R.string.Device_More_Powerclear_Feedbackprocess), null, getResources().getInteger(R.integer.http_timeout));
        JSONObject object = new JSONObject();
        object.put("cmd", "501");
        object.put("gwID", gwID);
        object.put("devID", deviceID);
        object.put("commandType", 1);
        object.put("commandId", 0x8013);

        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                } else if (event.device.mode == 1) {
                    // 更新上线
                } else if (event.device.mode == 0) {
                    String powerInfo = null;
                    JSONObject object = JSON.parseObject(event.device.data);
                    JSONArray endpoints = object.getJSONArray("endpoints");
                    JSONArray clusters = endpoints.getJSONObject(0).getJSONArray("clusters");
                    for (int i = 0; i < clusters.size(); i++) {
                        JSONObject cluster = clusters.getJSONObject(i);
                        if (cluster.getInteger("clusterId") == 0x0b04) {//电量信息
                            JSONArray attributes = cluster.getJSONArray("attributes");
                            for (int j = 0; j < attributes.size(); j++) {
                                JSONObject attribute = attributes.getJSONObject(j);
                                if (attribute.getInteger("attributeId") == 0x8005) {
                                    powerInfo = attribute.getString("attributeValue");
                                    if (powerInfo != null) {
                                        ProgressDialogManager.getDialogManager().dimissDialog(CMD_CLEAR, 0);
                                        ToastUtil.show(R.string.Device_More_Powerclear_Feedbackresult);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}