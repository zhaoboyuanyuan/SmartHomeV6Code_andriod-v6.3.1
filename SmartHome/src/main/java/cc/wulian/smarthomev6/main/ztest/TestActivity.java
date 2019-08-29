package cc.wulian.smarthomev6.main.ztest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.lookever.CameraLiveStreamActivity;
import cc.wulian.smarthomev6.main.device.safeDog.SafeDogMainActivity;
import cc.wulian.smarthomev6.main.h5.H5BridgeCommonActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.WLog;


/**
 * 测试页
 *
 * 功能列表
 * 1. 查看BuildType
 * 2. 查看和修改Domain
 * 3. 查看和修改Project
 *          目前支持 wulian, 5038 仅加网不同
 * 4. 开启页面远程调试
 * 5. 查看设备信息
 * 6. 其他功能
 */
public class TestActivity extends BaseTitleActivity implements View.OnClickListener {

    public static boolean USE_REMOTE_DOMAIN = false;
    public static String REMOTE_BASE_URL = "http://172.19.5.208:8080";

    public static void start(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    private TextView mTextDeviceReport, mTextProject;
    private LinearLayout mLinearDomainCtrl;
    private EditText mEditRemote;
    private SwitchCompat mSwitchRemote;
    private RecyclerView mRecyclerDevice, mRecyclerProject;
    private TestDeviceRecyclerAdapter mTestDeviceRecyclerAdapter;
    private TestProjectRecyclerAdapter mTestProjectRecyclerAdapter;
    private String mTestDeviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test, true);

        TextView textType = (TextView) findViewById(R.id.test_text_build_type);
        TextView textDomain = (TextView) findViewById(R.id.test_text_domain);
        mTextProject = (TextView) findViewById(R.id.test_text_project);
        mTextDeviceReport = (TextView) findViewById(R.id.test_text_device_report);
        mRecyclerDevice = (RecyclerView) findViewById(R.id.test_recycler_devices);
        mRecyclerProject = (RecyclerView) findViewById(R.id.test_recycler_project);
        mLinearDomainCtrl = (LinearLayout) findViewById(R.id.test_linear_domain_ctrl);
        mEditRemote = (EditText) findViewById(R.id.test_edit_remote);
        mSwitchRemote = (SwitchCompat) findViewById(R.id.test_switch_remote);

        textType.setText(BuildConfig.BUILD_TYPE);
        textDomain.setText(ApiConstant.BASE_URL);
        mTextProject.setText(ApiConstant.getAppProject());

        mTestDeviceRecyclerAdapter = new TestDeviceRecyclerAdapter();
        mTestProjectRecyclerAdapter = new TestProjectRecyclerAdapter();

        mTestDeviceRecyclerAdapter.setOnItemClickListener(new TestDeviceRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String deviceID) {
                mTestDeviceId = deviceID;
                Device device = MainApplication.getApplication().getDeviceCache().get(mTestDeviceId);
                if (device != null) {
                    mTextDeviceReport.setText(WLog.format(JSON.toJSONString(device)));
                } else {
                    mTextDeviceReport.setText("");
                }
            }
        });

        mTestProjectRecyclerAdapter.setOnItemClickListener(new TestProjectRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String s) {
                ApiConstant.setAppProject(s);
                mTextProject.setText(ApiConstant.getAppProject());
            }
        });

        mRecyclerDevice.setNestedScrollingEnabled(false);
        mRecyclerDevice.setAdapter(mTestDeviceRecyclerAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerProject.setLayoutManager(linearLayoutManager);
        mRecyclerProject.setAdapter(mTestProjectRecyclerAdapter);
        List<String > list = new ArrayList<>();
        list.add("wulian_app");
        list.add("Legrand_app");
        mTestProjectRecyclerAdapter.setChoiceName(ApiConstant.getAppProject());
        mTestProjectRecyclerAdapter.update(list);

        updateDevice();

        mSwitchRemote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEditRemote.setVisibility(View.VISIBLE);
                    String remote = mEditRemote.getText().toString().trim();
                    if (!TextUtils.isEmpty(remote)) {
                        REMOTE_BASE_URL = remote;
                        USE_REMOTE_DOMAIN = true;
                    }
                } else {
                    mEditRemote.setVisibility(View.GONE);
                    USE_REMOTE_DOMAIN = false;
                }
            }
        });
        if (USE_REMOTE_DOMAIN) {
            mSwitchRemote.setChecked(true);
        }
        mEditRemote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String remote = mEditRemote.getText().toString().trim();
                if (TextUtils.isEmpty(remote)) {
                    USE_REMOTE_DOMAIN = false;
                    REMOTE_BASE_URL = "http://172.19.5.208:8080";
                } else {
                    REMOTE_BASE_URL = remote;
                    USE_REMOTE_DOMAIN = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle("测试");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        updateDevice();
        if (event != null && event.device != null) {
            if (event.device.devID.equals(mTestDeviceId)) {
                mTextDeviceReport.setText(WLog.format(JSON.toJSONString(event.device)));
            }
        }
    }

    private void updateDevice() {
        List<Device> list = new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices());
        mTestDeviceRecyclerAdapter.update(list);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_tv_controller:
//                RemoteControlBrandActivity.start(this,"62344A09004B1200","T");
//                TvRemoteMainActivity.start(this,"62344A09004B1200","T","T","T","T0816");
//                startActivity(new Intent(this, RadioStationSettingActivity.class));
//                AddHisenseDeviceActivity.start(this,"HS01");
//                startActivity(new Intent(this, SDConfigActivity.class));
                startActivity(new Intent(this, CameraLiveStreamActivity.class));
                break;
            case R.id.button_tv_box:
//                TvRemoteMainActivity.start(this,"62344A09004B1200","C","C","C","T0816");
                break;
            case R.id.button_assets:
//                AssetsManager.updateHtmlCommonAssets(this, null);

//                org.json.JSONObject setSceneBindingJson = new org.json.JSONObject();
//                try {
//                    setSceneBindingJson.put("cmd", "513");
//                    setSceneBindingJson.put("gwID", "50294D000123");
//                    setSceneBindingJson.put("devID", "D26DB805004B1200");
//                    setSceneBindingJson.put("mode", 1);
//                    setSceneBindingJson.put("userID", "7dba0b546fcf443fb4c7c62ce1384abb");
//                    JSONArray dataJson = new JSONArray();
//                    {
//                        org.json.JSONObject dataItemJson = new org.json.JSONObject();
//                        dataItemJson.put("endpointNumber", "2");
//                        dataItemJson.put("sceneID", "87");
//                        dataJson.put(dataItemJson);
//                    }
//                    setSceneBindingJson.put("data", dataJson);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                String data = setSceneBindingJson.toString();
                String data = "{\"userID\": \"7dba0b546fcf443fb4c7c62ce1384abb\", \"cmd\": \"513\", \"devID\": \"D26DB805004B1200\", \"gwID\": \"50294D000123\", \"mode\": 1, \"data\": [{\"endpointNumber\": \"2\", \"sceneID\": \"87\"}]}";
                MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                        data, MQTTManager.MODE_CLOUD_ONLY);
                break;

//            case R.id.button_change_2_dev:
//                ApiConstant.setBaseUrl("https://testv2.wulian.cc:50090");
//                break;
//            case R.id.button_change_2_test:
//                ApiConstant.setBaseUrl("https://testv6.wulian.cc");
//                break;
//            case R.id.button_change_2_release:
//                ApiConstant.setBaseUrl("https://iot.wuliancloud.com:443");
//                break;
            case R.id.button_print_devices:
                for (Device device : MainApplication.getApplication().getDeviceCache().getDevices()) {
                    WLog.i(TAG, device.data);
                }
                break;
            case R.id.test_linear_domain_panel:
                mLinearDomainCtrl.setVisibility(mLinearDomainCtrl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.button_bgm:
                Intent intent = new Intent(TestActivity.this, H5BridgeCommonActivity.class);
                intent.putExtra("url", "device/conditioning_Bp/device_Bp.html");
                startActivity(intent);
                break;
            case R.id.button_safedog:
                SafeDogMainActivity.start(TestActivity.this,"test");
                break;
            default:
                break;
        }
    }
}
