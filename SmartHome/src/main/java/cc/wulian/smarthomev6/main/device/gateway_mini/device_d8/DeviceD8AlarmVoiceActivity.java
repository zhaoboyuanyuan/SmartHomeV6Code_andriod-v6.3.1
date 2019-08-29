package cc.wulian.smarthomev6.main.device.gateway_mini.device_d8;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import cc.wulian.smarthomev6.main.device.gateway_mini.D8AlarmTimeAdapter;
import cc.wulian.smarthomev6.main.device.gateway_mini.D8AlarmVoiceAdapter;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/8/23.
 * func： mini网关报时声音界面
 * email: hxc242313@qq.com
 */

public class DeviceD8AlarmVoiceActivity extends BaseTitleActivity implements View.OnClickListener {
    private ListView lvAlarmVoice;
    private D8AlarmVoiceAdapter adapter;

    private String deviceID;
    private String attributeValue;
    private Device mDevice;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_d8_alarm_voice, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightBtn(getString(R.string.Minigateway_Givetime_Selectionsound), getString(R.string.Sure));
    }

    @Override
    protected void initView() {
        super.initView();
        lvAlarmVoice = (ListView) findViewById(R.id.lv_alarm_voice);

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        lvAlarmVoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectItem(position);
                type = position + 1;
                WLog.i(TAG, "onItemClick: " + position);
                adapter.notifyDataSetInvalidated();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        deviceID = getIntent().getStringExtra("deviceID");
        attributeValue = getIntent().getStringExtra("attributeValue");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        adapter = new D8AlarmVoiceAdapter(this, getData());
        if(!TextUtils.isEmpty(attributeValue)){
            adapter.setSelectItem(Integer.parseInt(attributeValue.substring(1,2))-1);
            WLog.i(TAG, "initData: setlectValue = "+(Integer.parseInt(attributeValue.substring(1,2))-1));
        }
        lvAlarmVoice.setAdapter(adapter);
    }

    private List<String> getData() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.Minigateway_Selectionsound_Voice));
        list.add(getString(R.string.Minigateway_Voice_Didi));
        list.add(getString(R.string.Minigateway_Voice_Wave));
        list.add(getString(R.string.Minigateway_Voice_Cuckoo));
        return list;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_right:
                setBroadcastVoice();
                this.finish();
                break;
        }
    }

    private void setBroadcastVoice() {
        if (!TextUtils.isEmpty(attributeValue)) {
            String isOpen = attributeValue.substring(0, 1);
            String broadcastTime = attributeValue.substring(2, 8);
            String param = isOpen + type + broadcastTime;
            sendCmd(param);
            WLog.i(TAG, "setBroadcastVoice: param = " + param);
        }
    }

    private void sendCmd(String param) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", deviceID);
            object.put("cluster", 0x0500);
            object.put("endpointNumber", 1);
            object.put("endpointType", 0x0402);
            object.put("commandType", 1);
            object.put("commandId", 0x8003);
            JSONArray array = new JSONArray();
            array.put(param);
            object.put("parameter", array);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
