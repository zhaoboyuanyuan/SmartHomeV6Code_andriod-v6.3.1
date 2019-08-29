package cc.wulian.smarthomev6.main.device.device_22;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22DetailBean;
import cc.wulian.smarthomev6.support.event.Device22ConfigEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by 上海滩小马哥 on 2017/12/18.
 * 红外转发22更多界面 -> 清空码库
 */

public class Device22MoreClearStorage extends FrameLayout implements IDeviceMore {

    private View mRootView;
    private TextView mTextName;
    private Device mDevice;
    private String deviceID;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    public Device22MoreClearStorage(Context context) {
        super(context);
        initView(context);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        deviceID = bean.getValueByKey("deviceID");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        mTextName = (TextView) mRootView.findViewById(R.id.op_name);

        mRootView.setEnabled(mDevice.isOnLine());
        mTextName.setText(bean.name);
        mTextName.setTextColor(mDevice.isOnLine()
                ? getResources().getColor(R.color.newPrimaryText)
                : getResources().getColor(R.color.newStateText));
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Device22ConfigEvent event) {
        if (!TextUtils.equals(event.deviceId, deviceID)) {
            return;
        }

        if (event.operType == 2 && event.mode == 1) {
            ToastUtil.single(R.string.Infraredtransponder_Clearcode_Success);
        }
    }

    private void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_device_23_clear_storage, null);
        addView(mRootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void sendCmd() {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "516");
            jsonMsgContent.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            jsonMsgContent.put("gwID", Preference.getPreferences().getCurrentGatewayID());
            jsonMsgContent.put("devID", deviceID);//设备id
            jsonMsgContent.put("operType", 2);//模式
            jsonMsgContent.put("mode", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                jsonMsgContent.toString(), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    private void showDialog() {
        builder = new WLDialog.Builder(getContext());
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(R.string.Infraredrelay_More_Emptylibrary_Confirm)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        sendCmd();
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
}
