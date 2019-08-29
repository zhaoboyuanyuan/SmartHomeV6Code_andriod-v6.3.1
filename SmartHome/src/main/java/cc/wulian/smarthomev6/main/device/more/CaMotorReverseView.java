package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * created by huxc  on 2018/11/5.
 * func：Ca电机反转
 * email: hxc242313@qq.com
 */

public class CaMotorReverseView extends FrameLayout implements IDeviceMore {
    private Context mContext;
    private Device device;

    String deviceID = "";
    String gwID = "";

    private View itemView;
    private WLDialog wlDialog;

    public CaMotorReverseView(Context context, String deviceID, String gwID) {
        super(context);
        this.mContext = context;
        this.gwID = gwID;
        this.deviceID = deviceID;
        initView(context);
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_ca_motor_reverse, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemView = (RelativeLayout) rootView.findViewById(R.id.rl_item_view);
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        updateMode();
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }


    private void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        }
        updateMode();
    }

    private void updateMode() {
        itemView.setEnabled(device.isOnLine());
        itemView.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
    }

    private void showDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setTitle(R.string.Hint)
                .setMessage(R.string.device_Cb_Motor_reversal_hint)
                .setPositiveButton(R.string.Tip_I_Known)
                .setCancelOnTouchOutSide(false)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        wlDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {

                    }
                });

        wlDialog = builder.create();
        if (wlDialog != null && !wlDialog.isShowing()) {
            wlDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                device = MainApplication.getApplication().getDeviceCache().get(device.devID);
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode();
                } else if (event.device.mode == 1) {
                    updateMode();
                } else if (event.device.mode == 0) {
                    updateMode();
                }
            }
        } else if (event.device == null) {
            updateMode();
        }
    }

}
