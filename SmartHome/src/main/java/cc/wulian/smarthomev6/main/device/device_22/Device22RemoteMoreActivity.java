package cc.wulian.smarthomev6.main.device.device_22;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.Device22ConfigEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_AC;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_CUSTOM;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_TV;

public class Device22RemoteMoreActivity extends BaseTitleActivity {

    public static final String CHANGE_NAME = "CHANGE_NAME";
    public static final String DELETE = "DELETE";
    private TextView mTextName;
    private String deviceID, mType, mName, mIndex;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    public static void start(Activity context, String deviceID, String index, String name, String type) {
        Intent intent = new Intent(context, Device22RemoteMoreActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("index", index);
        intent.putExtra("name", name);
        intent.putExtra("type", type);
        context.startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device22_more, true);

        EventBus.getDefault().register(this);

        deviceID = getIntent().getStringExtra("deviceID");
        mType = getIntent().getStringExtra("type");
        mName = getIntent().getStringExtra("name");
        mIndex = getIntent().getStringExtra("index");

        mTextName = (TextView) findViewById(R.id.item_device_more_rename_name);

        if (!TextUtils.isEmpty(mName)) {
            mTextName.setText(mName);
        }

        findViewById(R.id.item_device_more_delete).setOnClickListener(this);
        findViewById(R.id.item_device_more_rename).setOnClickListener(this);
        findViewById(R.id.item_device_more_edit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.item_device_more_rename:
                showChangeNameDialog();
                break;
            case R.id.item_device_more_delete:
                showDeleteDialog();
                break;
            case R.id.item_device_more_edit:
                if (TextUtils.equals(mType, TYPE_TV)){
                    TVRemoteActivity.edit(this, deviceID, mName, mIndex);
                }else if (TextUtils.equals(mType, TYPE_AC) || TextUtils.equals(mType, TYPE_CUSTOM)){
                    CustomRemoteActivity.edit(this, deviceID, mName, mType, mIndex);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private void showChangeNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextText(mName)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (TextUtils.isEmpty(msg.trim())) {
                            ToastUtil.show(R.string.Mine_Rename_Empty);
                            return;
                        }

                        dialog.dismiss();
                        rename(msg.trim());
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

    private void rename(String name) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            JSONObject dataObject = new JSONObject();
            dataObject.put("index", mIndex);
            dataObject.put("type", mType);
            dataObject.put("name", name);
            array.put(dataObject);

            jsonMsgContent.put("cmd", "516");
            jsonMsgContent.put("appID", mainApplication.getLocalInfo().appID);
            jsonMsgContent.put("gwID", preference.getCurrentGatewayID());
            jsonMsgContent.put("devID", deviceID);//设备id
            jsonMsgContent.put("operType", 3);//模式
            jsonMsgContent.put("mode", 1);
            jsonMsgContent.put("data", array);//模式
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialogManager.showDialog(CHANGE_NAME, Device22RemoteMoreActivity.this, getString(R.string.Disposing), null, getResources().getInteger(R.integer.http_timeout));
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                jsonMsgContent.toString(), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    private void showDeleteDialog() {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(R.string.Infraredtransponder_Airconditioner_Reconfirm)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        dialog.dismiss();
                        delete();
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

    private void delete() {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            JSONObject dataObject = new JSONObject();
            dataObject.put("index", mIndex);
            dataObject.put("type", mType);
            dataObject.put("name", mName);
            array.put(dataObject);

            jsonMsgContent.put("cmd", "516");
            jsonMsgContent.put("appID", mainApplication.getLocalInfo().appID);
            jsonMsgContent.put("gwID", preference.getCurrentGatewayID());
            jsonMsgContent.put("devID", deviceID);//设备id
            jsonMsgContent.put("operType", 2);//模式
            jsonMsgContent.put("mode", 1);
            jsonMsgContent.put("data", array);//模式
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialogManager.showDialog(DELETE, Device22RemoteMoreActivity.this, getString(R.string.Disposing), null, getResources().getInteger(R.integer.http_timeout));
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                jsonMsgContent.toString(), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Device22ConfigEvent event) {
        WLog.d(TAG, "Device22ConfigEvent: " + event.data);
        if (!TextUtils.equals(event.deviceId, deviceID)) {
            return;
        }
        if (event.mode == 1) {
            if (event.operType == 3) {
                progressDialogManager.dimissDialog(CHANGE_NAME, 0);
                try {
                    JSONArray dataArray = new JSONArray(event.data);
                    JSONObject object = dataArray.getJSONObject(0);
                    String name = object.optString("name");
                    String index = object.optString("index");
                    String type = object.optString("type");
                    if (TextUtils.equals(index, mIndex)) {
                        mName = name;
                        mTextName.setText(mName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (event.operType == 2) {
                progressDialogManager.dimissDialog(DELETE, 0);
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        setToolBarTitle(R.string.Home_Edit_More);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
