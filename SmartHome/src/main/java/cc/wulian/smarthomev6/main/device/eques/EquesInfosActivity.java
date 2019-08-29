package cc.wulian.smarthomev6.main.device.eques;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eques.icvss.api.ICVSSUserInstance;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesInfoBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesUpdateBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 作者: chao
 * 时间: 2017/6/8
 * 描述: 设备信息页
 * 联系方式: 805901025@qq.com
 */

public class EquesInfosActivity extends BaseTitleActivity {
    public static String GET_INFO = "GET_INFO";

    private RelativeLayout rl_setting_model, rl_setting_number, rl_setting_version, rl_setting_wifi, rl_setting_wifi_level, rl_setting_wifi_mac;
    private TextView tv_setting_model;
    private TextView tv_setting_number;
    private TextView tv_setting_version;
    private TextView tv_setting_wifi;
    private TextView tv_setting_wifi_level;
    private TextView tv_setting_wifi_mac;
    private Button btn_setting_version;

    private ICVSSUserInstance icvss;
    private Device device;

    private String uid;
    private String deviceId;

    public static void start(Context context, String deviceId, String uid) {
        Intent intent = new Intent(context, EquesInfosActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        icvss = MainApplication.getApplication().getEquesApiUnit().getIcvss();
        setContentView(R.layout.activity_eques_info, true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Device_Info));
    }

    @Override
    protected void initView() {
        rl_setting_model = (RelativeLayout) findViewById(R.id.rl_setting_model);
        rl_setting_number = (RelativeLayout) findViewById(R.id.rl_setting_number);
        rl_setting_version = (RelativeLayout) findViewById(R.id.rl_setting_version);
        rl_setting_wifi = (RelativeLayout) findViewById(R.id.rl_setting_wifi);
        rl_setting_wifi_level = (RelativeLayout) findViewById(R.id.rl_setting_wifi_level);
        rl_setting_wifi_mac = (RelativeLayout) findViewById(R.id.rl_setting_wifi_mac);
        tv_setting_model = (TextView) findViewById(R.id.tv_setting_model);
        tv_setting_number = (TextView) findViewById(R.id.tv_setting_number);
        tv_setting_version = (TextView) findViewById(R.id.tv_setting_version);
        tv_setting_wifi = (TextView) findViewById(R.id.tv_setting_wifi);
        tv_setting_wifi_level = (TextView) findViewById(R.id.tv_setting_wifi_level);
        tv_setting_wifi_mac = (TextView) findViewById(R.id.tv_setting_wifi_mac);
        btn_setting_version = (Button) findViewById(R.id.btn_setting_version);
    }

    @Override
    protected void initListeners() {
        btn_setting_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icvss.equesUpgradeDevice(uid);
            }
        });
    }

    @Override
    protected void initData() {
        uid = getIntent().getStringExtra("uid");
        deviceId = getIntent().getStringExtra("deviceId");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        tv_setting_number.setText(deviceId);
        if (!TextUtils.isEmpty(uid) && device.isOnLine()){
            updateView(true);
            progressDialogManager.showDialog(GET_INFO, EquesInfosActivity.this, null, null);
            icvss.equesGetDeviceInfo(uid);
            setUpdate(device.data);
        }else {
            updateView(false);
        }
    }

    private void setWifiLevel(String level){
        if (!TextUtils.isEmpty(level)){
            if (TextUtils.equals(level, "0")){
                tv_setting_wifi_level.setText(getString(R.string.Cateyemini_Humandetection_Nothing));
            }else if (TextUtils.equals(level, "1")){
                tv_setting_wifi_level.setText(getString(R.string.Cateyemini_Humandetection_Weak));
            }else if (TextUtils.equals(level, "2")){
                tv_setting_wifi_level.setText(getString(R.string.Cateyemini_Humandetection_Middle));
            }else if (TextUtils.equals(level, "3")){
                tv_setting_wifi_level.setText(getString(R.string.Cateyemini_Humandetection_Strong));
            }else if (TextUtils.equals(level, "4")){
                tv_setting_wifi_level.setText(getString(R.string.Cateyemini_Humandetection_Verystrong));
            }
        }
    }

    private void setWifiMac(String mac){
        if (!TextUtils.isEmpty(mac)){
            try {
                JSONObject jsonObject = new JSONObject(mac);
                String tmpMac = jsonObject.optString("wifimac", null);
                if (!TextUtils.isEmpty(tmpMac)){
                    String[] temp = tmpMac.split("-");
                    temp[1] = temp[1].replaceAll("(.{2})", "$1:");
                    tv_setting_wifi_mac.setText(temp[1].substring(0,temp[1].length() - 1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpdate(String json){
        if (!TextUtils.isEmpty(json)){
            try {
                JSONObject jsonObject = new JSONObject(json);
                String remoteupg = jsonObject.optString("remoteupg", null);
                if (TextUtils.equals(remoteupg, "0")){
                    btn_setting_version.setVisibility(View.GONE);
                }else if (TextUtils.equals(remoteupg, "1")|| TextUtils.equals(remoteupg, "2")){
                    btn_setting_version.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EquesInfoBean event) {
        if (TextUtils.equals(deviceId, event.from)){
            progressDialogManager.dimissDialog(GET_INFO, 0);
            tv_setting_version.setText(event.sw_version);
            tv_setting_wifi.setText(event.wifi_config);
            setWifiLevel(event.wifi_level);
            setWifiMac(device.data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateResult(EquesUpdateBean event) {
        if (TextUtils.equals(deviceId, event.from)){
            if (event.result == 0){
                ToastUtil.show("更新失败");
                btn_setting_version.setVisibility(View.VISIBLE);
                btn_setting_version.setText("点击重试");
            }else if (event.result == 1){

            }else if (event.result == 2){
                btn_setting_version.setVisibility(View.VISIBLE);
                btn_setting_version.setText("正在升级...");
            }else if (event.result == 3){
                ToastUtil.show("更新失败");
                btn_setting_version.setVisibility(View.VISIBLE);
                btn_setting_version.setText("点击重试");
            }else if (event.result == 4){
                ToastUtil.show("更新失败");
                btn_setting_version.setVisibility(View.VISIBLE);
                btn_setting_version.setText("点击重试");
            }
        }
    }

    private void updateView(boolean isShow){
        if (isShow){
            rl_setting_version.setVisibility(View.VISIBLE);
            rl_setting_wifi.setVisibility(View.VISIBLE);
            rl_setting_wifi_level.setVisibility(View.VISIBLE);
            rl_setting_wifi_mac.setVisibility(View.VISIBLE);
        }else {
            rl_setting_version.setVisibility(View.GONE);
            rl_setting_wifi.setVisibility(View.GONE);
            rl_setting_wifi_level.setVisibility(View.GONE);
            rl_setting_wifi_mac.setVisibility(View.GONE);
        }
    }
}
