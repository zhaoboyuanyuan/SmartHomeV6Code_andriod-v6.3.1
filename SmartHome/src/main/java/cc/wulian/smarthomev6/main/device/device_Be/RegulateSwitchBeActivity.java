package cc.wulian.smarthomev6.main.device.device_Be;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.RegulateSwitch;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetSceneBindingEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.DrawableUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class RegulateSwitchBeActivity extends BaseTitleActivity {

    private String deviceID;
    private Device mDevice;
    private RegulateSwitch regulate_switch;
    private ImageView iv_scene_add_btn;
    private TextView tv_scene_add_text;
    private View relative_offline;
//    private String setPercentValue = "0";
    private String sceneID;

    public static void start(Context context, String deviceID) {
        Intent intent = new Intent(context, RegulateSwitchBeActivity.class);
        intent.putExtra("deviceID", deviceID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_be, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        iv_scene_add_btn = (ImageView) findViewById(R.id.scene_add_btn);
        tv_scene_add_text = (TextView) findViewById(R.id.scene_add_text);
        regulate_switch = (RegulateSwitch) findViewById(R.id.regulate_switch);
        relative_offline = findViewById(R.id.relative_offline);
        regulate_switch.setTouchMove(true);
        regulate_switch.setOnSwitchListener(new RegulateSwitch.OnSwitchListener() {
            @Override
            public void onSwitch() {
                if(regulate_switch.getPercent()> 0){
                    sendCmd(0x03, 0x0008, 1, null);
                }else{
                    sendCmd(0x04, 0x0008, 1,null);
                }
            }

            @Override
            public void onRegulate(float percent) {
                if(percent == 0){
                    sendCmd(0x03, 0x0008, 1,null);
                }else{
                    JSONArray ja = new JSONArray();
                    if(regulate_switch.getPercent() > 0){
//                        setPercentValue = String.format("%.0f", percent * 100);
                        ja.put(String.format("%.0f", percent * 100));
                    }
                    sendCmd(0x08, 0x0008, 1, ja);
                }
            }
        });
        iv_scene_add_btn.setOnClickListener(this);
        getBindScenes();
        initShowData();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        deviceID = getIntent().getStringExtra("deviceID");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
    }

    private void initShowData() {
        if(mDevice.mode == 0){
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 0x0008) {
                        if (attribute.attributeId == 0x0011) {
                            if(!TextUtils.isEmpty(attribute.attributeValue)){
                                int value = Integer.valueOf(attribute.attributeValue);
                                regulate_switch.setPercent(value / 100f);
                            }
                        }
                    }
                }
            });
        }else if(mDevice.mode == 2){
            // 设备离线
            updateMode();
        }else if(mDevice.mode == 1){
            // 更新上线
            updateMode();
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 0x0008) {
                        if (attribute.attributeId == 0x0011) {
                            if(!TextUtils.isEmpty(attribute.attributeValue)){
                                int value = Integer.valueOf(attribute.attributeValue);
                                regulate_switch.setPercent(value / 100f);
                            }
                        }
                    }
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void updateScene(String json){
        if (json != null) {
            try {
                JSONObject jsonData = new JSONObject(json);
                String devID = jsonData.optString("devID");
                if(devID.equals(mDevice.devID)){
                    JSONArray data = jsonData.optJSONArray("data");
                    if(data == null || data.length() == 0){
                        return;
                    }
                    JSONObject jo = data.optJSONObject(0);
                    sceneID = jo.optString("sceneID");
                    SceneManager manager = new SceneManager(this);
                    SceneInfo scene = manager.getSceneById(sceneID);
                    if(TextUtils.isEmpty(sceneID)){
                        iv_scene_add_btn.setImageBitmap(null);
                        iv_scene_add_btn.setBackgroundResource(R.drawable.icon_scene_add);
                        tv_scene_add_text.setText(R.string.Device_More_Bind_Scene_Default);
                    }else{
                        Bitmap bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconNormal(RegulateSwitchBeActivity.this, scene.getIcon()));
                        iv_scene_add_btn.setImageBitmap(BitmapUtil.changeBitmapColor(bitmap, getResources().getColor(R.color.newPrimaryDark)));
                        iv_scene_add_btn.setBackgroundResource(R.drawable.scene_bg);
                        tv_scene_add_text.setText(scene.getName());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSceneBindingEvent(GetSceneBindingEvent event) {
        updateScene(event.jsonData);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneBindingReport(SetSceneBindingEvent event) {
        updateScene(event.jsonData);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if(event.device.mode == 0){
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 0x0008) {
                                if (attribute.attributeId == 0x0011) {
                                    regulate_switch.waiting(false);
                                    if(!TextUtils.isEmpty(attribute.attributeValue)){
                                        try {
//                                            int percent = Integer.valueOf(setPercentValue);
                                            int result = Integer.valueOf(attribute.attributeValue);
//                                            if(result != percent || percent == 0){
                                                regulate_switch.setPercent(result / 100f);
//                                            }
                                        }catch (Exception e){
                                            WLog.e("luzx", "attributeValue is num");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }else if(event.device.mode == 2){
                    // 设备离线
                    updateMode();
                }else if(event.device.mode == 1){
                    // 更新上线
                    updateMode();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.deviceInfoBean.mode == 2) {
                    setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
                }
            }
        }
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            regulate_switch.setEnabled(true);
            relative_offline.setVisibility(View.INVISIBLE);
        } else {
            // 离线
            regulate_switch.setPercent(0);
            regulate_switch.setEnabled(false);
            relative_offline.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_right:
                startMore();
                break;
            case R.id.scene_add_btn:
                Intent i = new Intent(this, BindSceneActivity.class);
                i.putExtra(BindSceneActivity.SCENE_ID, sceneID);
                i.putExtra(BindSceneActivity.DEVICE_ID, deviceID);
                startActivity(i);
                break;
        }
    }

    private void getBindScenes() {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "514");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendCmd(int commandId, int clusterId, int endpointNumber, JSONArray parameter) {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", endpointNumber);
            if(parameter != null){
                object.put("parameter", parameter);
            }
            object.put("clusterId", clusterId);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置更多信息
     */
    private void startMore() {
        Intent intent = new Intent(RegulateSwitchBeActivity.this, RegulateSwitchBeMoreActivity.class);
        intent.putExtra(RegulateSwitchBeMoreActivity.KEY_DEVICE_ID, deviceID);
        intent.putExtra(RegulateSwitchBeMoreActivity.KEY_GW_ID, mDevice.gwID);
        startActivity(intent);
    }
}
