package cc.wulian.smarthomev6.main.device.device_Bx;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 * created by huxc  on 2018/11/6.
 * func：联排温控恢复出厂设置
 * email: hxc242313@qq.com
 */

public class BxRestoreFactorySetView extends FrameLayout implements IDeviceMore, View.OnClickListener {
    private static final String BxRestoreFactorySetView = "BxRestoreFactorySetView";

    private RelativeLayout rlRestoreFactory;
    private TextView tvName;

    private Context mContext;

    private String deviceID;
    private String gwID;
    private Device device;
    private WLDialog.Builder builder;
    private WLDialog wlDialog;


    public BxRestoreFactorySetView(Context context, String deviceID, String gwID) {
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bx_restore_factory, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rlRestoreFactory = rootView.findViewById(R.id.rl_restore_factory);
        tvName = rootView.findViewById(R.id.tv_name);

    }

    private void initListener() {
        rlRestoreFactory.setOnClickListener(this);
    }


    private void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        }
        updateMode();
    }


    private void updateMode() {
        rlRestoreFactory.setEnabled(device.isOnLine());
        tvName.setTextColor(device.isOnLine() ? getResources().getColor(R.color.newPrimaryText) : getResources().getColor(R.color.newStateText));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_restore_factory:
                showRestoreProtectDialog();
                break;
        }

    }

    private void showRestoreProtectDialog() {
        builder = new WLDialog.Builder(getContext());
        builder.setCancelOnTouchOutSide(false)
                .setTitle(R.string.Hint)
                .setMessage(R.string.Device_Bm_Details_Restore_tips)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        sendCmd(0x8018, null);
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
        if (device.isOnLine()) {
            ProgressDialogManager.getDialogManager().showDialog(BxRestoreFactorySetView, mContext, null, null, getResources().getInteger(R.integer.http_timeout));
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        ProgressDialogManager.getDialogManager().dimissDialog(BxRestoreFactorySetView, 0);
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
                }
            }
        }
    }
}
