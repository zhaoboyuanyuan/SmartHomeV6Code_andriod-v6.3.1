package cc.wulian.smarthomev6.main.device.device_Bx;

import android.content.Context;
import android.content.Intent;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.h5.H5BridgeCommonActivity;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

/**
 * Created by Veev on 2017/11/13
 * 联排温控器系统选择
 * Function:    BxSystemChooseView
 */

public class BxSystemChooseView extends LinearLayout implements IDeviceMore {

    private static String TAG = BxSystemChooseView.class.getSimpleName();

    private static final String WATER_SYSTEM = "0";//水系统
    private static final String ELECTRICITY_SYSTEM = "1";//电系统


    private LinearLayout mLinearSys, mLinearAnti;
    private TextView mTextPanel, mTextName, mTextName2;

    private String deviceID, sys, anti;
    private Device mDevice;
    // 系统
    // 1 电系统(默认), 0 水系统
    private String mSys = WATER_SYSTEM;
    private Context mContext;

    public BxSystemChooseView(Context context) {
        super(context);
        this.mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bx_system_choose, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mLinearAnti = (LinearLayout) rootView.findViewById(R.id.bm_system_choose_linear_anti);
        mLinearSys = (LinearLayout) rootView.findViewById(R.id.bm_system_choose_linear_sys);
        mTextPanel = (TextView) rootView.findViewById(R.id.bm_system_choose_text_panel);
        mTextName = (TextView) rootView.findViewById(R.id.bm_system_choose_text_sys_name);
        mTextName2 = (TextView) rootView.findViewById(R.id.bm_system_choose_text_sys_name_2);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);

        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceID".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            }
            if ("sys".equals(p.key)) {
                sys = p.value;
            }
            if ("anti".equals(p.key)) {
                anti = p.value;
            }
        }

        dealData(mDevice);

//        sendCmd(0x8010, null);

        mLinearSys.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(sys)) {
                    Intent intent = new Intent(getContext(), H5BridgeCommonActivity.class);
                    intent.putExtra("url", sys);
                    intent.putExtra("deviceID", deviceID);
                    getContext().startActivity(intent);
                }
            }
        });
        mLinearAnti.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(anti)) {
                    Intent intent = new Intent(getContext(), H5BridgeCommonActivity.class);
                    intent.putExtra("url", anti);
                    intent.putExtra("deviceID", deviceID);
                    getContext().startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        ProgressDialogManager.getDialogManager().dimissDialog(TAG, 0);
        if (event != null && TextUtils.equals(event.device.devID, deviceID)) {
            dealData(event.device);
        }
    }

    private void sendCmd(int commandId, String parameter) {
        if (mDevice.isOnLine()) {
            ProgressDialogManager.getDialogManager().showDialog(TAG, mContext, null, null, getResources().getInteger(R.integer.http_timeout));
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
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

    private void dealData(Device device) {
        if (device == null) {
            return;
        }

        if (device.isOnLine()) {
            mTextName.setTextColor(getResources().getColor(R.color.newPrimaryText));
            mTextName2.setTextColor(getResources().getColor(R.color.newPrimaryText));
            mLinearSys.setEnabled(true);
            mLinearAnti.setEnabled(true);
            setEnabled(true);
        } else {
            mTextName.setTextColor(getResources().getColor(R.color.newStateText));
            mTextName2.setTextColor(getResources().getColor(R.color.newStateText));
            mLinearSys.setEnabled(false);
            mLinearAnti.setEnabled(false);
            setEnabled(false);
        }

        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x8006) {
                    mSys = attribute.attributeValue;
                }
            }
        });

        // 电
        if (TextUtils.equals(mSys, ELECTRICITY_SYSTEM)) {
            mTextPanel.setText(R.string.Device_Bm_Details_System1);
            mLinearAnti.setVisibility(GONE);
        } else if (TextUtils.equals(mSys, WATER_SYSTEM)) {
            // 水
            mTextPanel.setText(R.string.Device_Bm_Details_System2);
            mLinearAnti.setVisibility(VISIBLE);
        }

    }

}
