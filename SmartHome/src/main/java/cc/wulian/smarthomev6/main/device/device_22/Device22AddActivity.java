package cc.wulian.smarthomev6.main.device.device_22;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.Device22ConfigEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_AC;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_CUSTOM;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_TV;

public class Device22AddActivity extends BaseTitleActivity {

    private static final String ADD_REMOTE = "ADD_REMOTE";
    private TextView nameTextView;
    private Button nextStep;

    private String deviceId;
    private String type;
    private Device device;

    public static void start(Context context, String deviceId, String type) {
        Intent intent = new Intent(context, Device22AddActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_device22_add, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        deviceId = getIntent().getStringExtra("deviceId");
        type = getIntent().getStringExtra("type");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setToolBarTitle(getResources().getString(R.string.Home_Widget_Addremote));
    }

    @Override
    protected void initView() {
        nameTextView = (TextView) findViewById(R.id.tv_remote_name);
        nextStep = (Button) findViewById(R.id.btn_next_step);
        nextStep.setEnabled(false);
        nextStep.setAlpha(0.54f);
    }

    @Override
    protected void initListeners() {
        super.initListeners();

        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonMsgContent = new JSONObject();
                try {
                    JSONArray array = new JSONArray();
                    JSONObject dataObject = new JSONObject();
                    dataObject.put("name", nameTextView.getText().toString());
                    dataObject.put("type", type);
                    array.put(dataObject);

                    jsonMsgContent.put("cmd", "516");
                    jsonMsgContent.put("appID", mainApplication.getLocalInfo().appID);
                    jsonMsgContent.put("gwID", preference.getCurrentGatewayID());
                    jsonMsgContent.put("devID", deviceId);//设备id
                    jsonMsgContent.put("operType", 1);//模式
                    jsonMsgContent.put("mode", 1);//模式
                    jsonMsgContent.put("data", array);//模式
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialogManager.showDialog(ADD_REMOTE, Device22AddActivity.this, getString(R.string.Disposing), null, getResources().getInteger(R.integer.http_timeout));
                MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                        jsonMsgContent.toString(), MQTTManager.MODE_GATEWAY_FIRST
                );
            }
        });

        nameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    nextStep.setEnabled(false);
                    nextStep.setAlpha(0.54f);
                } else {
                    nextStep.setEnabled(true);
                    nextStep.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Device22ConfigEvent event) {
        WLog.d(TAG, "Device22ConfigEvent: " + event.data);
        progressDialogManager.dimissDialog(ADD_REMOTE, 0);
        if (!TextUtils.equals(event.deviceId, deviceId)) {
            return;
        }
        if (event.mode == 1) {
            try {
                JSONArray dataArray = new JSONArray(event.data);
                JSONObject object = dataArray.getJSONObject(0);

                String name = object.getString("name");
                String index = object.getString("index");
                String type = object.getString("type");

                if (!TextUtils.equals(name, nameTextView.getText().toString())){
                    return;
                }
                if (!TextUtils.equals(type, this.type)){
                    return;
                }

                if (TextUtils.equals(type, TYPE_TV)) {
                    TVRemoteActivity.edit(Device22AddActivity.this, deviceId, name, index);
                    finish();
                } else if (TextUtils.equals(type, TYPE_AC)) {
                    CustomRemoteActivity.edit(Device22AddActivity.this, deviceId, name, type, index);
                    finish();
                } else if (TextUtils.equals(type, TYPE_CUSTOM)) {
                    CustomRemoteActivity.edit(Device22AddActivity.this, deviceId, name, type, index);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
