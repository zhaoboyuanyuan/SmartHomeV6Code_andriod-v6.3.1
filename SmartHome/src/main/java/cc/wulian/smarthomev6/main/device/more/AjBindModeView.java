package cc.wulian.smarthomev6.main.device.more;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONObject;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DialogUtil;

/**
 * Created by zbl on 2017/7/20.
 * 内嵌一路开关，开启绑定/解绑模式
 */

public class AjBindModeView extends FrameLayout implements IDeviceMore {
    private static final String CMD_BIND_MODE = "cmd_bind_mode";
    private Context context;
    private String deviceID;
    private String gwID;
    private WLDialog dialog_tip;

    public AjBindModeView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        gwID = bean.getValueByKey("gwID");
        deviceID = bean.getValueByKey("deviceID");
    }

    @Override
    public void onViewRecycled() {
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_aj_bind_mode, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            sendCmd();
        }
    };

    private void sendCmd() {
        ProgressDialogManager.getDialogManager().showDialog(CMD_BIND_MODE, context, null, null, getResources().getInteger(R.integer.http_timeout));
        JSONObject object = new JSONObject();
        object.put("cmd", "501");
        object.put("gwID", gwID);
        object.put("devID", deviceID);
        object.put("commandType", 1);
        object.put("commandId", 0x8014);

        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                ProgressDialogManager.getDialogManager().dimissDialog(CMD_BIND_MODE, 0);
                                showDialog();
                            }
                        });
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
    }

    private void showDialog() {
        String message = getResources().getString(R.string.Device_More_Bindsetting_Tip2) +
                "\n" + getResources().getString(R.string.Device_More_Bindsetting_Tip3);
        dialog_tip = DialogUtil.showTipsDialog(context,
                getResources().getString(R.string.Device_More_Bindsetting_Tip1),
                message,
                getResources().getString(R.string.Tip_I_Known),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        dialog_tip.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {

                    }
                });
        dialog_tip.show();
    }
}