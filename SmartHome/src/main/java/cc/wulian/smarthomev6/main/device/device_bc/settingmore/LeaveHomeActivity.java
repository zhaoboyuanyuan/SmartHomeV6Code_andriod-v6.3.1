package cc.wulian.smarthomev6.main.device.device_bc.settingmore;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;

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
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.main.home.scene.HouseKeeperActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBindingBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetSceneBindingEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by yuxiaoxuan on 2017/6/5.
 * 用于离家按钮选择场景
 * 对应的H5页面是:device/doorLock_Bc/LeaveHomeForChooseScene.html
 */

public class LeaveHomeActivity extends H5BridgeActivity {

    private Context context;
    private String devID;
    private String gwID;
    private Device device;
    private String leaveHomeUrl= HttpUrlKey.URL_BASE_DEVICE+"/doorLock_Bc/LeaveHomeForChooseScene.html";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        context=this;
        super.onCreate(savedInstanceState);
        if(getIntent()!=null){
            gwID=getIntent().getStringExtra("gwID");
            devID=getIntent().getStringExtra("devID");
            device = MainApplication.getApplication().getDeviceCache().get(devID);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        getSceneBinding();
        super.onDestroy();
    }
    private void getSceneBinding(){
        MainApplication.getApplication().getMqttManager().
                publishEncryptedMessage(
                        MQTTCmdHelper.createGetSceneBinding(gwID,devID),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }
    @Override
    protected void init() {

    }

    @Override
    protected String getUrl() {
        return leaveHomeUrl;
    }

    @Override
    protected void registerHandler() {
        super.registerHandler();
        mWebViewClient.registerHandler("getDeviceInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求 : 设备信息 " + data);
                if (TextUtils.isEmpty(device.data)) {
                    ToastUtil.single(R.string.Device_data_error);
                    return;
                }
                try {
                    callback.callback(new JSONObject(JSON.toJSONString(device)));
                    WLog.i(TAG, "设备信息：" + new JSONObject(JSON.toJSONString(device)));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mWebViewClient.registerHandler("controlDevice", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 控制设备 " + data);
                if (data != null) {
                    ((MainApplication) getApplication())
                            .getMqttManager()
                            .publishEncryptedMessage(data.toString(), MQTTManager.MODE_GATEWAY_FIRST);
                }
                callback.callback("YES");
            }
        });
//        mWebViewClient.registerHandler("bindingScene", new WVJBWebViewClient.WVJBHandler() {
//            @Override
//            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
//                String sceneID=data.toString();
//                setSceneBinding(sceneID);
//            }
//        });
    }

    private void setSceneBinding(String sceneID){
        List<SceneBindingBean> dataList=new ArrayList<>();
        SceneBindingBean sceneBindingBean=new SceneBindingBean();
        sceneBindingBean.endpointNumber="1";
        sceneBindingBean.sceneID=sceneID;
        dataList.add(sceneBindingBean);
        MainApplication.getApplication().getMqttManager().
                publishEncryptedMessage(
                        MQTTCmdHelper.createSetSceneBinding(gwID,devID,dataList),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSetSceneBindingEvent(SetSceneBindingEvent event) {
//        WLog.d(TAG,event.jsonData);
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event!=null&&event.device!=null) {
            if (TextUtils.equals(event.device.devID, devID)) {
                newDataRefresh(event.device.data);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetSceneBindingEvent(SetSceneBindingEvent result){
        if(result!=null){
            newDataRefresh(result.jsonData);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSceneBindingEvent(GetSceneBindingEvent result){
        if(result!=null&&TextUtils.equals(result.getDevID(),devID)){
            newDataRefresh(result.jsonData);
        }
    }
    private void newDataRefresh(String data) {

        if (TextUtils.isEmpty(data)) {
            ToastUtil.single(R.string.Device_data_error);
            return;
        }
        try {
            WLog.i(TAG, "newDataRefresh: " + new JSONObject(data));
            mWebViewClient.callHandler("newDataRefresh", new JSONObject(data), new WVJBWebViewClient.WVJBResponseCallback() {
                @Override
                public void callback(Object data) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
