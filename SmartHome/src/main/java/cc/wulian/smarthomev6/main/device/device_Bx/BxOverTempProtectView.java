package cc.wulian.smarthomev6.main.device.device_Bx;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
 * func：联排温控器地面过温保护
 * email: hxc242313@qq.com
 */

public class BxOverTempProtectView extends FrameLayout implements IDeviceMore, View.OnClickListener {
    private static final String BxOverTempProtectView = "BxOverTempProtectView";

    private View layoutView;
    private LinearLayout llOverTempProtect;
    private TextView tvTemp;
    private TextView tvTempLeft;


    private Context mContext;
    private BottomMenu tempBottomMenu;

    private String deviceID;
    private String gwID;
    private Device device;
    private ArrayList tempData;
    private static String[] tempKeyArray;
    private static String[] tempValueArray;


    public BxOverTempProtectView(Context context, String deviceID, String gwID) {
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bx_over_temp_protect, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llOverTempProtect = rootView.findViewById(R.id.ll_over_temp_protect);
        layoutView = rootView.findViewById(R.id.item_device_more_root);
        tvTemp = rootView.findViewById(R.id.tv_temp);
        tvTempLeft = rootView.findViewById(R.id.tv_temp_left);
        initTempView();

    }

    private void initListener() {
        llOverTempProtect.setOnClickListener(this);
    }


    private void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        }
        dealDevice(device);
        updateMode();
    }

    private void initTempKeyValueArray() {
        tempKeyArray = new String[41];
        tempValueArray = new String[41];
        for (int i = 40, j = 0; i < 81; i++) {
            tempKeyArray[j] = i + "℃";
            tempValueArray[j] = Integer.toHexString(i).toUpperCase();
            j++;
        }

    }


    private void updateMode() {
        llOverTempProtect.setEnabled(device.isOnLine());
//        llOverTempProtect.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
        tvTempLeft.setTextColor(device.isOnLine() ? getResources().getColor(R.color.newPrimaryText) : getResources().getColor(R.color.newStateText));

    }


    private void initTempView() {
        initTempKeyValueArray();
        tempBottomMenu = new BottomMenu(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                tvTemp.setText(tempBottomMenu.getCurrentItem());
                sendCmd(0x8011, tempConvertKeyValue(tempBottomMenu.getCurrentItem(), tempKeyArray, tempValueArray));

            }

            @Override
            public void onCancel() {

            }
        });
        tempData = new ArrayList();
        for (int i = 0; i < tempKeyArray.length; i++) {
            tempData.add(tempKeyArray[i]);
        }
        tempBottomMenu.setData(tempData);
    }


    /**
     * 地面过温保护发送数据和显示数据转化
     *
     * @param data
     * @param keyArray
     * @param valueArray
     * @return
     */
    private String tempConvertKeyValue(String data, String[] keyArray, String[] valueArray) {
        int index = 0;
        for (int i = 0; i < keyArray.length; i++) {
            if (TextUtils.equals(keyArray[i], data)) {
                index = i;
            }
        }
        tempBottomMenu.setCurrent(index);
        return valueArray[index];
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_over_temp_protect:
                tempBottomMenu.setTitle(getResources().getString(R.string.Device_Bm_Details_High_Protect));
                tempBottomMenu.show(view);
                break;
        }

    }


    private void sendCmd(int commandId, String parameter) {
        if (device.isOnLine()) {
            ProgressDialogManager.getDialogManager().showDialog(BxOverTempProtectView, mContext, null, null, getResources().getInteger(R.integer.http_timeout));
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
            case 0x8003:
                tvTemp.setText(tempConvertKeyValue(attributeValue, tempValueArray, tempKeyArray));
                break;
            case 0x8006:
                //水系统时地面过温保护不可见
                layoutView.setVisibility(TextUtils.equals("0", attributeValue) ? GONE : VISIBLE);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        ProgressDialogManager.getDialogManager().dimissDialog(BxOverTempProtectView, 0);
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
