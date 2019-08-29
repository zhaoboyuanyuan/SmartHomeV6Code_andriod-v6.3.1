package cc.wulian.smarthomev6.main.device.device_Ao;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by yuxx on 2017/7/20.
 * 用于三键金属开关首页Widget跳转到选择场景或者绑定设备的页面
 */

public class DevAoForChooseBindActivity extends H5BridgeActivity {
    private Context context;
    private String devID;
    private String gwID;
    private Device device;
    private String epNum;
    private int chooseMode=0;//1 绑定设备；2 绑定场景
    private String chooseDeviceUrl3 = HttpUrlKey.URL_BASE_DEVICE+"/switch_Ao/bindDevice.html?";
    private String chooseSeceneUrl3 = HttpUrlKey.URL_BASE_DEVICE+"/switch_Ao/sceneList.html?";
    private String chooseDeviceUrl2 = HttpUrlKey.URL_BASE_DEVICE+"/switch_An/bindDevice.html?";
    private String chooseSeceneUrl2 = HttpUrlKey.URL_BASE_DEVICE+"/switch_An/sceneList.html?";
    private String chooseDeviceUrl1 = HttpUrlKey.URL_BASE_DEVICE+"/switch_Am/bindDevice.html?";
    private String chooseSeceneUrl1 = HttpUrlKey.URL_BASE_DEVICE+"/switch_Am/sceneList.html?";
    String room = "deviceAn";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
    }

    @Override
    protected void init() {
        super.init();
        if(getIntent()!=null){
            gwID=getIntent().getStringExtra("gwID");
            devID=getIntent().getStringExtra("devID");
            epNum=getIntent().getStringExtra("epNum");
            chooseMode=getIntent().getIntExtra("chooseMode",0);
//            nativeStorage.setItem("Token_Ao","epNum",epNum);
            device = MainApplication.getApplication().getDeviceCache().get(devID);
        }
    }

    @Override
    protected String getUrl() {
        String fullUrl = "";
        if(chooseMode==1){
            nativeStorage.setItem(room, "deviceID", devID);
            nativeStorage.setItem(room, "endpointNumber", epNum);
            switch (device.type){
                case "Am":
                    fullUrl = chooseDeviceUrl1;
                    break;
                case "An":
                    fullUrl = chooseDeviceUrl2;
                    break;
                case "Ao":
                    nativeStorage.setItem(room, "gwID", gwID);
                    fullUrl = chooseDeviceUrl3;
                    break;
            }
        }else if(chooseMode==2){
            switch (device.type){
                case "Am":
                    fullUrl = chooseSeceneUrl1;
                    break;
                case "An":
                    fullUrl = chooseSeceneUrl2;
                    break;
                case "Ao":
                    fullUrl = chooseSeceneUrl3;
                    break;
            }
            fullUrl += "sceneID=null&epNum="+epNum+"&pageMode=1";
        }else{
            finish();
        }
        return fullUrl;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetSceneBindingEvent(SetSceneBindingEvent result){
        if(result!=null){
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event!=null&&event.device!=null) {
            if (TextUtils.equals(event.device.devID, devID)) {
                newDataRefresh(event.device.data);
            }
        }
    }

}
