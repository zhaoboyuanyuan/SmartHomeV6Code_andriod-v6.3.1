package cc.wulian.smarthomev6.main.device.eques;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.eques.icvss.api.ICVSSUserInstance;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesPirInfoBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesPirSwitchBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesSetPirBean;
import cc.wulian.smarthomev6.support.customview.NumTipSeekBar;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * 作者: chao
 * 时间: 2017/6/8
 * 描述: 移动侦测设置页
 * 联系方式: 805901025@qq.com
 */

public class EquesPirSettingActivity extends BaseTitleActivity {
    public static String GET_PIR = "GET_PIR";
    public static String SET_PIR = "SET_PIR";

    private RelativeLayout rl_pir_sensibility, rl_pir_time, rl_pir_type, rl_pir_ring, rl_pir_volume;
    private TextView tv_pir_sensibility, tv_pir_time, tv_pir_type, tv_pir_ring;
    private ToggleButton tb_pir_switch;
    private NumTipSeekBar sb_pir_volume;
    private Button btn_device_save;

    private String deviceId;
    private String uid;
    private boolean alarm_enable;
    private boolean isFirst = true;
    private EquesPirInfoBean equesPirInfoBean;
    private int[] ringNames = {R.string.Cateyemini_Humandetection_Who, R.string.Cateyemini_Humandetection_Du,
            R.string.Cateyemini_Humandetection_Alert,R.string.Cateyemini_Humandetection_Scream,
            R.string.Cateyemini_Humandetection_Mute,};

    private ICVSSUserInstance icvss;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        icvss = MainApplication.getApplication().getEquesApiUnit().getIcvss();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_eques_pir, true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Device_More_AlarmSetting_BodyDetectSetting));
    }

    @Override
    protected void initView() {
        rl_pir_sensibility = (RelativeLayout) findViewById(R.id.rl_pir_sensibility);
        rl_pir_time = (RelativeLayout) findViewById(R.id.rl_pir_time);
        rl_pir_type = (RelativeLayout) findViewById(R.id.rl_pir_type);
        rl_pir_ring = (RelativeLayout) findViewById(R.id.rl_pir_ring);
        rl_pir_volume = (RelativeLayout) findViewById(R.id.rl_pir_volume);
        tv_pir_sensibility = (TextView) findViewById(R.id.tv_pir_sensibility);
        tv_pir_time = (TextView) findViewById(R.id.tv_pir_time);
        tv_pir_type = (TextView) findViewById(R.id.tv_pir_type);
        tv_pir_ring = (TextView) findViewById(R.id.tv_pir_ring);
        tb_pir_switch = (ToggleButton) findViewById(R.id.tb_pir_switch);
        sb_pir_volume = (NumTipSeekBar) findViewById(R.id.sb_pir_volume);
        btn_device_save = (Button) findViewById(R.id.btn_device_save);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btn_device_save, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btn_device_save, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        uid = getIntent().getStringExtra("uid");
        deviceId = getIntent().getStringExtra("deviceId");
        alarm_enable = getIntent().getBooleanExtra("alarm_enable", false);
        tb_pir_switch.setChecked(alarm_enable);
        initSettingTab(alarm_enable);
        if (alarm_enable){
            if (!TextUtils.isEmpty(uid)) {
                progressDialogManager.showDialog(GET_PIR, EquesPirSettingActivity.this, null, null);
                icvss.equesGetDevicePirInfo(uid);
            }
        }else {
            isFirst = false;
        }
    }

    @Override
    protected void initListeners() {
        rl_pir_sensibility.setOnClickListener(this);
        rl_pir_time.setOnClickListener(this);
        rl_pir_type.setOnClickListener(this);
        rl_pir_ring.setOnClickListener(this);
        tb_pir_switch.setOnClickListener(this);
        btn_device_save.setOnClickListener(this);
        tb_pir_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                initSettingTab(isChecked);
            }
        });
        sb_pir_volume.setOnProgressChangeListener(new NumTipSeekBar.OnProgressChangeListener() {
            @Override
            public void onChange(int selectProgress) {
                equesPirInfoBean.volume = selectProgress + 1;
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tb_pir_switch:
                progressDialogManager.showDialog(GET_PIR, EquesPirSettingActivity.this, null, null);
                if (alarm_enable) {
                    if (!TextUtils.isEmpty(uid)) {
                        icvss.equesSetPirEnable(uid, 0);
                    }
                } else {
                    if (!TextUtils.isEmpty(uid)) {
                        icvss.equesSetPirEnable(uid, 1);
                    }
                }
                break;
            case R.id.rl_pir_sensibility:
                Intent sensibilityIntent = new Intent(this, EquesPirSensibilityActivity.class);
                sensibilityIntent.putExtra("equesPirInfoBean", equesPirInfoBean);
                startActivityForResult(sensibilityIntent, 0);
                break;
            case R.id.rl_pir_time:
                Intent timeIntent = new Intent(this, EquesPirTimeActivity.class);
                timeIntent.putExtra("equesPirInfoBean", equesPirInfoBean);
                startActivityForResult(timeIntent, 0);
                break;
            case R.id.rl_pir_type:
                Intent typeIntent = new Intent(this, EquesPirTypeActivity.class);
                typeIntent.putExtra("equesPirInfoBean", equesPirInfoBean);
                startActivityForResult(typeIntent, 0);
                break;
            case R.id.rl_pir_ring:
                Intent ringIntent = new Intent(this, EquesPirRingActivity.class);
                ringIntent.putExtra("equesPirInfoBean", equesPirInfoBean);
                startActivityForResult(ringIntent, 0);
                break;
            case R.id.btn_device_save:
                progressDialogManager.showDialog(SET_PIR, this, null, null, null);
                icvss.equesSetDevicePirInfo(uid, equesPirInfoBean.sense_time, equesPirInfoBean.sense_sensitivity, equesPirInfoBean.ringtone,
                        equesPirInfoBean.volume, equesPirInfoBean.Capture_num, equesPirInfoBean.format);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("status", alarm_enable);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initSettingTab(boolean isShow) {
        if (isShow) {
            rl_pir_sensibility.setVisibility(View.VISIBLE);
            rl_pir_time.setVisibility(View.VISIBLE);
            rl_pir_type.setVisibility(View.VISIBLE);
            rl_pir_ring.setVisibility(View.VISIBLE);
            rl_pir_volume.setVisibility(View.VISIBLE);
        } else {
            rl_pir_sensibility.setVisibility(View.GONE);
            rl_pir_time.setVisibility(View.GONE);
            rl_pir_type.setVisibility(View.GONE);
            rl_pir_ring.setVisibility(View.GONE);
            rl_pir_volume.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPirInfo(EquesPirInfoBean event) {
        if (TextUtils.equals(deviceId, event.from)) {
            equesPirInfoBean = event;
            progressDialogManager.dimissDialog(GET_PIR, 0);
            if (isFirst){
                isFirst = false;
            }else {
                alarm_enable = !alarm_enable;
                tb_pir_switch.setChecked(alarm_enable);
            }
            if (event.sense_sensitivity == 1) {
                tv_pir_sensibility.setText(R.string.High);
            } else if (event.sense_sensitivity== 2) {
                tv_pir_sensibility.setText(R.string.Low);
            }

            tv_pir_time.setText(event.sense_time + "s");

            if (event.format== 1) {
                tv_pir_type.setText(R.string.Cateye_Video);
            } else if (event.format== 0) {
                tv_pir_type.setText(R.string.Cateye_Snapshot);
            }

            if (event.ringtone== 1) {
                tv_pir_ring.setText(R.string.Cateyemini_Humandetection_Who);
            } else if (event.ringtone== 2) {
                tv_pir_ring.setText(R.string.Cateyemini_Humandetection_Du);
            } else if (event.ringtone== 3) {
                tv_pir_ring.setText(R.string.Cateyemini_Humandetection_Alert);
            } else if (event.ringtone== 4) {
                tv_pir_ring.setText(R.string.Cateyemini_Humandetection_Scream);
            } else if (event.ringtone== 5) {
                tv_pir_ring.setText(R.string.Cateyemini_Humandetection_Mute);
            }

            sb_pir_volume.setSelectProgress(event.volume - 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPirSwitcho(EquesPirSwitchBean event) {
        if (TextUtils.equals(deviceId, event.from)) {
            if (event.result == 1) {
                if (alarm_enable) {
                    progressDialogManager.dimissDialog(GET_PIR, 0);
                    alarm_enable = !alarm_enable;
                    tb_pir_switch.setChecked(alarm_enable);
                } else {
                    if (!TextUtils.isEmpty(uid)) {
                        icvss.equesGetDevicePirInfo(uid);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPirSet(EquesSetPirBean event) {
        if (TextUtils.equals(deviceId, event.from)) {
            progressDialogManager.dimissDialog(SET_PIR, 0);
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 4) {
            int position = data.getIntExtra("ring", equesPirInfoBean.ringtone);
            tv_pir_ring.setText(ringNames[position- 1]);
            equesPirInfoBean.ringtone = position;
        }else if (resultCode == 2){
            int time = data.getIntExtra("time", equesPirInfoBean.sense_time);
            tv_pir_time.setText(time +"s");
            equesPirInfoBean.sense_time = time;
        }else if (resultCode == 1){
            int sensibility = data.getIntExtra("sensibility", equesPirInfoBean.sense_sensitivity);
            if (sensibility == 1){
                tv_pir_sensibility.setText(R.string.High);
            }else if(sensibility == 2){
                tv_pir_sensibility.setText(R.string.Low);
            }
            equesPirInfoBean.sense_sensitivity = sensibility;
        }else if (resultCode == 3){
            int type = data.getIntExtra("type", equesPirInfoBean.format);
            if (type == 0){
                tv_pir_type.setText(R.string.Cateye_Snapshot);
            }else if(type == 1){
                tv_pir_type.setText(R.string.Cateye_Video);
            }
            equesPirInfoBean.format = type;
        }
    }
}
