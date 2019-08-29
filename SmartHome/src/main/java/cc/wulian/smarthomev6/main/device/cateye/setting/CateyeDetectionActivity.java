package cc.wulian.smarthomev6.main.device.cateye.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.CateyeStatusEntity;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.callback.IcamMsgEventHandler;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by hxc on 2017/5/16.
 * func:人体侦测设置界面
 */

public class CateyeDetectionActivity extends BaseTitleActivity implements IcamMsgEventHandler, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final int REQUEST_FOR_SENSITIVE = 1;
    public static final int REQUEST_FOR_RECORD_TYPE = 2;
    public static final int REQUEST_FOR_STAY_TIME = 3;
    private ToggleButton tbPirSwitch;
    private TextView tvSensitivityShow;
    private RelativeLayout rlPirSensitivity;
    private TextView tvStayShow;
    private RelativeLayout rlStayTime;
    private TextView tvRecordTypeShow;
    private RelativeLayout rlRecordType;
    private Button btnStartProtect;
    private CateyeStatusEntity cateyeStatusEntity;
    private ICamDeviceBean iCamDeviceBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_detection, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.CateEye_Move_Setting));
    }

    @Override
    protected void initView() {
        tbPirSwitch = (ToggleButton) findViewById(R.id.tb_pir_switch);
        tvSensitivityShow = (TextView) findViewById(R.id.tv_sensitivity_show);
        rlPirSensitivity = (RelativeLayout) findViewById(R.id.rl_pir_sensitivity);
        tvStayShow = (TextView) findViewById(R.id.tv_stay_show);
        rlStayTime = (RelativeLayout) findViewById(R.id.rl_stay_time);
        tvRecordTypeShow = (TextView) findViewById(R.id.tv_record_type_show);
        rlRecordType = (RelativeLayout) findViewById(R.id.rl_record_type);
        btnStartProtect = (Button) findViewById(R.id.btn_start_protect);

    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnStartProtect, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnStartProtect, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        super.initData();
        cateyeStatusEntity = (CateyeStatusEntity) getIntent().getSerializableExtra("CateyeStatusEntity");
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        if (cateyeStatusEntity != null) {
            if (!StringUtil.isNullOrEmpty(cateyeStatusEntity.getPIRSwitch()) && cateyeStatusEntity.getPIRSwitch().equals("0")) {
                hidePIRSettingView();
            } else if(!StringUtil.isNullOrEmpty(cateyeStatusEntity.getPIRSwitch()) && cateyeStatusEntity.getPIRSwitch().equals("1")) {
                showPIRSettingView();
            }
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlPirSensitivity.setOnClickListener(this);
        rlStayTime.setOnClickListener(this);
        rlRecordType.setOnClickListener(this);
        tbPirSwitch.setOnCheckedChangeListener(this);
        btnStartProtect.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_start_protect:
                configPirSetting();
                break;
            case R.id.rl_pir_sensitivity:
                startActivityForResult(new Intent(this, CateyeSensitivityActivity.class)
                        .putExtra("CateyeStatusEntity", cateyeStatusEntity), REQUEST_FOR_SENSITIVE);
                break;
            case R.id.rl_stay_time:
                startActivityForResult(new Intent(this, CateyeRecordTimeActivity.class)
                        .putExtra("CateyeStatusEntity", cateyeStatusEntity), REQUEST_FOR_STAY_TIME);
                break;
            case R.id.rl_record_type:
                startActivityForResult(new Intent(this, CateyeRecordTypeActivity.class)
                        .putExtra("CateyeStatusEntity", cateyeStatusEntity), REQUEST_FOR_RECORD_TYPE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FOR_RECORD_TYPE:
            case REQUEST_FOR_SENSITIVE:
            case REQUEST_FOR_STAY_TIME:
                if (resultCode == RESULT_OK) {
                    cateyeStatusEntity = (CateyeStatusEntity) data.getSerializableExtra("CateyeStatusEntity");
                    showPIRSettingView();
                }
                break;
        }
    }

    private void hidePIRSettingView() {
        tbPirSwitch.setChecked(false);
        rlRecordType.setVisibility(View.INVISIBLE);
        rlStayTime.setVisibility(View.INVISIBLE);
        rlPirSensitivity.setVisibility(View.INVISIBLE);
    }

    private void showPIRSettingView() {
        tbPirSwitch.setChecked(true);
        rlRecordType.setVisibility(View.VISIBLE);
        rlStayTime.setVisibility(View.VISIBLE);
        rlPirSensitivity.setVisibility(View.VISIBLE);
        switch (cateyeStatusEntity.getPIRDetectLevel()) {
            case "0":
                tvSensitivityShow.setText(getResources().getString(R.string.High));
                break;
            case "1":
                tvSensitivityShow.setText(getResources().getString(R.string.Low));
                break;
            default:
                break;
        }

        switch (cateyeStatusEntity.getHoverDetectTime()) {
            case "5":
                tvStayShow.setText("5s");
                break;
            case "10":
                tvStayShow.setText("10s");
                break;
            case "15":
                tvStayShow.setText("15s");
                break;
            case "20":
                tvStayShow.setText("20s");
                break;
            default:
                break;
        }
        switch (cateyeStatusEntity.getHoverProcMode()) {
            case "0":
                tvRecordTypeShow.setText(getResources().getString(R.string.Cateye_Snapshot));
                break;
            case "1":
                tvRecordTypeShow.setText(getResources().getString(R.string.Cateye_Video));
                break;
            default:
                break;
        }
    }

    private void configPirSetting() {
        IPCMsgController.MsgWulianBellQuerySetPIRConfigInformation(iCamDeviceBean.did, iCamDeviceBean.sdomain,
                Integer.parseInt(cateyeStatusEntity.getPIRSwitch()),
                Integer.parseInt(cateyeStatusEntity.getHoverDetectTime()),
                Integer.parseInt(cateyeStatusEntity.getPIRDetectLevel()),
                Integer.parseInt(cateyeStatusEntity.getHoverProcMode()));
        Log.i("hxc","-----");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (isChecked) {
            cateyeStatusEntity.setPIRSwitch("1");
            showPIRSettingView();
        } else {
            cateyeStatusEntity.setPIRSwitch("0");
            hidePIRSettingView();
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            Log.i("SettingSipMSg", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
            switch (event.getApiType()) {
                case BELL_QUERY_SET_PIR_CONFIG_INFORMATION:
                    ToastUtil.show(R.string.Setting_Fail);
                    break;
            }
        } else {
            switch (event.getApiType()) {
                case BELL_QUERY_SET_PIR_CONFIG_INFORMATION:
                    ToastUtil.show(R.string.Setting_Success);
                    this.finish();
                break;
            }
        }
    }
}
