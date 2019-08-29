package cc.wulian.smarthomev6.main.device.dreamFlower.radioStation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.dreamFlower.bean.BroadcastBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DreamFlowerBroadcastEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/12/8.
 * func： 梦想之花电台设置
 * email: hxc242313@qq.com
 */

public class RadioStationSettingActivity extends BaseTitleActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String QUERY = "query"; // 查询播报信息
    public static final String SET = "set"; // 设置播报信息
    public static final String FLOWER_BROADCAST_CONVENTIONAL = "62"; // 播报设置-常规播报
    public static final String FLOWER_BROADCAST_VOLUME = "65"; // 播报设置-音量设置
    public static final String CLOSE = "0"; // 开关关
    public static final String OPEN = "1"; // 开关开

    private ToggleButton tbCommonVoice;
    private RelativeLayout rlNowTime;
    private SeekBar seekBar;
    private TextView tvVoiceSize;
    private ImageView ivMute;
    private Device device;
    private BroadcastBean broadcastBean;

    public static void start(Context context, String deviceId) {
        Intent it = new Intent(context, RadioStationSettingActivity.class);
        it.putExtra("deviceId", deviceId);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_station_setting, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Mine_Setts));

    }

    @Override
    protected void initView() {
        super.initView();
        tbCommonVoice = (ToggleButton) findViewById(R.id.tb_common_voice);
        rlNowTime = (RelativeLayout) findViewById(R.id.rl_nowTime);
        seekBar = (SeekBar) findViewById(R.id.sb_voice);
        tvVoiceSize = (TextView) findViewById(R.id.tv_voice_size);
        ivMute = (ImageView) findViewById(R.id.tv_mute);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlNowTime.setOnClickListener(this);
        tbCommonVoice.setOnCheckedChangeListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVoiceSize.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendSetDreamFlowerConfigMsg(FLOWER_BROADCAST_VOLUME, String.valueOf(seekBar.getProgress()));
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        String deviceId = getIntent().getStringExtra("deviceId");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        sendGetDreamFlowerConfigMsg();

    }


    /**
     * 根据查询到的播报数据更新view
     *
     * @param broadcastBean
     */
    private void updateView(BroadcastBean broadcastBean) {
        if (TextUtils.equals(OPEN, broadcastBean.getNorVoice())) {
            tbCommonVoice.setChecked(true);
        } else if (TextUtils.equals(CLOSE, broadcastBean.getNorVoice())) {
            tbCommonVoice.setChecked(false);
        }
        seekBar.setProgress(Integer.parseInt(broadcastBean.getVolum()));
    }

    /**
     * 查询梦想之花播报设置信息
     */
    private void sendGetDreamFlowerConfigMsg() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "402");
            object.put("gwID", device.gwID);
            object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            object.put("cmdtype", "get");

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProgressDialogManager.getDialogManager().showDialog(QUERY,this,null,null);
    }

    /**
     * 设置梦想之花播报设置信息
     *
     * @param cmdindex
     * @param data
     */
    public void sendSetDreamFlowerConfigMsg(String cmdindex, Object data) {
        JSONObject object = new JSONObject();

        try {
            object.put("cmd", "402");
            object.put("gwID", device.gwID);
            object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            object.put("cmdindex", cmdindex);
            object.put("cmdtype", "set");
            if (data != null) {
                object.put("data", data);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        ProgressDialogManager.getDialogManager().showDialog(SET,this,null,null);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_nowTime:
                startActivity(new Intent(this, BroadcastTimeActivity.class)
                        .putExtra("deviceID", device.devID)
                        .putExtra("playTime", broadcastBean.getplayTime()));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tb_common_voice:
                sendSetDreamFlowerConfigMsg(FLOWER_BROADCAST_CONVENTIONAL, tbCommonVoice.isChecked() ? OPEN : CLOSE);
                break;
        }

    }

    private BroadcastBean parseCmd402Data(String json) {
        BroadcastBean bean = new BroadcastBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            JSONObject dataObject = jsonArray.optJSONObject(0);
            String norVoice = dataObject.optString("norVoice");
            String volum = dataObject.optString("volum");
            String playTime = dataObject.getString("playTime");

            bean.setNorVoice(norVoice);
            bean.setplayTime(playTime);
            bean.setVolum(volum);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

        return bean;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDreamFlowerBroadcast(DreamFlowerBroadcastEvent event) {
        if (event != null) {
            ProgressDialogManager.getDialogManager().dimissDialog(QUERY,0);
            ProgressDialogManager.getDialogManager().dimissDialog(SET,0);
            String data = event.jsonData;
            broadcastBean = parseCmd402Data(data);
            updateView(broadcastBean);
        }
    }
}
