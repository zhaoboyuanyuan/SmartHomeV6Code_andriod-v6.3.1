package cc.wulian.smarthomev6.main.device.device_Ap;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * Created by Veev on 2017/11/13
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    BmSystemChooseView
 */
public class ApSystemResetView extends LinearLayout implements IDeviceMore {

    private static String TAG = ApSystemResetView.class.getSimpleName();

    private String deviceID;
    private Device mDevice;

    protected WLDialog.Builder builder;
    protected WLDialog dialog;

    private TextView mTextName;

    public ApSystemResetView(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bm_system_reset, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextName = (TextView) rootView.findViewById(R.id.bm_return_setts_text_sys_name);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDelete();
            }
        });
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceID".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            }
        }

        dealData(mDevice);
    }


    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event != null && TextUtils.equals(event.device.devID, deviceID)) {
            dealData(event.device);
        }
    }

    private void dealData(Device device) {
        if (device == null) {
            return;
        }

        if (device.isOnLine()) {
            mTextName.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
            mTextName.setTextColor(getResources().getColor(R.color.newStateText));
            setEnabled(false);
        }
    }

    protected void showDelete() {
        builder = new WLDialog.Builder(getContext());
        builder.setCancelOnTouchOutSide(false)
                .setTitle(R.string.Hint)
                .setMessage(R.string.Device_Bm_Details_Restore_tips)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        sendCmd();
                        dialog.dismiss();
                        // 是否要菊花加载 ?
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 发送命令
     */
    private void sendCmd(){
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 0x0201);
            object.put("commandType", 1);
            object.put("commandId", 0x8020);
            object.put("endpointNumber", 1);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
