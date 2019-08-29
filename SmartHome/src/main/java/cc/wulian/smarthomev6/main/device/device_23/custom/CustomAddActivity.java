package cc.wulian.smarthomev6.main.device.device_23.custom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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
import cc.wulian.smarthomev6.main.device.device_22.CustomRemoteActivity;
import cc.wulian.smarthomev6.main.device.device_22.Device22AddActivity;
import cc.wulian.smarthomev6.main.device.device_22.TVRemoteActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.TvRemoteMainActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.Device22ConfigEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_AC;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_CUSTOM;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_TV;

public class CustomAddActivity extends BaseTitleActivity {

    private TextView nameTextView;
    private Button nextStep;

    private String deviceId;
    private Device device;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, CustomAddActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_add, true);
    }

    @Override
    protected void initTitle() {
        deviceId = getIntent().getStringExtra("deviceId");
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
                CustomActivity.learn(CustomAddActivity.this, deviceId, nameTextView.getText().toString(), null);
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
}
