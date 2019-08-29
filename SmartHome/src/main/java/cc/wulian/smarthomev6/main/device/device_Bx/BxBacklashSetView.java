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

/**
 * created by huxc  on 2018/11/6.
 * func：联排温控器回差设置
 * email: hxc242313@qq.com
 */

public class BxBacklashSetView extends FrameLayout implements IDeviceMore, View.OnClickListener {
    private static final String BxBacklashView = "BxBacklashView";

    private RelativeLayout rlBacklashSet;
    private TextView tvBacklashSet;
    private TextView tvBacklashSetLeft;


    private Context mContext;
    private BottomMenu backlashBottomMenu;

    private String deviceID;
    private String gwID;
    private Device device;
    private ArrayList backlashData;
    private static String[] backlashKeyArray = {"1.0℃", "2.0℃", "3.0℃", "4.0℃", "5.0℃", "6.0℃", "7.0℃", "8.0℃", "9.0℃", "10.0℃"};
    private static String[] backlashValueArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "0A"};


    public BxBacklashSetView(Context context, String deviceID, String gwID) {
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bx_backlash, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvBacklashSet = rootView.findViewById(R.id.tv_backlash_set);
        tvBacklashSetLeft = rootView.findViewById(R.id.tv_backlash_set_left);
        rlBacklashSet = rootView.findViewById(R.id.rl_backlash_set);
        initBacklashView();

    }

    private void initListener() {
        rlBacklashSet.setOnClickListener(this);
    }


    private void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        }
        dealDevice(device);
        updateMode();
    }


    private void updateMode() {
        rlBacklashSet.setEnabled(device.isOnLine());
//        rlBacklashSet.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        tvBacklashSetLeft.setTextColor(device.isOnLine() ? getResources().getColor(R.color.newPrimaryText) : getResources().getColor(R.color.newStateText));
    }


    private void initBacklashView() {
        backlashBottomMenu = new BottomMenu(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                tvBacklashSet.setText(backlashBottomMenu.getCurrentItem());
                sendCmd(0x8014, backlashConvertKeyValue(backlashBottomMenu.getCurrentItem(), backlashKeyArray, backlashValueArray));

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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_backlash_set:
                backlashBottomMenu.setTitle(getResources().getString(R.string.Device_Bm_Details_Return));
                backlashBottomMenu.show(view);
                break;
        }

    }


    private void sendCmd(int commandId, String parameter) {
        if (device.isOnLine()) {
            ProgressDialogManager.getDialogManager().showDialog(BxBacklashView, mContext, null, null, getResources().getInteger(R.integer.http_timeout));
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
            case 0x8009:
                tvBacklashSet.setText(backlashConvertKeyValue(attributeValue, backlashValueArray, backlashKeyArray));
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        ProgressDialogManager.getDialogManager().dimissDialog(BxBacklashView, 0);
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
