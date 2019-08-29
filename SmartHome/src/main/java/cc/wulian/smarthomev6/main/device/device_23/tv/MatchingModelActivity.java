package cc.wulian.smarthomev6.main.device.device_23.tv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uei.control.ACEService;
import com.wulian.routelibrary.utils.Base64Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_23.bean.ReportKeyBean;
import cc.wulian.smarthomev6.main.device.device_23.bean.TestCodeBean;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.SINGLE_CODE_PROJECTOR;

/**
 * created by huxc  on 2017/11/2.
 * func：匹配型号测试界面
 * email: hxc242313@qq.com
 */

public class MatchingModelActivity extends BaseTitleActivity {
    private LinearLayout llResponse;
    private LinearLayout llTestCode;
    private Button btnPositive;
    private Button btnNegative;
    private Button btnTest;
    private TextView tvCode;
    private TextView tvNoOsm;
    private ImageView ivType;

    private int testTime = 1;
    private String deviceId;
    private String mUeiUserID;
    private String brandName;
    private String localName;
    private String ueiType;
    private String singleCode;
    private String matchType;
    private String state;
    private String testCode;
    private String groupId;
    private int testKeyId;
    private String testCodeData;

    private DataApiUnit dataApiUnit;
    private Device device;

    public static void start(Context context, String deviceID, String brandName, String ueiType, String localName) {
        Intent intent = new Intent(context, MatchingModelActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("brandName", brandName);
        intent.putExtra("ueiType", ueiType);
        intent.putExtra("localName", localName);
        context.startActivity(intent);
    }

    public static void start(Context context, String deviceID, String brandName, String ueiType, String singleCode, String localName) {
        Intent intent = new Intent(context, MatchingModelActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("brandName", brandName);
        intent.putExtra("ueiType", ueiType);
        intent.putExtra("singleCode", singleCode);
        intent.putExtra("localName", localName);
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_mode, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Infraredrelay_Addremote_Matchingmodel));
    }

    @Override
    protected void initView() {
        super.initView();
        llResponse = (LinearLayout) findViewById(R.id.ll_response);
        llTestCode = (LinearLayout) findViewById(R.id.ll_test_code);
        btnPositive = (Button) findViewById(R.id.btn_positive);
        btnNegative = (Button) findViewById(R.id.btn_negative);
        btnTest = (Button) findViewById(R.id.btn_test);
        tvCode = (TextView) findViewById(R.id.tv_code);
        tvNoOsm = (TextView) findViewById(R.id.tv_no_osm);
        ivType = (ImageView) findViewById(R.id.img_uei);

    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnTest, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnTest, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
        btnTest.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceID");
        brandName = getIntent().getStringExtra("brandName");
        localName = getIntent().getStringExtra("localName");
        ueiType = getIntent().getStringExtra("ueiType");
        singleCode = getIntent().getStringExtra("singleCode");
        if (TextUtils.equals(ueiType, "C")) {
            ivType.setImageResource(R.drawable.bg_uei_type_c);
            groupId = "1";
            matchType = "C,N,S";
        } else if (TextUtils.equals(ueiType, "T")) {
            if(SINGLE_CODE_PROJECTOR.equals(singleCode)){
                ivType.setImageResource(R.drawable.bg_uei_type_t_t);
            }else{
                ivType.setImageResource(R.drawable.bg_uei_type_t);
            }
            groupId = "0";
            matchType = "T";
        } else if (TextUtils.equals(ueiType, "R,M")) {
            ivType.setImageResource(R.drawable.bg_uei_type_rm);
            groupId = "7";
            matchType = "R,M";
        }

        dataApiUnit = new DataApiUnit(this);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        mUeiUserID = ACEService.ACEncryptUserId(deviceId + System.currentTimeMillis());
        dataApiUnit.deGetUeiTestCode(mUeiUserID, brandName, matchType, groupId, "0", new DataApiUnit.DataApiCommonListener<TestCodeBean>() {
            @Override
            public void onSuccess(TestCodeBean bean) {
                WLog.i(TAG, "deGetUeiTestCode onSuccess: ");
                state = bean.state;
                testCode = bean.testCode;
                testCodeData = bean.testCodeData;
                testKeyId = bean.testKeyId;
                if (bean.hasOSM) {
                    updateView();
                } else {
                    tvNoOsm.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }


    /**
     * @param keyWorked
     * @func: 反馈是否有响应并且获取下一个key
     */
    private void reportOSMResultAndGetNextKey(boolean keyWorked) {
        dataApiUnit.doReportOSMResultAndGetNextKey(mUeiUserID, keyWorked, state, new DataApiUnit.DataApiCommonListener<ReportKeyBean>() {
            @Override
            public void onSuccess(ReportKeyBean bean) {
                state = bean.state;
                testCodeData = bean.testCodeData;
                testCode = bean.testCode;
                testKeyId = bean.testKeyId;
                updateView();
                if (bean.testResult == 5 || bean.testResult == 6 || bean.testResult == 7) {
                    DownLoadCodeLibraryActivity.start(MatchingModelActivity.this, deviceId, brandName, ueiType, singleCode, bean.matchedCode, localName);
                } else if (TextUtils.isEmpty(bean.testCode)) {
                    tvNoOsm.setText(MatchingModelActivity.this.getString(R.string.Infraredtransponder_Matching_Nomore));
                    tvNoOsm.setVisibility(View.VISIBLE);
                    llTestCode.setVisibility(View.GONE);
                    llResponse.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void updateView() {
        btnTest.setText(getString(R.string.Infraredrelay_Match_Testbutton) + testTime);
        tvCode.setText(testCode);
        llTestCode.setVisibility(View.VISIBLE);
        tvNoOsm.setVisibility(View.GONE);
    }

    private String getEpData(String testCodeData) {
        byte[] base64Data = Base64Util.decode(testCodeData);
        String data = toHexString1(base64Data);
        String epData = "0A10000190" + data;
        WLog.i(TAG, "getEpData: " + epData);
        return epData;
    }

    /**
     * 数组转成十六进制字符串
     * //     * @param byte[]
     *
     * @return HexString
     */
    public static String toHexString1(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }

    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

    //将匹配服务器匹配的结果下发给网关
    private void sendCmd(String data) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("commandType", 1);
            object.put("commandId", 0x8010);
            object.put("gwID", device.gwID);
            object.put("devID", deviceId);

            JSONArray array = new JSONArray();
            array.put(data);
            object.put("parameter", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_negative:
                testTime++;
                reportOSMResultAndGetNextKey(false);
                break;
            case R.id.btn_positive:
                testTime++;
                reportOSMResultAndGetNextKey(true);
                break;
            case R.id.btn_test:
                llResponse.setVisibility(View.VISIBLE);
                dataApiUnit.doSendToUeiHelper(testCode, testKeyId, testCodeData, new DataApiUnit.DataApiCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        WLog.i(TAG, "doSendToUeiHelper :" + bean.toString());
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(bean.toString());
                            JSONObject result = jsonobject.optJSONObject("result");
                            String keyCodeData = result.optString("keyCodeData");
                            sendCmd(getEpData(keyCodeData));
                            WLog.i(TAG, "keyCodeData: " + keyCodeData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.show(msg);
                    }
                });
                break;
        }
    }
}
