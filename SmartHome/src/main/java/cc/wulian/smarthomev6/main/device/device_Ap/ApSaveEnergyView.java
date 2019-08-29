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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.device.device_Bm.BmReturnSettsView;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.BottomMenu;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/11/13
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    节能模式
 */
public class ApSaveEnergyView extends LinearLayout implements IDeviceMore {

    private static String TAG = BmReturnSettsView.class.getSimpleName();

    private TextView mTextPanel, mTextName;
    private BottomMenu mBottomMenu;

    private String deviceID;
    private Device mDevice;

    // 是不是华氏度
    private boolean isC = true;
    private ArrayList<String> list;
    // 回差 标号
    private float temp = 18;
    private int state = 0;
    private int mPower = 0;

    public ApSaveEnergyView(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bm_save_energy, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextPanel = (TextView) rootView.findViewById(R.id.bm_return_setts_text_panel);
        mTextName = (TextView) rootView.findViewById(R.id.bm_return_setts_text_sys_name);

        list = new ArrayList<>();

        mBottomMenu = new BottomMenu(getContext(), new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                WLog.i(TAG, "onSure: " + mBottomMenu.getCurrent());
                mTextPanel.setText(mBottomMenu.getCurrentItem());
                sendCmd(mBottomMenu.getCurrent());
            }

            @Override
            public void onCancel() {

            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                mBottomMenu.show(v);
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
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            dealData(event.device);
        }
    }

    private void dealData(Device device) {
        if (device == null) {
            return;
        }

        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x8006) {
                    isC =  TextUtils.equals(attribute.attributeValue.substring(0, 2), "00");
                }
                if (attribute.attributeId == 0x800F) {
                    try {
                        state = Integer.parseInt(attribute.attributeValue.substring(0, 2), 16);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        temp = Integer.parseInt(attribute.attributeValue.substring(2), 16) / 100f;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (attribute.attributeId == 0x8001) {
                    mPower = TextUtils.equals(attribute.attributeValue.substring(0, 2), "00") ? 0 : 1;
                    isC =  TextUtils.equals(attribute.attributeValue.substring(14, 16), "00");
                    try {
                        state = Integer.parseInt(attribute.attributeValue.substring(34, 36), 16);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        temp = Integer.parseInt(attribute.attributeValue.substring(36, 40), 16) / 100f;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // 设备离线 或者 关机 都不可用
        if (device.isOnLine() && mPower == 1) {
            mTextName.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
            mTextName.setTextColor(getResources().getColor(R.color.newStateText));
            setEnabled(false);
        }

        if (isC) {
            if (temp == (int) temp) {
                mTextPanel.setText(String.format("%.0f℃", temp));
            } else {
                mTextPanel.setText(String.format("%.1f℃", temp));
            }
        } else {
            mTextPanel.setText(String.format("%d℉", (int)(temp * 1.8 + 32)));
        }

        list.clear();
        if (isC) {
            for (int i = 20; i <= 42; i ++) {
                if (i %2 == 0) {
                    list.add((i / 2) + "℃");
                } else {
                    list.add((i / 2) + ".5℃");
                }
            }
        } else {
            for (int i = 20; i <= 42; i ++) {
                float t = i / 2f;
                int f = (int) Math.round(t * 1.8 + 32);
                list.add(f + "℉");
            }
        }
        mBottomMenu.setData(list);
        mBottomMenu.setTitle(getResources().getString(R.string.Device_Bm_Details_Save));
    }

    private void sendCmd(int index){
        float c = 10 + index / 2f;
        int c100 = (int) (c * 100);
        String args = state + "" + c100;
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 0x0201);
            object.put("commandType", 1);
            object.put("commandId", 0x8017);
            object.put("endpointNumber", 1);
            JSONArray array = new JSONArray();
            array.put(args);
            object.put("parameter", array);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
