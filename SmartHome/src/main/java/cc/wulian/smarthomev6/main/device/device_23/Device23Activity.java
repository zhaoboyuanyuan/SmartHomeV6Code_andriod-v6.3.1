package cc.wulian.smarthomev6.main.device.device_23;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UeiConfig;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.device.adapter.Device23Adapter;
import cc.wulian.smarthomev6.main.device.device_23.AirConditioning.AirConditioningMainActivity;
import cc.wulian.smarthomev6.main.device.device_23.custom.CustomActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.AudioRemoteMainActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.ProjectorRemoteMainActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.TvRemoteMainActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayConfigBean;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.MessageListenerAdapter;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.SINGLE_CODE_PROJECTOR;

/**
 * Created by Veev on 2017/8/22
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    红外转发器主页面
 */
public class Device23Activity extends BaseTitleActivity {

    private String deviceID;
    private boolean isSceneOrHouseKeeper;
    private Device mDevice;
    private View mViewAdd;
    private RecyclerView mRecyclerView;
    private Device23Adapter mAdapter;
    private ImageView mImageEmpty;
    private TextView mTextEmpty;
    private RelativeLayout mRelativeOffline;
    private GatewayConfigBean mConfigBean;

    public static void start(Context context, String deviceID, boolean isSceneOrHouseKeeper) {
        Intent intent = new Intent(context, Device23Activity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("isSceneOrHouseKeeper", isSceneOrHouseKeeper);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device23, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        isSceneOrHouseKeeper = getIntent().getBooleanExtra("isSceneOrHouseKeeper", false);
        deviceID = getIntent().getStringExtra("deviceID");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
    }

    @Override
    protected void initView() {
        super.initView();

        mRecyclerView = (RecyclerView) findViewById(R.id.device_23_recycler);
        mImageEmpty = (ImageView) findViewById(R.id.device_23_image_empty);
        mTextEmpty = (TextView) findViewById(R.id.device_23_text_empty);
        mRelativeOffline = (RelativeLayout) findViewById(R.id.device_23_relative_offline);
        mViewAdd = findViewById(R.id.device_23_add);

        if (isSceneOrHouseKeeper){
            mViewAdd.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        mAdapter = new Device23Adapter(deviceID);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                MQTTCmdHelper.createGatewayConfig(
                        Preference.getPreferences().getCurrentGatewayID(),
                        3,
                        MainApplication.getApplication().getLocalInfo().appID,
                        deviceID,
                        "list",
                        null,
                        null
                ), MQTTManager.MODE_GATEWAY_FIRST
        );

        update();
        updateState();
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();

        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(mViewAdd, SkinResouceKey.BITMAP_BUTTON_BG_S);
    }

    @Override
    protected void initListeners() {
        super.initListeners();

        mViewAdd.setOnClickListener(this);
        mRelativeOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAdapter.setOnItemClickListener(new Device23Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(UeiConfig bean) {
                switch (bean.type) {
                    case "Z":
                        if (isSceneOrHouseKeeper){
                            AirConditioningMainActivity.startForScene(
                                    Device23Activity.this,
                                    deviceID,
                                    bean.name,
                                    bean.brand,
                                    bean.pick,
                                    bean.time);
                        }else {
                            AirConditioningMainActivity.start(
                                    Device23Activity.this,
                                    deviceID,
                                    bean.name,
                                    bean.brand,
                                    bean.pick,
                                    bean.time);
                        }
                        break;
                    case "T":
                        if(SINGLE_CODE_PROJECTOR.equals(bean.singleCode)){
                            if (isSceneOrHouseKeeper){
                                ProjectorRemoteMainActivity.startForScene(Device23Activity.this,deviceID,bean.type,
                                        bean.name,
                                        bean.brand,
                                        bean.pick,
                                        bean.time);
                            }else {
                                ProjectorRemoteMainActivity.start(Device23Activity.this,deviceID,bean.type,
                                        bean.name,
                                        bean.brand,
                                        bean.pick,
                                        bean.time);
                            }
                        }else{
                            if (isSceneOrHouseKeeper){
                                TvRemoteMainActivity.startForScene(Device23Activity.this,deviceID,bean.type,
                                        bean.name,
                                        bean.brand,
                                        bean.pick,
                                        bean.time);
                            }else {
                                TvRemoteMainActivity.start(Device23Activity.this,deviceID,bean.type,
                                        bean.name,
                                        bean.brand,
                                        bean.pick,
                                        bean.time);
                            }
                        }
                        break;
                    case "C":
                        if (isSceneOrHouseKeeper){
                            TvRemoteMainActivity.startForScene(Device23Activity.this,deviceID,bean.type,
                                    bean.name,
                                    bean.brand,
                                    bean.pick,
                                    bean.time);
                        }else {
                            TvRemoteMainActivity.start(Device23Activity.this,deviceID,bean.type,
                                    bean.name,
                                    bean.brand,
                                    bean.pick,
                                    bean.time);
                        }
                        break;
                    case "R,M":
                        if (isSceneOrHouseKeeper){
                            AudioRemoteMainActivity.startForScene(Device23Activity.this,deviceID,bean.type,
                                    bean.name,
                                    bean.brand,
                                    bean.pick,
                                    bean.time);
                        }else {
                            AudioRemoteMainActivity.start(Device23Activity.this,deviceID,bean.type,
                                    bean.name,
                                    bean.brand,
                                    bean.pick,
                                    bean.time);
                        }
                        break;
                    case "CUSTOM":
                        if (isSceneOrHouseKeeper){
                            CustomActivity.startForScene(Device23Activity.this,deviceID,
                                    bean.name,
                                    bean.brand,
                                    bean.pick,
                                    bean.time);
                        }else {
                            CustomActivity.start(Device23Activity.this,deviceID,
                                    bean.name,
                                    bean.brand,
                                    bean.pick,
                                    bean.time);
                        }
                        break;
                }
            }
        });
    }

    private void updateState() {
        mViewAdd.setEnabled(mDevice.isOnLine());
        mRelativeOffline.setVisibility(mDevice.isOnLine() ? View.GONE : View.VISIBLE);
        setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_right:
                startMore();
                break;
            case R.id.device_23_add:
                if (!TextUtils.equals(preference.getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
                    showDialog();
                } else {
                    if (mAdapter.getItemCount() >= 20){
                        ToastUtil.single(R.string.Infraredtransponder_Upper_Limit);
                    }else {
                        Device23CategoryListActivity.start(Device23Activity.this, deviceID);
                    }
                }
                break;
        }
    }

    private WLDialog.Builder builder;
    private WLDialog dialog;

    private void showDialog() {
        builder = new WLDialog.Builder(Device23Activity.this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(R.string.Infraredtransponder_Adddevice_LANlogin)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setListener(new MessageListenerAdapter() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GatewayConfigEvent event) {
        WLog.i(TAG, "收到红外config了: " + event.bean);
        if (!TextUtils.equals(event.bean.d, deviceID)) {
            return;
        }
        update();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceReportEvent event) {
        if (event != null && TextUtils.equals(event.device.devID, deviceID)) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            updateState();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceInfoChangedEvent event) {
        if (event != null && TextUtils.equals(event.deviceInfoBean.devID, deviceID)) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
        }
    }

    private void update() {
        mConfigBean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");

        if (mConfigBean == null || TextUtils.isEmpty(mConfigBean.v)) {
            mAdapter.clear();
            mRecyclerView.setVisibility(View.INVISIBLE);
            mImageEmpty.setVisibility(View.VISIBLE);
            mTextEmpty.setVisibility(View.VISIBLE);
            return;
        }


        List<UeiConfig> configs = JSON.parseArray(mConfigBean.v, UeiConfig.class);

        WLog.i(TAG, "onEvent: " + configs);

        if (!configs.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mImageEmpty.setVisibility(View.INVISIBLE);
            mTextEmpty.setVisibility(View.INVISIBLE);
            mAdapter.setData(configs);
        }
    }

    /**
     * 设置更多信息
     */
    private void startMore() {
        JSONObject json = new JSONObject();
        try {
            json.put("deviceID", deviceID);
            JSONArray data = new JSONArray();

            JSONObject logJson = new JSONObject();
            logJson.put("group", "log");
            logJson.put("enableWithEnterType", "account");
            JSONArray param = new JSONArray();
            JSONObject deviceIdJson = new JSONObject();
            deviceIdJson.put("key", "deviceID");
            deviceIdJson.put("type", "string");
            deviceIdJson.put("value", deviceID);
            param.put(deviceIdJson);
            logJson.put("param", param);
            JSONArray item = new JSONArray();
            JSONObject alarmItem = new JSONObject();
            alarmItem.put("type", "jump");
            alarmItem.put("name", getString(R.string.Message_Center_AlarmMessage));
            alarmItem.put("action", "jump:Alarm");
            JSONObject logItem = new JSONObject();
            logItem.put("type", "jump");
            logItem.put("name", getString(R.string.Message_Center_Log));
            logItem.put("action", "jump:Log");
            item.put(alarmItem).put(logItem);
            logJson.put("item", item);

            JSONObject controllerJson = new JSONObject();
            controllerJson.put("group", "controller");
            controllerJson.put("enableWithEnterType", "account");
            controllerJson.put("offLineDisable", true);
            controllerJson.put("param", param);
            JSONArray item2 = new JSONArray();
            JSONObject deleteItem = new JSONObject();
            deleteItem.put("type", "custom");
            deleteItem.put("name", getString(R.string.Infraredrelay_More_Emptylibrary));
            deleteItem.put("action", "custom:Clear_Controller_Storage");
            item2.put(deleteItem);
            controllerJson.put("item", item2);

//            data.put(logJson);//.put(controllerJson);
            data.put(controllerJson);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Device23Activity.this, DeviceMoreActivity.class);
        intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceID);
        intent.putExtra(DeviceMoreActivity.KEY_MORE_CONFIG, json.toString());
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            finish();
        }
    }
}
