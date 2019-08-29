package cc.wulian.smarthomev6.main.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.tools.AlarmTool;

/**
 * 门锁widget  dialog
 */
public class LockDialogActivity extends Activity {

    private TextView textMsg;
    private EditText editInput;
    private Button btnCancel, btnSure;

    private Device device;

    private String lockType, key;

    private boolean hasExtData = false;

    public static void start(Context context, String devID) {
        Intent intent = new Intent(context, LockDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("devID", devID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lock_dialog);

        parserIntent();

        btnCancel = (Button) findViewById(R.id.cancel);
        btnSure = (Button) findViewById(R.id.sure);
        textMsg = (TextView) findViewById(R.id.text_msg);
        editInput = (EditText) findViewById(R.id.edit_input);

        textMsg.setText(String.format(getString(R.string.Device_DoorLock_Set_NickName_Tip), lockType));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editInput.getText().toString().trim();
                if (!name.isEmpty()) {
//                    MainApplication.getApplication().getGatewayConfigCache().put(key, name);
//                    String vv = MainApplication.getApplication().getGatewayConfigCache().getJson(key);
//                    MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
//                            MQTTCmdHelper.createGatewayConfig(
//                                    Preference.getPreferences().getCurrentGatewayID(),
//                                    hasExtData ? 2 : 1,
//                                    MainApplication.getApplication().getLocalInfo().appID,
//                                    device.devID,
//                                    GatewayConfigCache.getKey(key),
//                                    vv,
//                                    null
//                            ), MQTTManager.MODE_GATEWAY_FIRST
//                    );
                    finish();
                }
            }
        });


    }

    private void parserIntent() {
        String devID = getIntent().getStringExtra("devID");
        device = MainApplication.getApplication().getDeviceCache().get(devID);

        try {
            JSONObject object = new JSONObject(device.data);
            JSONArray endpoints = object.getJSONArray("endpoints");
            // TODO: 2017/5/16 更新 endpointNumber
//                        endpointNumber = object.getString("endpointNumber");
            String extData = object.optString("extData");
            hasExtData = !TextUtils.isEmpty(extData);
            JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).optInt("attributeId");

            if (attributeId == 0x8002) {
                try {
                    int value = Integer.parseInt(attributeValue);
                    key = attributeValue;
                    lockType = AlarmTool.getLockType(value).substring(0, 2) + key.substring(2) ;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
