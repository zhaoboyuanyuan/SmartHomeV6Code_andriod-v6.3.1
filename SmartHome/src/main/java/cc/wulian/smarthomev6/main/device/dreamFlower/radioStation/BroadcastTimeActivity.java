package cc.wulian.smarthomev6.main.device.dreamFlower.radioStation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/8/22.
 * func： 梦想之花整点报时时间界面
 * email: hxc242313@qq.com
 */

public class BroadcastTimeActivity extends BaseTitleActivity implements View.OnClickListener {
    public static final String FLOWER_BROADCAST_TIME = "5"; // 播报设置-时间设置
    private ListView lvAlarmTime;
    private BroadcastTimeAdapter adapter;

    private String deviceID;
    private String playTime;
    private Device mDevice;
    private int type;
    private int[] data = new int[24];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_d8_alarm_time, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightBtn(getString(R.string.Minigateway_Givetime_Selectiontime), getString(R.string.Sure));
    }

    @Override
    protected void initView() {
        super.initView();
        lvAlarmTime = (ListView) findViewById(R.id.lv_alarm_time);

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        lvAlarmTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectItem(position);
                WLog.i(TAG, "onItemClick: " + position);
                if (data[position] == 1) {
                    data[position] = 0;
                } else {
                    data[position] = 1;
                }
                adapter.notifyDataSetInvalidated();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        deviceID = getIntent().getStringExtra("deviceID");
        playTime = getIntent().getStringExtra("playTime");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        adapter = new BroadcastTimeAdapter(this, getData());
        setSelectedItem(playTime);
        lvAlarmTime.setAdapter(adapter);
    }

    private List<String> getData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String time = "00:00";
            if (i < 10) {
                time = "0:00";
                list.add("0" + time.replaceFirst(time.substring(0, 1), i + ""));
            } else {
                list.add(time.replaceFirst(time.substring(0, 2), i + ""));
            }
        }
        return list;
    }

    //设置网关返回的整点报时数据
    private void setSelectedItem(String playTime) {
        String time = null;
        String tmp = Integer.toBinaryString(Integer.valueOf(playTime, 16));
        String selectTime = new StringBuilder(tmp).reverse().toString();
        for (int i = 0; i < selectTime.length(); i++) {
            data[i] = Integer.parseInt(selectTime.substring(i, i + 1));
        }
        WLog.i(TAG, "initData: selectValue =  " + tmp);
        WLog.i(TAG, "initData: selectReverseValue =  " + selectTime);
        WLog.i(TAG, "initData: data =  " + data);
        for (int i = 0; i < selectTime.length(); i++) {
            if (selectTime.substring(i, i + 1).equals("1")) {
                adapter.setSelectItem(i);
            }
        }
    }

    private String arrayToString(int[] a) {
        String selectTime = new String();
        for (int i = 0; i < a.length; i++) {
            selectTime = selectTime + a[i];
        }
        return selectTime;
    }

    /**
     * 设置梦想之花播报设置信息
     *
     * @param cmdindex
     * @param data
     */
    public void sendSetDreamFlowerConfigMsg(String cmdindex, Object data) {
        JSONObject object = new JSONObject();

        try {
            object.put("cmd", "402");
            object.put("gwID", mDevice.gwID);
            object.put("cmdindex", cmdindex);
            object.put("cmdtype", "set");
            if (data != null) {
                object.put("data", data);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 2进制转16进制（一次转4位）
     *
     * @param binary
     * @return
     */
    private String binary2Hex(String binary) {
        String tmp1 = new String();
        for (int i = 0; i < 24; ) {
            String tmp2 = Integer.toHexString(Integer.parseInt(binary.substring(i, i + 4), 2));
            i = i + 4;
            tmp1 = tmp1 + tmp2;
        }
        WLog.i(TAG, "binary2Hex: tmp1 = " + tmp1);
        return tmp1;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_right:
                String oldStr = arrayToString(data);
                String reverseStr = new StringBuilder(oldStr).reverse().toString();
                String binary = reverseStr.substring(0, 24);
                sendSetDreamFlowerConfigMsg(FLOWER_BROADCAST_TIME,binary2Hex(binary));
                this.finish();
                break;
        }
    }
}

