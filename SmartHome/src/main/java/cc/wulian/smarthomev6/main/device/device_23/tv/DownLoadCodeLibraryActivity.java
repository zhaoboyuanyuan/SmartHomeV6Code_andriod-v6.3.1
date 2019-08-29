package cc.wulian.smarthomev6.main.device.device_23.tv;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.uei.control.ACEService;
import com.wulian.routelibrary.utils.Base64Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UeiConfig;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_23.Device23Activity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.BrandBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayConfigBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/11/3.
 * func：下载码库界面
 * email: hxc242313@qq.com
 */

public class DownLoadCodeLibraryActivity extends BaseTitleActivity {
    private static final long TIMEOUT_TIME = 120 * 1000;
    private String code;
    private String deviceID;
    private String mUeiUserID;
    private String brandName;
    private String localName;
    private String ueiType;
    private String singleCode;
    private Device device;
    private String code2hex;

    private ImageView loadingAnimation;
    private AnimationDrawable mAnimation;
    private WLDialog failDialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    showFailDialog();
                    break;
            }
        }
    };
    private Runnable failRunnable;

    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };

    public static void start(Context context, String deviceID, String brandName, String ueiType, String code, String localName) {
        Intent intent = new Intent(context, DownLoadCodeLibraryActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("brandName", brandName);
        intent.putExtra("ueiType", ueiType);
        intent.putExtra("code", code);
        intent.putExtra("localName", localName);
        context.startActivity(intent);
    }

    public static void start(Context context, String deviceID, String brandName, String ueiType, String singleCode, String code, String localName) {
        Intent intent = new Intent(context, DownLoadCodeLibraryActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("brandName", brandName);
        intent.putExtra("ueiType", ueiType);
        intent.putExtra("singleCode", singleCode);
        intent.putExtra("code", code);
        intent.putExtra("localName", localName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_code_library, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (failDialog != null && failDialog.isShowing()) {
            failDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        super.initView();
        loadingAnimation = (ImageView) findViewById(R.id.loading);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Infraredrelay_Addremote_Download));
    }

    @Override
    protected void initData() {
        super.initData();
        code = getIntent().getStringExtra("code");
        deviceID = getIntent().getStringExtra("deviceID");
        brandName = getIntent().getStringExtra("brandName");
        localName = getIntent().getStringExtra("localName");
        ueiType = getIntent().getStringExtra("ueiType");
        singleCode = getIntent().getStringExtra("singleCode");
        mUeiUserID = ACEService.ACEncryptUserId(deviceID + System.currentTimeMillis());

        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        mAnimation = (AnimationDrawable) loadingAnimation
                .getDrawable();
        failRunnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        handler.postDelayed(failRunnable, TIMEOUT_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(mRunnable, 1000);
        downloadCodeLibrary();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAnimation(mAnimation);
    }

    protected void startAnimation(final AnimationDrawable animation) {
        if (animation != null && !animation.isRunning()) {
            animation.run();
        }
    }

    protected void stopAnimation(final AnimationDrawable animation) {
        if (animation != null && animation.isRunning())
            animation.stop();
    }

    private void downloadCodeLibrary() {
        DataApiUnit dataApiUnit = new DataApiUnit(this);
        dataApiUnit.doGetBrandCode(ueiType, code, mUeiUserID, new DataApiUnit.DataApiCommonListener<BrandBean.CodeBean>() {
            @Override
            public void onSuccess(BrandBean.CodeBean bean) {
                WLog.i(TAG, "onSuccess: " + bean.picks);
                if (!TextUtils.isEmpty(bean.picks)) {
                    sendCmd(getEpData(bean.picks, ueiType));
                }

            }

            @Override
            public void onFail(int code, String msg) {
                showFailDialog();
                handler.removeCallbacks(failRunnable);
            }
        });
    }

    private void updateGatewayInfo(String pick, String[] supportKeyArr, LinkedHashMap learnKeyCodeArr) {
        int m = 2;
        String time = System.currentTimeMillis() + "";
        UeiConfig ueiConfig = UeiConfig.newUeiDevice(time, brandName, localName, pick, ueiType, singleCode, supportKeyArr, learnKeyCodeArr);
        List<UeiConfig> configs;
        GatewayConfigBean bean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");
        if (bean == null) {
            m = 1;
            configs = new ArrayList<>();
        } else {
            if (TextUtils.isEmpty(bean.v)) {
                m = 1;
                configs = new ArrayList<>();
            } else {
                configs = JSON.parseArray(bean.v, UeiConfig.class);
            }
        }
        configs.add(ueiConfig);
        String v = Base64.encodeToString(JSON.toJSONString(configs).getBytes(), Base64.NO_WRAP);
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                MQTTCmdHelper.createGatewayConfig(
                        Preference.getPreferences().getCurrentGatewayID(),
                        m,
                        MainApplication.getApplication().getLocalInfo().appID,
                        deviceID,
                        "list",
                        v,
                        System.currentTimeMillis() + ""
                ), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    private void sendCmd(String data) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("devID", device.devID);
            object.put("clusterId", 0x0F01);
            object.put("commandType", 1);
            object.put("gwID", device.gwID);
            object.put("commandId", 0x8010);
            object.put("endpointNumber", 1);

            JSONArray array = new JSONArray();
            array.put(data);
            object.put("parameter", array);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showFailDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setMessage(getString(R.string.Infraredrelay_Downloadfailed_Prompt))
                .setTitle(getString(R.string.Infraredrelay_Downloadfailed_Title))
                .setPositiveButton(R.string.Sure)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        RemoteControlModelActivity.start(DownLoadCodeLibraryActivity.this, deviceID, ueiType, brandName, ueiType);
                    }

                    @Override
                    public void onClickNegative(View var1) {

                    }
                });
        failDialog = builder.create();
        if (!failDialog.isShowing()) {
            failDialog.show();
        }
    }

    private String getEpData(String picks, String ueiType) {
        String epData = null;
        byte[] data = Base64Util.decode(picks);
        //pickData经过base64解码后转成16进制字符串
        String picksData = StringUtil.toHexString1(data);
        //码库code后四位转成16进制
        code2hex = StringUtil.toHexString(Integer.parseInt(code.substring(1, code.length())), 4);
        int length = ("22" + getDeviceHexType(ueiType) + code2hex + picksData).length() / 2;
        String length2hex = StringUtil.toHexString(length, 4);
        //epData:0A+长度转16进制+22+ueiType转16进制+picksData转16进制
        epData = ("0A" + length2hex + "22" + getDeviceHexType(ueiType) + code2hex + picksData).toUpperCase();
        return epData;
    }

    private String getDeviceHexType(String ueiType) {
        String hexType = null;
        switch (ueiType) {
            case "T":
                hexType = "00";
                break;
            case "C":
                hexType = "01";
                break;
            case "R,M":
                hexType = "07";
                break;
            default:
                break;
        }
        return hexType;

    }

    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray endpoints = object.optJSONArray("endpoints");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
            if (!TextUtils.isEmpty(attributeValue) && TextUtils.equals("22", attributeValue.substring(2, 4))) {
                String result = attributeValue.substring(attributeValue.length() - 2, attributeValue.length());
                if (TextUtils.equals(result, "08") || TextUtils.equals(result, "00")) {//sendKey
                    String data2 = ("0A000402" + getDeviceHexType(ueiType) + code2hex).toUpperCase();
                    sendCmd(data2);
                } else {
                    showFailDialog();
                    handler.removeCallbacks(failRunnable);
                }
            } else if (!TextUtils.isEmpty(attributeValue) && TextUtils.equals("02", attributeValue.substring(2, 4))) {
                if (TextUtils.equals(attributeValue.substring(8, 10), "00")) {//getKeyMap
                    String supportValue = attributeValue.substring(10, attributeValue.length());
                    int keyLength = supportValue.length() / 2;
                    String[] supportKeyArr = new String[keyLength];
                    int index = 0;
                    for (int i = 0; i < supportValue.length(); i = i + 2) {
                        supportKeyArr[index] = Integer.parseInt(supportValue.substring(i, i + 2), 16) + "";
                        index++;
                    }
                    updateGatewayInfo(code, supportKeyArr, null);
                } else {
                    showFailDialog();
                    handler.removeCallbacks(failRunnable);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                dealData(event.device.data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayConfigEvent(GatewayConfigEvent event) {
        WLog.i(TAG, "onGatewayConfigEvent: " + event.bean.v);
        if (event.bean != null) {
            if (event.bean.appID != null && TextUtils.equals(event.bean.appID, MainApplication.getApplication().getLocalInfo().appID))
                if (event.bean.d != null && TextUtils.equals(event.bean.d, device.devID)) {
                    Device23Activity.start(this, deviceID, false);
                    finish();
                }
        }
    }
}
